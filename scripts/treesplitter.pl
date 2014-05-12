#!/usr/bin/perl
use strict;
use warnings;
use File::Path 'make_path';
use Digest::MD5 'md5_hex';
use Getopt::Long;
use Bio::Phylo::IO 'parse_tree';

# process command line arguments
my $infile;
my $outdir;
my $format         = 'newick';
my $ultrametricize = 0;
my $hashdepth      = 5;
my $clean          = 1;
GetOptions(
	'infile=s'       => \$infile,
	'format=s'       => \$format,
	'outdir=s'       => \$outdir,
	'hashdepth=i'    => \$hashdepth,
	'ultrametricize' => \$ultrametricize,
	'clean'          => \$clean,
);

# read the tree
my $tree = parse_tree(
	'-format' => $format,
	'-file'   => $infile,
	'-as_project' => 1,
);

# apply the pre-order traversal
my $counter = 1;
$tree->visit_depth_first( 
	'-pre' => sub { 
		my $node = shift;
		$node->set_generic( 'pre' => $counter++ );
		$node->set_branch_length( 1 ) if $ultrametricize;
	} 
);
$tree->ultrametricize if $ultrametricize;

# start writing the files
for my $tip ( @{ $tree->get_terminals } ) {

	# clean up the taxon name
	my $name = $tip->get_name;
	if ( $clean ) {
		$name = ucfirst $name;
		$name =~ s/_/ /g;
	}
	
	# make the hashed output dir
	my $md5   = md5_hex($name);
	my @parts = split //, $md5;
	my $dir   = join '/', $outdir, @parts[0..($hashdepth-1)];
	make_path( $dir ) if not -d $dir;
	
	# encode the path string
	my @ancestors = ( $tip, @{ $tip->get_ancestors } );
	my $encoded = join '|', map { $_->get_generic('pre') . ':' . $_->get_branch_length } @ancestors;
	
	# write the file
	open my $fh, '>', "$dir/$md5" or die $!;
	print $fh $encoded;
}
