package classes;

import java.util.ArrayList;

public class Vehicle {

    private Depot startDepot;
    private Depot endDepot = null;
    private int currentLoad = 0;
    private double duration = 0.0; // Route duration to Final Customer. Does not include end depot
    private ArrayList<Customer> customers = new ArrayList<>();

    public Vehicle(Depot startDepot) {
        this.startDepot = startDepot;
        this.endDepot = startDepot;
    }

    /*
     * Methods
     */
    public boolean addCustomer(Customer c) {
        return addCustomer(c, customers.size()-1);
    }

    public boolean addCustomer(Customer c, int pos) {
        double newDist = validateCustomer(c, pos);
        if (newDist != -1) {
            customers.add(pos, c);

            duration = newDist;
            endDepot = customers.get(customers.size() - 1).getClosestDepot();
            return true;
        }
        return false;
    }

    private double getDistanceWithC(Customer c, int pos) {
        double distance = 0.0;
        if(customers.size() == 0){
            distance += PositionNode.distanceTo(c, startDepot);
            distance += c.getClosestDepotLength();
            return distance;
        }
        for(int i = 0; i < customers.size(); i++){
         if(i == pos){
             if(i == 0){
                 distance += PositionNode.distanceTo(c,startDepot);
                 distance += PositionNode.distanceTo(customers.get(i), c);

             }else{
                 distance += PositionNode.distanceTo(c,customers.get(i-1));
                 distance += PositionNode.distanceTo(customers.get(i), c);

             }
         }else if(i == 0){
             distance += PositionNode.distanceTo(customers.get(i),startDepot);

         }else{
             distance += PositionNode.distanceTo(customers.get(i),customers.get(i-1));
         }

        }

        if(customers.size() == pos){
            distance += PositionNode.distanceTo(c, customers.get(customers.size()-1));
            distance += c.getClosestDepotLength();

        }else{
            distance += customers.get(customers.size()-1).getClosestDepotLength();
        }

        return distance;
    }

    private double validateCustomer(Customer c, int pos) {
        if(currentLoad + c.getDemand() > startDepot.getMaxLoad()){
            return -1;
        }

        double newLength = 0.0;
        if(startDepot.getMaxDuration() != 0){
            if(customers.size() > 0){
                newLength = getDistanceWithC(c,pos);
            } else {
                newLength = PositionNode.distanceTo(startDepot, c) + c.getClosestDepotLength();
            }

            //If new length is longer than the maximum size.
            if(startDepot.getMaxDuration() < newLength){
                return -1;
            }
        }
        return newLength;

    }

    /**
     * Checks if given customer can be added to the end of the current route.
     * @return
     */
    public double validateCustomerEnd(Customer c){
        return validateCustomer(c,customers.size());
    }

    /*
     * Getters and Setters
     */
    public Depot getStartDepot() {
        return startDepot;
    }
    public void setStartDepot(Depot startDepot) {
        this.startDepot = startDepot;
    }
    public Depot getEndDepot() {
        return endDepot;
    }
    public int getCurrentLoad() {
        return currentLoad;
    }
    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public double getRouteDuration() {
            return duration;
    }

    public double[] getMinDistanceWithC(Customer c){
        double minDistance = Double.MAX_VALUE;
        double index = -1;

        for(int i = 0; i <= customers.size(); i++){
            double tempDist = getDistanceWithC(c,i);
            if (tempDist < minDistance){
                if(validateCustomer(c,i) != -1){
                    index = i;
                    minDistance = tempDist;
                }
            }
        }

        return new double[]{minDistance, index};
    }
}