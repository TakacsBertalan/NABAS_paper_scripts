[![DOI](https://zenodo.org/badge/556662190.svg)](https://doi.org/10.5281/zenodo.14097479)
[![CC BY-NC 4.0][cc-by-nc-shield]][cc-by-nc]

This work is licensed under a
[Creative Commons Attribution-NonCommercial 4.0 International License][cc-by-nc].

[![CC BY-NC 4.0][cc-by-nc-image]][cc-by-nc]

[cc-by-nc]: https://creativecommons.org/licenses/by-nc/4.0/
[cc-by-nc-image]: https://licensebuttons.net/l/by-nc/4.0/88x31.png
[cc-by-nc-shield]: https://img.shields.io/badge/License-CC%20BY--NC%204.0-lightgrey.svg

# Code for benchmarking our novel metagenomic classifier, NABAS+

This repository contains code for the project "Accurate and highly efficient alignment-based classification of metagenomic sequences with NABAS+". This is the source code used for statistical analysis, and data visualization.

### How to use NABAS+

#### Setting up NABAS+ 

Prerequisites: [Java](https://www.java.com/en/), [samtools](https://github.com/samtools/samtools), [bwa](https://github.com/lh3/bwa)


`git clone https://github.com/TakacsBertalan/NABAS_paper_scripts.git`

`cd NABAS_paper_scripts/NABASStandAlone`

`mkdir test/Taxonomy`

Download the contents of the following Zenodo repository into the test/Taxonomy folder: https://zenodo.org/records/14016582

`bwa index test/NABAS_database_1.fa.gz`

#### Test run

`java -jar dist/NABAS.jar -d test/ -o test/ -r1 test/small_test_sample_in_silico_casava_S0_L001_R1_001.fastq.gz -r2 test/small_test_sample_in_silico_casava_S0_L001_R2_001.fastq.gz`

Running the above command produces the small_test_sample_in_silico_casava_S0_L001_R1_001.ShotgunResult.xlsx found in the NABASStandalone/test folder

#### General use

`java -jar dist/NABAS.jar [-d ] [-h] [-maxMismatch ] [-measureBin ] [-minNotNullBin ] [-o ] [-r1 ] [-r1Mask ]
[-r2 ] [-r2Mask ] [-t ] [-taxonomy ]`



For detailed instructions, see the README.md in the NABASStandalone folder

### Scripts in the repository
This repository contains a stand-alone, exacutable version of NABAS+, see under NABASStandAlone. NABASStandAlone/test contains test input and output files for testing the software.

For de-interleaving CAMI samples and adding a CASAVA-style header, see the FixFastqHeaders project.

Reference database was generated using the NABASCreateDatabase project.

Scripts for comparing the classifier outputs can be found in the NABASCompare folder.

### Data Availability
(a) The _human gastrooral_ dataset retrieved from the 2nd CAMI Challenge can be downloaded from the following link: https://frl.publisso.de/data/frl:6425518. 

(b) The newly generated sample19 is available on Zenodo, along with the list of reference genomes. https://zenodo.org/uploads/13828312 

(c) Illumina sequencing results of the Zymo microbial standards are accessible at the European Nucleotide Archive via the ERR2984773 and ERR2935805 accession IDs.




#### Contact information
If you have any questions, please do not hesitate to contact Bertalan Takács at takacs.bertalan@brc.hu.

