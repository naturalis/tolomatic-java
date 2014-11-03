#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use Digest::MD5 'md5_hex';

my ( $dir, $infile, $steps );
GetOptions(
	'dir=s'    => \$dir,
	'infile=s' => \$infile,
	'steps=i'  => \$steps,
);

open my $fh, '<', $infile or die $!;
while(<$fh>) {
	chomp;
	my $id = $_;
	my $md5 = md5_hex($id);
	my @segments = split //, $md5;
	my $path = join '/', $dir, @segments[0..($steps-1)], $md5;
	if ( not -e $path ) {
		warn "no path for $id: $path";
	}
	else {
		print "OK: $id\n";
	}
}