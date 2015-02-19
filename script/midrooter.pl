#!/usr/bin/perl
use strict;
use warnings;
use Megatree;
use Getopt::Long;
use Bio::Phylo::IO 'parse_tree';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $intree, $outfile, $newick );
GetOptions(
	'verbose+'  => \$verbosity,
	'intree=s'  => \$intree,
	'outfile=s' => \$outfile,
	'newick'    => \$newick,
);

# instantiate helper objects
my $log  = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);
$log->info("going to parse tree $intree");
my $tree = parse_tree(
	'-format'     => 'newick',
	'-file'       => $intree,
	'-as_project' => 1,
);

# get the midpoint node
$log->info("going to calculate the midpoint node");
my $mid = $tree->get_midpoint;
$log->info("found midpoint node");

# apply the rooting
$log->info("going to root below the midpoint");
$mid->set_root_below;
$log->info("done rooting");

# persist the tree
if ( $newick ) {
	open my $fh, '>', $outfile or die $!;
	print $fh $tree->to_newick;
}
else {
	$log->info("going to persist re-rooted tree in $outfile");
	Megatree->persist( 
		'-tree' => $tree,
		'-file' => $outfile,
	);
	$log->info("done persisting - exiting");
}


