#!/bin/bash

PRIMERS="F04R22 NF118Sr2b"
SITES="BelleairBlvd BayfrontPark RyanCt ShellfishLab"
DATA=../data/bik
OUTPUT=../output
ROOTING=midroot
CUT=6
THRESH=0.01
BLASTDB=${DATA}/raw/merged.fasta
STATS=${OUTPUT}/clade-stats-dist0.99.tsv
LOG=${OUTPUT}/clade-stats-dist0.99.log

echo '' > $STATS
echo '' > $LOG

COMMAND="perl clade-stats.pl -v -c $CUT -t $THRESH -b $BLASTDB"
for PRIMER in $PRIMERS; do	
	DB=${DATA}/trees/${PRIMER}-${ROOTING}.db
	for SITE in $SITES; do
		TREE=${DATA}/trees/${PRIMER}-${SITE}-${ROOTING}.dnd
		PRE=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		$COMMAND -i $TREE -db $DB -pre $PRE -post $POST >> $STATS 2>> $LOG
	done
done
