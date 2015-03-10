library("ape")

# the post_tree appeared to come out unresolved, which is not
# good for ape. It might be possible to use a single tree for both,
# as long as the data record are labeled in a way that matches the
# leaf labels, as per the instructions for ape's pic()
pre_tree <- read.tree(file="abundance_distance_pre_F04R22.tre")
post_tree <- read.tree(file="abundance_distance_post_F04R22.tre")

# these should be recomputed to get more data points, basically
# for every OTU its average distance to everything that is inside
# 97% sequence similarity, and using the CSS-normalized abundance
pre_values <- read.table("abundance_distance_pre_F04R22.tsv",header=TRUE,sep="\t");
post_values <- read.table("abundance_distance_post_F04R22.tsv",header=TRUE,sep="\t");

# we should be able to do these calls with the same tree each time 
pre_con_abundance <- pic(pre_values$abundance_CSS,pre_tree)
pre_con_distance <- pic(pre_values$distance, pre_tree)
post_con_abundance <- pic(post_values$abundance_CSS,post_tree)
post_con_distance <- pic(post_values$distance,post_tree)

# these regressions show a highly *** significant negative correlation 
# between distance and abundance. However, we would like to be able to 
# do an aov() that includes an interaction term distance:treatment.
# right now we are getting p=0.102 but we're not using all the data
reg1 <- lm(pre_con_abundance~pre_con_distance)
summary(reg1)
reg2 <- lm(post_con_abundance~post_con_distance)
summary(reg2)

# plot the figure
plot(pre_con_abundance~pre_con_distance, data=pre_values, type='n')
points(pre_con_distance, pre_con_abundance, pch=20)
points(post_con_distance, post_con_abundance, pch=1)
abline(reg1, lty=1)
abline(reg2, lty=2)
legend("topright", c("Pre","Post"), lty=c(1,2), pch=c(20,1) )