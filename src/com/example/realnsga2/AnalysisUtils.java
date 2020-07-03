package com.example.realnsga2;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public final class AnalysisUtils {

    public static void generateDatFileRealSol(ArrayList<Solution> population, String filename)
    {
        try{
            FileWriter myWriter = new FileWriter(filename+".dat");

            for (Solution sol:population)
            {
                myWriter.write(Double.toString(sol.getFitness(0))+" "+ Double.toString(sol.getFitness(1))+"\n");
            }
            myWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}



