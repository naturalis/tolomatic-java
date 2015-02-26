#!/usr/bin/perl

# pragmas
use strict;
use warnings;
use feature 'say';

# libraries
use Megatree;
use File::Slurp;
use Getopt::Long;
use Data::Dumper;
use List::Util qw(shuffle sum);
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $qiime, $db, $reps, $pre, $post );
GetOptions(
	'qiime=s'  => \$qiime,
	'db=s'     => \$db,
	'reps=i'   => \$reps,
	'pre=s'    => \$pre,
	'post=s'   => \$post,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $tree = Megatree->connect($db);
my $log  = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);

# read pre and post lists
my %class;
$log->info("going to read pre file ($pre)");
$log->info("going to read post file ($post)");
$class{$_} = 'pre'  for read_file( $pre,  'chomp' => 1 );
$class{$_} = 'post' for read_file( $post, 'chomp' => 1 );

# read qiime data
my $qtree = {};
{
	
	# read from QIIME v1.2.1-dev OTU table
	$log->info("going to read QIIME table ($qiime)");
	open my $fh, '<', $qiime or die $!;
	LINE: while(<$fh>) {
		/^#/ ? next LINE : chomp;
				
		# tokenize line
		my @line = split;
		my $otu  = shift @line;
		next LINE unless $class{$otu};
		
		# iterate over taxonomy lineage
		my $root = $qtree;			
		for my $name ( split /;/, $line[-1] ) {
			$root = $root->{$name} || ( $root->{$name} = {} );
		}
		$root->{$otu} = $class{$otu};
	}
}

# analyze tree shape
$log->info("going to analyze tree shape");
say join "\t", qw(name replicate pre post n);
recurse( 'root' => $qtree );
sub recurse {
	my ( $name, $node ) = @_;
	
	# bins for pre and post children of focal node
	my %set = ( 'pre'  => [], 'post' => [] );
	
	# iterate over focal node's children
	while ( my ( $key, $value ) = each %{ $node } ) {
	
		# recurse if child is internal, otherwise add to bin
		ref $value ? recurse( $key => $value ) : push @{ $set{$value} }, $key;
	}
	
	# do rarefaction avtd if both bins are populated
	my $pre_size  = scalar( @{ $set{'pre'} } );
	my $post_size = scalar( @{ $set{'post'} } );
	if ( $pre_size >= 2 and $post_size >= 2 and $name ne 'environmental' ) {
		$log->info("going to do AVTD for $name (pre: $pre_size, post: $post_size)");
		$log->debug(Dumper(\%set));
		avtd( $name, $set{'pre'}, $set{'post'} );
	}
}

sub get_nodes {
	return grep { defined $_ } map { $tree->_rs->search({ 'name' => $_ })->single } @_;
}

# do average taxonomic distance
sub avtd {
	my ( $name, $pre, $post ) = @_;
	my @pre  = get_nodes( @{ $pre  } );
	my @post = get_nodes( @{ $post } );
	$log->info("pre nodes: ".scalar(@pre));
	$log->info("post nodes: ".scalar(@post));
	for my $i ( 1 .. $reps ) {
		my ( @prd, @pod );
		
		# make shuffled, even-sized copies
		my ($max) = sort { $a <=> $b } scalar(@pre), scalar(@post);
		my @pr = shuffle @pre;
		my @po = shuffle @post;
		splice @pr, $max if @pr > $max;
		splice @po, $max if @po > $max;
		
		# compute heights
		for my $j ( 0 .. $#pr ) {
			my $height = $pr[$j]->get_mrca( $po[$j] )->height;		
			push @prd, ( $pr[$j]->height - $height );
			push @pod, ( $po[$j]->height - $height );
		}
		say join "\t", $name, $i, sum(@prd)/scalar(@prd), sum(@pod)/scalar(@pod), $max;
	}
}
