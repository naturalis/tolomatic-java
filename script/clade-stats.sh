#!/bin/bash

PRIMERS="F04R22 NF118Sr2b"
SITES="BelleairBlvd BayfrontPark RyanCt ShellfishLab"
DATA=../data/bik
OUTPUT=../output
ROOTING=midroot
REPLICATES=5
CUT=4

STATS=${OUTPUT}/pendant_edge.tsv
LOG=${OUTPUT}/pendant_edge.log
echo '' > $STATS
echo '' > $LOG

COMMAND="perl pendant_edge.pl"
for PRIMER in $PRIMERS; do
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db
	for SITE in $SITES; do
		PRE=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		$COMMAND -d $TREE -r $REPLICATES -pre $PRE -post $POST -v -c $CUT >> $STATS 2>> $LOG
	done
done
