#!/usr/bin/perl
use strict;
use warnings;
use Cwd;
use File::Find;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::IO 'parse_tree';
use Bio::Phylo::Util::Logger ':levels';

# store current working directory for File::Find
my $cwd = getcwd();

# process command line arguments
my ( $find, $infile );
my ( $ext, $verbosity, $type ) = ( 'tsv', WARN, 'adjacency' );

# possible properties:
# 	tree_length
# 	tree_height
# 	number_of_terminals
# 	imbalance
# 	i2
# 	gamma
# 	rohlf_stemminess
# 	fp
# 	es
# 	pe
# 	shapley	
# 	avtd
my @properties = qw(fiala_stemminess);
GetOptions(
	'ext=s'    => \$ext,
	'infile=s' => \$infile,
	'prop=s'   => \@properties,
	'find'     => \$find,
	'verbose+' => \$verbosity,
	'type=s'   => \$type,
);

# instantiate logger
my $log = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => [ 'main', 'Bio::Phylo::Parsers::Adjacency' ],
);

# print header
$|++;
print join( "\t", 'file', @properties ), "\n";

# start processing
if ( $find ) {	

	# start recursing
	$log->info("going to scan for $ext files recursively, starting at $indir");
	find(\&wanted,$indir);
}
else {

	# start reading
	$log->info("going to scan for $ext files in $indir");
	opendir my $dh, $indir or die $!;
	while( my $entry = readdir $dh ) {
		next if $entry =~ /^\.\.?$/;
		next if $entry !~ /\.$ext$/;

		# process file
		my $file = $indir . '/' . $entry;
		eval { do_stats($infile) };
		if ( $@ ) {
			$log->error("Problem with $file: $@");
		}		
	}
}

sub wanted {
	if ( /\.$ext$/ ) {
	
		# process file
		my $file = $cwd . '/' . $File::Find::name;
		eval { do_stats($file) };
		if ( $@ ) {
			$log->error("Problem with $file: $@");
		}
	}
}

sub do_stats {
	my $file = shift;
	$log->info("going to compute tree stats for $file");

	# read tree
	my $tree = parse_tree(
		'-format'     => $type,
		'-file'       => $file,
		'-as_project' => 1,
	);
	$log->info("done reading $type data from $file");
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