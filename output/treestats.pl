#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::IO 'parse_tree';

# process command line arguments
my $ext = 'txt';
my $indir = '.';
my @properties = qw(
	tree_length
	tree_height
	number_of_terminals
	imbalance
	i2
	gamma
	fiala_stemminess
	rohlf_stemminess
	fp
	es
	pe
	shapley	
	avtd
);
GetOptions(
	'ext=s'   => \$ext,
	'indir=s' => \$indir,
	'prop=s'  => \@properties,
);

# print header
$|++;
print join( "\t", 'file', @properties ), "\n";

# start reading
opendir my $dh, $indir or die $!;
while( my $entry = readdir $dh ) {
	next if $entry =~ /^\.\.?$/;
	next if $entry !~ /\.$ext$/;

	# create path
	my $file = $indir . '/' . $entry;
	
	# read tree
	my $tree = parse_tree(
		'-format'     => 'newick',
		'-file'       => $file,
		'-as_project' => 1,
	);
	$tree->scale(1);
	
	# count tips to average over
	my $tips = scalar( @{ $tree->get_terminals } );
	
	# resolve if need be
	if ( not $tree->is_binary ) {
		$tree->resolve;
		$file .= '*';
	}
	my @result = ( $file );
	
	# calculate results
	for my $p ( @properties ) {
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