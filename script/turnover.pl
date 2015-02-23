#!/usr/bin/perl
use strict;
use warnings;
use Megatree;
use File::Slurp;
use Getopt::Long;
use List::Util 'sum';
use Data::Dumper;
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my ( $db, $pre, $post );
my $verbosity = WARN;
GetOptions(
	'db=s'     => \$db,
	'pre=s'    => \$pre,
	'post=s'   => \$post,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $log  = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);
$log->info("going to connect to tree db $db");
my $tree = Megatree->connect($db);
$log->info("connected to TREE $tree");

# combined lookup table, survivors, deceased and immigrants
my ( %lookup, @survivors, @immigrants, @deceased );

# read pre list
$log->info("going to read PRE_FILE $pre");
my %pre;
for my $id ( read_file( $pre,  'chomp' => 1 ) ) {
	if ( my $node = $tree->_rs->single( { 'name' => $id } ) ) {
		$pre{$id} = $node;
		$lookup{$id} = $node;
	}
	else {
		$log->warn("'$id' not in tree!");
	}
}
$log->info("read ".scalar(keys(%pre))." (PRE) leaves from $pre");

# read post list
$log->info("going to read POST_FILE $post");
my %post;
for my $id ( read_file( $post, 'chomp' => 1 ) ) {
	if ( my $node = $tree->_rs->single( { 'name' => $id } ) ) {
		$post{$id} = $node;
		$lookup{$id} ? push @survivors, $id : push @immigrants, $id;
		$lookup{$id} = $node;
	}
	else {
		$log->warn("'$id' not in tree!");
	}	
}
$log->info("read ".scalar(keys(%post))." (POST) leaves from $post");

# compute deceased
for my $id ( keys %pre ) {
	push @deceased, $id if not $post{$id};
}

# compute all distances. we store this as a symmetrical table (both diagonals) for
# easier lookup, though we of course compute the distance only once for each permutation.
my %node_dist;
$log->info("going to compute node distances");
{
	no warnings 'recursion';
	recurse($tree->get_root);
}
print Dumper(\%node_dist);
$log->info("done computing node distances");
sub recurse {
	my $node = shift;
	my $id = $node->get_id;
	for my $child ( @{ $node->get_children } ) {
		recurse($child);
		my $cid = $child->get_id;		
		if ( $child->is_terminal ) {
			$node_dist{$cid} = { $id => $child->get_branch_length };
		}
		else {
			for my $h ( values %node_dist ) {
				if ( $h->{$cid} ) {
					$h->{$id} = $h->{$cid} + $child->get_branch_length;
				}
			}
		}
	}
}
my %dist = map { $_ => {} } keys %lookup;
my @all = keys %lookup;
for my $i ( 0 .. $#all - 1 ) {
	my @anc = sort { $b <=> $a } keys %{ $node_dist{$all[$i]} };
	for my $j ( $i + 1 .. $#all ) {
		$log->info("$i <-> $j") if not $i % 100 and not $j % 100;
		ANC: for my $anc ( @anc ) {
			if ( $node_dist{$all[$j]}->{$anc} ) {
				my $dist = $node_dist{$all[$j]}->{$anc} + $node_dist{$all[$i]}->{$anc};
				$dist{$all[$i]}->{$all[$j]} = $dist;
				$dist{$all[$j]}->{$all[$i]} = $dist;
				last ANC;
			}
		}
	}
}

# tally S<->I and S<->D distances
my ( @dist_si, @dist_sd );
for my $s ( @survivors ) {
	for my $i ( @immigrants ) {
		push @dist_si, $dist{$s}->{$i};
	}
	for my $d ( @deceased ) {
		push @dist_sd, $dist{$s}->{$d};
	}
}

# tally PRE<->I and PRE<->D distances
my ( @dist_pi, @dist_pd );
for my $p ( keys %pre ) {
	for my $i ( @immigrants ) {
		push @dist_si, $dist{$p}->{$i};
	}
	for my $d ( @deceased ) {
		push @dist_sd, $dist{$p}->{$d};
	}	
}

# tally PRE<->PRE distances
my @dist_pre;
my @pre = keys %pre;
for my $i ( 0 .. $#pre - 1 ) {
	for my $j ( $i + 1 .. $#pre ) {
		push @dist_pre, $dist{$pre[$i]}->{$pre[$j]};
	}
}

# tally POST<->POST distances
my @dist_post;
my @post = keys %post;
for my $i ( 0 .. $#post - 1 ) {
	for my $j ( $i + 1 .. $#post ) {
		push @dist_post, $dist{$post[$i]}->{$post[$j]};
	}
}

# print output
print join("\t",(
	'TREE',       # input tree database
	'PRE_FILE',   # list of taxa pre 
	'POST_FILE',  # list of taxa post
	'PRE',        # number of taxa pre
	'POST',       # number of taxa post
	'I',          # number of immigrants
	'S',          # number of survivors
	'D',          # number of deceased
	'S<->I',      # mean distance between survivors and immigrants
	'S<->D',      # mean distance between survivors and deceased
	'PRE<->I',    # mean distance between pre and immigrants
	'PRE<->D',    # mean distance between pre and deceased
	'PRE<->PRE',  # mean distance within pre
	'POST<->POST',# mean distance within post
)),"\n";
print 
	$db, "\t",
	$pre, "\t",
	$post, "\t",
	scalar(@pre), "\t",
	scalar(@post), "\t",
	scalar(@immigrants), "\t",
	scalar(@survivors), "\t",
	scalar(@deceased), "\t",
	scalar(@dist_si) ? (sum(@dist_si)/scalar(@dist_si)) : 'N/A', "\t",
	scalar(@dist_sd) ? (sum(@dist_sd)/scalar(@dist_sd)) : 'N/A', "\t",
	scalar(@dist_pi) ? (sum(@dist_pi)/scalar(@dist_pi)) : 'N/A', "\t",
	scalar(@dist_pd) ? (sum(@dist_pd)/scalar(@dist_pd)) : 'N/A', "\t",
	(sum(@dist_pre)/scalar(@dist_pre)), "\t",
	(sum(@dist_post)/scalar(@dist_post)), "\n";
