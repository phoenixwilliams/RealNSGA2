package com.example.realnsga2;

import java.util.ArrayList;
import java.util.Arrays;

public final class ProblemUtils {

    public static int ZDTObjectives(){return 2;}

    public static ArrayList<Double> ZDT1(ArrayList<Double> decisionVariables)
    {
        double f1 = decisionVariables.get(0);
        double sum =0;
        for (int i=1; i<decisionVariables.size(); i++)
        {
            sum += decisionVariables.get(i);
        }
        double g = 1.0 + (9.0/(decisionVariables.size()-1))*sum;
        double h = 1.0 - Math.sqrt(f1/g);
        double f2 = g*h;

        return new ArrayList<>(Arrays.asList(f1,f2));
    }

}
