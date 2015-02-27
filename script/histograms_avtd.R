treestats <- read.table("treestats-jackknife.tsv",header=TRUE,sep="\t");
title <- 'AVTD, N = 1000';

# compute min and max over all sites and primers so we can
# superimpose plots
min <- round(min(treestats$avtd)) - 1;
max <- round(max(treestats$avtd)) + 1;

# binning in 1000 steps for 100 replicates appears to result in 
# no bins having more than 50 trees in them
steps <- ( max - min ) / 1000;
breaks <- seq(from = min, to = max, by = steps );

# primers: F04R22, NF118Sr2b
# sites: BayfrontPark, BelleairBlvd, RyanCt, ShellfishLab
# NOTE: RyanCt has more taxa post than pre, so jackknifing should be inverted
# NOTE: it appears that for F04R22 the jackknifed pre trees have higher average
# pairwise distance than the post trees, but this is not so for NF118Sr2b

mySite <- 'ShellfishLab';
myPrimer <- 'NF118Sr2b';

# this subset contains the 1000 jackknifed replicates, where the pre tree is
# randomly pruned to the same size as the post tree. 
reps <- subset(treestats, site == mySite & primer == myPrimer & replicate != 0);

# this gives the true value for the post tree
true <- subset(treestats, site == mySite & primer == myPrimer & replicate == 0);

# draws the histogram for the jackknifed replicates
hist(reps$avtd, xlim=range(min,max), ylim=range(0,70), breaks = breaks, border = rgb(0,0,1,alpha=0.2), main = mySite, xlab = title);

# places an arrow for the true value on the post tree
arrows(true$avtd, 3, true$avtd, 0.1, length = 0, code = 2, col = 'red', xpd = TRUE, lwd = 2);
points(true$avtd, 0.5, pch = 25, col = 'red', bg = 'red');
