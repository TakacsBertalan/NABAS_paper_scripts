from sys import argv
import os
import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import statistics
from matplotlib.colors import LogNorm
from pylab import *

def read_results(result_folder):
    programs_list = ['gottcha', 'CLARK', 'kaiju', 'bracken', 'kaiju_webserver', 'metaphlan', 'kraken2', 'centrifuge',"diamond"]
    results_dict = {}

    unknown = ["UNKNOWN", "unidentified on species level", "unclassified", "belong to a (non-viral) species with less than 1% of all reads", "cannot be assigned to a (non-viral) species", ' unidentified on species level ']

    for program in programs_list:
        results_dict[program] = {}
        filelist = os.listdir("/".join([result_folder, program]))
        type = ""
        for f in filelist:
            if f.split(".")[-1] not in ["gz", "log", "py", "sh", "out", "data", "xlsx"]: 
                results_dict[program][f] = {}

                with open("/".join([result_folder, program, f]), "r") as result_input:
                    if program == "kraken2":
                        type = "kraken2"
                        for line in result_input:
                            if line.split("\t")[3] in ["U", "S"] and float(line.split("\t")[0].strip()) > 0 and "virus" not in line.split("\t")[-1] and "phage" not in line.split("\t")[-1]:
                                if line.split("\t")[-1].strip() not in unknown:
                                    results_dict["kraken2"][f][line.split("\t")[-1].strip()]= {"Value":float(line.split("\t")[0].strip())}
                    elif program == "metaphlan":
                        type = "metaphlan"
                        for line in result_input:
                            if "|s__" in line.split("\t")[0] and float(line.split("\t")[-2]) > 0:
                                species_name = " ".join(line.split("\t")[0].strip().split("__")[-1].split("_"))
                                species_name = species_name.replace(" sp ", " sp. ")
                                uj_alt = []
                                if "#Additional species represented by this clade: " in line:
                                	alternatives =line.split("#Additional species represented by this clade: ")[1].split(",")
                                	uj_alt = []
                                	for alt in alternatives:
                                		alt = " ".join(alt.split("__")[-1].split("_")).strip()
                                		alt = alt.replace(" sp", " sp.")
                                		uj_alt.append(alt)
                                results_dict["metaphlan"][f][species_name] = {"Value":float(line.split("\t")[-2].strip()), "Alternatives":uj_alt}

                    elif program == "gottcha":
                        type = "gottcha"
                        for line in result_input:
                            if line.split("\t")[0] == "species" and float(line.split("\t")[2]) > 0:
                                results_dict["gottcha"][f][line.split("\t")[1].strip()] = {"Value":float(line.split("\t")[2]) * 100, "Alternatives":[]}
                    elif program == "CLARK":
                        type = "CLARK"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split(",")[4]) > 0:
                                results_dict["CLARK"][f][line.split(",")[0].rstrip()] = {"Value":float(line.split(",")[4]), "Alternatives":[]}
                    elif program == "kaiju_webserver":
                        type = "kaiju_webserver"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split("\t")[1].strip()) > 0 and line.split("\t")[-1].strip() not in unknown:
                                    results_dict["kaiju_webserver"][f][line.split("\t")[-1].strip()]= {"Value":float(line.split("\t")[1].strip()), "Alternatives":[]}
                    elif program == "kaiju":
                        type = "kaiju"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split("\t")[1].strip()) > 0:
                                results_dict["kaiju"][f][line.split("\t")[-1].strip()] = {"Value":float(line.split("\t")[
                                    1].strip()), "Alternatives":[]}
                    elif program == "centrifuge":
                        type = "centrifuge"
                        for line in result_input:
                            if line.split("\t")[2] == "species" and float(line.split("\t")[-1].strip()) > 0:
                                results_dict["centrifuge"][f][line.split("\t")[0].strip()] = {"Value":float(line.split("\t")[
                                    -1].strip())*100, "Alternatives":[]}
                    elif program == "bracken":
                        type = "bracken"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif float(line.split("\t")[-1].strip()) > 0:
                                results_dict["bracken"][f][line.split("\t")[0].strip()] = {"Value":float(line.split("\t")[-1].strip()) * 100, "Alternatives":[]}
                    elif program == "nabas":
                        type = "nabas"
                        header = True
                        for line in result_input:
                            try:
                                if header:
                                    header = False
                                elif str(line.split("\t")[9]).rstrip("%") != "0.000":
                                    fajnev = line.split("\t")[7].strip()
                                    fajnev = "".join(fajnev.split("["))
                                    fajnev = "".join(fajnev.split("]"))
                                    results_dict["nabas"][f][fajnev] = {"Value":float(str(
                                        line.split("\t")[9]).rstrip("%")), "Alternatives":[]}
                            except:
                                print(line)
                                continue
                    elif program == "diamond":
                        type = "diamond"
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif "virus" not in line.split("\t")[1] and "phage" not in line.split("\t")[1] and line.split("\t")[1] not in unknown:
                                if line.split("\t")[1] not in list(results_dict["diamond"]):
                                    results_dict["diamond"][f][line.split("\t")[1]] = {"Value" :float(line.split("\t")[3].strip()), "Alternatives":[]}
                                else:
                                    results_dict["diamond"][f][line.split("\t")[1]]["Value"] += float(line.split("\t")[3].strip())
                    """elif program == "diamond":
                        header = True
                        for line in result_input:
                            if header:
                                header = False
                            elif line.split("\t")[2].rstrip() == "species" or line.split("\t")[2].rstrip() == "strain" :
                                results_dict["diamond"][f][line.split("\t")[1]] = str(line.split("\t")[4].strip())"""
    results_dict = recalculate_percentage(results_dict)
    calculate_jaccard_distance(results_dict)
    
def recalculate_percentage(results_dict):
    for tipus in results_dict:
        for f in results_dict[tipus]:
        	szum = 0.0
        	for faj in results_dict[tipus][f]:
        		try:
        			szum += results_dict[tipus][f][faj]["Value"]
        		except:
        			print(tipus)
        			print(results_dict[tipus][f][faj])
        	if szum < 99:
		        a = 0
		        for faj in results_dict[tipus][f]:
		            results_dict[tipus][f][faj]["Value"] = results_dict[tipus][f][faj]["Value"] / szum * 100
    return results_dict

def calculate_jaccard_distance(result_dict):
	jaccard_distances = pd.DataFrame(index = ["GOTTCHA", "CLARK", "Kaiju", "Bracken", "Kaiju - webserver","MetaPhlAn 3", "Kraken 2", "Centrifuge", "DIAMOND"])
	fajszamok = pd.DataFrame(index = ["GOTTCHA", "CLARK", "Kaiju", "Bracken", "Kaiju - webserver","MetaPhlAn 3", "Kraken 2", "Centrifuge", "DIAMOND"])
	for class1 in result_dict:
		distances = []
		fajok = []
		for class2 in result_dict:
			s = []
			if class1 == class2:
				distances.append(0)
				for minta in result_dict[class1]:
					s.append(len(list(result_dict[class1][minta].keys())))
				fajok.append(statistics.mean(s))
			else:
				mintankenti_tavolsag = []
				for minta1 in result_dict[class1]:
					for minta2 in result_dict[class2]:
						if minta1 == minta2:
							unio = len(list(result_dict[class1][minta1].keys())) + len(list(result_dict[class2][minta2].keys()))
							metszet = len(set(list(result_dict[class1][minta1].keys())).intersection(list(result_dict[class2][minta2].keys())))
							if class1 == "metaphlan":
								for f2 in result_dict[class1][minta2]:
									for f1 in result_dict[class1][minta1]:
										if f2 in result_dict[class1][minta1][f1]["Alternatives"]:
											metszet += 1
											continue
									continue
							mintankenti_tavolsag.append(1-metszet/(unio-metszet))
							s.append(metszet)
				distances.append(statistics.mean(mintankenti_tavolsag))
				fajok.append(statistics.mean(s))
		jaccard_distances[class1] = distances
		fajszamok[class1] = fajok
	print(jaccard_distances)
	jaccard_distances.columns = ["GOTTCHA", "CLARK", "Kaiju", "Bracken", "Kaiju - webserver","MetaPhlAn 3", "Kraken 2", "Centrifuge", "DIAMOND"]
	fajszamok.columns = ["GOTTCHA", "CLARK", "Kaiju", "Bracken", "Kaiju - webserver","MetaPhlAn 3", "Kraken 2", "Centrifuge", "DIAMOND"]
	cmap = sns.cubehelix_palette(start=.6, rot=-.78, dark=0.3, light=.9, as_cmap=True)
	mask = np.triu(np.ones_like(jaccard_distances.corr(method="spearman",), dtype=bool))
	p1 = sns.heatmap(jaccard_distances, vmin = 0, vmax = 1, annot = True,cmap=cmap).set(title = "Pairwise Jaccard-distances")
	plt.show()
	p2 = sns.heatmap(fajszamok.where(np.tril(np.ones(fajszamok.shape)).astype(bool)), annot = True, center = 2000, cmap=cmap, fmt =".0f", norm = LogNorm()).set(title = "Number of species in common")
	plt.show()
	p3 = sns.heatmap(jaccard_distances.where(np.tril(np.ones(jaccard_distances.shape)).astype(bool)) , vmin = 0, vmax = 1, annot = True,cmap=cmap).set(title = "Pairwise Jaccard-distances")
	plt.show()
	find_common_species(result_dict)
	
def find_common_species(result_dict):
	mintalista = list(result_dict["kraken2"].keys())
	clas_lista = list(result_dict.keys())
	kimenet = {}
	for minta in mintalista:
		kimenet[minta] = []
		all_species = set()
		for clas in clas_lista:
			kimenet[minta].append(list(result_dict[clas][minta]))
			all_species.update(list(result_dict[clas][minta]))
		result = set(kimenet[minta][0])
		for s in kimenet[minta][1:]:
    			result.intersection_update(s)
		print(minta)
		print(result)
		print(1-len(result)/len(all_species))
			

read_results(argv[1])
