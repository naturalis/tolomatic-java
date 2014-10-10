Tree pruning to assess microbial phylogenetic diversity
=======================================================

This directory and subfolders contains data from [Dramatic Shifts in Benthic Microbial 
Eukaryote Communities following the Deepwater Horizon Oil Spill](http://dx.doi.org/10.1371/journal.pone.0038550)
by Bik et al., 2012.

The data are organized as follows:

* `raw` contains the data as received from <holly.bik@gmail.com> in a thread started on 
2014-08-25, this consists of two trees based respectively on the F04/R22 and the 
NF1/18Sr2b primer sets, and on a QIIME file that reports pooled read counts for each OTU 
for a number of sites in the Gulf of Mexico.

* `presence_lists` separates out the pooled QIIME file into presence lists for the 
respective primer sets (i.e. the IDs here correspond with their respective trees), then
split by pre- and post spill.

* `trees` contains a zipped, pre-processed tree for F04/R22 and one for NF1/18Sr2b

* `script` contains some pre-processing scripts
