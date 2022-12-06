# R script to generate the line graphs in the paper, "NABAS+: Improved Species Classification and Reduction of False Positive Detection in Metagenomic Samples"
# Author: Erda Qorri
# Date: 12-06-2022

# Load libraries -------
library(tidyverse)
library(magrittr)
library(dplyr)
library(ggplot2)
library(grid)
library(gridExtra)

# Import the data  -------
metrics <- read.csv("path_to_file/file.tsv",
                    sep = "\t")

# Correctly Identified Species -------
all_found_species <- nabas_data_new %>%
  select("Reference.type", "Software", "Read.number", "All.found.species")

# Define the reference types within a variable
sets = unique(top4$Reference.type)
dataset_plots = list()

# for loop that iterates over the datasets to generate the corresponding plots
for(reftype_ in sets){
  dataset_plots[[reftype_]] = ggplot(top4 %>% filter(Reference.type == reftype_) %>%
                                    arrange(factor(Read.number, levels = ordered_reads)),
                                  aes(x= factor(Read.number, level = level_order), y=All.found.species, group=Software)) + 
    geom_line(aes(color = Software ), size = 0.8) +
    geom_point(aes(color = Software ), size = 1.5) +
    scale_y_log10(limits = c(10, 300)) +
    theme_classic() +
    ggtitle(reftype_) +
    ylab("") +
    xlab("") +
    theme(
      plot.title = element_text(
        hjust = 0.5,
        face = "plain",
        size = 14,
        color = "black",
      ),
      axis.text.x = element_text(
        hjust = 1,
        size = 11,
        angle = 35,
        vjust = 1,
        color = "black"
      ),
      axis.title.x = element_text(
        hjust = 0.5,
        size = 11,
        vjust = 0.2
      ),
      axis.text.y = element_text(
        hjust = 0.8,
        size = 11,
        angle = 0,
        color = "black"),
      axis.title.y = element_text(
        hjust = 0.5,
        size = 13.5,
        vjust = 1.5
      ),
      legend.position =  "right",
      panel.border = element_rect(
        colour = "black",
        fill = NA,
        size = 0.5
      ))
  
  print(dataset_plots[[reftype_]])
}