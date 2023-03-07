# Title: Accurate and highly efficient alignment-based classification of metagenomic sequences with NABAS+
# Script_Info: Extracts citations for the corresponding publications of the classifiers benchmarked in the paper with the title: Accurate and highly efficient alignment-based classification of metagenomic sequences with NABAS+.
# Author: Erda Qorri
# Date: 28-02-2023

# load libraries ----
library(scholar)
library(tidyverse)
library(dplyr)
library(magrittr)
library(ggplot2)
library(gridExtra)
library(ggpubr)
library(patchwork)

# Local functions ----
# Takes as input: dataframe (with citations from different years) and the target column (where the citations are stored)
citation_sum <- function(data, target_col) {
  sum <- sum(data[[target_col]])
  return(sum)
}

# Generate plots
# generate plot with angled x-axis
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

# Order x-axis items
# Orders the x-axis items based on a user defined col
citation_x_axis_order <- function(dataframe, col) {
  dataframe[[col]] <-
    factor(dataframe[[col]],
           levels = unique(dataframe[[col]]),
           ordered = TRUE)
  return(dataframe)
}

# Citation Extraction ----
# Kraken (2014) ----
kraken_publications <- get_publications("EJvCgYcAAAAJ")
kraken_citation_num <-
  get_article_cite_history("EJvCgYcAAAAJ", kraken_publications$pubid[1])
citation_sum(citation_num_kraken, "cites")

kraken_citation_num__plot <-
  citation_x_axis_order(citation_num_kraken, "year")

# plot
kraken_cit_plot <-
  generate_citation_plot(dataframe = citation_num_kraken, "year", "Kraken", "2014")

# Kraken2 (2019) ----
kraken2_publications <- get_publications("2JMaTKsAAAAJ")
kraken2_citation_num <-
  get_article_cite_history("2JMaTKsAAAAJ", p$pubid[4])
citation_sum(kraken2_citation_num, "cites")

# plot
kraken2_citation_num <-
  citation_x_axis_order(kraken2_citation_num, "year")
kraken2_cit_num_plot <-
  generate_citation_plot(dataframe = kraken2_citation_num, "year", "Kraken2", "2019")

# Bracken (2016) ----
bracken_titles <- get_publications("VMSuK4QAAAAJ")
bracken_citation_num = get_article_cite_history("VMSuK4QAAAAJ", bracken_titles$pubid[2])
citation_sum(bracken_citation_num, "cites")

# plot
bracken_citation_num <-
  citation_x_axis_order(bracken_citation_num, "year")
bracken_cit_num_plot <-
  generate_citation_plot(dataframe = bracken_citation_num, "year", "Bracken", "2016")

# CLARK (2015) ----
clark_titles <- get_publications("dctDJHsAAAAJ")
clark_citation_num = get_article_cite_history("dctDJHsAAAAJ", clark_titles$pubid[2])
citation_sum(clark_citation_num, "cites")

# plot
clark_citation_num <-
  citation_x_axis_order(clark_citation_num, "year")
clark_cit_num_plot <-
  generate_citation_plot(dataframe = clark_citation_num, "year", "CLARK", "2015")


# Alignment-Based Classifiers ----
# MetaPhlan ----
metaphlan_titles <- get_publications("ZXjO-Q4AAAAJ")
metaphlan_citation_num = get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_titles$pubid[5])
citation_sum(metaphlan_citation_num, "cites")

# plot
metaphlan_citation_num <-
  citation_x_axis_order(metaphlan_citation_num, "year")
metaphlan_cit_num_plot <-
  generate_citation_plot(dataframe = metaphlan_citation_num, "year", "MetaPhlAn", "2012")

# MetaPhlAn2
metaphlan2_citation_num = get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_titles$pubid[6])
citation_sum(metaphlan2_citation_num, "cites")
get_article_scholar_url("ZXjO-Q4AAAAJ", metaphlan_titles$pubid[6])

# plot
metaphlan2_citation_num <-
  citation_x_axis_order(metaphlan2_citation_num, "year")
metaphlan2_cit_num_plot <-
  generate_citation_plot(dataframe = metaphlan2_citation_num, "year", "MetaPhlAn2", "2016")

# MetaPhlan2 ----
metaphlan2_citation_num = get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_titles$pubid[6])
citation_sum(metaphlan2_citation_num, "cites")

# plot
metaphlan2_citation_num <-
  citation_x_axis_order(metaphlan2_citation_num, "year")
metaphlan2_citation_num <-
  generate_citation_plot(dataframe = metaphlan2_citation_num, "year", "MetaPhlAn3", "2019")

# MetaPhlan3 ----
metaphlan3_citation_num = get_article_cite_history("ZXjO-Q4AAAAJ", metaphlan_titles$pubid[23])
citation_sum(metaphlan3_citation_num, "cites")

# plot
metaphlan3_citation_num <-
  citation_x_axis_order(metaphlan3_citation_num, "year")
metaphlan3_cit_num_plot <-
  generate_citation_plot(dataframe = metaphlan3_citation_num, "year", "MetaPhlAn3", "2019")

# GOTTCHA (2015)
gottcha_titles <- get_publications("Wef7-8kAAAAJ")
gottcha_citation_num = get_article_cite_history("Wef7-8kAAAAJ", gottcha_titles$pubid[49])
get_article_scholar_url("Wef7-8kAAAAJ", gottcha_titles$pubid[49])
citation_sum(gottcha_citation_num, "cites")

# plot
# citation_num_clark$year <- factor(citation_num_clark$year, levels = unique(citation_num_clark$year), ordered = TRUE)
gottcha_citation_num <-
  citation_x_axis_order(gottcha_citation_num, "year")
gottcha_citation_num_plot <-
  generate_citation_plot(dataframe = gottcha_citation_num, "year", "GOTTCHA", "2015")

# Kaiju ----
kaiju_titles <- get_publications("-vGMjmwAAAAJ")
kaiju_citation_num = get_article_cite_history("-vGMjmwAAAAJ", kaiju_titles$pubid[14])
citation_sum(kaiju_citation_num, "cites")

# plot
kaiju_citation_num <-
  citation_x_axis_order(kaiju_citation_num, "year")
kaiju_citation_num_plot <-
  generate_citation_plot(dataframe = kaiju_citation_num, "year", "Kaiju", "2016")

# DIAMOND (2015)
diamond_titles <- get_publications("kjPIF1cAAAAJ")
diamond_citation_num = get_article_cite_history("kjPIF1cAAAAJ", diamond_titles$pubid[1])
get_article_scholar_url("kjPIF1cAAAAJ", diamond_titles$pubid[1])
citation_sum(diamond_citation_num, "cites")

# plot
diamond_citation_num <-
  citation_x_axis_order(diamond_citation_num, "year")
diamond_citation_num_plot <-
  generate_citation_plot(dataframe = diamond_citation_num, "year", "DIAMOND", "2015")

# Centrifuge (2016)
centrifuge_titles <- get_publications("VMSuK4QAAAAJ")
centrifuge_citation_num = get_article_cite_history("VMSuK4QAAAAJ", centrifuge_titles$pubid[1])
get_article_scholar_url("VMSuK4QAAAAJ", centrifuge_titles$pubid[1])
citation_sum(centrifuge_citation_num, "cites")

# plot
centrifuge_citation_num <-
  citation_x_axis_order(centrifuge_citation_num, "year")
centrifuge_citation_num_plot <-
  generate_citation_plot(dataframe = centrifuge_citation_num, "year", "Centrifuge", "2016")


# Total citations by classifier type (2015-2023) ----
total_citations <- read_csv2("/path/to/file/citations.csv")

total_citations_pivoted <- total_citations %>%
  pivot_longer(
    cols = c(`k-mer Based`, `Alignment Based`),
    names_to = "Classifier Type",
    values_to = "Citation_Number"
  )

total_citations_pivoted <-
  citation_x_axis_order(citation_summary_pivoted, "Years")

colors <- c("#008C79", "#FFA07A")

total_citations_plot  = tootal_citations_pivoted %>%
  ggplot(aes(Years, Citation_Number, fill = `Classifier Type`)) +
  geom_bar(stat = "identity", position = "dodge") +
  geom_text(
    aes(label = Citation_Number),
    position = position_dodge(width = 1),
    vjust = -0.5,
    size = 3.5
  ) +
  ylab("Citation Number") +
  xlab("") +
  ggtitle("All Citations of the Chosen Classifiers") +
  theme_classic() +
  theme(
    axis.text.x = element_text(
      angle = 35,
      hjust = 1,
      size = 16
    ),
    axis.text.y = element_text(
      angle = 0,
      size = 14,
      hjust = 0.6
    ),
    axis.title.y = element_text(size = 15),
    plot.title = element_text(
      hjust = 0.6,
      vjust = -1.5,
      size = 16
    ),
    panel.border = element_blank(),
    legend.text = element_text(size = 12.5),
    legend.title = element_text(size = 12.5),
    axis.line = element_line(color = "black")
  ) +
  scale_fill_manual(values = colors) +
  coord_cartesian(ylim = c(0, 2500), expand = FALSE)


# Total citations (to generate the stacked barplots) ----
total_citations_classifiers_stacked <-
  read_csv2("/path/to/file/kmer_based_citations.csv")

total_citations_classifiers_stacked_pivoted <-
  total_citations_classifiers_stacked %>%
  pivot_longer(
    cols = c(
      `2015`,
      `2016`,
      `2017`,
      `2018`,
      `2019`,
      `2020`,
      `2021`,
      `2022`,
      `2023`
    ),
    names_to = "Year",
    values_to = "Values"
  )

# kmer-based classifiers
kmer_based_classifiers_stacked <-
  total_citations_classifiers_stacked_pivoted %>%
  filter(Classifiers %in% c("Kraken*", "Bracken", "CLARK"))

colors <- c("#77B77D", "#E49C39", "#6EA6CD")

kmer_based_classifiers_stacked %>%
  ggplot(aes(Year, Values, fill = Classifiers)) +
  geom_bar(position = "stack", stat = "identity") +
  stat_summary(
    fun = sum,
    aes(label = round(..y..), group = Year),
    geom = "text",
    vjust = -1,
    size = 5
  ) + # modify this line  xlab("") +
  ggtitle("All Citations of the k-mer-based Classifiers")  +
  ylab("Citation Number") +
  xlab("") +
  theme_classic() +
  theme(
    axis.text.x = element_text(
      angle = 35,
      hjust = 1,
      size = 16
    ),
    axis.text.y = element_text(
      angle = 0,
      size = 14,
      hjust = 0.6
    ),
    axis.title.y = element_text(size = 15),
    plot.title = element_text(
      hjust = 0.6,
      vjust = -1.5,
      size = 16
    ),
    panel.border = element_blank(),
    legend.text = element_text(size = 12.5),
    legend.title = element_text(size = 12.5),
    axis.line = element_line(color = "black")
  ) +
  scale_fill_manual(values = colors) +
  coord_cartesian(ylim = c(0, 2500), expand = FALSE)

# alignment-based classifiers
aln_based_classifiers_stacked <-
  total_citations_classifiers_stacked_pivoted %>%
  filter(Classifiers %in% c("Metaphlan*", "GOTTCHA", "Kaiju", "DIAMOND", "Centrifuge"))

aln_colors <-
  c("#A06B85", "#92C5DE", "#E2CC60", "#5DA5CE", "#2D9886")

aln_based_classifiers_stacked %>%
  ggplot(aes(Year, Values, fill = Classifiers)) +
  geom_bar(position = "stack", stat = "identity") +
  stat_summary(
    fun = sum,
    aes(label = round(..y..), group = Year),
    geom = "text",
    vjust = -1,
    size = 5
  ) + # modify this line  xlab("") +
  ylab("Citation Number") +
  xlab("") +
  ggtitle("All Citations of the Alignment-based Classifiers") +
  theme_classic() +
  theme(
    axis.text.x = element_text(
      angle = 35,
      hjust = 1,
      size = 16
    ),
    axis.text.y = element_text(
      angle = 0,
      size = 14,
      hjust = 0.6
    ),
    axis.title.y = element_text(size = 15),
    plot.title = element_text(
      hjust = 0.6,
      vjust = -1.5,
      size = 16
    ),
    panel.border = element_blank(),
    legend.text = element_text(size = 12.5),
    legend.title = element_text(size = 12.5),
    axis.line = element_line(color = "black")
  ) +
  # scale_fill_brewer(palette = "Set2") +
  scale_fill_manual(values = aln_colors) +
  coord_cartesian(ylim = c(0, 2500), expand = FALSE)
