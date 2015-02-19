#!/bin/bash

PRIMERS="F04R22 NF118Sr2b"
TREATMENTS="pre post"
DATA=../data/bik/raw
REPLICATES=100
STATS=treestats.tsv

# iterate over primer sets
for PRIMER in $PRIMERS; do

	# iterate over pre- and post treatment within primer set
	for TREATMENT in $TREATMENTS; do
	
		# iterate over the sites
		LISTS=`ls ${PRIMER}/${TREATMENT}/*.lst`
		for LIST in $LISTS; do
		
			# calculate number of lines to sample
			LENGTH=`wc -l ${LIST}`
			
			# create directory for replicates
			STEM=`echo $LIST | sed -e 's/.lst//'`
			mkdir ${STEM}
			
			# create replicates
			for (( i=1; i <= REPLICATES; i++ )); do
			
				# make shorter names
				OUTSTEM=${STEM}/${i}
				INSTEM=${DATA}/${PRIMER}
				
				# sample the taxon list
				sampler -s $LENGTH -i ${INSTEM}.txt > ${OUTSTEM}.lst
				
				# prune the tree from Megatree database, verbose, with tabular output
				prune_megatree -d ${INSTEM}.db -i ${OUTSTEM}.lst -v -t > ${OUTSTEM}.tsv
				
				# compute tree statistics. we're writing to the same file multiple
				# times, therefore append ***BE AWARE OF THIS***
				perl treestats-file.pl -v -i ${OUTSTEM}.tsv >> ${STATS}
			done  
		done
	done
done