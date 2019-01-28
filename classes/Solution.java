package classes;

import java.util.ArrayList;

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
    public void initialize() {
        // Create vehicles
        for(int i = 0; i < Run.t; i++) {
            for(int j = 0; j < Run.m; j++) {
                vehicles.add(new Vehicle(Run.depots.get(i)));
            }
        }

        // Assign customers randomly to vehicles
        for(int i = 0; i < Run.customers.size(); i++) {
            int randomVehicle = (int) Math.random() * vehicles.size();
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