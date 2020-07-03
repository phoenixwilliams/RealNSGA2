package com.example.realnsga2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RealNSGA2 {


    public static ArrayList<Solution> generateChildPopulation(ArrayList<Solution> population, int matingPoolSize,
                                                              double mutProb, double repProb, int nc, int nm,
                                                              ArrayList<Double> lowerBounds,
                                                              ArrayList<Double> upperBounds)
    {
        ArrayList<Solution> matingPool = new ArrayList<>();
        ArrayList<Solution> childPopulation = new ArrayList<>();
        Random random = new Random();
        int p1,p2;
        double reproduce;

        for (int i=0; i<matingPoolSize; i++)
        // Generate Mating Pool
        {
            p1 = random.nextInt(population.size());
            p2 = random.nextInt(population.size());
            matingPool.add(NSGA2Utils.TournamentSelection(population.get(p1), population.get(p2)));
        }
        Solution parent1,parent2;
        ArrayList<ArrayList<Double>> offspringGenotypes;
        while(childPopulation.size()<population.size())
        {
            //generate crossover children
            reproduce = random.nextDouble();
            //System.out.println(Double.toString(reproduce));
            if (reproduce<repProb)
            {
                //System.out.println("REPRODUCED!"+ Double.toString(reproduce));
                childPopulation.add(matingPool.get(random.nextInt(matingPoolSize)));
            }else
            {
                parent1 = matingPool.get(random.nextInt(matingPoolSize));
                parent2 = matingPool.get(random.nextInt(matingPoolSize));

                while(parent2.getGenotype().equals(parent1.getGenotype()))
                {
                    parent2 = matingPool.get(random.nextInt(matingPoolSize));
                }

                offspringGenotypes = NSGA2Utils.SBXCrossover(parent1.getGenotype(), parent2.getGenotype(), nc,
                                                                lowerBounds, upperBounds);



                offspringGenotypes.set(0, NSGA2Utils.polyMutation(offspringGenotypes.get(0),nm,lowerBounds,upperBounds
                                                                                ,mutProb));
                offspringGenotypes.set(1, NSGA2Utils.polyMutation(offspringGenotypes.get(1),nm,lowerBounds,upperBounds
                        ,mutProb));

                childPopulation.add(new Solution(offspringGenotypes.get(0)));
                childPopulation.add(new Solution(offspringGenotypes.get(1)));
            }
        }
        return childPopulation;
    }


    public static void nsga2()
    {
        int variableNumber = 2;
        int populationSize = 100;
        int iterations = 100;
        int matingPoolSize = 50;
        double mutationProbability = 0.0;
        double reproductionProbability = 0.0;

        int nc = 20;
        int nm = 5;
        ArrayList<Double> lowerBounds = new ArrayList<>();
        ArrayList<Double> upperBounds = new ArrayList<>();

        for (int i=0;i<variableNumber;i++)
        {
            lowerBounds.add(0.0);
            upperBounds.add(1.0);
        }

        long startTime = System.currentTimeMillis();
       ArrayList<Solution> population = NSGA2Utils.InitialPopulation(variableNumber,populationSize);
       ArrayList<Solution> childPopulation;

       for (int i=0;i<iterations;i++)
       {
           NSGA2Utils.EvaluatePopulationZDT1(population);
           NSGA2Utils.SetPopulationCrowdingDistance(population, ProblemUtils.ZDTObjectives());
           childPopulation = generateChildPopulation(population, matingPoolSize,mutationProbability,
                                                           reproductionProbability, nc, nm,lowerBounds,upperBounds);



            NSGA2Utils.EvaluatePopulationZDT1(childPopulation);
            NSGA2Utils.SetPopulationCrowdingDistance(childPopulation, ProblemUtils.ZDTObjectives());

           //for (Solution sol:population)
           //{
           //    System.out.println(Arrays.toString(sol.getGenotype().toArray()));
           //}
           //System.out.println("------------------------------------");
           //for (Solution sol:childPopulation)
           //{
           //    System.out.println(Arrays.toString(sol.getGenotype().toArray()));
           //}
           //System.out.println("------------------------------------");

            population.addAll(childPopulation);

            ///Elitise Selection
           population = NSGA2Utils.ElitistSelection(population);
           NSGA2Utils.ResetPopulationAttributes(population);
           //System.out.println(Integer.toString(population.size()));

       }

        long endTime = System.currentTimeMillis();
        float duration = (endTime - startTime)/1000F;

        NSGA2Utils.EvaluatePopulationZDT1(population);
        ArrayList<Solution> non_dominated_front = new ArrayList<>();
        int domCount;


        for (int i=0; i<population.size(); i++) {
            domCount = NSGA2Utils.GetDominatedCount(i, population);
            if (domCount==0)
            {
                non_dominated_front.add(population.get(i));
            }
            System.out.println(Arrays.toString(population.get(i).getFitness().toArray()));
            System.out.println(Arrays.toString(population.get(i).getGenotype().toArray()));
        }

        System.out.println("non-dominated set size:" + Integer.toString(non_dominated_front.size())+" process took:"+Float.toString(duration)+"seconds");
        AnalysisUtils.generateDatFileRealSol(non_dominated_front, "non-dominated-pop");

    }

    public static void main(String[] args)
    {
        nsga2();

        //ArrayList<Solution> pop = NSGA2Utils.InitialPopulation(2, 2);
//
        //ArrayList<ArrayList<Double>> offsprings = NSGA2Utils.SBXCrossover(pop.get(0).getGenotype(),pop.get(1).getGenotype(),2);
//
        //System.out.println(Arrays.toString(pop.get(0).getGenotype().toArray()));
        //System.out.println(Arrays.toString(pop.get(1).getGenotype().toArray()));
//
        //System.out.println(Arrays.toString(offsprings.get(0).toArray()));
        //System.out.println(Arrays.toString(offsprings.get(1).toArray()));

        //NSGA2Utils.EvaluatePopulationZDT1(pop);
        //AnalysisUtils.generateDatFileRealSol(pop, "objective_space");


    }


}
