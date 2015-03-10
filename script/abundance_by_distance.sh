#!/bin/bash

SITES="BayfrontPark BelleairBlvd ShellfishLab RyanCt"
TREATMENTS="Post Pre";
DATA=../data/bik
OUTPUT=../output

QIIME=${DATA}/raw/otu_table_nochimeras_CSS.tsv
BLASTDB=${DATA}/raw/merged.fasta
STATS=${OUTPUT}/abundance_by_distance.tsv
LOG=${OUTPUT}/abundance_by_distance.log
CUT=0.03

COMMAND="perl abundance_by_distance.pl -v -n -b $BLASTDB -q $QIIME -c $CUT"

# clear the files
echo '' > ${STATS}
echo '' > ${LOG}

# iterate over treatments
for TREATMENT in $TREATMENTS; do
	for SITE in $SITES; do
		
		# do primer sets separately
		TREE=${OUTPUT}/${TREATMENT}F04R22${SITE}.dnd
		$COMMAND -i $TREE -t $TREATMENT -s $SITE -p F04 -p R22 >> $STATS 2>> $LOG

		TREE=${OUTPUT}/${TREATMENT}NF118Sr2b${SITE}.dnd
		$COMMAND -i $TREE -t $TREATMENT -s $SITE -p NF1 -p 18Sr2b >> $STATS 2>> $LOG		
	done
done