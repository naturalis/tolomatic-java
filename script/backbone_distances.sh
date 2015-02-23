#!/bin/bash

PRIMERS="NF118Sr2b F04R22"
SITES="BayfrontPark BelleairBlvd RyanCt ShellfishLab"
ROOTING=midroot
DATA=../data/bik
SCRIPT=../script
OUTPUT=../output
OUT=${OUTPUT}/backbone_distances
OUTFILE=${OUT}.tsv
LOG=${OUT}.log

for PRIMER in $PRIMERS; do
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db
	for SITE in $SITES; do
		MRCAS=${OUTPUT}/mrcas/${PRIMER}/${SITE}.lst
		
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		${SCRIPT}/backbone_distances.pl -v -db $TREE -i $POST -m $MRCAS >>$OUTFILE 2>>$LOG
		
		LISTS=`ls ${OUTPUT}/jackknife/${PRIMER}/${SITE}/*.lst`
		for LIST in $LISTS; do
			${SCRIPT}/backbone_distances.pl -v -db $TREE -i $LIST -m $MRCAS >>$OUTFILE 2>>$LOG
		done
	done
done