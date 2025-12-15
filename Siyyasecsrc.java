package com.antarisfinances.app;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;          
import javafx.scene.Scene;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.ObjectProperty;

import com.antarisfinances.app.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.antarisfinances.app.Simulator.EvolutionType;

public class Siyya extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    ObjectProperty <EvolutionType> evolution = new SimpleObjectProperty<>(EvolutionType.ST);

    @Override
    public void start(Stage stg) {
        Clock clock = new Clock();
        AtomicDouble ordacoin = new AtomicDouble (1.003);
        AtomicDouble virtuacoin = new AtomicDouble (1.003);

        Simulator sim = new Simulator();
        AtomicInteger cl = new AtomicInteger(1);

        stg = new Stage();
        stg.setTitle("Siyya, the trade game");
        BorderPane bp = new BorderPane();
        AtomicInteger vol = new AtomicInteger(100);
        Button startSim = new Button("Start Simulation");
        startSim.setOnAction(e -> {
            // Start simulation logic here
           Timeline symevol = new Timeline(new KeyFrame(Duration.millis(500),"",e2->{ sim.evolveSymbols(ordacoin, evolution,vol);        sim.evolveSymbols(virtuacoin, evolution,vol);
           })) ;symevol.setCycleCount(Animation.INDEFINITE);symevol.play(); clock.model(cl);
        });
        Button setVol = new Button("Set volatility"); TextArea volval = new TextArea(vol.get()+"");
        setVol.setOnAction(e->{vol.set(Integer.parseInt(volval.getText()));});
        volval.setMaxSize(25,18);
        Button [] changeEvols = {new Button("Fall Mode 3 (Strong sell trend)"),new Button("Fall Mode 2"), new Button("Fall Mode 1")
            ,new Button("Steady"),new Button("Rise Mode 3"),new Button("Rise Mode 2"),
            new Button("Rise Mode 1 (Light buy trend)")        };

        Simulator.EvolutionType [] pEvol = {EvolutionType.F3, EvolutionType.F2,
            EvolutionType.F1, EvolutionType.ST, EvolutionType.R3, EvolutionType.R2,
            EvolutionType.R1
        };
        for (int i = 0; i<pEvol.length;i++){
            int fi = i;
            changeEvols[fi].setOnAction(e->{
                evolution.set(pEvol[fi]);
            });
        }
        
        
        bp.setBottom(startSim);
        HBox buttons = new HBox(5,changeEvols);
        HBox labels = new HBox(12);
        Label [] lbls = {new Label("Clock"), new Label("Ordacoin"), new Label("Virtuacoin")};
        Label [] dataLbls = {new Label("Time: 00:00:00"), new Label(ordacoin.get()+" USD"), new Label(virtuacoin.get()+" USD")};
        for(Label l : dataLbls){   
            l.setFont(new Font("Arial", 16));
            
        }
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);
        List<StackedAreaChart<Number, Number>> charts = new ArrayList<>();
        charts.add(createChart("Ordacoin price",ordacoin));
        charts.add(createChart("Virtuacoin price",virtuacoin));
        
        HBox upchartContainer = new HBox();
        upchartContainer.setSpacing(20);
         VBox [] conts = {new VBox(lbls[0], dataLbls[0]), new VBox(lbls[1], dataLbls[1]), new VBox(lbls[2], dataLbls[2])};

        upchartContainer.getChildren().addAll(new VBox(5,new HBox(5,buttons,volval,setVol),labels));
        HBox downchartContainer = new HBox();
                downchartContainer.getChildren().addAll(charts.get(0), charts.get(1));

        VBox chartContainer = new VBox(5, upchartContainer, downchartContainer);
        downchartContainer.setSpacing(20);
        for(Label l : lbls){
            l.setFont(new Font("Arial", 20));

        }
        for(VBox v : conts){
            v.setSpacing(10);
            labels.getChildren().add(v);
        }
        Pane guiContainer = new Pane();

        labels.setLayoutX(50);
        labels.setLayoutY(100);
        buttons.setLayoutY(50); buttons.setLayoutX(350);
        guiContainer.getChildren().addAll( chartContainer);

        Timeline guiUpdate = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            dataLbls[0].setText("Time: " + clock.getTimeString());
            dataLbls[1].setText(String.format("Ordacoin: %.5f USD", ordacoin.get()));
            dataLbls[2].setText(String.format("VirtuaCoin: %.5f USD", virtuacoin.get()));
        })); guiUpdate.setCycleCount(Animation.INDEFINITE); guiUpdate.play();

        bp.setCenter(guiContainer); bp.setTop(menuBar);
        stg.setScene(new Scene(bp, 800, 600));
        stg.show();
        
        // The simulation would typically run within a JavaFX Application thread.
        // Here we just set up the models. Actual rendering and application loop is omitted.
    }


    StackedAreaChart<Number, Number> createChart(String varName, AtomicDouble val) {
         
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        StackedAreaChart<Number, Number> StackedAreaChart = new StackedAreaChart<>(xAxis, yAxis);
        xAxis.setLabel("Time");
        yAxis.setLabel(varName);
        StackedAreaChart.setCreateSymbols(false);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        
        series.getData().add(new XYChart.Data<>(0, val.get()));
        StackedAreaChart.getData().add(series);
        StackedAreaChart.setTitle(varName + " over Time");
        
        Timeline chartUpdater = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            
                series.getData().add(new XYChart.Data<>(series.getData().size(), val.get()));
           
            
        }));
        AtomicDouble isfall = new AtomicDouble(val.get());
        AtomicDouble isfall2 = new AtomicDouble(val.get());
        Timeline trendmem = new Timeline(new KeyFrame(Duration.millis(1000), "", e->isfall.set(val.get())),
    new KeyFrame(Duration.millis(2000), "", e->isfall2.set(val.get())),
    new KeyFrame(Duration.millis(3500), "", e->{
        if(isfall.get()<val.get() && isfall2.get()<val.get()){
            List <XYChart.Series<Number, Number>> voidslist = new ArrayList<>();
            for (int i = 0; i<5; i++){
                XYChart.Series<Number, Number> voids = new XYChart.Series<>();
                voidslist.add(voids);
                voids.getData().add(new XYChart.Data<>(0, val.get()));
                                StackedAreaChart.getData().add(voids);
                            StackedAreaChart.getData().remove(series);

            }
            StackedAreaChart.getData().add(series);
            StackedAreaChart.getData().removeAll(voidslist);
        }
        if(isfall.get()>val.get() && isfall2.get()>val.get()){
            List <XYChart.Series<Number, Number>> voidslist = new ArrayList<>();
            
            for (int i = 0; i<3; i++){
                XYChart.Series<Number, Number> voids = new XYChart.Series<>();
                voidslist.add(voids);
                voids.getData().add(new XYChart.Data<>(0, val.get()));
                                StackedAreaChart.getData().add(voids);
                            StackedAreaChart.getData().remove(series);

            }
            StackedAreaChart.getData().add(series);
            StackedAreaChart.getData().removeAll(voidslist);
        }
        else{
            StackedAreaChart.getData().remove(series);
            List <XYChart.Series<Number, Number>> voidslist = new ArrayList<>();
        
            for (int i = 0; i<7; i++){
                XYChart.Series<Number, Number> voids = new XYChart.Series<>();
                voidslist.add(voids);
                voids.getData().add(new XYChart.Data<>(0, val.get()));
                                StackedAreaChart.getData().add(voids);
            StackedAreaChart.getData().remove(series);


            }
            StackedAreaChart.getData().add(series);
            StackedAreaChart.getData().removeAll(voidslist);
        }
}));trendmem.setCycleCount(Animation.INDEFINITE);trendmem.play();
        chartUpdater.setCycleCount(Animation.INDEFINITE);
        chartUpdater.play();
        return StackedAreaChart;

    }
    public class AtomicDouble{
        double val = 0;
        public AtomicDouble(double val){
            this.val=val;
        }
        public void set(double nwval){
            val=nwval;
        }
        public double get(){
            return val;
        }
    }
}