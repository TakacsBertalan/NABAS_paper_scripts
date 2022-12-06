# R script to generate the FDR and read number boxplots in the paper: "Accurate alignment-based classification of metagenomic sequences with NABAS+"
# Author: Erda Qorri
# Date: 18-10-2022

library(tidyverse)
library(ggplot2)
library(dplyr)
library(ggpubr)

# Load dataset -------
metrics <- read.csv("path_to_file/file.tsv",
                    sep = "\t")

# False Discovery Rate (FDR) ------
fdr_data <- metrics %>%
  select(Reference.type, Software, Read.number, FDR)

# ordering the reads in an increasing order
ordered_reads <- c("500k","1m","2m","5m","10m","20m")

# arranging the reads based on the order determined above
ordered_fdr_data <- fdr_data %>% 
  arrange(factor(Read.number, levels = ordered_reads))

# determining the level order for ggplot2
level_order <- c("500k","1m","2m","5m","10m","20m")

ggplot(ordered_fdr_data, aes(x = factor(Read.number, level = level_order), y = FDR, fill = Software)) +            # Applying ggplot function
  geom_boxplot(position = (position_dodge(width = 0.9)), outlier.shape = 23)+
  stat_boxplot(geom = "errorbar", width = 0.5) +
  xlab("Read Number") +
  ylab("FDR") +
  theme_classic() +
  facet_wrap(~ Software, ncol = 2, dir = "v",scales = "fixed") +
  theme(plot.title = element_text(hjust = 0.5,
                                  face = "plain",
                                  size = 14,
                                  color = "black",
  ),
  axis.text.x = element_text(hjust = 1,
                             size = 13.5,
                             angle = 35,
                             vjust = 1,
                             color = "black"
  ),
  axis.title.x = element_text(hjust = 0.5,
                              size = 13.5,
                              vjust = 0.2),
  axis.text.y = element_text(hjust = 0.8,
                             size = 11,
                             angle = 0,
                             color = "black"
  ),
  axis.title.y = element_text(hjust = 0.5,
                              size = 13.5, 
                              vjust = 1.5,
                              color = "black"
  ),
  legend.position =  "none",
  panel.border = element_rect(colour = "black", fill = NA, size = 0.5),
  strip.background = element_rect(color = "black",
                                  fill = "azure2"))

