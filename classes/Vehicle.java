package classes;

import java.util.ArrayList;

public class Vehicle {

    private Depot startDepot;
    private Depot endDepot = null;
    private int currentLoad = 0;
    private double rDurNoReturn = 0.0; // Route duration to Final Customer. Does not include end depot
    private ArrayList<Customer> customers = new ArrayList<>();

    public Vehicle(Depot startDepot) {
        this.startDepot = startDepot;
        this.endDepot = startDepot;
    }

    /*
     * Methods
     */
    public boolean addCustomer(Customer c) {
        if(validateCustomerEnd(c) ) {
            if(customers.size() == 0){
                rDurNoReturn += PositionNode.distanceTo(startDepot, c);
            } else {
                rDurNoReturn += PositionNode.distanceTo(customers.get(customers.size() - 1), c);
            }
            endDepot = c.getClosestDepot();
            currentLoad += c.getDemand();

            this.customers.add(c);
            
            return true;
        }
        return false;
    }

    //Not jet implemented!!!!!!!!!!!!
    public void addCustomer(Customer c, int pos) {

    }

    /**
     * Checks if given customer can be added to the end of the current route.
     * @return
     */
    public boolean validateCustomerEnd(Customer c){
        if(currentLoad + c.getDemand() > startDepot.getMaxLoad()){
            return false;
        }
        if(startDepot.getMaxDuration() != 0){
            double newLength = 0.0; // Lengden på den den nye ruten hvis c blir lagt til på slutten av ruten.
            if(customers.size() > 0){
                newLength = rDurNoReturn + PositionNode.distanceTo(customers.get(customers.size()-1),c) + c.getClosestDepotLength();
            } else {
                newLength = rDurNoReturn + PositionNode.distanceTo(startDepot, c) + c.getClosestDepotLength();
            }

            //If new length is longer than the maximum size.
            if(startDepot.getMaxDuration() < newLength){
                return false;
            }
        }
        return true;
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

    public double getRouteDuration() {
        if(customers.size() > 0)
            return rDurNoReturn + customers.get(customers.size()-1).getClosestDepotLength();
        else
            return 0.0;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

}