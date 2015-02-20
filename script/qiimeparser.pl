#!/usr/bin/perl
use strict;
use warnings;
use IO::File;
use Getopt::Long;

# process command line arguments
my ( $infile, $outdir );
GetOptions(
	'infile=s' => \$infile,
	'outdir=s' => \$outdir,
);

my @header;
my %handle;
open my $fh, '<', $infile or die $!;
LINE: while(<$fh>) {
	chomp;
	
	# skip over format header		
	next LINE if /# QIIME .+ OTU table/;
	
	# process line
	my @line = split /\t/, $_;	
	
	# read column headers
	if ( not @header ) {
		@header = @line;
		
		# open writable handles for all the site columns
		COLUMN: for my $h ( @header ) {
		
			# skip column with OTU ID or taxonomic consensus lineage
			next COLUMN if $h eq '#OTU ID' || $h eq 'Consensus Lineage';
			$handle{$h} = IO::File->new( "${outdir}/${h}.txt", 'w' );
		}
		next LINE;
	}
	
	# write the ID (column 0) to the handle that corresponds with the focal column ($i)
	# if the focal cell has a non-zero value
	for my $i ( 1 .. $#header - 1 ) {
		$handle{$header[$i]}->say( $line[0] ) if $line[$i];
	}	
}