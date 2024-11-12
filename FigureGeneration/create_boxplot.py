# Import libraries
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sys import argv

def create_boxplot(input_file):
     
    # Collecting data

    data = pd.DataFrame(pd.read_excel(, index_col = 0))


    fig, axes = plt.subplots(3, 1, figsize =(7, 15)) 

    axes[0].set_title('F1 Score per classifier', fontsize=16)
    axes[1].set_title('Precision per classifier', fontsize=16)
    axes[2].set_title('Recall per classifier', fontsize=16)

    
    # Creating plot

    sns.boxplot(ax  = axes[0], y = data["F1 Score"], x = data["classifier"]).set(xlabel = None, ylim = [0,1])
    axes[0].set_ylabel('F1 Score', fontsize=18)

    sns.boxplot(ax  = axes[1], y = data["Precision"], x = data["classifier"]).set(xlabel = None, ylim = [0,1])
    axes[1].set_ylabel('Precision', fontsize=18)

    sns.boxplot(ax  = axes[2], y = data["Recall"], x = data["classifier"]).set(xlabel = None, ylim = [0,1])
    axes[2].set_ylabel('Recall', fontsize=18)

    axes[0].tick_params(axis='x', which='both', bottom=False, labelbottom=False)
    axes[1].tick_params(axis='x', which='both', bottom=False, labelbottom=False)

    for i in range(3):
        axes[i].tick_params(axis='x', which='major', labelsize=14)


    # show plot
    plt.show()
    
create_boxplot(argv[1])
