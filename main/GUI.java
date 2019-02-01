package main;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import classes.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static java.lang.Thread.State.*;

public class GUI implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private ChoiceBox cBox;

    private Stage primaryStage;
    private GraphicsContext gc;

    private double xOffset;
    private double yOffset;

    private double recSize;
    private double lineOffset;

    private double padding = 5;
    ArrayList<Solution> intList = new ArrayList();
    Solution bestSolution;

    Thread calcThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        calcRecSizeAndOffest();

        initListener();
        initChoiceBox();
        initThread();
    }
    private void initThread(){
        calcThread = new Thread(new Runnable() {
        public void run() {
            EvaluationAlgorithm algorithm = new EvaluationAlgorithm();
            algorithm.loadObservableList(intList);
            algorithm.run();
        }
    });

    }
    private void initChoiceBox() {
        File folder = new File("./src/Data Files/");
        File[] listOfFiles = folder.listFiles();

        ObservableList<String> objects = FXCollections.observableArrayList();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                objects.add(listOfFiles[i].getName());
            }
        }
        cBox.setItems(objects.sorted());
        cBox.setValue(cBox.getItems().get(0));
    }

    @FXML
    public void changeFile(){
        Data.ReadData("./src/Data Files/"+cBox.getValue());
        calcRecSizeAndOffest();
        drawShapes();

    }
    @FXML
    public void calculate(){
        if(calcThread.getState() == NEW)
            calcThread.start();
        if(calcThread.getState() == TERMINATED){
            initThread();
            calcThread.start();
        }

    }

    @FXML
    public void drawShapes() {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        
        // Draw customers
        gc.setFill(Color.web("BLACK"));
        for(int i = 0; i < Run.customers.size(); i++) {
            drawRec(Run.customers.get(i).getX(),Run.customers.get(i).getY());
            //gc.fillOval(Run.customers.get(i).getX()*10, Run.customers.get(i).getY()*10, 10, 10);
        }
        
        // Draw depots
        gc.setFill(Color.web("RED"));
        for(int i = 0; i < Run.depots.size(); i++) {
            drawOval(Run.depots.get(i).getX(),Run.depots.get(i).getY());
            //gc.fillOval(Run.depots.get(i).getX()*10, Run.depots.get(i).getY()*10, 10, 10);
        }
    }

    public void drawPath(Solution s){
        if(s.duplicates != null){
            for(DuplicateNode d : s.duplicates){
                if(d.getVehicles().size() > 1){
                gc.setFill(Color.web("YELLOW"));

                drawRec(d.getCustomer().getX(),d.getCustomer().getY());
                gc.setFill(Color.web("BLACK"));
            }
            }
        }
        for(Vehicle vehicle : s.getVehicles()) {
            gc.setLineWidth(2);
            Random r = new Random();
            gc.setStroke(Color.web( String.format("#%06x", r.nextInt(256*256*256)) ));

            int prevX = vehicle.getStartDepot().getX();
            int prevY = vehicle.getStartDepot().getY();

            for(Customer customer : vehicle.getCustomers()) {
                gc.strokeLine((prevX+xOffset)*recSize+lineOffset, (prevY+yOffset)*recSize+lineOffset, (customer.getX()+xOffset)*recSize+lineOffset, (customer.getY()+yOffset)*recSize+lineOffset);

                prevX = customer.getX();
                prevY = customer.getY();
            }
            if(vehicle.getEndDepot() == null){
                System.out.println("whoops");
            }

            gc.strokeLine((prevX+xOffset)*recSize+lineOffset, (prevY+yOffset)*recSize+lineOffset, (vehicle.getEndDepot().getX()+xOffset)*recSize+lineOffset, (vehicle.getEndDepot().getY()+yOffset)*recSize+lineOffset);
        }
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    private void drawRec(int x, int y){
        gc.fillRect((x+xOffset)*recSize+padding,(y+yOffset)*recSize+padding,recSize,recSize);
    }
    private void drawOval(int x, int y){
        gc.fillOval((x+xOffset)*recSize+padding,(y+yOffset)*recSize+padding,recSize,recSize);
    }
    private void drawLine(PositionNode p1, PositionNode p2){
        gc.strokeLine(40, 10, 10, 40);
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

        xOffset = -minX;
        yOffset = -minY;

        recSize = (w-padding*2)/(maxX-minX+1);
        recSize = Math.min(recSize, (h-padding*2)/(maxY-minY+1));
        
        lineOffset = recSize / 2+padding;
    }



    public void initListener(){
        intList = new ArrayList();

    /*

        ob.addListener(new ListChangeListener<Solution>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Solution> c) {
                c.next();
                if(c.wasAdded()){
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            drawShapes();
                            if(c.getList().size() != 0){
                                drawPath(c.getList().get(c.getList().size()-1));
                            }
                        }
                    });
                }
            }

        });*/
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                if(intList.size() > 0){
                    bestSolution = intList.get(intList.size()-1);
                    intList.clear();
                    drawShapes();
                    drawPath(bestSolution);
                }
            }
        }.start();
    }

}