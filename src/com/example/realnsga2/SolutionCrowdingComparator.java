package com.example.realnsga2;

import java.util.Comparator;

public class SolutionCrowdingComparator implements Comparator<Solution> {

    @Override
    public int compare(Solution o1, Solution o2) {
        return o1.getCrowdingDistance().compareTo(o2.getCrowdingDistance());
    }
}
