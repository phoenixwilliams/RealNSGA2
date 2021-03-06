package com.example.realnsga2;

import java.util.ArrayList;
import java.util.Arrays;

public final class AttainmentUtils {

    public static Double computeHypervolume(ArrayList<ArrayList<Double>> nonDominatedSet, ArrayList<Double> w)
    {
       // System.out.println("w:"+Arrays.toString(w.toArray()));
        double tempY = w.get(1);
        double hypervolume = 0.0;
        double tempWidth, tempLength =0.0;

        for (int i=0; i<nonDominatedSet.size(); i++)
        {

            if (i==0 || (i>0 && !nonDominatedSet.get(i).equals(nonDominatedSet.get(i-1)))) {
                //System.out.println(Double.toString(tempY)+":"+ Arrays.toString(w.toArray()));
                tempLength = w.get(0) - nonDominatedSet.get(i).get(0);
                tempWidth = tempY - nonDominatedSet.get(i).get(1);

                hypervolume += tempLength * tempWidth;
                tempY = nonDominatedSet.get(i).get(1);
            }

        }


        return hypervolume;
    }



    public static ArrayList<Solution> computeAttainedPopulationRealSol(ArrayList<Solution> population, int popSize,int variableNumber)
    {
        ArrayList<Solution> randomPopulation = NSGA2Utils.InitialPopulation(popSize,variableNumber);
        NSGA2Utils.EvaluatePopulationZDT1(randomPopulation);
        ArrayList<Solution> attainedPopulation = new ArrayList<>();

        for (Solution sol: randomPopulation)
        {
            for (Solution ndSol: population)
            {
                if (NSGA2Utils.dominatesMinOpt(ndSol.getFitness(),sol.getFitness()))
                {
                    attainedPopulation.add(sol);
                    break;
                }
            }
        }
        return attainedPopulation;
    }

    public static Double IGD(ArrayList<ArrayList<Double>> paretoPoints, ArrayList<ArrayList<Double>> population)
    {
        Double closestDist;
        Double dist = 0.0;
        Double tempDist;

        for (int i=0; i<paretoPoints.size();i++)
        {
            closestDist = Double.POSITIVE_INFINITY;

            for (int j=0;j<population.size();j++)
            {
                tempDist = euclideanDist(paretoPoints.get(i), population.get(j));
                if (tempDist<closestDist)
                {
                    closestDist = tempDist;
                }
            }
            //System.out.println(Double.toString(closestDist));
            dist+=closestDist;
        }
        return dist/(double)paretoPoints.size();
    }

    public static Double euclideanDist(ArrayList<Double> a, ArrayList<Double> b)
    {
        Double difference = 0.0;

        for (int i=0;i<a.size();i++)
        {
            difference += Math.pow(a.get(i)-b.get(i), 2);
        }
        //System.out.println(Double.toString(Math.sqrt(difference)));
        return Math.sqrt(difference);

    }
}

