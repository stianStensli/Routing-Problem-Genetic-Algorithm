package main;

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
import java.util.ResourceBundle;

public class GUI implements Initializable {

    @FXML
    private Canvas canvas;
    private Stage primaryStage;
    private GraphicsContext gc;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();
    }

    @FXML
    public void calculate(){
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm();
    }

    @FXML
    public void drawShapes() {
        // Draw depots
        gc.setFill(Color.web("#00c8d6"));
        for(int i = 0; i < Run.depots.size(); i++) {
            gc.fillOval(Run.depots.get(i).getX()*10, Run.depots.get(i).getY()*10, 10, 10);
        }

        // Draw customers
        gc.setFill(Color.web("#353535"));
        for(int i = 0; i < Run.customers.size(); i++) {
            gc.fillOval(Run.customers.get(i).getX()*10, Run.customers.get(i).getY()*10, 10, 10);
        }

        // Draw connections between depots and customers
        /*
		gc.setStroke(Color.web("#e6e6e6"));
        gc.setLineWidth(2);
        gc.strokeLine(40, 10, 10, 40);
        */
    }

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }
}