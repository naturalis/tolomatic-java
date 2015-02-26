#!/bin/bash

PRIMERS="NF118Sr2b F04R22"
#RyanCt
SITES="BayfrontPark BelleairBlvd ShellfishLab"
ROOTING=midroot
DATA=../data/bik
COMMAND="../script/backbone_distances.pl -v"
OUTPUT=../output
OUT=${OUTPUT}/backbone_distances
OUTFILE=${OUT}.tsv
LOG=${OUT}.log

echo '' > $OUTFILE
echo '' > $LOG

for PRIMER in $PRIMERS; do
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db
	for SITE in $SITES; do
		MRCAS=${OUTPUT}/mrcas/${PRIMER}/${SITE}.lst
		
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		$COMMAND -db $TREE -i $POST -m $MRCAS >>$OUTFILE 2>>$LOG
		
		PRE=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		$COMMAND -db $TREE -i $PRE -m $MRCAS >>$OUTFILE 2>>$LOG
		
 		for LIST in `ls ${OUTPUT}/jackknife/${PRIMER}/${SITE}/*.lst`; do
 			$COMMAND -db $TREE -i $LIST -m $MRCAS >>$OUTFILE 2>>$LOG
 		done
	done
done