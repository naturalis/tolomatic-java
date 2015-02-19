treestats <- read.table("treestats-reformatted.tsv",header=TRUE,sep="\t");
truedata <- read.table("treestats-truedata.tsv",header=TRUE,sep="\t");
title <- 'Stemminess sensu Fiala & Sokal (1985)';
breaks <- seq(from = 0, to = 0.35, by = 0.001);

# I guess here we want to iterate over all factors and create histograms for each
# combination of treatment (pre/post), site (7 sites) and primer set (F04R22/NF118Sr2b)

post <- subset(treestats, treatment == 'post' & site == 'ShellfishLab' & primer == 'NF118Sr2b');
pre <- subset(treestats, treatment == 'pre' & site == 'ShellfishLab' & primer == 'NF118Sr2b');

hist(pre$stemminess, xlim=range(0.05,0.35), ylim=range(0,50), breaks = breaks, border = 'green', main = 'ShellfishLab', xlab = title);
lines(density(pre$stemminess, adjust=2), col="green", lwd=2);
arrows(0.103338373863124, -3, 0.103338373863124, 0, code = 2, col = 'green', xpd = TRUE);


hist(post$stemminess, border = 'red', breaks = breaks, add = T);
lines(density(post$stemminess, adjust=2), col="red", lwd=2);
arrows(0.337518835225476, -3, 0.337518835225476, 0, code = 2, col = 'red', xpd = TRUE);

