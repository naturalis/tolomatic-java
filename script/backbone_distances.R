distances <- read.table("backbone_distances.tsv",header=TRUE,sep="\t");

title <- 'Distance to backbone';
breaks <- seq(from = 0, to = 0.8, by = 0.001);

# primers: F04R22, NF118Sr2b
# sites: BayfrontPark, BelleairBlvd, RyanCt, ShellfishLab
mySite <- 'ShellfishLab';
myPrimer <- 'NF118Sr2b';

reps <- subset(distances, site == mySite & primer == myPrimer & replicate != 0 & post == 1);

hist(reps$distance, xlim=range(0,0.8), ylim=range(0,45), breaks = breaks, border = rgb(0,0,1,alpha=0.2), col=rgb(0,0,1,alpha=0.2), main = mySite, xlab = title);
lines(density(reps$distance, adjust=2), col=rgb(0,0,1,alpha=0.7), lwd=2);

post <- subset(distances, site == mySite & primer == myPrimer & replicate == 0 & post == 1);
arrows(post$distance, -1, post$distance, 0, code = 2, col = rgb(1,0,0), xpd = TRUE);

pre <- subset(distances, site == mySite & primer == myPrimer & replicate == 0 & post == 0);
arrows(pre$distance, -1, pre$distance, 0, code = 2, col = rgb(0,0,1), xpd = TRUE);
