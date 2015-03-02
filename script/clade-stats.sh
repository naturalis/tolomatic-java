#!/bin/bash

PRIMERS="F04R22 NF118Sr2b"
SITES="BelleairBlvd BayfrontPark RyanCt ShellfishLab"
DATA=../data/bik
OUTPUT=../output
ROOTING=midroot
CUT=6

STATS=${OUTPUT}/clade-stats.tsv
LOG=${OUTPUT}/clade-stats.log
echo '' > $STATS
echo '' > $LOG

COMMAND="perl clade-stats.pl"
for PRIMER in $PRIMERS; do	
	DB=${DATA}/trees/${PRIMER}-${ROOTING}.db
	for SITE in $SITES; do
		TREE=${DATA}/trees/${PRIMER}-${SITE}-${ROOTING}.dnd
		PRE=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		$COMMAND -i $TREE -db $DB -pre $PRE -post $POST -v -c $CUT >> $STATS 2>> $LOG
	done
done
