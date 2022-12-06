from sys import argv
import os

import pandas
import pandas as pd
import math
import re
import numpy as np
import matplotlib.pyplot as plt
from statistics import median, mean
import random
import sklearn.metrics as metrics
import seaborn as sns
import warnings

def compare_with_print(reference_dict, results_dict):
    reference_types = list(reference_dict.keys())
    softwares = list(results_dict)
    samples = []
    for s in softwares:
        samples.append(list(results_dict[s]))
    sample_types = ["1m","2m","5m","10m","20m","500k"]
    with open("all_samples_nabas_float.tsv","w") as outp:
        outp.write("Reference type\tSoftware\tRead number\tAll found species\tCorrectly identified species\tPercentage difference squared\tBray-Curtis dissimilarity\tJaccard distance\tManhattan distance\tPrecision\tRecall\tF1\tFDR\n")
        for reference in reference_types:
            for software in softwares:
                for sample in list(results_dict[software]):
                    for type in sample_types:
                        if reference in sample and software in sample and type in sample:
                            jaccard_distance = 0
                            megtalalt = set()
                            rosszul_azonositott = set()
                            nem_talalt = set()
                            difference_sqrt = 0.0
                            bray_curtis_c = 0
                            reference_species_number = 0
                            manhattan_distance = 0
                            for faj,abundance, ali in zip(reference_dict[reference]["Species"],reference_dict[reference]["Abundance"], reference_dict[reference]["Alias"]):
                                for talalat in results_dict[software][sample]:
                                    if faj.strip() == talalat.strip():
                                        megtalalt.add(talalat)
                                        difference_sqrt += (float(abundance)-float(results_dict[software][sample][talalat]))**2
                                        manhattan_distance += abs(float(abundance)-float(results_dict[software][sample][talalat]))
                                        if float(abundance) > float(results_dict[software][sample][talalat]):
                                            bray_curtis_c += float(results_dict[software][sample][talalat])
                                        else:
                                            bray_curtis_c += float(abundance)
                                    elif isinstance(ali, str) and talalat.strip() in ali.split(","):
                                        megtalalt.add(faj.strip())
                                        manhattan_distance += abs(
                                            float(abundance) - float(results_dict[software][sample][talalat]))
                                        difference_sqrt += (float(abundance) - float(
                                            results_dict[software][sample][talalat])) ** 2
                            for faj in reference_dict[reference]["Species"]:
                                if faj not in megtalalt:
                                    nem_talalt.add(faj)
                            for talalat in results_dict[software][sample]:
                                if talalat not in megtalalt:
                                    rosszul_azonositott.add(talalat)
                                    difference_sqrt += float(results_dict[software][sample][talalat]) ** 2
                                    manhattan_distance += abs(
                                        float(abundance) - float(results_dict[software][sample][talalat]))
                            if len(reference.split("_")) > 1:
                                reference_species_number = int(reference.split("_")[-1])
                            else:
                                reference_species_number = int(reference[-2:])
                            jaccard_distance = len(megtalalt) / (
                                        len(results_dict[software][sample]) + reference_species_number - len(
                                    megtalalt))
                            try:
                                precision = len(megtalalt) / len(results_dict[software][sample])
                            except:
                                print(software + ", " + sample )
                                print(results_dict[software][sample])
                            recall = len(megtalalt) / (len(megtalalt) + len(nem_talalt))
                            FDR = (len(results_dict[software][sample]) - len(megtalalt)) / len(results_dict[software][sample])
                            try:
                                f1 = 2 * ((precision * recall) / (precision + recall))
                            except:
                                f1 = 0
                            outp.write(reference + "\t" + software + "\t" + type + "\t" + str(
                                len(results_dict[software][sample])) + "\t" + str(len(megtalalt)) + "\t "+ str(difference_sqrt)+\
                                       "\t" + str(1-(bray_curtis_c/100)) + "\t" + str(1-jaccard_distance) +"\t" + str(manhattan_distance) + "\t" + str(precision) + "\t" + str(recall) +"\t" + str(f1) + "\t" + str(FDR) + "\n")


#FDR = FP / (FP + TP)
#Precision = TruePositives / (TruePositives + FalsePositives)
#To calculate Bray–Curtis, let’s first calculate the sum of only the lesser counts for each species found in both sites: c
#Divide it by the sum of all specimens


def read_reference_datasets(reference_file):
    reference_dict = {}
    df = pd.ExcelFile(reference_file)
    for sheet in df.sheet_names:
        reference_dict[sheet] = {}
        reference_dict[sheet] = df.parse(sheet_name=sheet)
    return reference_dict


def read_results(result_folder):
    programs_list = ['gottcha', 'CLARK', 'kaiju', 'bracken', 'kaiju_webserver', 'metaphlan3', 'kraken2', 'centrifuge',
                     "nabas","diamond"]
    results_dict = {}

    unknown = ["UNKNOWN", "unidentified on species level", "unclassified", "belong to a (non-viral) species with less than 1% of all reads", "cannot be assigned to a (non-viral) species"]

    for program in programs_list:
        results_dict[program] = {}
        filelist = os.listdir("/".join([result_folder, program]))
        type = ""
        for f in filelist:
            if f.split(".")[-1] not in ["gz", "log", "py", "sh", "out", "data", "xlsx"]: #Ezeket a fájltípusokat nem nézzük
                results_dict[program][f] = {}
                #végigmegyek az összes olyen mappán, aminek programnév a nevük, az ezekben levő összes fájlon, aminek megfelelő a típusa
                with open("/".join([result_folder, program, f]), "r") as result_input:
                    if program == "kraken2":
                        type = "kraken2"
                        for line in result_input:
                            if line.split("\t")[3] in ["U", "S"] and float(line.split("\t")[0].strip()) > 0 and "virus" not in line.split("\t")[-1] and "phage" not in line.split("\t")[-1]:
                                if line.split("\t")[-1].strip() not in unknown:
                                    results_dict["kraken2"][f][line.split("\t")[-1].strip()] = float(line.split("\t")[0].strip())
                    elif program == "metaphlan3":
                        type = "metaphlan3"
                        for line in result_input:
                            try:
                                if "|s__" in line.split("\t")[0] and float(line.split("\t")[-2]) > 0:
                                    species_name = " ".join(line.split("\t")[0].strip().split("__")[-1].split("_"))
                                    results_dict["metaphlan3"][f][species_name] = \
                                        float(line.split("\t")[-2].strip())
                            except:
                                print(f)
                    elif program == "gottcha":
                        type = "gottcha"
                        for line in result_input:
                            if line.split("\t")[0] == "species" and float(line.split("\t")[2]) > 0:
                                results_dict["gottcha"][f][line.split("\t")[1].strip()] = float(line.split("\t")[2]) * 100
                    elif program == "CLARK":
                        type = "CLARK"
                        header = True
                        for line in result_input:
                            try:
                                if header:
                                    header = False
                                elif float(line.split(",")[4]) > 0:
                                    results_dict["CLARK"][f][line.split(",")[0].rstrip()] = float(line.split(",")[4])
                            except:
                                    print(f)
                    elif program == "kaiju_webserver":
                        type = "kaiju_webserver"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split("\t")[1].strip()) > 0 and line.split("\t")[-1].strip() not in unknown:
                                    results_dict["kaiju_webserver"][f][line.split("\t")[-1].strip()] = float(line.split("\t")[1].strip())
                    elif program == "kaiju":
                        type = "kaiju"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split("\t")[1].strip()) > 0:
                                results_dict["kaiju"][f][line.split("\t")[-1].strip()] = float(line.split("\t")[
                                    1].strip())
                    elif program == "centrifuge":
                        type = "centrifuge"
                        for line in result_input:
                            if line.split("\t")[2] == "species" and float(line.split("\t")[-1].strip()) > 0:
                                results_dict["centrifuge"][f][line.split("\t")[0].strip()] = float(line.split("\t")[
                                    -1].strip())*100
                    elif program == "bracken":
                        type = "bracken"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split("\t")[-1].strip()) > 0:
                                results_dict["bracken"][f][line.split("\t")[0].strip()] = float(line.split("\t")[-1].strip()) * 100
                    elif program == "nabas":
                        type = "nabas"
                        header = True
                        for line in result_input:
                            try:
                                if header:
                                    header = False
                                elif float(str(line.split("\t")[9]).rstrip("%")) > 0:
                                    fajnev = line.split("\t")[7].strip()
                                    fajnev = "".join(fajnev.split("["))
                                    fajnev = "".join(fajnev.split("]"))
                                    if fajnev not in results_dict["nabas"][f]:
                                        results_dict["nabas"][f][fajnev] = float(str(
                                            line.split("\t")[9]).rstrip("%"))
                                    else:
                                        results_dict["nabas"][f][fajnev] += float(str(
                                            line.split("\t")[9]).rstrip("%"))
                            except:
                                print(line)
                                continue
                    elif program == "diamond":
                        type = "diamond"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif "virus" not in line.split("\t")[1] and "phage" not in line.split("\t")[1]:
                                try:
                                    if line.split("\t")[1] not in list(results_dict["diamond"]):
                                        results_dict["diamond"][f][line.split("\t")[1]] = float(line.split("\t")[3].strip())
                                    else:
                                        results_dict["diamond"][f][line.split("\t")[1]] += float(line.split("\t")[3].strip())
                                except:
                                    print(f)
    return recalculate_percentage(results_dict)

def recalculate_percentage(results_dict):
    for type in results_dict:
        for f in results_dict[type]:
            if sum(results_dict[type][f].values()) < 99:
                osszeg = sum(results_dict[type][f].values())
                a = 0
                for key in results_dict[type][f]:
                    results_dict[type][f][key] = results_dict[type][f][key] / osszeg * 100
    return results_dict


def fonok(reference_file, result_folder):
    reference_dict = read_reference_datasets(reference_file)
    results_dict = read_results(result_folder)
    compare_with_print(reference_dict, results_dict)


fonok(argv[1], argv[2])


