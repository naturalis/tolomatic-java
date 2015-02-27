treestats <- read.table("treestats-jackknife.tsv",header=TRUE,sep="\t");
title <- 'Colless\' Imbalance, N = 1000';

# Colless' Imbalance always ranges between 0 (perfectly balanced) and 1 (ladder)
min <- 0;
max <- 1;

# binning in 1000 steps for 100 replicates appears to result in 
# no bins having more than 50 trees in them
steps <- ( max - min ) / 1000;
breaks <- seq(from = min, to = max, by = steps );

# primers: F04R22, NF118Sr2b
# sites: BayfrontPark, BelleairBlvd, RyanCt, ShellfishLab
# NOTE: RyanCt has more taxa post than pre, so jackknifing should be inverted
# NOTE: For F04R22 the post trees are significantly more imbalanced than the 
# jackknifed pre trees, but this is not so for NF118Sr2b

mySite <- 'ShellfishLab';
myPrimer <- 'NF118Sr2b';

# this subset contains the 1000 jackknifed replicates, where the pre tree is
# randomly pruned to the same size as the post tree. 
reps <- subset(treestats, site == mySite & primer == myPrimer & replicate != 0);

# this gives the true value for the post tree
true <- subset(treestats, site == mySite & primer == myPrimer & replicate == 0);

# draws the histogram for the jackknifed replicates
hist(reps$imbalance, xlim=range(min,max), ylim=range(0,50), breaks = breaks, border = rgb(0,0,1,alpha=0.2), main = mySite, xlab = title);
lines(density(reps$imbalance, adjust=2), col="blue", lwd=2);

# places an arrow for the true value on the post tree
arrows(true$imbalance, 3, true$imbalance, 0.1, length = 0, code = 2, col = 'red', xpd = TRUE, lwd = 2);
points(true$imbalance, 0.5, pch = 25, col = 'red', bg = 'red');
