#!/bin/bash

SITES="BayfrontPark BelleairBlvd RyanCt ShellfishLab"
PRIMERS="F04R22 NF118Sr2b"
DATA=../data/bik/raw
BLASTDB=${DATA}/merged.fasta
QIIME=${DATA}/otu_table_nochimeras_normalized.txt
OUTPUT=../output/
OUTFILE=${OUTPUT}/abundance_distance.tsv
LOGFILE=${OUTPUT}/abundance_distance.log
COMMAND="perl abundance_distance.pl -v"

echo '' > $OUTFILE
echo '' > $LOGFILE

for PRIMER in $PRIMERS; do
	for SITE in $SITES; do
		$COMMAND -q $QIIME -s $SITE -p $PRIMER -b $BLASTDB >>$OUTFILE 2>>$LOGFILE
	done
done
