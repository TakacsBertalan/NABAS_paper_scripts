# Title: Accurate and highly efficient alignment-based classification of metagenomic sequences with NABAS+
# Script_Info: Extracts citations of all the classifiers analyzed in the paper with the title: "Accurate and highly efficient alignment-based classification of metagenomic sequences with NABAS+".
# Author: Erda Qorri
# Date: 28-02-2023

# libraries ----
library(scholar)
library(tidyverse)
library(dplyr)
library(magrittr)
library(ggplot2)
library(ggpubr)

# local functions ----

# Takes as input: dataframe (with citations from different years) and the target column (where the citations are stored)
citation_sum <- function(data, target_col) {
  sum <- sum(data[[target_col]])
  return(sum)
}

# Generates citation plots for each classifer
# Takes as input: citation dataframe, desired title of the plot, and the year of the classifier
generate_citation_plot <- function(dataframe, col, title, year) {
  dataframe[[col]] <-
    factor(dataframe[[col]],
           levels = unique(dataframe[[col]]),
           ordered = TRUE)
  dataframe %>%
    ggplot(aes(year, cites)) +
    geom_bar(stat = "identity",
             col = "#c47a64",
             fill = "#c47a64") +
    theme_bw() +
    geom_text(aes(label = cites), vjust = -0.4, size = 4) +
    ggtitle(paste(title, "(", year, ")")) +
    ylab("Citation Number") +
    xlab("") +
    # scale_x_discrete(labels = unique(dataframe$year)) +
    theme(
      axis.text.x = element_text(
        angle = 35,
        size = 13,
        hjust = 1
      ),
      axis.text.y = element_text(angle = 0,
                                 size = 13),
      axis.title.y = element_text(size = 12),
      plot.title = element_text(hjust = 0.5,
                                size = 13),
      panel.border = element_blank(),
      panel.grid.major = element_blank(),
      panel.grid.minor = element_blank(),
      axis.line = element_line(color = "black")
    )
}

# Extract citations: k-mer based classifiers ----

# Kraken (2014)
kraken_publications <- get_publications("EJvCgYcAAAAJ")
kraken_citation_num <-
  get_article_cite_history("EJvCgYcAAAAJ", kraken_publications$pubid[1])
citation_sum(kraken_citation_num, "cites")

kraken_citation_num_plot <-
  generate_citation_plot(dataframe = kraken_citation_num, "year", "Kraken", "2014")

# Kraken2 (2019)
kraken2_publications <- get_publications("2JMaTKsAAAAJ")
kraken2_citation_num <-
  get_article_cite_history("2JMaTKsAAAAJ", p$pubid[4])
citation_sum(kraken2_citation_num, "cites")

kraken2_citation_num_plot <-
  generate_citation_plot(dataframe = kraken2_citation_num, "year", "Kraken", "2019")

# Bracken (2016)
bracken_publications <- get_publications("VMSuK4QAAAAJ")
bracken_citation_num <-
  get_article_cite_history("VMSuK4QAAAAJ", bracken_publications$pubid[2])
citation_sum(bracken_citation_num, "cites")

bracken_citation_num_plot <-
  generate_citation_plot(dataframe = bracken_citation_num, "year", "Bracken", "2016")

# CLARK (2015)
clark_publications <- get_publications("dctDJHsAAAAJ")
clark_citation_num <-
  get_article_cite_history("dctDJHsAAAAJ", clark_publications$pubid[2])
citation_sum(clark_citation_num, "cites")

clark_citation_num_plot <-
  generate_citation_plot(dataframe = clark_citation_num, "year", "CLARK", "2015")

# Merge Kraken and Kraken2
kraken_citation_num_plot <-
  kraken_citation_num_plot + coord_cartesian(ylim = c(0, 800), expand = FALSE)
kraken2_citation_num_plot <-
  kraken2_citation_num_plot + coord_cartesian(ylim = c(0, 1000), e = FALSE)

kmer_krakens <-
  ggarrange(
    kraken_citation_num_plot,
    kraken2_citation_num_plot,
    labels = c("A", "B"),
    widths = c(30, 30),
    ncol = 2
  )


# Extract citations: Alingment based classifiers ----

# MetaPhlAn (2012)
metaphlan_publications <- get_publications("ZXjO-Q4AAAAJ")
metaphlan_citation_num <-
  get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_publications$pubid[5])
citation_sum(metaphlan_citation_num, "cites")

metaphlan_citation_num_plot <-
  generate_citation_plot(dataframe = metaphlan_citation_num, "year", "MetaPhlAn", "2012")

# MetaPhlAn2 (2015)
metaphlan2_citation_num <-
  get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_publications$pubid[6])
citation_sum(metaphlan2_citation_num, "cites")

metaphlan2_citation_num_plot <-
  generate_citation_plot(dataframe = metaphlan2_citation_num, "year", "MetaPhlAn2", "2015")

# MetaPhlAn3 (2021)
metaphlan3_citation_num <-
  get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_publications$pubid[23])
citation_sum(metaphlan3_citation_num, "cites")

metaphlan3_citation_num_plot <-
  generate_citation_plot(dataframe = metaphlan3_citation_num, "year", "MetaPhlAn3", "2021")

# GOTTCHA (2015)
gottcha_publications <- get_publications("Wef7-8kAAAAJ")
gottcha_citation_num <-
  get_article_cite_history("Wef7-8kAAAAJ", gottcha_publications$pubid[49])
citation_sum(gottcha_citation_num, "cites")

gottcha_citation_num_plot <-
  generate_citation_plot(dataframe = gottcha_citation_num, "year", "GOTTCHA", "2015")

# Kaiju (2016)
kaiju_publications <- get_publications("-vGMjmwAAAAJ")
kaiju_citation_num <-
  get_article_cite_history("-vGMjmwAAAAJ", kaiju_publications$pubid[14])
citation_sum(kaiju_citation_num, "cites")

kaiju_citation_num_plot <-
  generate_citation_plot(dataframe = kaiju_citation_num, "year", "Kaiju", "2016")

# DIAMOND (2014)
diamond_publications <- get_publications("kjPIF1cAAAAJ")
diamond_citation_num <-
  get_article_cite_history("kjPIF1cAAAAJ", diamond_publications$pubid[1])
citation_sum(diamond_citation_num, "cites")

diamond_citation_num_plot <-
  generate_citation_plot(dataframe = diamond_citation_num, "year", "DIAMOND", "2014")

# Centrifuge (2016)
centrifuge_publications <- get_publications("VMSuK4QAAAAJ")
centrifuge_citation_num <-
  get_article_cite_history("VMSuK4QAAAAJ", centrifuge_publications$pubid[1])
citation_sum(centrifuge_citation_num, "cites")

centrifuge_citation_num_plot <-
  generate_citation_plot(dataframe = centrifuge_citation_num, "year", "Centrifuge", "2016")

# Merge MetaPhlAn, MetaPhlAn2, MetaPhlAn3
metaphlan_citation_num_plot <-
  metaphlan_citation_num_plot + coord_cartesian(ylim = c(0, 300), expand = FALSE)
metaphlan2_citation_num_plot <-
  metaphlan2_citation_num_plot +  coord_cartesian(ylim = c(0, 500), expand = FALSE)
metaphlan3_citation_num_plot <-
  metaphlan3_citation_num_plot + coord_cartesian(ylim = c(0, 400), expand = FALSE)

metaphlan_versions <-
  ggarrange(
    metaphlan_citation_num_plot,
    metaphlan2_citation_num_plot,
    metaphlan3_citation_num_plot,
    labels = c("A", "B", "C"),
    ncol = 2,
    nrow = 2
  )

#### PART 2 #####

# Sum of citations ----
# Kraken: Total number of citations
dataframe <- read_csv2("path/to/file/")

dataframe <- citation_x_axis_order(release_the_krakens, "Year")

kraken_total_citations_plot <- dataframe %>%
  ggplot(aes(Year, Total)) +
  geom_bar(stat = "identity", fill = "#008C79") +
  geom_text(aes(label = Total), vjust = -0.4, size = 4) +
  ggtitle("Kraken Citations") +
  ylab("Citation Number") +
  xlab("") +
  theme_classic() +
  theme(
    axis.text.x = element_text(
      angle = 35,
      hjust = 1,
      size = 13
    ),
    axis.text.y = element_text(angle = 0,
                               size = 13),
    axis.title.y = element_text(size = 12),
    plot.title = element_text(
      hjust = 0.5,
      vjust = 1,
      size = 16
    ),
    panel.border = element_blank(),
    axis.line = element_line(color = "black")
  ) +
  coord_cartesian(ylim = c(0, 1600), expand = FALSE)

# MetaPhlAn: Total Number of Citations
metaphlan_dataframe <- read_csv2("path/to/file/")

metaphlan_dataframe <-
  citation_x_axis_order(bake_the_phlans, "Year")

metaphlan_total_citations_plot <- metaphlan_dataframe %>%
  ggplot(aes(Year, Total)) +
  geom_bar(stat = "identity", fill = "#0878b0") +
  geom_text(aes(label = Total), vjust = -0.4, size = 4) +
  ggtitle("MetaPhlAn Citations") +
  ylab("Citation Number") +
  xlab("") +
  theme_classic() +
  theme(
    axis.text.x = element_text(
      angle = 35,
      hjust = 1,
      size = 13
    ),
    axis.text.y = element_text(angle = 0,
                               size = 13),
    axis.title.y = element_text(size = 12),
    plot.title = element_text(
      hjust = 0.5,
      vjust = 1,
      size = 16
    ),
    panel.border = element_blank(),
    axis.line = element_line(color = "black")
  ) +
  coord_cartesian(ylim = c(0, 1000), expand = FALSE)

# Citation Summary grouped by classifier type ----
dataframe_summary <- read_csv2("path/to/file/")

dataframe_summary <- dataframe_summary %>%
  pivot_longer(
    cols = c(`k-mer Based`, `Alignment Based`),
    names_to = "Classifier Type",
    values_to = "Citation_Number"
  )

dataframe_summary <-
  citation_x_axis_order(dataframe_summary, "Years")

# colors <- c("#008C79", "#FFA07A")

dataframe_summary_plot  = dataframe_summary %>%
  ggplot(aes(Years, Citation_Number, fill = `Classifier Type`)) +
  geom_bar(stat = "identity", position = "dodge") +
  ylab("Citation Number") +
  xlab("") +
  ggtitle("All citations of the chosen classifiers") +
  theme_bw() +
  theme(
    axis.text.x = element_text(
      angle = 35,
      hjust = 1,
      size = 13
    ),
    axis.text.y = element_text(angle = 0,
                               size = 13),
    axis.title.y = element_text(size = 12),
    plot.title = element_text(
      hjust = 0.5,
      vjust = -1.5,
      size = 16
    ),
    panel.border = element_blank(),
    axis.line = element_line(color = "black")
  ) +
  scale_fill_manual(values = colors) +
  coord_cartesian(ylim = c(0, 2500), expand = FALSE)
