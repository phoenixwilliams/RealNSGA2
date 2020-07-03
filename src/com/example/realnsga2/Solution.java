package com.example.realnsga2;

import java.util.ArrayList;

public class Solution{

    private ArrayList<Double> fitness;
    private ArrayList<Double> genotype;
    private Double crowdingDistance;

    public Solution(ArrayList<Double> genotype)
    {
        this.genotype = genotype;
        crowdingDistance = 0.0;
    }

    public void setFitness(ArrayList<Double> fitness)
    {
        this.fitness = fitness;
    }
    public void setCrowdingDistance(Double crowdingDistance)
    {
        this.crowdingDistance = crowdingDistance;
    }
    public void increaseCrowdingDistance(Double crowdingDistance)
    {
        this.crowdingDistance+=crowdingDistance;
    }

    public ArrayList<Double> getGenotype(){return  this.genotype;}
    public Double getFitness(int i){return this.fitness.get(i);}
    public ArrayList<Double> getFitness(){return this.fitness;}
    public Double getCrowdingDistance(){return this.crowdingDistance;}
    public void resetCrowdingDistance(){this.crowdingDistance=0.0;}
    public void resetFitness(){this.fitness=new ArrayList<Double>();}

}
