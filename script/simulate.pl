#!/usr/bin/perl
use strict;
use warnings;
use Math::Round;
use Getopt::Long;
use List::Util 'sum';
use Data::Dumper;
use Bio::Phylo::EvolutionaryModels 'sample';

# process command line arguments
my ( $tips, $death, $size );
my @properties = qw(i2 rohlf_stemminess imbalance gamma fiala_stemminess avtd fp es pe shapley);
GetOptions(
	'tips=s'  => \$tips, # number of tips, either a single number or min,max
	'death=s' => \$death, # death rate, either a single fraction (<1) or min,max
	'size=i'  => \$size, # sample size, e.g. 100
	'prop=s'  => \@properties, # tree stats to calculate, has defaults
);

# post-process command line arguments
my ( $death_min, $death_max ) = split /,/, $death;
my ( $tips_min,  $tips_max )  = split /,/, $tips;
my @methods = map { "calc_$_" } @properties;

# iterate over samples
$|++;
print "tips\tdeath_rate\t", join("\t",@properties), "\tnewick\n";
for my $i ( 1 .. $size ) {

	# either sample within a range (if max provided) or use the min value
	my $tree_size  = $tips_max ? round(rand($tips_max-$tips_min)+$tips_min) : $tips_min;
	my $death_rate = $death_max ? rand($death_max-$death_min) + $death_min : $death_min;
	
	# sample 1 tree
	my ( $sample, $stats ) = sample(
		'sample_size' => 1,
		'tree_size'   => $tree_size,
		'algorithm'   => 'constant_rate_bd',
		'model_options'  => {
			'birth_rate' => 1.0,
			'death_rate' => $death_rate,
		}
	);	
	my $t = $sample->[0];
	
	# scale so that sum of branch lengths = 1
# 	my $scale = 1 / sum( map { $_->get_branch_length || 0 } @{ $t->get_entities } );
# 	$t->visit(sub{
# 		my $n = shift;
# 		my $l = ( $n->get_branch_length || 0 ) * $scale;
# 		$n->set_branch_length($l);
# 	});
	$t->scale(1);
	
	# prepare results	
	my $ntax = $t->calc_number_of_terminals;
	my @results = ( $ntax, $death_rate );
	for my $m ( @methods ) {
		my $val = $t->$m;
		if ( ref $val ) {
			if ( UNIVERSAL::isa( $val, 'Math::BigFloat' ) ) {
				$val = $val->bstr();
			}
			if ( UNIVERSAL::isa( $val, 'HASH' ) ) {
				$val = sum(values(%$val))/$ntax;
			}
		}
		push @results, $val;
	}
	push @results, $t->to_newick;
	
	# print results
	print join("\t", @results), "\n";	
}