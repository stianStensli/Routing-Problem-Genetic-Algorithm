package classes;

import main.Run;

public class Customer extends PositionNode{

    private int id = 0; // Customer number
    private int duration = 0; // Necessary service duration required for this customer
    private int demand = 0; // Demand for this customer
    private double closestDepotLength = 0; // How far until closest depot
    private Depot closestDepot; // The closest depot
    
    /*
     * Methods
     */
    public Customer(int i, int x, int y, int d, int q) {
        super(x, y);
        this.id = i;
        this.duration = d;
        this.demand = q;
    }

    public void findNearestEndDepot() {
        double minDist = 0.0;

        for(Depot depot: Run.depots){
            double dist = PositionNode.distanceTo(this, depot);
            if(closestDepot == null){
                closestDepot = depot;
                minDist = dist;
            } else if(dist < minDist){
            	closestDepot = depot;
            	minDist = dist;
            }
        }
        closestDepotLength = minDist;
    }

    /*
     * Getters and Setters
     */
    public int getId() {
        return id;
    }
    public void setId(int i) {
        this.id = i;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDemand() {
        return demand;
    }
    public void setDemand(int demand) {
        this.demand = demand;
    }
    public Depot getClosestDepot() {
        return closestDepot;
    }
    public double getClosestDepotLength() {
        return closestDepotLength;
    }
}