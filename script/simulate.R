sim <- read.table("simulate.tsv", header = TRUE, sep = "\t");

# plot stemminess index sensu Fiala & Sokahl (1985) on tip count and 
# death rate. this is intended to demonstrate that this stemminess 
# measure is independent of tree size, but correlated with death rate
plot(sim$tips,sim$fiala_stemminess);
plot(sim$death_rate,sim$fiala_stemminess);

# not significant, i.e. independent
lm.r <- lm(sim$tips ~ sim$fiala_stemminess);
summary(lm.r);

# significant, i.e. correlated
lm.r <- lm(sim$death_rate ~ sim$fiala_stemminess);
summary(lm.r);
