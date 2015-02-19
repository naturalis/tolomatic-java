#!/usr/bin/perl
use strict;
use warnings;

my @columns = qw(primer treatment site replicate resolved stemminess);
print join("\t", @columns), "\n";

while(<>) {
	chomp;
	
	# skip over redundant header lines
	if ( m{^(\w+)/(pre|post)/([a-zA-Z]+)/(\d+)\.tsv(\*?)\t(\d+\.\d+)$} ) {
		my ( $primer, $treatment, $site, $replicate, $resolved, $stemminess ) = 
		   ( $1,      $2,         $3,    $4,         $5,        $6          );
		
		# turn asterisk into boolean   
		$resolved = $resolved ? 'TRUE' : 'FALSE';
		   
		# print output data
		print join("\t", 
			$primer, 
			$treatment, 
			$site, 
			$replicate, 
			$resolved, 
			$stemminess 		
		), "\n";	
	}
}