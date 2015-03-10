library("ape")
#############################################################################
pre_tree <- read.tree(file="abundance_distance_pre_F04R22.tre")
pre_values <- read.table("abundance_distance_pre_F04R22.tsv",header=TRUE,sep="\t");
pre_con_abundance <- pic(pre_values$abundance_CSS,pre_tree)
pre_con_distance <- pic(pre_values$distance, pre_tree)

post_tree <- read.tree(file="abundance_distance_post_F04R22.tre")
post_values <- read.table("abundance_distance_post_F04R22.tsv",header=TRUE,sep="\t");
post_con_abundance <- pic(post_values$abundance_CSS,post_tree)
post_con_distance <- pic(post_values$distance,post_tree)

reg1 <- lm(pre_con_abundance~pre_con_distance)
summary(reg1)
reg2 <- lm(post_con_abundance~post_con_distance)
summary(reg2)

plot(pre_con_abundance~pre_con_distance, data=pre_values, type='n')
points(pre_con_distance, pre_con_abundance, pch=20)
points(post_con_distance, post_con_abundance, pch=1)
abline(reg1, lty=1)
abline(reg2, lty=2)
legend("topright", c("Pre","Post"), lty=c(1,2), pch=c(20,1) )