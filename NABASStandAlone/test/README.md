This folder contains files for testing NABAS+. Small in-silico samples with casava headers were created from the genomes included. The reference was also created from the same genomes using the NABASCreateDatabase script. For running these test files, a Taxonomy folder should be included in the database folder.

To run the test data from the root directory:

java -jar dist/NABAS.jar -d test/ -o test/ -r1 test/small_test_sample_in_silico_casava_S0_L001_R1_001.fastq.gz -r2 test/small_test_sample_in_silico_casava_S0_L001_R2_001.fastq.gz

