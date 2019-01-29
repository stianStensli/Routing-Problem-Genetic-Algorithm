package classes;

import java.util.ArrayList;

import main.Run;

public class Solution {

    ArrayList<Vehicle> vehicles = new ArrayList<>();
    double totalCost = 0; // Fitness score. Mulig ta 1/score

    int numberOfC = 0; //Control only! no other function

    public Solution() {
        initialize();
    }

    /*
     * Methods
     */
    public void initialize() {
        // Create vehicles and assign to depots
        for(int i = 0; i < Run.t; i++) {
            for(int j = 0; j < Run.m; j++) {
                vehicles.add(new Vehicle(Run.depots.get(i)));
            }
        }

        // Assign customers randomly to vehicles
        for(int i = 0; i < Run.customers.size(); i++) {
            int randomVehicle = (int) (Math.random() * vehicles.size());
            boolean added = vehicles.get(randomVehicle).addCustomer(Run.customers.get(i));
            
            /*
             * If customer couldn't be assigned to the first found vehicle,
             * keep trying other vehicles until the customer is assigned to one
             */
            int j = 1;
            while (!added){
                int nextRandomVehicle = (randomVehicle + j) % vehicles.size();

                if(nextRandomVehicle == randomVehicle){
                    System.err.println("No Valid Solution found! This needs to be fixed!!!");
                    break;
                }
                added = vehicles.get(nextRandomVehicle).addCustomer(Run.customers.get(i));

                j++;
            }
        }

    }

    public void addVehicle(Vehicle v) {
        this.vehicles.add(v);
    }
    
    public void calculateTotalCost(){
        totalCost = 0.0;
        numberOfC = 0;
        for(Vehicle v : vehicles){
            totalCost += v.getRouteDuration();

            numberOfC += v.getCustomers().size();
        }
        System.out.println(numberOfC);
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