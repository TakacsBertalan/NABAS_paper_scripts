This stand-alone java project contains the scripts used to create the NABAS reference library from a RefSeq genomic collection.

usage: java -jar dist/NABASCreateDatabase.jar [-d <arg>] [-h] [-i <arg>] [-o <arg>] [-p
       <arg>]
This program expects the fasta files in the input folder as gzipped and
having the RefSeq assembly id as filename

 -d,--input-dictionary <arg>   Tab separated file containing the refseq
                               assembly and ncbi tax ids [required] 
                               
 -h,--help                     Display this message
 
 -i,--input-folder <arg>       Location of the folder containing the input
                               fasta.gz files [required]
                               
 -o,--output-folder <arg>      Output folder [required]
 
 -p,--output-prefix <arg>      Output file prefix. Optional. Default:
                               NABAS_database


For example input and output files, see the test/ folder!
