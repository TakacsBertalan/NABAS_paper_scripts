/*                                                                                                                                                  
NNNNNNNN        NNNNNNNN               AAA               BBBBBBBBBBBBBBBBB               AAA                 SSSSSSSSSSSSSSS                      
N:::::::N       N::::::N              A:::A              B::::::::::::::::B             A:::A              SS:::::::::::::::S                     
N::::::::N      N::::::N             A:::::A             B::::::BBBBBB:::::B           A:::::A            S:::::SSSSSS::::::S                     
N:::::::::N     N::::::N            A:::::::A            BB:::::B     B:::::B         A:::::::A           S:::::S     SSSSSSS       +++++++       
N::::::::::N    N::::::N           A:::::::::A             B::::B     B:::::B        A:::::::::A          S:::::S                   +:::::+       
N:::::::::::N   N::::::N          A:::::A:::::A            B::::B     B:::::B       A:::::A:::::A         S:::::S                   +:::::+       
N:::::::N::::N  N::::::N         A:::::A A:::::A           B::::BBBBBB:::::B       A:::::A A:::::A         S::::SSSS          +++++++:::::+++++++ 
N::::::N N::::N N::::::N        A:::::A   A:::::A          B:::::::::::::BB       A:::::A   A:::::A         SS::::::SSSSS     +:::::::::::::::::+ 
N::::::N  N::::N:::::::N       A:::::A     A:::::A         B::::BBBBBB:::::B     A:::::A     A:::::A          SSS::::::::SS   +:::::::::::::::::+ 
N::::::N   N:::::::::::N      A:::::AAAAAAAAA:::::A        B::::B     B:::::B   A:::::AAAAAAAAA:::::A            SSSSSS::::S  +++++++:::::+++++++ 
N::::::N    N::::::::::N     A:::::::::::::::::::::A       B::::B     B:::::B  A:::::::::::::::::::::A                S:::::S       +:::::+       
N::::::N     N:::::::::N    A:::::AAAAAAAAAAAAA:::::A      B::::B     B:::::B A:::::AAAAAAAAAAAAA:::::A               S:::::S       +:::::+       
N::::::N      N::::::::N   A:::::A             A:::::A   BB:::::BBBBBB::::::BA:::::A             A:::::A  SSSSSSS     S:::::S       +++++++       
N::::::N       N:::::::N  A:::::A               A:::::A  B:::::::::::::::::BA:::::A               A:::::A S::::::SSSSSS:::::S                     
N::::::N        N::::::N A:::::A                 A:::::A B::::::::::::::::BA:::::A                 A:::::AS:::::::::::::::SS                      
NNNNNNNN         NNNNNNNAAAAAAA                   AAAAAAABBBBBBBBBBBBBBBBBAAAAAAA                   AAAAAAASSSSSSSSSSSSSSS                        
                                             developed by Gábor Jaksa and Bertalan Takács
 */
package hu.deltabio.nabas;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import hu.deltabio.core.Core_Utils;
import hu.deltabio.core.io.SimpleFileReader;
import hu.deltabio.core.bio.Taxonomy;

/**
 *
 * @author Gábor Jaksa
 */
public class NCBI_Taxonomy {

    HashMap<Integer, Node> nodes = new HashMap<>();
    HashMap<Integer, Integer> alias = new HashMap<>();
    HashMap<Integer, String> names = new HashMap<>();
    public static String taxonomyFolder = null;

    public void getNames() {
        String baseDir;
        if (NCBI_Taxonomy.taxonomyFolder == null) {
            baseDir = Core_Utils.getRelativeDataFileAsString("Taxonomy" + File.separator);
        } else {
            baseDir = NCBI_Taxonomy.taxonomyFolder;
        }
        SimpleFileReader r = new SimpleFileReader(baseDir + "names.dmp.gz", SimpleFileReader.FileReaderWriterType.GZ);
        names.clear();
        while (true) {
            String line = r.readLine();
            if (line == null) {
                r.close();
                break;
            }
            String temp[] = line.split(Pattern.quote("\t|\t"));
            int id = Integer.parseInt(temp[0]);
            if (temp[3].contains("scientific name")) {
                names.put(id, temp[1]);
            }
        }
    }

    public void getNodes() {
        String baseDir;
        if (NCBI_Taxonomy.taxonomyFolder == null) {
            baseDir = Core_Utils.getRelativeDataFileAsString("Taxonomy" + File.separator);
        } else {
            baseDir = NCBI_Taxonomy.taxonomyFolder;
        }
        SimpleFileReader r = new SimpleFileReader(baseDir + "nodes.dmp.gz", SimpleFileReader.FileReaderWriterType.GZ);
        nodes.clear();
        while (true) {
            String line = r.readLine();
            if (line == null) {
                r.close();
                break;
            }
            String temp[] = line.split(Pattern.quote("\t|\t"));
            int id = Integer.parseInt(temp[0]);
            int parent = Integer.parseInt(temp[1]);
            nodes.put(id, new Node(id, parent, temp[2]));
        }
        if (new File(baseDir + "merged.dmp.gz").exists()) {
            r = new SimpleFileReader(baseDir + "merged.dmp.gz", SimpleFileReader.FileReaderWriterType.GZ);
            alias.clear();
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    r.close();
                    break;
                }
                String temp[] = (line + "\t").split(Pattern.quote("\t|\t"));
                int id = Integer.parseInt(temp[0]);
                int al = Integer.parseInt(temp[1].trim());
                alias.put(id, al);
            }
        }
    }

    public String printLineage(ArrayList<Node> nodes) {
        StringBuilder b = new StringBuilder();
        for (Node node : nodes) {
            if (node != null) {
                b.append(findName(node.id) + " > ");
            }
        }
        return b.toString();
    }

    public String[] getLineageArrayNull(ArrayList<Node> nodes) {
        ArrayList<String> process = new ArrayList<>();
        process.add(Taxonomy.species);
        process.add(Taxonomy.genus);
        process.add(Taxonomy.family);
        process.add(Taxonomy.order);
        process.add(Taxonomy.clas);
        process.add(Taxonomy.phylum);
        process.add(Taxonomy.superkingdom);

        String[] ret = new String[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = getLineageNull(nodes, process.get(6 - i), 6 - i, process);
        }
        if(ret[0].equals("null")) {
            return getLineageArrayNullKingdom(nodes);
        } 
        return ret;
    }

    public String[] getLineageArray(ArrayList<Node> nodes) {
        ArrayList<String> process = new ArrayList<>();
        process.add(Taxonomy.species);
        process.add(Taxonomy.genus);
        process.add(Taxonomy.family);
        process.add(Taxonomy.order);
        process.add(Taxonomy.clas);
        process.add(Taxonomy.phylum);
        process.add(Taxonomy.superkingdom);

        String[] ret = new String[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = getLineage(nodes, process.get(6 - i), 6 - i, process);
        }
        return ret;
    }
    
    public String[] getLineageArrayNullKingdom(ArrayList<Node> nodes) {
        ArrayList<String> process = new ArrayList<>();
        process.add(Taxonomy.species);
        process.add(Taxonomy.genus);
        process.add(Taxonomy.family);
        process.add(Taxonomy.order);
        process.add(Taxonomy.clas);
        process.add(Taxonomy.phylum);
        process.add(Taxonomy.kingdom);

        String[] ret = new String[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = getLineageNull(nodes, process.get(6 - i), 6 - i, process);
        }
        return ret;
    }

    public String getLineageNull(ArrayList<Node> nodes, String rank, int index, ArrayList<String> process) {
        for (Node node : nodes) {
            if (node != null) {
                if (node.getRank().equals(rank)) {
                    return findName(node.id);
                }
            }
        }
        for (int i = index + 1; i < 7; i++) {
            String next = getLineage(nodes, process.get(i));
            if (!next.equals("?")) {
                return "null";
            }
        }

        return "null";
    }

    public String getLineage(ArrayList<Node> nodes, String rank, int index, ArrayList<String> process) {
        for (Node node : nodes) {
            if (node != null) {
                if (node.getRank().equals(rank)) {
                    return findName(node.id);
                }
            }
        }
        for (int i = index + 1; i < 7; i++) {
            String next = getLineage(nodes, process.get(i));
            if (!next.equals("?")) {
                return "Unspecified " + next;
            }
        }

        return "?";
    }

    public String getLineage(ArrayList<Node> nodes, String rank) {
        for (Node node : nodes) {
            if (node != null) {
                if (node.getRank().equals(rank)) {
                    return findName(node.id);
                }
            }
        }

        return "?";
    }

    public String findName(int id) {
        return names.get(id);
    }

    public int findExactID(String name) {

        for (Integer k : names.keySet()) {
            if (names.get(k).equals(name)) {
                return k;
            }
        }

        return -1;
    }

    public int findID(String name) {
        if (name.endsWith("sp.")) {
            for (Integer k : names.keySet()) {
                if (names.get(k).equals(name.substring(0, name.length() - 4))) {
                    return k;
                }
            }
        } else {
            for (Integer k : names.keySet()) {
                if (names.get(k).startsWith(name)) {
                    return k;
                }
            }
        }
        return -1;
    }

    public ArrayList<Node> findNodes(int taxid) {
        int find = taxid;
        ArrayList<Node> lineageNodes = new ArrayList<>();
        while (true) {
            if (alias.containsKey(find)) {
                find = alias.get(find);
            }
            Node curr = nodes.get(find);
            System.out.println(find + " " + curr.rank + " " + findName(find));
            lineageNodes.add(curr);
            if (find == 1 && curr.parentID == 1) {
                break;
            }
            if (curr == null) {
                find = 1;
            } else {
                find = curr.parentID;
            }
        }
        return lineageNodes;
    }

    public class Node {

        int id;
        int parentID;
        String rank;

        public Node(int id, int parentID, String rank) {
            this.parentID = parentID;
            this.rank = rank;
            this.id = id;
        }

        public String getRank() {
            return rank;
        }

        public int getParentID() {
            return parentID;
        }

        public int getId() {
            return id;
        }
    }
}
