library("ape");

# the general idea for now is that we should probably treat the two
# primer sets separately. perhaps because the different parts of the
# sequence evolve differently? the real reason is actually that we
# have two distinct trees. maybe we can compute the contrasts on the
# two separate trees and then combine them?
primer <- '...primer...'
tree <- read.tree(file="...primer tree...");
values <- read.table("abundance_by_distance.tsv",header=TRUE,sep="\t");

# everything before the event
pre_set <- subset(values, treatment == 'Pre' & primer == primer);
pre_distances <- pre_set$distance
pre_abundances <- pre_set$abundance
names(pre_distances) <- pre_set$id
names(pre_abundances) <- pre_set$id
contrasts_pre_distances <- pic(pre_distances,tree);
contrasts_pre_abundances <- pic(pre_abundances,tree);
pre_contrasts <- cbind(distance=contrasts_pre_distances, abundance=contrasts_pre_abundances, treatment=c("Pre"))

# everything after the event
post_set <- subset(values, treatment == 'Post' & primer == primer);
post_distances <- post_set$distance
post_abundances <- post_set$abundance
names(post_distances) <- post_set$id
names(post_abundances) <- post_set$id
contrasts_post_distances <- pic(post_distances,tree);
contrasts_post_abundances <- pic(post_abundances,tree);
post_contrasts <- cbind(distance=contrasts_post_distances, abundance=contrasts_post_abundances, treatment=c("Post"))
