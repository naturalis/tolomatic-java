#!/usr/bin/perl
use strict;
use warnings;
use Megatree;
use File::Slurp;
use Data::Dumper;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $db, $pre, $post );
GetOptions(
	'db=s'     => \$db,
	'pre=s'    => \$pre,
	'post=s'   => \$post,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $tree = Megatree->connect($db);
my $log = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);

# compute distances from all tips to each of their ancestors.
# the rationale is that we can then compute the patristic
# distance between two tips by searching for their MRCA
# (being the shared ancestor with the highest ID) and add
# up the distances to the MRCA for the two nodes
my %node_dist;
$log->info("going to compute distances in TREE $db ($tree)");
recurse($tree->get_root);
sub recurse {
	my $node = shift;
	
	# avoid warnings, log progress
	no warnings 'recursion';
	my $count = scalar keys %node_dist;
	$log->debug($count) unless $count % 1000;

	# depth first, working from tips to root
	recurse($_) for @{ $node->get_children };

	# we always add the distance to the focal node's parent, 
	# so never process the root itself
	if ( my $parent = $node->get_parent ) {
		my $pid = $parent->get_id;
		my $nid = $node->get_id;
		
		# instantiate a new hash for this tip, populate it with
		# the path to the parent, which is the length of the 
		# tip's branch
		if ( $node->is_terminal ) {
			$node_dist{$nid} = { $pid => $node->get_branch_length };
		}
		
		# focal node is internal, need to extend existing paths,
		# not create a new one
		else {
		
			# iterate over all the paths that contain the focal node
			for my $path ( grep { defined $_->{$nid} } values %node_dist ) {
			
				# add an entry to the focal node's parent, assign that
				# the distance to the focal node + the node's length
				$path->{$pid} = ( $path->{$nid} || 0 ) + ( $node->get_branch_length || 0 );
			}
		}
	}
}

# read the taxon lists
my ( @pre, @post, @survivors, @immigrants, @deceased, %lookup );
turnover();
sub turnover {

	# instantiate local variables, will take keys afterwards
	my ( %pre, %post, %survivors, %immigrants, %deceased );
	
	# read the list of leaves before the event
	$log->info("reading PRE file $pre");
	PRE: for my $id ( read_file( $pre, 'chomp' => 1 ) ) {
		if ( my $node = $tree->_rs->search({'name'=>$id})->single ) {
			$pre{$id} = $node;
			next PRE;
		}
		$log->warn("$id is not in tree");
	}
	
	# read the list of leaves after the event
	$log->info("reading POST file $post");
	POST: for my $id ( read_file( $post, 'chomp' => 1 ) ) {
		if ( my $node = $tree->_rs->search({'name'=>$id})->single ) {
			$post{$id} = $node;
			$pre{$id} ? $survivors{$id} = $node : $immigrants{$id} = $node;
			next POST;
		}
		$log->warn("$id is not in tree");
	}
	
	# compute the deceased list
	not $post{$_} and $deceased{$_} = $pre{$_} for keys %pre;
	
	# get the IDs
	@pre        = keys %pre;
	@post       = keys %post;
	@survivors  = keys %survivors; 
	@immigrants = keys %immigrants; 
	@deceased   = keys %deceased;
	%lookup = ( %pre, %post );
}

# calculates patristic distance between arguments (IDs)
sub dist {
	my ( $this_name, $that_name ) = @_;
	
	# the distances hash uses the primary keys from 
	# the database, hence need to do a lookup
	my $this = $lookup{$this_name}->get_id;
	my $that = $lookup{$that_name}->get_id;
	
	# primary keys are applied in pre-order, hence sorting
	# in descending order starts with the most recent
	for my $anc ( sort { $b <=> $a } keys %{ $node_dist{$this} } ) {
	
		# found the MRCA
		if ( defined $node_dist{$that}->{$anc} ) {
			return $node_dist{$this}->{$anc} + $node_dist{$that}->{$anc};
		}
	}
	$log->error("$this and $that not in same tree?");
}

# calculates mean distance between two sets
sub mean_dist {
	$log->info("calculating mean distance between two sets");
	my ( $this, $that ) = @_;
	my @dist;
	for my $this ( @$this ) {
		for my $that ( @$that ) {
			push @dist, dist( $this, $that );
		}
	}
	return scalar(@dist) ? sum(@dist)/scalar(@dist) : undef;
}

# calculates mean distance within a set
sub mean_dist_within {
	$log->info("calculating mean distance within a set");
	my @list = @_;
	my @dist;
	for my $i ( 0 .. $#list - 1 ) {
		for my $j ( $i + 1 .. $#list ) {
			push @dist, dist( $list[$i], $list[$j] );
		}
	}
	return scalar(@dist) ? sum(@dist)/scalar(@dist) : undef;	
}

# print hash as tsv table
sub print_tabular {
	my %hash = @_;
	my @keys = sort { $a cmp $b } keys %hash;
	no warnings 'uninitialized';
	print join("\t",@keys), "\n";
	print join("\t",@hash{@keys}), "\n";
}

# compute and print results
print_tabular(
	'TREE' => $db,                                # tree database file
	'PRE' => $pre,                                # pre list file
	'POST' => $post,                              # post list file
	'PRE_N' => scalar(@pre),                      # pre list size
	'POST_N' => scalar(@post),                    # post list size
	'I_N' => scalar(@immigrants),                 # immigrants list size
	'S_N' => scalar(@survivors),                  # survivors list size
	'D_N' => scalar(@deceased),                   # deceased list size
	'PRE<->PRE' => mean_dist_within(@pre),        # mean dist within pre list
	'POST<->POST' => mean_dist_within(@post),     # mean dist within post list
	'I<->I' => mean_dist_within(@immigrants),     # mean dist within immigrants
	'S<->S' => mean_dist_within(@survivors),      # mean dist within survivors
	'D<->D' => mean_dist_within(@deceased),       # mean dist within deceased
	'PRE<->I' => mean_dist(\@pre,\@immigrants),   # mean dist between pre and immigrants
	'PRE<->S' => mean_dist(\@pre,\@survivors),    # mean dist between pre and survivors
	'PRE<->D' => mean_dist(\@pre,\@deceased),     # mean dist between pre and deceased
	'POST<->I' => mean_dist(\@post,\@immigrants), # mean dist between post and immigrants
	'POST<->S' => mean_dist(\@post,\@survivors),  # mean dist between post and survivors
	'POST<->D' => mean_dist(\@post,\@deceased),   # mean dist between post and deceased
);