package classes;

public class Depot extends PositionNode{

    private int id = 0; // Depot number
    private double maxDuration = 0; // Maximum duration of a route
    private int maxLoad = 0; // Allowed maximum load of a vehicle
    
    private int heihei = 0; //еееее

    public Depot(double maxDuration, int maxLoad) {
        super();
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
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

    public double getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int d) {
        maxDuration = d;
    }

    public int getMaxLoad() {
        return maxLoad;
    }

    public void setMaxLoad(int maxLoad) {
        this.maxLoad = maxLoad;
    }
}