#!/usr/bin/perl
use strict;
use warnings;
use Megatree;
use File::Slurp;
use Data::Dumper;
use Getopt::Long;
use List::Util 'sum';
use Bio::Phylo::Util::Logger ':levels';

# process command line arguments
my $verbosity = WARN;
my ( $db, $pre, $post );
GetOptions(
	'db=s'     => \$db,
	'pre=s'    => \$pre,
	'post=s'   => \$post,
	'verbose+' => \$verbosity,
);

# instantiate helper objects
my $tree = Megatree->connect($db);
my $log = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);

# compute distances from all tips to each of their ancestors.
# the rationale is that we can then compute the patristic
# distance between two tips by searching for their MRCA
# (being the shared ancestor with the highest ID) and add
# up the distances to the MRCA for the two nodes
my %node_dist;
$log->info("going to compute distances in TREE $db ($tree)");
recurse($tree->get_root);
sub recurse {
	my $node = shift;
	
	# avoid warnings, log progress
	no warnings 'recursion';
	my $count = scalar keys %node_dist;
	$log->debug($count) unless $count % 1000;

	# depth first, working from tips to root
	recurse($_) for @{ $node->get_children };

	# we always add the distance to the focal node's parent, 
	# so never process the root itself
	if ( my $parent = $node->get_parent ) {
		my $pid = $parent->get_id;
		my $nid = $node->get_id;
		
		# instantiate a new hash for this tip, populate it with
		# the path to the parent, which is the length of the 
		# tip's branch
		if ( $node->is_terminal ) {
			$node_dist{$nid} = { $pid => $node->get_branch_length };
		}
		
		# focal node is internal, need to extend existing paths,
		# not create a new one
		else {
		
			# iterate over all the paths that contain the focal node
			for my $path ( grep { defined $_->{$nid} } values %node_dist ) {
			
				# add an entry to the focal node's parent, assign that
				# the distance to the focal node + the node's length
				$path->{$pid} = ( $path->{$nid} || 0 ) + ( $node->get_branch_length || 0 );
			}
		}
	}
}

# read the taxon lists
my ( @pre, @post, %lookup );
turnover();
sub turnover {

	# instantiate local variables, will take keys afterwards
	my ( %pre, %post );
	
	# read the list of leaves before the event
	$log->info("reading PRE file $pre");
	PRE: for my $id ( read_file( $pre, 'chomp' => 1 ) ) {
		if ( my $node = $tree->_rs->search({'name'=>$id})->single ) {
			$pre{$id} = $node;
			next PRE;
		}
		$log->warn("$id is not in tree");
	}
	
	# read the list of leaves after the event
	$log->info("reading POST file $post");
	POST: for my $id ( read_file( $post, 'chomp' => 1 ) ) {
		if ( my $node = $tree->_rs->search({'name'=>$id})->single ) {
			$post{$id} = $node;
			next POST;
		}
		$log->warn("$id is not in tree");
	}
	
	# get the IDs
	@pre    = keys %pre;
	@post   = keys %post;
	%lookup = ( %pre, %post );
}

# calculates MRCA
sub mrca {
	my ( $this_name, $that_name ) = @_;
	
	# the distances hash uses the primary keys from 
	# the database, hence need to do a lookup
	my $this = $lookup{$this_name}->get_id;
	my $that = $lookup{$that_name}->get_id;
	
	# primary keys are applied in pre-order, hence sorting
	# in descending order starts with the most recent
	for my $anc ( sort { $b <=> $a } keys %{ $node_dist{$this} } ) {
	
		# found the MRCA
		if ( defined $node_dist{$that}->{$anc} ) {
			return $anc;
		}
	}
	$log->error("$this and $that not in same tree?");
}

# calculate and print all MRCAs
my %mrca;
for my $post ( @post ) {
	for my $pre ( @pre ) {
		my $this_mrca = mrca( $post => $pre );
		$mrca{$this_mrca}++;
	}
}
print join "\n", keys %mrca;