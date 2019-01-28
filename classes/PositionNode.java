package classes;

public abstract class PositionNode {

    protected int x = 0; // X coordinate
    protected int y = 0; // Y coordinate

    PositionNode(){

    }
    PositionNode(int x,int y){
        this.x = x;
        this.y = y;
    }

    public static double distanceTo(PositionNode d, PositionNode c){
        return Math.sqrt(Math.pow(d.getX() - c.getX(), 2) + Math.pow(d.getY() - c.getY(), 2));
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
}
