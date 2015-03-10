#!/bin/bash

PRIMERS="F04R22"
#PRIMERS=NF118Sr2b
#SITES="BayfrontPark BelleairBlvd ShellfishLab"
SITES=RyanCt
DATA=../data/bik
OUTPUT=../output
ROOTING=midroot
REPLICATES=1000
STATS=${OUTPUT}/treestats-jackknife.tsv
LOG=${OUTPUT}/treestats-jackknife.log
#STATS=${OUTPUT}/treestats-jackknife-NF118Sr2b-RyanCt.tsv
#LOG=${OUTPUT}/treestats-jackknife-NF118Sr2b-RyanCt.log

COMMAND="perl treestats-file.pl -v -p fiala_stemminess"

# clear the files
#echo '' > ${STATS}
echo '' > ${LOG}

# iterate over primer sets
for PRIMER in $PRIMERS; do

	# get the tree name
	TREE=${DATA}/trees/${PRIMER}-${ROOTING}.db

	# iterate over the sites that have both pre and post treatment
	for SITE in $SITES; do
	
		# compute the tree stats for the real post tree
		${COMMAND} -i ${OUTPUT}/${PRIMER}/post/${SITE}-${ROOTING}.tsv >> ${STATS} 2>> ${LOG}
	
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
			
			# prune the tree from Megatree database, verbose, with newick output
			prune_megatree -d ${TREE} -i ${OUTSTEM}.lst -v > ${OUTSTEM}.dnd 2>> ${LOG}
			
			# compute tree statistics. we're writing to the same file multiple
			# times, therefore append ***BE AWARE OF THIS***
			${COMMAND} -i ${OUTSTEM}.dnd -f newick >> ${STATS} 2>> ${LOG}
		done  
	done
done