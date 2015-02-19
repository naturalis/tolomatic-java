#!/usr/bin/perl
use strict;
use warnings;
use Megatree;
use Getopt::Long;
use Bio::Phylo::IO 'parse_tree';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $intree, $outfile, $newick, $taxa );
GetOptions(
	'verbose+'  => \$verbosity,
	'intree=s'  => \$intree,
	'outfile=s' => \$outfile,
	'taxa=s'    => \$taxa, # comma-separated list
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

# get the outgroup leaves
my @outgroup;
for my $label ( split /,/, $taxa ) {
	if ( my $leaf = $tree->get_by_name($label) ) {
		$log->info("found leaf label $label");
		push @outgroup, $leaf;
	}
	else {
		$log->warn("couldn't find leaf label $label");
	}
}

# get the MRCA of the outgroup leaves
$log->info("going to compute MRCA for ".scalar(@outgroup)." outgroup leaves");
my $mrca = $tree->get_mrca(\@outgroup);
$log->info("found MRCA $mrca");

# apply the rooting
$log->info("going to root below the MRCA");
$mrca->set_root_below;
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


