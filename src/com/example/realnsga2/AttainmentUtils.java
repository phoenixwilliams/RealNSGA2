package com.example.realnsga2;

import java.util.ArrayList;

public final class AttainmentUtils {

    public static Double computeHypervolume(ArrayList<ArrayList<Double>> nonDominatedSet, ArrayList<Double> w)
    {
        double tempY = w.get(1);
        double hypervolume = 0.0;
        double tempWidth, tempLength =0.0;

        for (int i=0; i<nonDominatedSet.size(); i++)
        {

            if (i==0 || (i>0 && !nonDominatedSet.get(i).equals(nonDominatedSet.get(i-1)))) {
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
}

