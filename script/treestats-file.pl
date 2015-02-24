#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::IO 'parse_tree';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $infile;
my $verbosity = WARN;
my $format = 'adjacency';

# possible properties:
# 	tree_length
# 	tree_height
# 	number_of_terminals
# 	imbalance
# 	i2
# 	gamma
# 	rohlf_stemminess
#	fiala_stemminess
# 	fp
# 	es
# 	pe
# 	shapley	
# 	avtd
my @properties;
GetOptions(
	'infile=s' => \$infile,
	'prop=s'   => \@properties,
	'verbose+' => \$verbosity,
	'format=s' => \$format,
);

# instantiate logger
my $log = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => [ 'main', 'Bio::Phylo::Parsers::Adjacency' ],
);

# print header
$|++;
print join( "\t", 'file', @properties ), "\n";
do_stats($infile);

sub do_stats {
	my $file = shift;
	$log->info("going to compute tree stats for $file");

	# read tree
	my $tree = parse_tree(
		'-format'     => $format,
		'-file'       => $file,
		'-as_project' => 1,
	);
	$log->info("done reading adjacency table from $file");
	$tree->scale(1);
	
	# count tips to average over
	my $tips = scalar( @{ $tree->get_terminals } );
	
	# resolve if need be
	if ( not $tree->is_binary ) {
		$tree->resolve;
		$file .= '*';
		$log->warn("tree needed to be resolved, marked with *");
	}
	my @result = ( $file );
	
	# calculate results
	for my $p ( @properties ) {
		$log->info("calculating $p");
		my $method = 'calc_' . $p;
		my $val = $tree->$method;
		
		# average, if need be
		if ( ref($val) and ref($val) eq 'HASH' ) {
			$val = sum(values(%$val))/$tips;
		}
		
		# store result
		push @result, $val;
	}
	
	# print output
	print join( "\t", @result ), "\n";
}