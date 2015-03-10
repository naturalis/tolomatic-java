#!/usr/bin/perl
use strict;
use warnings;
use feature 'say';
use Getopt::Long;
use List::Util 'sum';
use File::Temp 'tempfile';
use Bio::Phylo::IO qw'parse_tree parse';
use Bio::Phylo::Util::Logger ':levels';
use Bio::Phylo::Util::CONSTANT ':objecttypes';

# process command line arguments
my $verbosity = WARN;
my $cut = 0.03;
my $notaxonomy = 0; # maybe QIIME table has no taxonomy column
my ( $blastdb, $qiime, $intree, $site, $treatment, @primer );
GetOptions(
	'verbose+'    => \$verbosity,
	'cut=f'       => \$cut,
	'blastdb=s'   => \$blastdb,
	'qiime=s'     => \$qiime,
	'intree=s'    => \$intree,
	'site=s'      => \$site,
	'primer=s'    => \@primer,
	'treatment=s' => \$treatment,
	'notaxonomy'  => \$notaxonomy,
);

# instantiate helper objects
my $log = Bio::Phylo::Util::Logger->new(
	'-level'  => $verbosity,
	'-class'  => 'main',
);
my $tree = parse_tree(
	'-format' => 'newick',
	'-file'   => $intree,
);
my @tips = @{ $tree->get_terminals };
$log->info("tree has ".scalar(@tips)." tips");

# read qiime table
my %table = map { $_->get_name => undef } @tips;
{
	my @cols;
	my $preamble;
	open my $fh, '<', $qiime or die $!;
	$log->info("going to read QIIME table $qiime");	
	LINE: while(<$fh>) {
		chomp;
		
		# skip over the preamble, i.e. the very first line
		++$preamble && next LINE if not $preamble;
		
		# either header or data record
		my @record = split /\t/, $_;
		
		# read the header
		if ( not @cols ) {
			my $lastindex = $notaxonomy ? $#record : $#record - 1;
			for my $i ( 1 .. $lastindex ) {
				my $column = $record[$i];
				my $pregex = join '|', @primer;
				if ( $column =~ /^(?:$treatment)$site\.($pregex)$/ ) {
					push @cols, $i;
				}
			}
			$log->info("columns of interest: @cols");
		}
		
		# store the values
		else {
			my $id = $record[0];
			if ( exists $table{$id} ) {
				my @values = @record[@cols];
				my $abundance = sum(@values)/scalar(@values);
				$log->info("values for $id: @values (mean: $abundance)");				
				$table{$id} = $abundance;
			}
		}
	}
}

# compute patristic distance matrix
my %pdist;
for my $i ( 0 .. $#tips - 1 ) {
	my $name1 = $tips[$i]->get_name;
	$pdist{$name1} = {} if not $pdist{$name1};
	for my $j ( $i + 1 .. $#tips ) {
		my $name2 = $tips[$j]->get_name;
		$pdist{$name2} = {} if not $pdist{$name2};
		my $dist = $tips[$i]->calc_patristic_distance($tips[$j]);
		$pdist{$name1}->{$name2} = $pdist{$name2}->{$name1} = $dist;
	}
}

say join "\t", qw(site primer treatment id distance abundance);
# iterate over tips
for my $tip ( keys %pdist ) {
	my %d = %{ $pdist{$tip} };
	my @sorted = sort { $d{$a} <=> $d{$b} } keys %d;

	# collect all pairwise distances smaller than cutoff
	my $i = 0;
	my @seqdists;
	my $seqdist = distance($tip,$sorted[$i]);
	PAIR: while( $seqdist < $cut ) {
		$log->debug("$tip <-> $sorted[$i] = $seqdist");
		push @seqdists, $seqdist;
		$seqdist = distance($tip,$sorted[++$i]);
		last PAIR if $i == $#sorted;
	}
	$log->info("distances from $tip: @seqdists");

	# print result
	if ( @seqdists > 2 ) {
		my @result = ( 
			$site, 
			join('',@primer), 
			$treatment, 
			$tip, 
			sum(@seqdists)/scalar(@seqdists), 
			$table{$tip}
		);
		say join "\t", @result;
	}
}

sub distance {
	my @ids = @_;
	
	# make comma-separated string of local accession numbers
	my $ids = join ',', map { "'lcl|$_'" } @ids;
	$log->debug("local accessions to fetch: @ids");	
	
	# get alignment object
	my $fasta = `blastdbcmd -db $blastdb -entry $ids | muscle -quiet`;
	$log->debug("aligned fasta:\n".$fasta);
	my ($matrix) = @{ parse(
		'-type'   => 'dna',
		'-format' => 'fasta',
		'-string' => $fasta,
		'-as_project' => 1,
	)->get_items(_MATRIX_) };
	
	# compute all pairwise distances
	my @r = @{ $matrix->get_entities };	
	return $r[0]->calc_distance($r[1]);
}