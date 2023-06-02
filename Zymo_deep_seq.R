# Generate the stacked barcharts for the deeply sequenced samples
# Author: Erda Qorri
# Date: 06-12-2022

# Load libraries
library(ggplot2)
library(tidyverse)
library(dplyr)
library(ggtext)
library(ggbreak)
library(pals)
library(ggpubr)
library(patchwork)
library(magrittr)

# Import dataset
abundances_deep <- read.csv("path_to_file.csv", sep = "\t", header = FALSE)

# Generate plot for deeply sequenced samples
abundances_deep_plot <- abundances_deep %>%
mutate(Species = factor(Species, levels = c("Listeria monocytogenes",
                                            "Pseudomonas aeruginosa",
                                            "Bacillus subtilis group*",
                                            "Escherichia coli",	
                                            "Salmonella enterica",
                                            "Lactobacillus fermentum group*",
                                            "Enterococcus faecalis",
                                            "Staphylococcus aureus",
                                            "Misidentified"))) %>%
  ggplot(., aes(x = Software, y = `Relative abundance`, fill = Species)) +
  geom_bar(stat="identity", width = 0.4) +
  scale_y_continuous(limits = c(0,101), expand = c(0, 0))+
  theme_classic() +
  xlab("") +
  ylab("") +
  theme(axis.text.x = element_text(hjust = 1,
                                   size = 12,
                                   angle = 30,
                                   vjust = 1,
                                   color = "black"),
        axis.text.y = element_text(hjust = 0.8,
                                   size = 11,
                                   angle = 0,
                                   color = "black"),
        axis.title.y = element_text(hjust = 0.5,
                                    size = 13,
                                    vjust = 1,
                                    color = "black"),
        legend.position =  "right",
        legend.text.align = 0) +
  scale_fill_manual(values=as.vector(tol(26)),
                    "Species",
                    labels = c(expression(italic("Listeria monocytogenes")), 
                               expression(italic("Pseudomonas aeruginosa")),
                               expression(paste(italic('Bacillus subtilis'), " group*")),
                               expression(italic("Escherichia coli")),
                               expression(italic("Salmonella enterica")),
                               expression(paste(italic('Lactobacillus fermentum'),  " group*")),
                               expression(italic("Enterococcus faecalis")),
                               expression(italic("Staphylococcus aureus")),
                               expression(plain("Misidentified")))) 


# Induce the break in the plot
deep_abundances_break_plot <- abundances_deep_plot + scale_y_cut(breaks=c(2), which=c(1, 2), scales=c(3, 10), expand = FALSE)

print(deep_abundances_break_plot)
