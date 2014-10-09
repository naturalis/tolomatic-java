#!/usr/bin/perl
use strict;
use warnings;
use Getopt::Long;
use Digest::MD5 'md5_hex';
use File::Path 'make_path';
use Bio::Phylo::IO 'parse_tree';
use Bio::Phylo::Util::Logger ':levels';

# process commmand line arguments
my $verbosity = WARN;
my ( $intree, $outdir );
GetOptions(
	'intree=s' => \$intree,
	'outdir=s' => \$outdir,
	'verbose+' => \$verbosity,
);

# instatiate the logger
my $log = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);
	
# read the tree... this might take a while
$log->info("going to read $intree");
my $tree = parse_tree(
	'-format' => 'newick',
	'-file'   => $intree,
	'-as_project' => 1,
);
$log->info("done reading $intree");

# relabel the nodes
$log->info("going to apply unique node labels");
my $counter = 1;
$tree->visit_depth_first( '-pre' => sub { shift->set_generic( 'pre' => $counter++ ) } );
$tree->get_root->set_branch_length(0.00);
$log->info("done applying unique node labels");

# write node paths
my $tip = 1;
$tree->visit(sub{
	my $node = shift;
	
	# only process tips
	if ( $node->is_terminal ) {
	
		# create hashed path
		my $name = $node->get_name;
		my $md5  = md5_hex($name);
		my $path = $outdir . '/' . join '/', split //, $md5, 6;
		$path =~ s|/[^/]+$||;
		make_path( $path );
		
		# create ancestor path
		my @path = ( $node, @{ $node->get_ancestors } );
		my $encoded = join '|', map { $_->get_generic('pre') . ':' . $_->get_branch_length } @path;
		
		# write to file
		open my $fh, '>', "${path}/${md5}" or die $!;
		print $fh $encoded;
		close $fh;
		
		# report progress every 100 tips
		$log->info("processed tip $tip") unless $tip++ % 100;
	}
});