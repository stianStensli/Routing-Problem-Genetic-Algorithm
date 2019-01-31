package classes;

import java.util.ArrayList;

import main.Run;

public class Solution implements Comparable<Solution>{

    ArrayList<Vehicle> vehicles = new ArrayList<>();
    ArrayList<Customer> notPlaced = new ArrayList<>();

    public boolean valid = true;
    double totalCost = 0; // Fitness score. Mulig ta 1/score
    int nrCustomers = 0;

    public Solution() { }

    public Solution(boolean initialize) {
        if(initialize) {
            initialize();
        }
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

        // Assign customers to vehicles
        for(int i = 0; i < Run.customers.size(); i++) {
            Vehicle tempVehicle = null;
            Customer c = Run.customers.get(i);

            Depot closestDepot = c.getClosestDepot();

            double minAddedDistance = Double.MAX_VALUE;
            int index = -1;
            for(Vehicle v : vehicles){
                    double[] newDist = v.getMinDistanceWithC(c);
                    if(newDist[1] != -1){
                        if(minAddedDistance > newDist[0]){
                            minAddedDistance = newDist[0];
                            index = (int)newDist[1];
                            tempVehicle = v;
                        }
                    }
            }

            if (tempVehicle == null){
                //System.err.println("No Valid Solution found! This needs to be fixed!!!");
                notPlaced.add(c);
                valid = false;
                totalCost = Double.MAX_VALUE;

            } else{
                tempVehicle.addCustomer(c,index);
            }
        }
        /*
        if(!valid){
            makeValid();
        }
        */
    }

    public void makeValid(){
        int itr = 0;
        while (notPlaced.size() != 0 && itr < 1){
            int rIndex = (int) (Math.random()*notPlaced.size());
            Customer temp = notPlaced.get(rIndex);
            Customer closestInVehicle = temp.getClosestCustomer();
            if(notPlaced.contains(closestInVehicle)){
                closestInVehicle = temp.getClosestCustomer(notPlaced);
            }

            for(Vehicle v : vehicles){
                if(v.getCustomers().contains(closestInVehicle)){
                    ArrayList<Customer> adding = v.forceFitC(temp, notPlaced);
                    if(adding != null){
                    for(Customer a : adding){
                        notPlaced.add(a);
                    }
                    notPlaced.remove(temp);
                    break;
                    }
                }
            }

            itr++;
        }

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

    public void updateMissingCustomers() {
        notPlaced = new ArrayList<>(Run.customers);

        for(Vehicle vehicle : vehicles) {
            for(Customer customer : vehicle.getCustomers()) {
                if(vehicle.getCustomers().contains(customer)) {
                    notPlaced.remove(customer);
                }
            }
        }
    }

    public void removeDuplicateCustomers() {
        ArrayList<DuplicateNode> duplicates = new ArrayList<>();

        for(Vehicle vehicle : vehicles) {
            for(Customer customer : vehicle.getCustomers()) {
                if(duplicates.contains(new DuplicateNode(customer.getId()))) {
                    System.out.println(customer.getX() + " " + customer.getY());
                    System.out.println("eksisterer");
                }
                else {
                    duplicates.add(new DuplicateNode(customer.getId()));
                }
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
    public void addVehicle(Vehicle v) {
        this.vehicles.add(v);
    }
}