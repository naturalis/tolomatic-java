library(MASS);
sim <- read.table("simulate.tsv", header = TRUE, sep = "\t");
plot(sim$tips,sim$i2);
plot(sim$death_rate,sim$gamma);

gamma <- read.table("gamma.tsv", header = TRUE, sep = "\t");
wilcox.test(gamma$gamma_pre, gamma$gamma_post, paired=TRUE);
t.test(gamma$gamma_pre, gamma$gamma_post, alternative = c("two.sided", "less", "greater"), paired=TRUE);
