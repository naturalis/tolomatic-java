#!/usr/bin/perl
use strict;
use warnings;
use feature 'say';
use File::Slurp;
use Getopt::Long;
use Data::Dumper;
use List::Util 'sum';
use File::Temp 'tempfile';
use Bio::Phylo::IO 'parse';
use Bio::Phylo::Util::Logger ':levels';
use Bio::Phylo::Util::CONSTANT ':objecttypes';

# process command line arguments
my $verbosity = WARN;
my $cut = 0.03;
my ( $qiime, $site, $primer, $blastdb );
GetOptions(
	'qiime=s'   => \$qiime,
	'site=s'    => \$site,
	'primer=s'  => \$primer,
	'blastdb=s' => \$blastdb,
	'verbose+'  => \$verbosity,
	'cut=f'     => \$cut,
);

# instantiate helper objects
my $log  = Bio::Phylo::Util::Logger->new(
	'-level' => $verbosity,
	'-class' => 'main',
);

# read qiime table
my %abundance;
{
	$log->info("going to read QIIME table $qiime");	
	my %index;
	my $regex = '(Pre|Post)' . $site . '\.' . $primer;
	open my $fh, '<', $qiime or die $!;
	LINE: while(<$fh>) {
		next if /^# QIIME/;
		my @record = split /\t/, $_;
		
		# index which columns to read
		if ( not %index ) {
			$log->info("going to select columns from QIIME table");
			for my $i ( 1 .. $#record - 1 ) {
				if ( $record[$i] =~ /$regex/ ) {
					my $treatment = $1;
					$index{$i} = $treatment;
				}
			}
			$log->info("columns to store:\n".Dumper(\%index));
			next LINE;
		}
		
		# store id and higher taxon
		my $id = $record[0];
		my @taxonomy = split /;/, $record[-1];
		next LINE if $taxonomy[-2]; # only want blah;blah;;Genus
		next LINE if @taxonomy < 4; # don't want shallow pseudo-taxonomy
		my $taxon = $taxonomy[-4];  # should be family name
		
		# instantiate data structure
		$abundance{$taxon} = { 'Pre' => {}, 'Post' => {} } if not $abundance{$taxon};
		
		# store abundances		
		for my $i ( keys %index ) {
			my $treatment = $index{$i};
			if ( $record[$i] != 0 ) {				
				$abundance{$taxon}->{$treatment}->{$id} = $record[$i];
			}
		}
	}
}
$log->debug("abundances:\n".Dumper(\%abundance));

# calculate the distances and abundances per taxon, per treatment
say join "\t", qw(site	primer treatment taxon	id	distance	abundance);
TAXON: for my $taxon ( keys %abundance ) {

	# only want taxa both before and after
	my %pre_a  = %{ $abundance{$taxon}->{'Pre'} };
	my %post_a = %{ $abundance{$taxon}->{'Post'} };
	if ( ( 2 < scalar keys %post_a ) and ( 2 < scalar keys %pre_a ) and ( 100 > scalar keys %pre_a ) ) {
		for my $treatment ( qw(Pre Post) ) {
	
			# fetch abundances
			my %a = %{ $abundance{$taxon}->{$treatment} };
			my %d;
			eval { %d = distances( $taxon, $treatment ) };
		
			# skip over errors
			if ( $@ ) {
				$log->info("no distances for $treatment tips for $taxon: $@");
				next TAXON;
			}
		
			# print output
			for my $id ( keys %d ) {
				say join "\t", $site, $primer, $treatment, $taxon, $id, $d{$id}, $a{$id};
			}
		}
	}
	else {
		$log->debug("skipping $taxon");
	}
}

sub distances {
	my ( $genus, $treatment ) = @_;
	my @ids = keys %{ $abundance{$genus}->{$treatment} };
	
	# make comma-separated string of local accession numbers
	my $ids = join ',', map { "'lcl|$_'" } @ids;
	$log->info("$genus, $treatment: ".scalar(@ids)." local accessions to fetch");	
	$log->debug("IDs: $ids");
	
	# get alignment object
	my ( $fh, $filename ) = tempfile();	
	system("blastdbcmd -db $blastdb -entry $ids > $filename") == 0 or die $?;
	my $fasta = `muscle -quiet -in $filename`;
	$log->debug("aligned fasta:\n".$fasta);
	unlink $filename;
	my ($matrix) = @{ parse(
		'-type'   => 'dna',
		'-format' => 'fasta',
		'-string' => $fasta,
		'-as_project' => 1,
	)->get_items(_MATRIX_) };
	
	# compute all pairwise distances
	my @rows = @{ $matrix->get_entities };		
	my %dist;		
	for my $i ( 0 .. $#rows - 1 ) {
	
		# instantiate distance matrix for outer taxon
		my $namei = $rows[$i]->get_name;
		$namei =~ s/lcl\|//;
		$dist{$namei} = [] if not $dist{$namei};	
			
		for my $j ( $i + 1 .. $#rows ) {	
		
			# instantiate distance matrix for inner taxon	
			my $namej = $rows[$j]->get_name;
			$namej =~ s/lcl\|//;
			$dist{$namej} = [] if not $dist{$namej};
			
			# compute distance
			my $d = $rows[$i]->calc_distance($rows[$j]);
			
			# we are only interested if this is an intraspecific distance
			if ( $d < $cut ) {
				push @{ $dist{$namei} }, $d;
				push @{ $dist{$namej} }, $d;
			}
			else {
				$log->info("$namei <=> $namej = $d, maybe interspecific (>$cut)"); 
			}
		}
	}
	
	# compute averages
	my %result;
	for my $key ( keys %dist ) {
		my @d = @{ $dist{$key} };
		if ( scalar(@d) ) {
			$result{$key} = sum(@d)/scalar(@d);
		}
	}

	return %result;	
}


