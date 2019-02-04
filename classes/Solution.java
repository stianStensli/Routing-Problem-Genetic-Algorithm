package classes;

import java.util.ArrayList;
import java.util.Collections;

import main.Run;

public class Solution implements Comparable<Solution>{
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    ArrayList<Customer> notPlaced = new ArrayList<>();
    private boolean valid = true;
    double totalCost = 0; // Fitness score
    int numCustomers = 0;

    public Solution() { }
    public Solution(boolean initialize) {
        if(initialize) {
            initialize();
        }
    }
    public Solution(Solution other) {
        for(int i = 0; i < other.getVehicles().size(); i++) {
            this.addVehicle(new Vehicle(other.getVehicles().get(i)));
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
            return 1;
        }
        if(this.valid){
            return -1;
        }

        if(comp.numCustomers > this.numCustomers){
            return -1;
        }else if(comp.numCustomers == this.numCustomers){
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
                vehicles.add(new Vehicle((j+1), Run.depots.get(i)));
            }
        }

        // Assign customers to vehicles
        for(int i = 0; i < Run.customers.size(); i++) {
            Vehicle tempVehicle = null;
            Customer c = Run.customers.get(i);

            double minAddedDistance = Double.MAX_VALUE;
            int index = -1;

            for(Vehicle v : vehicles){
                double[] newDist = v.getMinDistanceWithC(c,false);
                if(newDist[1] != -1){
                    if(minAddedDistance > newDist[0]){
                        minAddedDistance = newDist[0];
                        index = (int)newDist[1];
                        tempVehicle = v;
                    }
                }
            }

            if (tempVehicle == null){
                notPlaced.add(c);
                valid = false;
                totalCost = Double.MAX_VALUE;
            } else {
                tempVehicle.addCustomer(c,index);
            }
        }

        if(!valid){
            repair();
        }
    }

    public void validate() {
        valid = true;
        if(notPlaced.size() > 0) {
            valid = false;
        }
        for(Vehicle v : vehicles) {
            if(!v.isValid()) {
                valid = false;
            }
        }
    }

    public void repair() {
        int itr = 0;
        while (!valid && itr < 999) {
            if(notPlaced.size() == 0) {
                valid = true;
            } else {
                ArrayList<Vehicle> inValid = placeRemaining();
                makeValid(inValid);
            }
            itr++;
        }
        calculateTotalCost();
    }

    private ArrayList<Vehicle> placeRemaining() {
        ArrayList<Vehicle> invalid = new ArrayList<>();

        int itr = 0;

        while (notPlaced.size() != 0 && itr < 1000) {
            int rIndex = (int) (Math.random()*notPlaced.size());
            Customer c = notPlaced.get(rIndex);
            double minDiff = Double.MAX_VALUE;
            Vehicle addTo = null;

            for(Vehicle v : vehicles) {
                double temp = v.getNewDiff(c);
                if(temp < minDiff){
                    minDiff = temp;
                    addTo = v;
                }
            }
            if(addTo == null) {
                System.err.println("ERROR! Solution make Valid");
            }else {
                boolean validForce = addTo.forceFitC(c);
                notPlaced.remove(c);

                if(!validForce && !invalid.contains(addTo)){
                        invalid.add(addTo);
                }
            }
            itr++;
        }
        return invalid;
    }

    private void makeValid(ArrayList<Vehicle> inValid) {
        if(inValid.size() == 0){
            valid = true;
            return;
        }
        for(Vehicle v : inValid){
            while (!v.isValid()){
                notPlaced.add(v.removeRandom());
            }
        }
    }

    public void calculateTotalCost() {
        if(!valid ){
            numCustomers = 0;
            for(Vehicle v : vehicles){
                numCustomers += v.getCustomers().size();
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
        ArrayList<DuplicateNode> tempDuplicates = new ArrayList<>();

        for(Vehicle vehicle : vehicles) {
            for(Customer customer : vehicle.getCustomers()) {
                boolean notFound = true;
                DuplicateNode temp = new DuplicateNode(customer.getId());

                for(DuplicateNode n : tempDuplicates) {
                    if(n.equals(temp)){
                        n.addVehicle(vehicle);
                        notFound = false;
                    }
                }
                if(notFound) {
                    DuplicateNode node = new DuplicateNode(customer);
                    node.addVehicle(vehicle);
                    tempDuplicates.add(node);
                }
            }
        }

        if(tempDuplicates.size() > 0) {
            for (DuplicateNode d : tempDuplicates) {
                ArrayList<Vehicle> vTemp = d.getVehicles();
                if (vTemp.size() > 1) {
                    Vehicle v = vTemp.get((int) (Math.random() * vTemp.size()));
                    v.removeCustomer(d.getCustomer());
                }
            }
        }
    }

    public void mutate(double mutationRate) {
        randomDeleteMutate(mutationRate);
    }
    private void swapMutate(double mutationRate) {
        for(Vehicle vehicle : vehicles) {
            if(Math.random() <= mutationRate && vehicle.getCustomers().size() > 1) {
                int indexCustomer = (int)(Math.random() * vehicle.getCustomers().size());
                int indexTempCustomer = (int)(Math.random() * vehicle.getCustomers().size());

                Collections.swap(vehicle.getCustomers(), indexCustomer, indexTempCustomer);

                vehicle.calcRouteDuration();
                vehicle.quickValidate();
            }
        }
    }
    private void randomDeleteMutate(double mutationRate) {
        // Fjerne potensielt mange customers per vehicle
        for(int i = 0; i < vehicles.size(); i++) {
            boolean subtract = false;
            for(int j = 0; j < vehicles.get(i).getCustomers().size(); j++) {
                if (Math.random() <= mutationRate) {
                    Customer removed = vehicles.get(i).removeRandom();
                    notPlaced.add(removed);
                }
            }
            if(subtract && i > 0) {
                i--;
            }
        }

        // Fjerne potensielt en customer per vehicle
        /*
        for(int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getCustomers().size() > 0) {
                if (Math.random() <= mutationRate) {
                    Customer removed = vehicles.get(i).removeRandom();
                    notPlaced.add(removed);
                    if(i > 0) {
                        i--;
                    }
                }
            }
        }
        */
    }

    /*
     * Getters and Setters
     */
    public ArrayList<Vehicle> getVehicles() { return vehicles; }
    public double getTotalCost() { return totalCost; }
    public void addVehicle(Vehicle v) { this.vehicles.add(v); }
    public boolean isValid() { return valid; }
}