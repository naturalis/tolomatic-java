#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use Bio::Phylo::IO 'parse';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $intree, $qiime );
GetOptions(
	'intree=s' => \$intree,
	'qiime=s'  => \$qiime,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $forest = parse(
	'-format' => 'newick',
	'-file'   => $intree,
);
my $log = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);

# read qiime table
my %taxon;
{
	$log->info("going to read qiime table");
	open my $fh, '<', $qiime or die $!;
	while(<$fh>) {
		chomp;
		next if /^#/;
		my @line = split;
		my $id = shift @line;
		my @taxonomy = split /;/, $line[-1];
		$taxon{$id} = $taxonomy[-1];	
	}
	$log->info("done reading qiime table");	
}

# relabel nodes
$log->info("going to relabel nodes");
$forest->visit(sub{
	my $tree = shift;
	for my $tip ( @{ $tree->get_terminals } ) {
		my $id = $tip->get_name;
		if ( my $taxon = $taxon{$id} ) {
			my $label = "${taxon}_${id}";
			$tip->set_name($label);
		}
		else {
			$log->warn("taxon $id not in tree");
		}
	}
});
$log->info("done");

# print output
print $forest->to_newick;