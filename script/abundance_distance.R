values <- read.table("abundance_distance.tsv",header=TRUE,sep="\t");

# http://r-eco-evo.blogspot.nl/2011/08/comparing-two-regression-slopes-by.html
set <- subset(values, primer == 'NF118Sr2b' & distance < 0.03);

# ANCOVA: 
# - abundance is the dependent variable, the hypothesis being that the more
#   you differ from your conspecifics, the less abundant you're going to be
# - distance is the covariate. the higher the average distance to your 
#   conspecifics, the lower your abundance will be
# - treatment is the factor: we expect the effect to be stronger in 'Post',
#   because we hypothesize that there is a more extreme environmental filter
mod1 <- aov(abundance_CSS~distance*treatment, data=set)
summary(mod1)
# the results show a significant effect of both distance and treatment, but
# no interaction. this is true for both primer sets. apparently, the slope 
# if the regression lines Pre and Post is similar.

# now, let's look at a simpler model without the interaction terms
mod2 <- aov(abundance_CSS~distance+treatment, data=set)
summary(mod2)
# the second model shows that treatment has a significant effect on the 
# dependent variable, which in this case can be interpreted as a significant
# difference in the intercepts between the regression lines of Pre and Post

# now let's assess whether removing the interaction significantly affects the
# fit of the model:
anova(mod1,mod2)
# it doesn't. hence, we prefer the simpler model.

# At this point we are going to fit linear regressions separately for
# Pre and Post. First we make to subsets.
pre <- subset(set, treatment == 'Pre')
post <- subset(set, treatment == 'Post')

# Now we do the regressions
reg1 <- lm(abundance_CSS~distance, data=pre)
summary(reg1)
reg2 <- lm(abundance_CSS~distance, data=post)
summary(reg2)
# the regression lines indicate that post have a higher intercept than pre

plot(abundance_CSS~distance, data=set, type='n')
points(pre$distance, pre$abundance_CSS, pch=20)
points(post$distance, post$abundance_CSS, pch=1)
abline(reg1, lty=1)
abline(reg2, lty=2)
legend("topright", c("Pre","Post"), lty=c(1,2), pch=c(20,1) )

