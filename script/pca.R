library(devtools);
install_github("ggbiplot", "vqv");
library(ggbiplot);

stats <- read.table("clade-stats-dist0.99-filtered1.txt",header=TRUE,sep="\t");
stats.subset <- subset(stats, class != 'mixed' & dist.count == 3);
stats.values <- stats.subset[,7:14];
stats.classes <- stats.subset[,3];
stats.pca <- prcomp(stats.values, center = TRUE, scale. = TRUE);

g <- ggbiplot(stats.pca, obs.scale = 1, var.scale = 1, groups = stats.classes, ellipse = TRUE, circle = TRUE);
g <- g + scale_color_discrete(name = '');
g <- g + theme(legend.direction = 'horizontal', legend.position = 'top');
print(g);

