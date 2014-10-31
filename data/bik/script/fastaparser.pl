#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;

# this script is used to parse a FASTA file 
# in order to get lists of names that actually occur in the megatrees, split by:
# 1. primer set (determines the tree)
# 2. pre/post deepwater horizon
# 3. geographical location
# this script then checks the previous names lists, which were parsed out of the QIIME
# table, to clean out all the names that failed to align (and are therefore missing
# from the trees), or, when using the --flip argument, to retain those names that occur
# in the FASTA file and omit all the others

# process command line arguments
my ( $infile, $outdir, $flip );
GetOptions(
	'infile=s' => \$infile,
	'outdir=s' => \$outdir,
	'flip'     => \$flip, # flip the intersection
);

# maps from primer to primer set
my %map = (
	'NF1'    => 'NF118Sr2b',
	'18Sr2b' => 'NF118Sr2b',
	'F04'    => 'F04R22',
	'R22'    => 'F04R22',
);

# start reading
my %delete;
{
	open my $fh, '<', $infile or die $!;
	while(<$fh>) {
		chomp;
		
		# parse FASTA defline, e.g. >40846 PreBayfrontPark.R22_665863 RC1..213
		if ( /^>(\d+) (Pre|Post)([a-zA-Z]+)\.([^_]+)_\d+/ ) {
			my ( $otu, $prepost, $location, $primer ) = ( $1, $2, $3, $4 );
		
			# create path to names list
			my $path = join '/', $outdir, $map{$primer}, lc $prepost, $location . '.lst';
		
			# start making the list of things to delete 
			$delete{$path} = {} if not $delete{$path};
		
			# add focal OTU
			$delete{$path}->{$otu} = 1;
		}
	}
}

# iterate over files, splice out things to delete
while( my ( $path, $skip ) = each %delete ) {
	my @keep;
	
	# read in
	{
		open my $fh, '<', $path or die $!;
		while(<$fh>) {
			chomp;
			if ( $flip ) {
				push @keep, $_ if $skip->{$_};
			}
			else {
				push @keep, $_ unless $skip->{$_};
			}
		}
	}	
	
	# write out
	{
		open my $fh, '>', $path or die $!;
		print $fh map { "$_\n" } @keep;
		close $fh;
	}
}