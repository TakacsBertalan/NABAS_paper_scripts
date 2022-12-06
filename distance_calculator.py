from sys import argv

result_dict = {}
fajok = set()
reference = argv[2]
			
with open(argv[1], "r") as inp:
	for line in inp:
		line = line.rstrip()
		classifier = line.split("\t")[0]
		species = line.split("\t")[1]
		abundance = line.split("\t")[2]
		fajok.add(species)
		if classifier not in result_dict:
			result_dict[classifier] = {}
		if species not in result_dict[classifier]:
			result_dict[classifier][species] = float(abundance)



			
def calculate_eucledian(result_dict):
	print("Eucledian distance")
	for classifier in result_dict:
		eucledian_distance = 0
		if classifier != reference:
			for species in result_dict[classifier]:
				try:
					eucledian_distance += (result_dict[classifier][species] - result_dict[reference][species])**2
				except:
					eucledian_distance += result_dict[classifier][species]**2
			print(classifier + " : " + str(eucledian_distance))
			
def calculate_manhattan(result_dict):
	print("Manhattan distance")
	for classifier in result_dict:
		manhattan_distance = 0
		if classifier != reference:

			for species in result_dict[classifier]:
				try:
					manhattan_distance += abs(result_dict[classifier][species] - result_dict[reference][species])
				except:
					manhattan_distance += result_dict[classifier][species]
			print(classifier + " : " + str(manhattan_distance))

def calculate_bray_curtis(result_dict):
	print("Bray-Curtis distance")
	for classifier in result_dict:
		csum = 0
		if classifier != reference:
			for species in result_dict[classifier]:
				if species in result_dict[reference]:
					if result_dict[classifier][species] < result_dict[reference][species]:
						csum += result_dict[classifier][species]
					else:
						csum += result_dict[reference][species]
			print(classifier + " : "  + str(1-csum/200))

calculate_eucledian(result_dict)
calculate_manhattan(result_dict)
calculate_bray_curtis(result_dict)
