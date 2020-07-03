package com.example.realnsga2;

import java.sql.Array;
import java.util.*;

public final class NSGA2Utils {

    public static ArrayList<Double> InitialSolution(int variableNumber)
    {
        Random rand = new Random();
        ArrayList<Double> genotype = new ArrayList<>();

        for (int i=0;i<variableNumber;i++)
        {
            genotype.add(rand.nextDouble());
        }
        return genotype;
    }

    public static ArrayList<Solution> InitialPopulation(int variableNumber, int popSize)
    {
        ArrayList<Solution> population = new ArrayList<>();

        for (int i=0; i<popSize; i++)
        {
            population.add(new Solution(InitialSolution(variableNumber)));
        }
        return population;
    }

    public static void EvaluatePopulationZDT1(ArrayList<Solution> population)
    {
        for (Solution sol:population)
        {
            sol.setFitness(ProblemUtils.ZDT1(sol.getGenotype()));
        }
    }

    public static void SetPopulationCrowdingDistance(ArrayList<Solution> population, int objectives)
    {
        double fmin,fmax,distanceDenom,distanceNum;

        for (int i=0;i<objectives;i++)
        {
            int finalI = i;

            //Orders Population in ascending order of the corresponding fitness

            Collections.sort(population, new SolutionFitnessComparator(i));


            population.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            population.get(population.size()-1).setCrowdingDistance(Double.POSITIVE_INFINITY);
            fmin = population.get(0).getFitness(i);
            fmax = population.get(population.size()-1).getFitness(i);
            distanceDenom = fmax-fmin;

            for (int j=1;j<population.size()-1;j++)
            {
                distanceNum = population.get(j+1).getFitness(i) - population.get(j-1).getFitness(i);
                population.get(j).increaseCrowdingDistance(distanceNum/distanceDenom);
            }

        }
    }

    public static boolean dominatesMinOpt(ArrayList<Double> fitnessA, ArrayList<Double> fitnessB)
    {
        int equalLess = 0;
        int explicitLess = 0;


        for (int i=0; i<fitnessA.size(); i++)
        {
            if (fitnessA.get(i) <= fitnessB.get(i)) equalLess +=1;
            if (fitnessA.get(i) < fitnessB.get(i)) explicitLess +=1;
        }

        if (equalLess==fitnessA.size() && explicitLess>0 ) {return true;}
        else return false;
    }

    public static Solution TournamentSelection(Solution a, Solution b)
    {
        if (dominatesMinOpt(a.getFitness(),b.getFitness()))
        {
            return a;
        }
        else if(dominatesMinOpt(a.getFitness(),b.getFitness()))
        {
            return b;
        }
        else if (a.getCrowdingDistance()>b.getCrowdingDistance())
        {
            return a;
        }
        else{

            if (new Random().nextDouble() < 0.5){
                return a;
            }else{
                return b;
            }
        }

    }

    public static ArrayList<ArrayList<Double>> SBXCrossover(ArrayList<Double> parent1Genotype, ArrayList<Double> parent2Genotype,
                                                            int nc, ArrayList<Double> lowerBounds,
                                                            ArrayList<Double> upperBounds)
    {
        Random random = new Random();
        double ui,bqui;
        ArrayList<Double> offspring1Genotype = new ArrayList<>();
        ArrayList<Double> offspring2Genotype = new ArrayList<>();
        double x1,x2;

        for (int i=0;i<parent1Genotype.size(); i++)
        {
            ui = random.nextDouble();
            //System.out.println("UI:"+Double.toString(ui));
            if (ui<=0.5)
            {
                bqui = Math.pow(2.0*ui, 1.0/(nc+1.0));
            }else{
                bqui = Math.pow(1.0/(2.0*(1.0-ui)), 1.0/(nc+1.0));
            }
            //System.out.println("BQUI:"+Double.toString(bqui));


            x1=0.5*((1.0+bqui)*parent1Genotype.get(i)+(1.0-bqui)*parent2Genotype.get(i));
            x2=0.5*((1.0-bqui)*parent1Genotype.get(i)+(1.0+bqui)*parent2Genotype.get(i));

            if (x1<lowerBounds.get(i))
            {
                x1 = lowerBounds.get(i);
            }
            if (x1>upperBounds.get(i))
            {
                x1 = upperBounds.get(i);
            }
            if (x2<lowerBounds.get(i))
            {
                x2 = lowerBounds.get(i);
            }
            if (x2>upperBounds.get(i))
            {
                x2 = upperBounds.get(i);
            }

            offspring1Genotype.add(x1);
            offspring2Genotype.add(x2);



        }
        return new ArrayList(Arrays.asList(offspring1Genotype,offspring2Genotype));
    }

    public static ArrayList<Double> polyMutation(ArrayList<Double> parentGenotype, int nm, ArrayList<Double> lowerBounds
                                                                ,ArrayList<Double> upperBounds, double mutProb)
    {
        Random random = new Random();
        double mi,ri, di;
        ArrayList<Double> offspringGenotype = new ArrayList<>();

        for (int i=0; i<parentGenotype.size(); i++)
        {
            mi = random.nextDouble();
            ri = random.nextDouble();
            if (mi < mutProb && ri < 0.5)
            {
                di = Math.pow(2.0*ri, 1/(nm+1))-1.0;
            }
            else if(mi < mutProb && ri >= 0.5)
            {
                di = 1.0 - Math.pow(2.0*(1-ri), 1/(nm+1));
            }
            else{
                di = 0.0;
            }
            offspringGenotype.add(parentGenotype.get(i) + (upperBounds.get(i)-lowerBounds.get(i))*di);
        }
        return offspringGenotype;
    }

    public static Integer GetDominatedCount(int solution, ArrayList<Solution> population)
    {
        int dominatedCount = 0;
        Solution checkedSol = population.get(solution);
        for (int i=0; i<population.size();i++)
        {
            if (i!=solution && dominatesMinOpt(population.get(i).getFitness(), checkedSol.getFitness()))
            {
                dominatedCount+=1;
            }
        }
        return dominatedCount;

    }


    public static ArrayList<Solution> ElitistSelection(ArrayList<Solution> combinedPopulation)
    {
        //Find dominated count
        double solutionsRemaining;
        ArrayList<Solution> nextGeneration = new ArrayList<>();
        ArrayList<Integer> dominatedCounts = new ArrayList<>();
        ArrayList<Solution> currentFront;

        for (int i=0; i<combinedPopulation.size(); i++) {
            dominatedCounts.add(GetDominatedCount(i, combinedPopulation));
        }

        while(nextGeneration.size()<(combinedPopulation.size()/2))
        {

            //get current front
            currentFront = new ArrayList<>();

            for (int i=0; i<combinedPopulation.size(); i++)
            {
                if (dominatedCounts.get(i)==0)
                {
                    currentFront.add(combinedPopulation.get(i));
                }
                dominatedCounts.set(i,dominatedCounts.get(i)-1);
            }

            if (currentFront.size()+nextGeneration.size()<(combinedPopulation.size()/2))
            {
                nextGeneration.addAll(currentFront);
            }else{
                solutionsRemaining = (combinedPopulation.size()/2) - nextGeneration.size();
                Collections.sort(currentFront,  new SolutionCrowdingComparator());
                //System.out.println(Integer.toString(combinedPopulation.size()/2));

                //System.out.println(Double.toString(solutionsRemaining)+"solutions left"+
                 //       Integer.toString(nextGeneration.size()));

                for (int j=0;j<solutionsRemaining;j++)
                {
                    nextGeneration.add(currentFront.get(currentFront.size()-1-j));
                }
            }
        }

        return nextGeneration;
    }

    public static void ResetPopulationAttributes(ArrayList<Solution> population)
    {
        for (Solution sol: population)
        {
            sol.resetCrowdingDistance();
            sol.resetFitness();
        }
    }

}
