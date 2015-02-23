#!/bin/bash

PRIMERS="NF118Sr2b F04R22"
SITES="BayfrontPark BelleairBlvd RyanCt ShellfishLab"
ROOTING=midroot
DATA=../data/bik
SCRIPT=../script
OUT=../output/turnover
OUTFILE=${OUT}.tsv
LOG=${OUT}.log

for PRIMER in $PRIMERS; do
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db
	for SITE in $SITES; do
		PRE=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		POST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		${SCRIPT}/turnover.pl -verbose -db $TREE -pre $PRE -post $POST >>$OUTFILE 2>>$LOG
	done
done