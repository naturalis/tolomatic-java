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
split by pre- and post spill

* `trees` contains a zipped, pre-processed tree for F04/R22 and one for NF1/18Sr2b

* `script` contains some pre-processing scripts

TODO
----

To draw a comparison in phylogenetic diversity before and after the spill we need to do
the following:

* only use the following sites: BayfrontPark, BelleairBlvd, RyanCt and ShellfishLab (the
others weren't sampled both before and after)

* for each primer set and each site, produce a pre- and post- spill tree, so:

Primer set F04/R22, before the spill
------------------------------------

- `trees/F04R22.zip - presence_lists/F04R22/pre/BayfrontPark.lst = BayfrontPark.F04R22.pre.tre`
- `trees/F04R22.zip - presence_lists/F04R22/pre/BelleairBlvd.lst = BelleairBlvd.F04R22.pre.tre`	
- `trees/F04R22.zip - presence_lists/F04R22/pre/RyanCt.lst = RyanCt.F04R22.pre.tre`	
- `trees/F04R22.zip - presence_lists/F04R22/pre/ShellfishLab.lst = ShellfishLab.F04R22.pre.tre`			

Primer set F04/R22, after the spill
-----------------------------------

- `trees/F04R22.zip - presence_lists/F04R22/post/BayfrontPark.lst = BayfrontPark.F04R22.post.tre`
- `trees/F04R22.zip - presence_lists/F04R22/post/BelleairBlvd.lst = BelleairBlvd.F04R22.post.tre`	
- `trees/F04R22.zip - presence_lists/F04R22/post/RyanCt.lst = RyanCt.F04R22.post.tre`	
- `trees/F04R22.zip - presence_lists/F04R22/post/ShellfishLab.lst = ShellfishLab.F04R22.post.tre`

Primer set NF1/18Sr2b, before the spill
---------------------------------------

- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/pre/BayfrontPark.lst = BayfrontPark.NF118Sr2b.pre.tre`
- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/pre/BelleairBlvd.lst = BelleairBlvd.NF118Sr2b.pre.tre`	
- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/pre/RyanCt.lst = RyanCt.NF118Sr2b.pre.tre`	
- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/pre/ShellfishLab.lst = ShellfishLab.NF118Sr2b.pre.tre`			

Primer set NF1/18Sr2b, after the spill
--------------------------------------

- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/post/BayfrontPark.lst = BayfrontPark.NF118Sr2b.post.tre`
- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/post/BelleairBlvd.lst = BelleairBlvd.NF118Sr2b.post.tre`	
- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/post/RyanCt.lst = RyanCt.NF118Sr2b.post.tre`	
- `trees/NF118Sr2b.zip - presence_lists/NF118Sr2b/post/ShellfishLab.lst = ShellfishLab.NF118Sr2b.post.tre`

* we will then compute some topology indices (tree balance, stemminess, overall length / 
number of taxa, etc.) to see if we can make sense out of what's happened.