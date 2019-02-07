package classes;

import java.util.ArrayList;

public class Vehicle {
    private int idForDepot = 0;
    private Depot startDepot;
    private Depot endDepot = null;
    private int currentLoad = 0;
    private double duration = 0.0; // Route duration to Final Customer. Does not include end depot
    private ArrayList<Customer> customers = new ArrayList<>();
    private boolean valid = true;

    public Vehicle(int idForDepot, Depot startDepot) {
        this.idForDepot = idForDepot;
        this.startDepot = startDepot;
        this.endDepot = startDepot;
    }
    // Clone a Vehicle
    public Vehicle(Vehicle vehicle) {
        this.idForDepot = vehicle.idForDepot;
        this.startDepot = vehicle.startDepot;
        this.endDepot = vehicle.endDepot;
        this.duration = vehicle.duration;
        this.currentLoad = vehicle.currentLoad;

        for(Customer c : vehicle.customers){
            customers.add(c);
        }
    }

    /*
     * Methods
     */
    private double getDistanceWithC(Customer c, int pos) {
        double distance = 0.0;
        if(customers.size() == 0) {
            distance += PositionNode.distanceTo(c, startDepot);
            distance += c.getClosestDepotLength();
            return distance;
        }
        for(int i = 0; i < customers.size(); i++) {
            if(i == pos) {
                if(i == 0) {
                distance += PositionNode.distanceTo(c,startDepot);
                distance += PositionNode.distanceTo(customers.get(i), c);

                } else {
                    distance += PositionNode.distanceTo(c,customers.get(i-1));
                    distance += PositionNode.distanceTo(customers.get(i), c);
                }
            } else if(i == 0) {
                distance += PositionNode.distanceTo(customers.get(i),startDepot);
            } else {
                distance += PositionNode.distanceTo(customers.get(i),customers.get(i-1));
            }
        }

        if(customers.size() == pos) {
            distance += PositionNode.distanceTo(c, customers.get(customers.size()-1));
            distance += c.getClosestDepotLength();
        } else {
            distance += customers.get(customers.size()-1).getClosestDepotLength();
        }

        return distance;
    }

    public double getNewDiff(Customer c) {
        double[] temp = getMinDistanceWithC(c, true);
        return temp[0] - duration;
    }

    public double[] getMinDistanceWithC(Customer c, boolean notValid) {
        double minDistance = Double.MAX_VALUE;
        double index = -1;

        for(int i = 0; i <= customers.size(); i++) {
            double tempDist = getDistanceWithC(c,i);
            if(tempDist < minDistance) {
                if(notValid || validateCustomer(c,i) != null) {
                    index = i;
                    minDistance = tempDist;
                }
            }
        }

        return new double[]{minDistance, index};
    }

    public void calcRouteDuration() {
        double distance = 0.0;
        int load = 0;

        if(customers.size() == 0) {
            duration = distance;
            currentLoad = load;
            endDepot = startDepot;
            return;
        }

        distance += PositionNode.distanceTo(customers.get(0),startDepot);
        for(int i = 1; i < customers.size(); i++){
            distance += PositionNode.distanceTo(customers.get(i),customers.get(i-1));
            load += customers.get(i).getDemand();

        }
        distance += customers.get(customers.size()-1).getClosestDepotLength();

        duration = distance;
        currentLoad = load;

        endDepot = customers.get(customers.size() - 1).getClosestDepot();
    }

    public Boolean forceFitC(Customer c) {
        double[] temp = getMinDistanceWithC(c, true);
        int index = (int)temp[1];

        customers.add(index, c);
        duration = temp[0];
        currentLoad += c.getDemand();
        endDepot = customers.get(customers.size() - 1).getClosestDepot();

        quickValidate();
        return valid;
    }

    public boolean addCustomer(Customer c) {
        return addCustomer(c, customers.size()-1);
    }
    public boolean addCustomer(Customer c, int pos) {
        Double newDist = validateCustomer(c, pos);
        if (newDist != null) {
            customers.add(pos, c);

            duration = newDist.doubleValue();
            currentLoad += c.getDemand();
            endDepot = customers.get(customers.size() - 1).getClosestDepot();
            return true;
        }
        return false;
    }

    // Checks if given customer can be added to the end of the current route.
    private Double validateCustomer(Customer c, int pos) {
        if(currentLoad + c.getDemand() > startDepot.getMaxLoad()){
            return null;
        }

        double newLength = 0.0;
        newLength = getDistanceWithC(c,pos);

        //If new length is longer than the maximum size.
        if(startDepot.getMaxDuration() == 0){
            return newLength;
        }else if(startDepot.getMaxDuration() < newLength){
            return null;
        }

        return newLength;
    }

    public void removeCustomer(Customer c) {
        removeCustomer(c, -1);
    }
    public void removeCustomer(int pos) {
        removeCustomer(null, pos);
    }
    public void removeCustomer(Customer c, int pos) {
        if(pos != -1) {
            customers.remove(pos);
        }
        else {
            customers.remove(c);
        }

        calcRouteDuration();
    }

    public Customer removeRandom() {
        Customer c = customers.get((int)(Math.random()*customers.size()));
        removeCustomer(c);
        quickValidate();
        return c;
    }

    public Customer removeCloseToPath(ArrayList<Vehicle> vehicles) {
        quickValidate();
        return null;
    }

    public void quickValidate(){
        valid = true;
        if(this.duration > this.startDepot.getMaxDuration() && !(this.startDepot.getMaxDuration() == 0)){
            valid = false;
        }
        if(currentLoad > this.startDepot.getMaxLoad()){
            valid = false;
        }
    }

    /*
     * Getters and Setters
     */
    public int getIdForDepot() {
        return this.idForDepot;
    }
    public Depot getStartDepot() {
        return this.startDepot;
    }
    public void setStartDepot(Depot startDepot) {
        this.startDepot = startDepot;
    }
    public Depot getEndDepot() {
        return this.endDepot;
    }
    public int getCurrentLoad() {
        return this.currentLoad;
    }
    public ArrayList<Customer> getCustomers() {
        return this.customers;
    }
    public double getRouteDuration() {
            return this.duration;
    }
    public boolean isValid() { return this.valid; }
}