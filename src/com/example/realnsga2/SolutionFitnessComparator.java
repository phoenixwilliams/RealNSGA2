package com.example.realnsga2;

import java.util.Comparator;

public class SolutionFitnessComparator implements Comparator<Solution> {

    Integer objective;

    public SolutionFitnessComparator(Integer objective)
    {
        this.objective = objective;
    }

    @Override
    public int compare(Solution o1, Solution o2) {
        return o1.getFitness(this.objective).compareTo(o2.getFitness(this.objective));
    }
}
