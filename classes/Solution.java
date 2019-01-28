package classes;

import java.util.ArrayList;
import java.util.Random;

import main.Run;

public class Solution {

    ArrayList<Vehicle> vehicles = new ArrayList<>();
    double totalCost = 0; // Fitness score. Mulig ta 1/score


    public Solution() {

        initialize();

    }

    /*
     * Methods
     */
    Random r = new Random();

    public void testt() {
        boolean feasible = true;

        while(true) {
            feasible = true;

            for(int i = 0; i < Run.customers.size(); i++) {
                double distance[] = new double[100];

                for(int j = 0; j < Run.depots.size(); j++) {
                    distance[j] = PositionNode.distanceTo(Run.depots.get(j), Run.customers.get(i));
                    System.out.println(PositionNode.distanceTo(Run.depots.get(j), Run.customers.get(i)));
                }
            }
        }
    }

    public void initialize() {
        // Create vehicles
        for(int i = 0; i < Run.t; i++) {
            for(int j = 0; j < Run.m; j++) {
                vehicles.add(new Vehicle(Run.depots.get(i)));
            }
        }

        // Assign customers randomly to vehicles
        for(int i = 0; i < Run.customers.size(); i++) {
            int randomVehicle = r.nextInt((vehicles.size() - 0)) + 0;
            vehicles.get(randomVehicle).addCustomer(Run.customers.get(i));
        }
    }

    public void addVehicle(Vehicle v) {
        this.vehicles.add(v);
    }

    /*
     * Getters and Setters
     */
    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }
    public double getTotalCost() {
        return totalCost;
    }

}