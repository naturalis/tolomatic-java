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
$log->info("going to read pre and post files ($pre and $post)");
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
recurse( 'root' => $qtree );
sub recurse {
	my ( $name, $node ) = @_;
	
	# bins for pre and post children of focal node
	my ( @pre, @post );
	my %set = ( 
		'pre'  => \@pre, 
		'post' => \@post,
	);
	
	# iterate over focal node's children
	while ( my ( $key, $value ) = each %{ $node } ) {
	
		# recurse if child is internal, otherwise add to bin
		ref $value ? recurse( $key => $value ) : push @{ $set{$value} }, $key;
	}
	
	# do rarefaction avtd if both bins are populated
	if ( @pre >= 2 and @post >= 2 ) {
		$log->info("going to do AVTD for $name");
		avtd( $name, \@pre, \@post );
	}
}

# do average taxonomic distance
sub avtd {
	my ( $name, $pre, $post ) = @_;
	my @pre  = grep { defined $_ } map { $tree->_rs->find($_) } @{ $pre  };
	my @post = grep { defined $_ } map { $tree->_rs->find($_) } @{ $post };
	for my $i ( 1 .. $reps ) {
		
		# make shuffled, rarefacted copies
		my ($max) = sort { $a <=> $b } scalar(@pre), scalar(@post);
		my @pre_copy  = shuffle @pre;
		my @post_copy = shuffle @post;
		@pre_copy  = splice @pre_copy,  $max if @pre_copy  > $max;
		@post_copy = splice @post_copy, $max if @post_cope > $max
		
			
	}
}
