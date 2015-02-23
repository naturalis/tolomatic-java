#!/usr/bin/perl
use strict;
use warnings;
use Megatree;
use File::Slurp;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $db, $mrcas, $infile );
GetOptions(
	'db=s'     => \$db,
	'mrcas=s'  => \$mrcas,
	'infile=s' => \$infile,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $log = Bio::Phylo::Util::Logger->new(
	'-level'  => $verbosity,
	'-class'  => 'main',
);
$log->info("going to use tree $db");
my $tree = Megatree->connect($db);


# read the leaves, return node objects
my @leaves;
{
	$log->info("going to read leaves from $infile");
	for my $id ( read_file( $infile, 'chomp' => 1 ) ) {
		if ( my $node = $tree->_rs->search({ 'name' => $id })->single ) {
			push @leaves, $node;
		}
		else {
			$log->warn("$id is not in tree");
		}
	}
}

# read the MRCAs, return node objects
my @mrcas;
{
	$log->info("going to read MRCAs from $mrcas");
	for my $id ( read_file( $mrcas, 'chomp' => 1 ) ) {
		if ( my $node = $tree->_rs->find($id) ) {
			push @mrcas, $node;
		}
		else {
			$log->warn("$id is not in tree");
		}
	}
}

# calculate the distances
$log->info("going to calculate distances");
my @distances;
for my $leaf ( @leaves ) {
	my $dist = ( $leaf->get_branch_length || 0 );
	ANC: for my $anc ( @{ $leaf->get_ancestors } ) {
		$dist += ( $anc->get_branch_length || 0 );
		my $id = $anc->get_id;
		for my $mrca ( @mrcas ) {
			last ANC if $mrca->get_id == $id;
		}
	}
	push @distances, $dist;
}

# print result
print join( "\t", qw(TREE MRCAS INFILE LEAF<->MRCA) ), "\n";
print
	$db, "\t",
	$mrcas, "\t",
	$infile, "\t",
	sum(@distances)/scalar(@distances), "\n";