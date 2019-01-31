package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import classes.Customer;
import classes.Depot;

public class Data {

    public static void ReadData(String filename) {
        clearOldValues();

        String line;
        int lineCounter = 0;
        int depotsCounter = 0;

        try {
            FileReader fileReader = new FileReader(filename);

            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                // Remove double space
                line = line.trim().replaceAll(" +", " ");
                String[] splitLine = line.split(" ");

                // Convert splitted line to integers
                int[] sLine = new int[splitLine.length];
                for(int i = 0; i < splitLine.length; i++) {
                    sLine[i] = Integer.parseInt(splitLine[i]);
                }

                // Read data about configuration into variables
                if(lineCounter == 0) {
                    Run.m = sLine[0];
                    Run.n = sLine[1];
                    Run.t = sLine[2];
                }
                // Read data about depots into an array
                else if(lineCounter <= Run.t) {
                    Run.depots.add(new Depot(sLine[0], sLine[1]));
                }
                // Read data about customers into an array
                else if(lineCounter <= (Run.n + Run.t)) {
                    Run.customers.add(new Customer(sLine[0], sLine[1], sLine[2], sLine[3], sLine[4]));
                }
                // Update data about depots
                else {
                    Run.depots.get(depotsCounter).setId(sLine[0]);
                    Run.depots.get(depotsCounter).setX(sLine[1]);
                    Run.depots.get(depotsCounter).setY(sLine[2]);
                    depotsCounter++;
                }

                lineCounter++;
            }

            bufferedReader.close();

            // Update customers and set nearest depot
            calculateClosestDepots();
            calculateClosestCustomer();

        }
        catch(FileNotFoundException ex) {
            System.out.println("Couldn't find file");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    private static void calculateClosestDepots(){
        for(Customer c : Run.customers){
            c.findNearestEndDepot();
        }
    }

    private static void calculateClosestCustomer(){
        for(Customer c : Run.customers){
            c.findNearestCustomer();
        }
    }
    private static void clearOldValues(){
        Run.customers.clear();
        Run.depots.clear();
            Run.t = 0;
            Run.m = 0;
            Run.n = 0;

    }

}