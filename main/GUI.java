package main;

import classes.Customer;
import classes.Depot;
import classes.PositionNode;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GUI implements Initializable {

    @FXML
    private Canvas canvas;
    private Stage primaryStage;
    private GraphicsContext gc;

    private double xOffset;
    private double yOffset;

    private double recSize;

    private double padding = 6;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        calcRecSizeAndOffest();
        System.out.println(canvas.getHeight());
    }

    @FXML
    public void calculate(){
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm();
    }

    @FXML
    public void drawShapes() {
        // Draw depots
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        gc.setFill(Color.web("BLACK"));
        for(int i = 0; i < Run.customers.size(); i++) {
            drawRec(Run.customers.get(i).getX(),Run.customers.get(i).getY());
            //gc.fillOval(Run.customers.get(i).getX()*10, Run.customers.get(i).getY()*10, 10, 10);
        }

        gc.setFill(Color.web("RED"));
        for(int i = 0; i < Run.depots.size(); i++) {
            //gc.fillOval(Run.depots.get(i).getX()*10, Run.depots.get(i).getY()*10, 10, 10);
            drawOval(Run.depots.get(i).getX(),Run.depots.get(i).getY());
        }

        // Draw customers


    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void drawRec(int x, int y){
        gc.fillRect((x+xOffset)*recSize,(y+yOffset)*recSize,recSize,recSize);
    }
    private void drawOval(int x, int y){
        gc.fillOval((x+xOffset)*recSize,(y+yOffset)*recSize,recSize,recSize);
    }

    private void calcRecSizeAndOffest(){
        List<Customer> customers = Run.customers;
        List<Depot> depots = Run.depots;

        double h = canvas.getHeight();
        double w = canvas.getWidth();

        int minX;
        int minY;

        int maxX;
        int maxY;
        if(depots.size() == 0){
            return;
        }
        minX = depots.get(0).getX();
        minY = depots.get(0).getY();
        maxY = minY;
        maxX = minX;

        for(int i = 1; i < depots.size(); i++){
            int x = depots.get(i).getX();
            int y = depots.get(i).getY();
            minX = Math.min(x,minX);
            minY = Math.min(y,minY);

            maxX = Math.max(x,maxX);
            maxY = Math.max(y,maxY);
        }

        for(int i = 0; i < customers.size(); i++){
            int x = customers.get(i).getX();
            int y = customers.get(i).getY();
            minX = Math.min(x,minX);
            minY = Math.min(y,minY);

            maxX = Math.max(x,maxX);
            maxY = Math.max(y,maxY);
        }

        xOffset = -minX+padding/2;
        yOffset = -minY+padding/2;

        recSize = (w-padding)/(maxX-minX);
        recSize = Math.min(recSize, (h-padding)/(maxY-minY));

    }

}