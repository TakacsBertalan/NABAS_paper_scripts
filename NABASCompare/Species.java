/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hu.deltabio.nabas.compare;

import java.util.ArrayList;

/**
 *
 * @author deltagene
 */
public class Species {
    String kingdom;
    String phylum;
    String order;
    String family;
    String genus;
    String species;
    
    Double relativeAbundance;
    String[] bins;
    
    public Species(String species, Double ra){
    this.species = species;
    this.relativeAbundance = ra;
    }
    
    public Species(String genus, String species, double ra){
    this.genus = genus;
    this.species = species;
    this.relativeAbundance = ra;
    }
    
    public ArrayList<String> getLineage(){
        ArrayList<String> lineage = new ArrayList();
        lineage.add(this.kingdom);
        lineage.add(this.phylum);
        lineage.add(this.order);
        lineage.add(this.family);
        lineage.add(this.genus);
        lineage.add(this.species);
        return lineage;
    
    }
    @Override
    public String toString() {
        return this.species + ": " + this.relativeAbundance;
    }
    
}
