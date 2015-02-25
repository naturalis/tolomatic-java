#!/bin/bash

PRIMERS="NF118Sr2b F04R22"
SITES="BayfrontPark BelleairBlvd RyanCt ShellfishLab"
ROOTING=midroot
DATA=../data/bik
SCRIPT=../script
OUT=../output/mrcas
LOG=${OUT}.log

echo '' > $LOG

for PRIMER in $PRIMERS; do
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db
	mkdir -p ${OUT}/${PRIMER}
	for SITE in $SITES; do
		OUTFILE=${OUT}/${PRIMER}/${SITE}.lst
		PRE=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		${SCRIPT}/mrcas.pl -verbose -db $TREE -pre $PRE -post $POST >$OUTFILE 2>>$LOG
	done
done