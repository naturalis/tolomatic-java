#!/usr/bin/perl
use strict;
use warnings;
use feature 'say';
use Megatree;
use Template;
use File::Slurp;
use Getopt::Long;
use Statistics::Descriptive;
use List::Util qw'shuffle sum';
use Bio::Phylo::IO 'parse_tree';
use Bio::Phylo::Util::Logger ':levels';

# these are the fields that will end up in the output. the ones prefixed with
# 'stat' are methods of the Statistics::Descriptive::Full object; the ones 
# prefixed with 'tree' are methods of the Bio::Phylo::Forest::Tree object;
# the others are defined in the call to the Template::Toolkit processor
my @fields = (
	'infile',  
	'pre', 
	'post', 
	'class', 
	'id', 
	'stat.count',
	'stat.mean',
	'stat.variance',
	'stat.standard_deviation',
	'stat.min',
	'stat.max',
	'stat.median',
	'stat.harmonic_mean',
	'stat.geometric_mean',
	'tree.calc_tree_length',
	'tree.calc_redundancy',
	'tree.calc_gamma',
	'tree.calc_fiala_stemminess',
	'tree.calc_imbalance',
	'tree.calc_i2',
	'tree.calc_fp_mean',
	'tree.calc_es_mean',
	'tree.calc_pe_mean',
	'tree.calc_shapley_mean',
	'tree.to_newick',
);

# process command line arguments
my $verbosity = WARN;
my ( $infile, $pre, $post, $cut, $db );
GetOptions(
	'db=s'     => \$db,
	'infile=s' => \$infile,
	'pre=s'    => \$pre,
	'post=s'   => \$post,
	'cut=i'    => \$cut,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $tmpl = Template->new;
my $stat = Statistics::Descriptive::Full->new;
my $tree = parse_tree(
	'-format' => 'newick',
	'-file'   => $infile,
);
my $log  = Bio::Phylo::Util::Logger->new(
	'-level'  => $verbosity,
	'-class'  => 'main',
);

# read presence lists
my %class;
$class{$_} = 'pre'  for grep { /\S/ } read_file( $pre,  'chomp' => 1 );
$class{$_} = 'post' for grep { /\S/ } read_file( $post, 'chomp' => 1 );

# this will bin the mrcas into sets of roots of clades that are entirely
# pre-, entirely post- and mixed.
my %bin  = ( 'pre' => [], 'post' => [], 'mixed' => [] );
$tree->visit_depth_first(
	'-post' => sub {
		my $node = shift;
		if ( $node->is_internal ) {
		
			# bin the subtended tips
			my ( @pre, @post );
			for my $tip ( @{ $node->get_terminals } ) {
				my $name = $tip->get_name;
				if ( my $class = $class{$name} ) {
					$class eq 'pre' ? push @pre, $tip : push @post, $tip;
				}
			}
			
			# bin the node, might be mixed as there are tips in both
			# @pre and @post. However, we only want this node if it's
			# not the ancestor of a node already in the mixed bin.
			if ( @pre and @post ) {
			
				# if 'mixed', we want the most recent one, because from there
				# 'mixedness' will only propagate further to the root	
				my @desc = grep { $_->is_descendant_of($node) } @{ $bin{'mixed'} };	
				if ( not @desc ) {	
					$log->info("found mixed mrca");
					push @{ $bin{'mixed'} }, $node;
				}
			}
			
			# bin the node, at least some descendants are in the focal
			# pre or post array. Assuming we want this node, we will have
			# to filter out all the nodes that descend from it.
			elsif ( @pre > 1 or @post > 1 ) {
				
				# this might be the mrca of a purely 'pre' or 'post' clade - but
				# we want the oldest pure mrca, so any descendants (which we 
				# have already seen in post-order) must be removed				
				my $key = @pre ? 'pre' : 'post';
				my @clean = grep { ! $_->is_descendant_of($node) } @{ $bin{$key} };
				push @clean, $node;
				$bin{$key} = \@clean;
				$log->info("have ".scalar(@clean)." $key mrcas");
			}
		}
	}
);
$log->info("done binning mrcas");

# print the header
say join "\t", @fields;

# prepare the template by turning header fields into directives
my $template = join( "\t", map { '[% '.$_.' %]' } @fields )."\n";

# iterate over the classes
for my $key ( keys %bin ) {	

	# iterate over the MRCAs in each class
	MRCA: for my $mrca ( @{ $bin{$key} } ) {
		my @tips = grep { $class{$_->get_name} } @{ $mrca->get_terminals };
		
		# check to see if we want this clade
		if ( $cut and @tips > $cut ) {
			$log->info(scalar(@tips). ">$cut, ignoring");
			next MRCA;
		}
		
		# collect all pairwise distances among subtended tips
		$log->info("calculating pairwise distances among ".scalar(@tips)." nodes");
		my @dist;		
		for my $i ( 0 .. $#tips - 1 ) {
			for my $j ( $i + 1 .. $#tips ) {
				push @dist, $tips[$i]->calc_patristic_distance($tips[$j]);				
			}
		}
		
		# produce output
		$stat->add_data(@dist);
		$tmpl->process( \$template, {
			'infile' => $infile,
			'pre'    => $pre,
			'post'   => $post,
			'class'  => $key,
			'id'     => $mrca->get_id,
			'stat'   => $stat,
			'tree'   => make_subtree(@tips),
		}) || $log->error($tmpl->error);
		$stat->clear;		
	}
}

sub make_subtree {
	my ( @tips ) = @_;
	my $list = join ',', map { $_->get_name } @tips;
	my $newick = `prune_megatree -db $db -list $list`;
	my $tree = parse_tree(
		'-format' => 'newick',
		'-string' => $newick,
	);
	$tree->get_root->set_branch_length(0);
	$tree->scale(1);
	return $tree;
}

