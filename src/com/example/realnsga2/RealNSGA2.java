package com.example.realnsga2;

import java.nio.file.Watchable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

               //while(parent2.getGenotype().equals(parent1.getGenotype()))
               //{
               //    parent2 = matingPool.get(random.nextInt(matingPoolSize));
               //}

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


    public static ArrayList<Solution> nsga2()
    {
        int variableNumber = 10;
        int populationSize = 100;
        int iterations = 5000;
        int matingPoolSize = 50;
        double mutationProbability = 1/variableNumber;
        double reproductionProbability = 0.1;

        int nc = 20;
        int nm = 20;
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
           //System.out.println(Integer.toString(i));
           NSGA2Utils.EvaluatePopulationZDT6(population);
           NSGA2Utils.SetPopulationCrowdingDistance(population, ProblemUtils.ZDTObjectives());
           childPopulation = generateChildPopulation(population, matingPoolSize,mutationProbability,
                                                           reproductionProbability, nc, nm,lowerBounds,upperBounds);


            NSGA2Utils.EvaluatePopulationZDT6(childPopulation);
            NSGA2Utils.SetPopulationCrowdingDistance(childPopulation, ProblemUtils.ZDTObjectives());

            population.addAll(childPopulation);

            ///Elitise Selection
           population = NSGA2Utils.ElitistSelection(population);
           NSGA2Utils.ResetPopulationAttributes(population);
       }
        return population;
       //long endTime = System.currentTimeMillis();
       //float duration = (endTime - startTime)/1000F;

       //NSGA2Utils.EvaluatePopulationZDT2(population);
       //ArrayList<Solution> non_dominated_front = new ArrayList<>();
       //ArrayList<ArrayList<Double>> non_dominated_points = new ArrayList<>();
       //int domCount;

       //Collections.sort(population, new SolutionFitnessComparator(1));


       //for (int i=population.size()-1; i>0; i--) {
       //    domCount = NSGA2Utils.GetDominatedCount(i, population);
       //    if (domCount==0)
       //    {
       //        non_dominated_front.add(population.get(i));
       //        non_dominated_points.add(population.get(i).getFitness());
       //    }
       //    //System.out.println(Arrays.toString(population.get(i).getFitness().toArray()));
       //    //System.out.println(Arrays.toString(population.get(i).getGenotype().toArray()));
       //}
        //ArrayList<ArrayList<Double>> populationPoints = AnalysisUtils.getFitnessPoints(population);
        //System.out.println("non-dominated set size:" + Integer.toString(non_dominated_front.size())+" process took:"+Float.toString(duration)+"seconds");
        //AnalysisUtils.generateDatFile(population, "non-dominated-pop");

        //Calculate IGD
        //ArrayList<Solution> paretoFrontSolutions = NSGA2Utils.InitialPopulation(2, 100000);
        //NSGA2Utils.EvaluatePopulationParetoZDT1(paretoFrontSolutions);
        //AnalysisUtils.generateDatFile(paretoFrontSolutions, "pareto-front");
        //ArrayList<ArrayList<Double>> paretoFrontPoints = AnalysisUtils.getFitnessPoints(paretoFrontSolutions);
        //Double IDG = AttainmentUtils.IGD(populationPoints, non_dominated_points);
        //System.out.println("IGD: "+Double.toString(IDG));


    }

    public static void main(String[] args)
    {
        int runs = 50;
        int domCount;
        ArrayList<Solution> allSolutions = new ArrayList<>();
        ArrayList<Solution> nonDominatedSolutions = new ArrayList<>();

        for (int i=0; i<runs;i++)
        {
            System.out.println(Integer.toString(i));
            allSolutions.addAll(nsga2());
        }

        NSGA2Utils.EvaluatePopulationZDT6(allSolutions);
        for (int i=0; i<allSolutions.size(); i++)
        {
            domCount = NSGA2Utils.GetDominatedCount(i, allSolutions);
            if (domCount == 0) {
                nonDominatedSolutions.add(allSolutions.get(i));
            }
        }
        Collections.sort(nonDominatedSolutions, new HypervolumeFitnessComparator(1));
        ArrayList<ArrayList<Double>> non_dominated_points = AnalysisUtils.getFitnessPoints(nonDominatedSolutions);
        NSGA2Utils.EvaluatePopulationZDT6(nonDominatedSolutions);
        AnalysisUtils.generateDatFile(nonDominatedSolutions, "non-dominated-pop");
        //Calculate IGD
        ArrayList<Solution> paretoFrontSolutions = NSGA2Utils.InitialPopulation(30, 10000);
        NSGA2Utils.EvaluatePopulationParetoZDT6(paretoFrontSolutions);
        ArrayList<Solution> truePF = new ArrayList<>();

        for (int i=0;i<paretoFrontSolutions.size();i++)
        {
            domCount = NSGA2Utils.GetDominatedCount(i, paretoFrontSolutions);
            if (domCount == 0) {
                //System.out.println(Integer.toString(i));
                truePF.add(paretoFrontSolutions.get(i));
            }
        }

        Collections.sort(truePF, new HypervolumeFitnessComparator(1));
        AnalysisUtils.generateDatFile(truePF, "pareto-front");
        ArrayList<ArrayList<Double>> paretoFrontPoints = AnalysisUtils.getFitnessPoints(truePF);
        Double IDG = AttainmentUtils.IGD(paretoFrontPoints, non_dominated_points);
        System.out.println("IGD: "+Double.toString(IDG));

        ArrayList<Double> worstPoint = new ArrayList<>(Arrays.asList(10.0,10.0));
        Double hypervolume = AttainmentUtils.computeHypervolume(non_dominated_points, worstPoint);
        System.out.println("Hypervolume: "+Double.toString(hypervolume));

        Double paretoHypervolume = AttainmentUtils.computeHypervolume(paretoFrontPoints, worstPoint);
        System.out.println("Hypervolume: "+Double.toString(paretoHypervolume));

        System.out.println("Normalised Hypervolume:"+Double.toString(hypervolume/paretoHypervolume));

    }


}
