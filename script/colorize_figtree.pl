#!/usr/bin/perl
use strict;
use warnings;
use File::Slurp;
use Getopt::Long;
use Bio::Phylo::IO 'parse_tree';

# process command line arguments
my ( $intree, $pre, $post );
GetOptions(
	'intree=s' => \$intree,
	'pre=s'    => \$pre,
	'post=s'   => \$post,
);

# make color codes
my %color;
$color{$_} = '[&!color=#0000ff]' for read_file( $pre,  'chomp' => 1 );
$color{$_} = '[&!color=#ff0000]' for read_file( $post, 'chomp' => 1 );
my $mix = '[&!color=#800080]';

# read tree
my $tree = parse_tree(
	'-format' => 'newick',
	'-file'   => $intree,
	'-as_project' => 1,
);

# color the nodes
$tree->visit_depth_first(
	'-post' => sub {
		my $node = shift;
		if ( $node->is_terminal ) {
			my $name = $node->get_name;
			$node->set_generic( 'color' => $color{$name} );
			$node->set_name( $name . $color{$name} );
		}
		else {
			my %c = map { $_->get_generic('color') => 1 } @{ $node->get_children };
			my @c = keys %c;
			my $c = @c > 1 ? $mix : $c[0];
			$node->set_generic( 'color' => $c );
			$node->set_name( $c );
		}
	}
);

# produce nexus
my $ntax = scalar keys %color;
my $labels = join "\n", keys %color;
my $newick = $tree->to_newick( 'nodelabels' => 1 );

print <<"NEXUS";
#NEXUS
begin taxa;
	dimensions ntax=${ntax};
	taxlabels
$labels	
;
end;

begin trees;
	tree tree_1 = [&R] $newick
end;
NEXUS
