treestats <- read.table("treestats-jackknife-NF118Sr2b-RyanCt.tsv",header=TRUE,sep="\t");
truedata <- read.table("treestats-truedata.rooting.tsv",header=TRUE,sep="\t");
title <- 'Stemminess sensu Fiala & Sokal (1985)';
breaks <- seq(from = 0, to = 0.35, by = 0.001);

# primers: F04R22, NF118Sr2b
# sites: BayfrontPark, BelleairBlvd, RyanCt, ShellfishLab
# NOTE: RyanCt has more taxa post than pre, so jackknifing should be inverted

reps <- subset(treestats, site == 'RyanCt' & primer == 'NF118Sr2b');

hist(reps$fiala_stemminess, xlim=range(0.05,0.35), ylim=range(0,50), breaks = breaks, border = 'red', main = 'RyanCt', xlab = title);
lines(density(reps$fiala_stemminess, adjust=2), col="red", lwd=2);

# midroot (pre)
arrows(0.087856522817098, -3, 0.087856522817098, 0, code = 2, col = 'green', xpd = TRUE);

# outroot (pre)
arrows(0.0877127409976452, -3, 0.0877127409976452, 0, code = 2, col = 'darkgreen', xpd = TRUE);

# midroot (post)
arrows(0.107314433566728, -3, 0.107314433566728, 0, code = 2, col = 'red', xpd = TRUE);

# outroot (post)
arrows(0.107262593036686, -3, 0.107262593036686, 0, code = 2, col = 'darkred', xpd = TRUE);
