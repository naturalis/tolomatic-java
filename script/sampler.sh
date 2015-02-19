#!/bin/bash

PRIMERS="F04R22 NF118Sr2b"
SITES="BayfrontPark BelleairBlvd RyanCt ShellfishLab"
DATA=../data/bik
OUTPUT=../output
ROOTING=midroot
REPLICATES=100
STATS=${OUTPUT}/treestats-jackknife.tsv

# iterate over primer sets
for PRIMER in $PRIMERS; do

	# get the tree name
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db

	# iterate over the sites that have both pre and post treatment
	for SITE in $SITES; do
	
		# get the site's pre- and post treatment's file name
		PRE_LIST=${DATA}/presence_lists/${PRIMER}/pre/${SITE}.lst
		POST_LIST=${DATA}/presence_lists/${PRIMER}/post/${SITE}.lst
		
		# calculate number of lines to sample
		LENGTH=`wc -l ${POST_LIST}`
		
		# create directory for replicates
		OUTDIR=${OUTPUT}/jackknife/${PRIMER}/${SITE}
		mkdir -p ${OUTDIR}
		
		# create replicates
		for (( i=1; i <= REPLICATES; i++ )); do
		
			# make a shorter name stem
			OUTSTEM=${OUTDIR}/${i}
			
			# sample the taxon list
			sampler -s ${LENGTH} -i ${PRE_LIST} > ${OUTSTEM}.lst
			
			# prune the tree from Megatree database, verbose, with tabular output
			prune_megatree -d ${TREE} -i ${OUTSTEM}.lst -v -t > ${OUTSTEM}.tsv
			
			# compute tree statistics. we're writing to the same file multiple
			# times, therefore append ***BE AWARE OF THIS***
			perl treestats-file.pl -v -i ${OUTSTEM}.tsv >> ${STATS}
		done  
	done
done