package classes;

import java.util.ArrayList;

public class DuplicateNode {
    private int customerId = 0;
    private ArrayList<Vehicle> vehicles = new ArrayList<>();

    public DuplicateNode(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Customer customer) {
        this.customerId = customerId;
    }

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }
    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof DuplicateNode) {
            if(((DuplicateNode) o).customerId == this.customerId) {
                return true;
            }
        }
        return false;
    }
}