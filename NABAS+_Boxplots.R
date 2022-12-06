# R script to generate the boxplots in the paper: "Accurate alignment-based classification of metagenomic sequences with NABAS+"
# Author: Erda Qorri
# Date: 18-10-2022

# Load libraries -------
library(tidyverse)
library(magrittr)
library(dplyr)
library(ggplot2)
library(grid)
library(gridExtra)

# Load dataset -------
metrics <- read.csv("path_to_file/file.tsv",
                    sep = "\t")

# Correctly Identified Species -------
all_found_species <- metrics %>%
  select(Reference.type, Software, Read.number, All.found.species)

sets = unique(all_found_species$Reference.type)
dataset_plots = list()

# for loop that iterates over the datasets to generate the corresponding plots
for(file_ in sets){
  dataset_plots[[file_]] = ggplot(all_found_species %>% filter(Reference.type == file_), 
                                  aes(x=Software, y=All.found.species, fill=Software)) + 
    geom_boxplot(notch = F, outlier.shape = 23) + 
    scale_y_log10(limits = c(10, 18000)) +
    theme_classic() +
    ggtitle(file_) +
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
      legend.position =  "none",
      panel.border = element_rect(
        colour = "black",
        fill = NA,
        size = 0.5
      ))
  
  print(dataset_plots[[file_]])
}


# Jaccard Distance -------
jaccard.distance <- metrics %>%
  select(Reference.type, Software, Read.number, Jaccard.distance)

sets = unique(jaccard.distance$Reference.type)
dataset_plots = list()

# for loop that iterates over the datasets to generate the corresponding plots
for(file_ in sets){
  dataset_plots[[file_]] = ggplot(jaccard.distance %>% filter(Reference.type == file_), 
                                  aes(x=Software, y=Jaccard.distance, fill=Software)) + 
    geom_boxplot(notch = F, outlier.shape = 23) + 
    ylim(c(0,1)) +
    theme_classic() +
    ggtitle(file_) +
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
      legend.position =  "none",
      panel.border = element_rect(
        colour = "black",
        fill = NA,
        size = 0.5
      ))
  
  print(dataset_plots[[file_]])
}


# F1 Score -------
f1_score <- metrics %>%
  select(Reference.type, Software, Read.number, F1)

sets = unique(f1_score$Reference.type)
dataset_plots = list()

# for loop that iterates over the datasets to generate the corresponding plots
for(file_ in sets){
  dataset_plots[[file_]] = ggplot(f1_score %>% filter(Reference.type == file_), 
                                  aes(x=Software, y=F1, fill=Software)) + 
    geom_boxplot(notch = F, outlier.shape = 23) + 
    ylim(c(0,1)) +
    theme_classic() +
    ggtitle(file_) +
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
      legend.position =  "none",
      panel.border = element_rect(
        colour = "black",
        fill = NA,
        size = 0.5
      ))
  
  print(dataset_plots[[file_]])
}

# False Discovery Rate (FDR) -------
fdr <- metrics %>%
  select(Reference.type, Software, Read.number, FDR)

sets = unique(fdr$Reference.type)
dataset_plots = list()

# for loop that iterates over the datasets to generate the corresponding plots
for(file_ in sets){
  dataset_plots[[file_]] = ggplot(fdr %>% filter(Reference.type == file_), 
                                  aes(x=Software, y=FDR, fill=Software)) + 
    geom_boxplot(notch = F, outlier.shape = 23) + 
    ylim(c(0,1)) +
    theme_classic() +
    ggtitle(file_) +
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
      legend.position =  "none",
      panel.border = element_rect(
        colour = "black",
        fill = NA,
        size = 0.5
      ))
  
  print(dataset_plots[[file_]])
}



