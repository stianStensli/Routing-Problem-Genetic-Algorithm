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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static java.lang.Thread.State.*;

public class GUI implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private ChoiceBox cBox;
    @FXML
    private Label generation;
    @FXML
    private Label fitness;
    @FXML
    private LineChart<Number, Number> linechart;
    @FXML
    private NumberAxis yAxis;

    XYChart.Series series = new XYChart.Series();


    private Stage primaryStage;
    private GraphicsContext gc;

    private EvaluationAlgorithm algorithm;

    private double xOffset;
    private double yOffset;

    private double recSize;
    private double lineOffset;

    private double padding = 5;
    ArrayList<Solution> intList = new ArrayList();
    Solution bestSolution;
    int solutionSize = 0;

    Thread calcThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
        calcRecSizeAndOffest();
        initListener();
        initChoiceBox();
        initThread();
        linechart.getData().add(series);
        yAxis.forceZeroInRangeProperty().setValue(false);

    }
    private void initThread(){
        calcThread = new Thread(new Runnable() {
            public void run() {
                intList.clear();
                solutionSize = 0;
                algorithm = new EvaluationAlgorithm();
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
    public void writeFile() {
        if(bestSolution != null) {
            Data.SolutionToFile(bestSolution, (String) cBox.getValue());
        }
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
            linechart.getData().clear();
            series = new XYChart.Series();
            linechart.getData().add(series);

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
        gc.setLineWidth(2);
        //Color[] colors = {Color.rgb(0,0,0), Color.rgb(1,0,103), Color.rgb(67,208,47), Color.rgb(255,0,86), Color.rgb(158,0,142), Color.rgb(14,76,161), Color.rgb(255,229,2), Color.rgb(0,95,57), Color.rgb(0,255,0), Color.rgb(149,0,58), Color.rgb(101,21,234), Color.rgb(0,21,68), Color.rgb(145,208,203), Color.rgb(107,104,130), Color.rgb(0,0,255), Color.rgb(0,125,181), Color.rgb(106,130,108), Color.rgb(0,174,126), Color.rgb(194,140,159), Color.rgb(255,0,246), Color.rgb(190,153,112), Color.rgb(0,143,156), Color.rgb(95,173,78), Color.rgb(255,0,0), Color.rgb(255,2,157), Color.rgb(104,61,59), Color.rgb(35,5,152), Color.rgb(150,138,232), Color.rgb(152,255,82), Color.rgb(167,87,64), Color.rgb(1,255,254), Color.rgb(136,11,11), Color.rgb(254,137,0), Color.rgb(189,198,255), Color.rgb(1,208,255), Color.rgb(187,136,0), Color.rgb(117,68,177), Color.rgb(165,255,210), Color.rgb(255,166,254), Color.rgb(119,77,0), Color.rgb(29,50,103), Color.rgb(23,253,20), Color.rgb(255,0,150), Color.rgb(232,12,56), Color.rgb(98,34,55)};
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.PINK, Color.YELLOW, Color.LIGHTBLUE, Color.BROWN};

        int i = 0;
        for(Vehicle vehicle : s.getVehicles()) {
            //gc.setStroke(colors[i]);
            gc.setStroke(colors[vehicle.getStartDepot().getId()-1]);

            int prevX = vehicle.getStartDepot().getX();
            int prevY = vehicle.getStartDepot().getY();

            for(Customer customer : vehicle.getCustomers()) {
                gc.strokeLine((prevX+xOffset)*recSize+lineOffset, (prevY+yOffset)*recSize+lineOffset, (customer.getX()+xOffset)*recSize+lineOffset, (customer.getY()+yOffset)*recSize+lineOffset);

                prevX = customer.getX();
                prevY = customer.getY();
            }

            gc.strokeLine((prevX+xOffset)*recSize+lineOffset, (prevY+yOffset)*recSize+lineOffset, (vehicle.getEndDepot().getX()+xOffset)*recSize+lineOffset, (vehicle.getEndDepot().getY()+yOffset)*recSize+lineOffset);
            i++;
        }
    }

    private void drawText(Solution s) {
        generation.setText(algorithm.getGeneration().toString());
        fitness.setText(Double.toString(s.getTotalCost()));
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

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(intList.size() > solutionSize){
                    solutionSize = intList.size();
                    if(bestSolution == null || !bestSolution.equals(intList.get(solutionSize-1)) || EvaluationAlgorithm.getMaxRuns() == solutionSize+1){
                        bestSolution = intList.get(solutionSize-1);
                        drawShapes();
                        drawPath(bestSolution);
                        series.getData().add(new XYChart.Data(solutionSize, bestSolution.getTotalCost()));
                    }

                    drawText(bestSolution);
                }
            }
        }.start();
    }

}