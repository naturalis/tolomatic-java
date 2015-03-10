#!/usr/bin/perl
use strict;
use warnings;
use SVG;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::IO 'parse';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $width, $height, $infile ) = ( 1200, 100 );
GetOptions(
	'infile=s' => \$infile,
	'verbose+' => \$verbosity,
	'width=i'  => \$width,
	'height=i' => \$height,
);

# instantiate helper objects
my $forest = parse( 
	'-format' => 'newick', 
	'-file'   => $infile, 
);
my $svg = SVG->new(
	'width'   => $width,
	'height'  => $height,
);
my $log = Bio::Phylo::Util::Logger->new(
	'-level'  => $verbosity,
	'-class'  => 'main',
);

# sort the nodes: 
# - every tree has only three tips, we want the single tip
#   on the left and the cherry on the right
# - within the cherry we want the tallest child on the right
# - also, compute the maximum distance from root to tip 
my $maxheight = 0;
$forest->visit(sub{
	my $tree = shift;
	
	# sort
	for my $node ( @{ $tree->get_internals } ) {
		my @children = @{ $node->get_children };
		if ( $node->is_root ) {
			my ($tip) = grep { $_->is_terminal } @children;
			my ($cherry) = grep { $_->get_id != $tip->get_id } @children;
			$node->insert_at_index( $tip, 0 );
			$node->insert_at_index( $cherry, 1 );
		}
		else {
			my @sorted = sort { $a->get_branch_length <=> $b->get_branch_length } @children;
			$node->insert_at_index( $sorted[$_], $_ ) for 0 .. 1;						
		}
	}
	
	# calculate height
	my $height = 0;
	for my $tip ( @{ $tree->get_terminals } ) {
		my $path = $tip->calc_path_to_root;
		$height = $path if $path > $height;
	}
	$height += $tree->get_root->get_branch_length;
	$maxheight = $height if $height > $maxheight;
});
my $scale = $width / $maxheight;

# now draw the trees
$forest->visit(sub{
	my $tree = shift;
	
	# compute coordinates
	my $h = 0;
	$tree->visit_depth_first(
		'-pre' => sub {
			my $node = shift;
			
			# calculate x coordinates
			my $x1 = 0;
			if ( my $parent = $node->get_parent ) {
				$x1 = $parent->get_generic('x2');
			}
			my $x2 = $x1 + $node->get_branch_length * $scale;
			$node->set_generic( 'x2' => $x2, 'x1' => $x1 );
		},
		'-post' => sub {
			my $node = shift;
			my @children = @{ $node->get_children };
			if ( not @children ) {
				my $y = ( $height / 2 ) * $h++;
				$node->set_generic( 'y' => $y );
			}
			else {
				my $y = sum( map { $_->get_generic('y') } @children ) / 2;
				$node->set_generic( 'y' => $y );
			}
		},
	);
	
	# draw branches
	$tree->visit(sub{
		my $node = shift;
		my %style = ( 'style' => { 'stroke' => 'rgba(0,0,0,0.1)', 'stroke-width' => 1 } );
		if ( my $parent = $node->get_parent ) {
			$svg->line(
				'x2'    => $node->get_generic('x2'),		
				'x1'    => $node->get_generic('x1'),
				'y1'    => $parent->get_generic('y'),
				'y2'    => $node->get_generic('y'),
				'class' => 'edge',
				%style,
			);		
		}
		else {
# 			$svg->line(
# 				'x2'    => $node->get_generic('x2'),		
# 				'x1'    => $node->get_generic('x1'),
# 				'y1'    => $node->get_generic('y'),
# 				'y2'    => $node->get_generic('y'),
# 				'class' => 'edge',
# 				%style,
# 			);		
		}
	});
});

print $svg->xmlify;