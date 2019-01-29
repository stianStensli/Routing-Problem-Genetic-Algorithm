package classes;

import java.util.ArrayList;

import main.Run;

public class Solution implements Comparable<Solution>{

    ArrayList<Vehicle> vehicles = new ArrayList<>();

    boolean valid = true;
    double totalCost = 0; // Fitness score. Mulig ta 1/score
    int nrCustomers = 0;

    public Solution() {
        initialize();
    }

    @Override
    public int compareTo(Solution comp) {
        if(comp.valid && this.valid){
            if(comp.totalCost > this.totalCost){
                return -1;
            }else if(comp.totalCost == this.totalCost){
                return 0;
            }else{
                return 1;
            }
        }

        if(comp.valid){
            return -1;
        }
        if(this.valid){
            return 1;
        }

        if(comp.nrCustomers > this.nrCustomers){
            return -1;
        }else if(comp.nrCustomers == this.nrCustomers){
            return 0;
        }else{
            return 1;
        }
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
            Vehicle tempVehicle = null;
            Customer c = Run.customers.get(i);

            Depot closestDepot = c.getClosestDepot();

            double minAddedDistance = Double.MAX_VALUE;
            int index = -1;
            for(Vehicle v : vehicles){
                //if(v.getStartDepot().equals(closestDepot)){
                        double[] newDist = v.getMinDistanceWithC(c);
                        if(newDist[1] != -1){
                            if(minAddedDistance > newDist[0]){
                                minAddedDistance = newDist[0];
                                index = (int)newDist[1];
                                tempVehicle = v;

                            }
                        }
                    }

                //}

            if (tempVehicle == null){

                //System.err.println("No Valid Solution found! This needs to be fixed!!!");
                valid = false;
                totalCost = Double.MAX_VALUE;

            }else{
                tempVehicle.addCustomer(c,index);
            }

        }

    }

    public void addVehicle(Vehicle v) {
        this.vehicles.add(v);
    }
    
    public void calculateTotalCost(){
        if(!valid ){
            nrCustomers = 0;
            for(Vehicle v : vehicles){
                nrCustomers += v.getCustomers().size();
            }
        }else{
            totalCost = 0.0;
            for(Vehicle v : vehicles){
                totalCost += v.getRouteDuration();
            }
        }
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