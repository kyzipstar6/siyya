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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.geometry.Insets;

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


    /** Applies a dark, trading-terminal style theme (no external CSS file required). */
    private void applyTradingTheme(Scene scene) {
        // JavaFX stylesheets must be loaded via URL; we write the CSS to a temp file at runtime.
        final String css = """
            .root {
                -fx-font-family: "Segoe UI", "Inter", "Arial";
                -fx-background-color: #2a4d45ff;
            }

            /* Top menu */
            .menu-bar {
                -fx-background-color: #0f1623;
                -fx-border-color: #22314a;
                -fx-border-width: 0 0 1 0;
            }
            .menu-bar .label { -fx-text-fill: #cbd5e1; }
            .menu-item .label { -fx-text-fill: #e2e8f0; }
            .context-menu {
                -fx-background-color: #0f1623;
                -fx-border-color: #22314a;
            }

            /* Panels */
            .card {
                -fx-background-color: #0f1623;
                -fx-background-radius: 14;
                -fx-border-radius: 14;
                -fx-border-color: #22314a;
                -fx-border-width: 1;
                -fx-padding: 14;
            }

            /* Labels */
            .kpi-title {
                -fx-text-fill: #94a3b8;
                -fx-font-size: 12px;
                -fx-font-weight: 600;
            }
            .kpi-value {
                -fx-text-fill: #e2e8f0;
                -fx-font-size: 16px;
                -fx-font-weight: 700;
            }

            /* Buttons */
            .button {
                -fx-background-radius: 10;
                -fx-padding: 8 12;
                -fx-font-weight: 700;
                -fx-cursor: hand;
                -fx-background-color: #1f2937;
                -fx-text-fill: #e5e7eb;
                -fx-border-color: #334155;
                -fx-border-radius: 10;
            }
            .button:hover { -fx-background-color: #263244; }
            .button:pressed { -fx-translate-y: 1; }

            .primary-button { -fx-background-color: #2563eb; -fx-border-color: #2563eb; }
            .primary-button:hover { -fx-background-color: #1d4ed8; }

            .good-button { -fx-background-color: #16a34a; -fx-border-color: #16a34a; }
            .good-button:hover { -fx-background-color: #15803d; }

            .danger-button { -fx-background-color: #dc2626; -fx-border-color: #dc2626; }
            .danger-button:hover { -fx-background-color: #b91c1c; }

            /* Volatility text area */
            .text-area, .text-field {
                -fx-control-inner-background: #0b0f14;
                -fx-background-color: #0b0f14;
                -fx-text-fill: #e2e8f0;
                -fx-highlight-fill: #2563eb;
                -fx-highlight-text-fill: white;
                -fx-border-color: #22314a;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
            }
            .text-area .content {
                -fx-background-color: #0b0f14;
                -fx-background-radius: 10;
            }

            /* Charts (dark terminal look) */
            .chart { -fx-background-color: transparent; }
            .chart-plot-background {
                -fx-background-color: #0a0505ff;
                -fx-background-insets: 0;
                -fx-background-radius: 12;
            }
            .chart-title {
                -fx-text-fill: #cbd5e1;
                -fx-font-weight: 800;
            }
            .axis-label, .axis-tick-mark, .axis-tick-label { -fx-text-fill: #94a3b8; }
            .axis { -fx-tick-label-fill: #94a3b8; }
            .chart-vertical-grid-lines, .chart-horizontal-grid-lines {
                -fx-stroke: rgba(148,163,184,0.18);
            }
            .chart-legend { -fx-background-color: transparent; }
            .chart-legend-item { -fx-text-fill: #94a3b8; }

            /* Series colors: green for first, cyan for second */
            .default-color0.chart-series-area-fill { -fx-fill: rgba(34, 197, 94, 0.32); }
            .default-color0.chart-series-area-line { -fx-stroke: #35c522ff; -fx-stroke-width: 2px; }

            .default-color1.chart-series-area-fill { -fx-fill: rgba(248, 85, 56, 0.37); }
            .default-color1.chart-series-area-line { -fx-stroke: #c52c2cff; -fx-stroke-width: 2px; }
        """;

        try {
            Path tmp = Files.createTempFile("siyya-trading-theme", ".css");
            Files.writeString(tmp, css, StandardCharsets.UTF_8);
            tmp.toFile().deleteOnExit();
            scene.getStylesheets().add(tmp.toUri().toString());
        } catch (IOException ex) {
            // If the stylesheet fails, keep the app usable (fall back to default JavaFX look).
            ex.printStackTrace();
        }
    }

    @Override
    public void start(Stage stg) {
        Clock clock = new Clock();
        AtomicDouble ordacoin = new AtomicDouble (4003);
        AtomicDouble virtuacoin = new AtomicDouble (85803);
        AtomicDouble[] prices = {ordacoin, virtuacoin};
        
        String coin1="Portcoin";
        String coin2="Sensecoin";

        String [] symbols = {coin1, coin2};
        Simulator sim = new Simulator();
        AtomicInteger cl = new AtomicInteger(1);

        stg = new Stage();
        stg.setTitle("Siyya, the trade game");
        BorderPane bp = new BorderPane();
        AtomicInteger vol = new AtomicInteger(500);
        Button startSim = new Button("Start Simulation");
        startSim.getStyleClass().add("primary-button");
        startSim.setOnAction(e -> {
            // Start simulation logic here
           Timeline symevol = new Timeline(new KeyFrame(Duration.millis(500),"",e2->{ sim.evolveSymbols(ordacoin, evolution,vol);        sim.evolveSymbols(virtuacoin, evolution,vol);
           })) ;symevol.setCycleCount(Animation.INDEFINITE);symevol.play(); clock.model(cl,prices,symbols);
        });
        Button setVol = new Button("Set volatility");
        setVol.getStyleClass().add("good-button");
        TextArea volval = new TextArea(vol.get()+"");
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

        // Trading-style button semantics (red = sell trend, green = buy trend)
        for (int i = 0; i < changeEvols.length; i++) {
            if (i <= 2) changeEvols[i].getStyleClass().add("danger-button"); // Fall modes
            else if (i == 3) { /* Steady */ }
            else changeEvols[i].getStyleClass().add("good-button");          // Rise modes
        }

        bp.setBottom(startSim);
        HBox buttons = new HBox(5,changeEvols);
        HBox labels = new HBox(12);
        Label [] lbls = {new Label("Clock"), new Label("Ordacoin"), new Label("Virtuacoin")};
        Label [] dataLbls = {new Label("Time: 00:00:00"), new Label(ordacoin.get()+" USD"), new Label(virtuacoin.get()+" USD")};
        Label [] tousand = {};
        for (Label l : lbls) {
            l.getStyleClass().add("kpi-title");
        }
        for (Label l : dataLbls) {
            l.getStyleClass().add("kpi-value");
            l.setFont(new Font("Arial", 16));
        }
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        Menu editMenu = new Menu("Edit");
        Menu viewMenu = new Menu("View");
        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu);
        List<StackedAreaChart<Number, Number>> charts = new ArrayList<>();
        charts.add(createChart(coin1+" price",ordacoin));
        charts.add(createChart(coin2+" price",virtuacoin));

        HBox upchartContainer = new HBox();
        upchartContainer.setSpacing(20);
         VBox [] conts = {new VBox(lbls[0], dataLbls[0]), new VBox(lbls[1], dataLbls[1]), new VBox(lbls[2], dataLbls[2])};

        VBox topPanel = new VBox(10, new HBox(10, buttons, volval, setVol), labels);
        topPanel.getStyleClass().add("card");
        upchartContainer.getChildren().addAll(topPanel);
        HBox downchartContainer = new HBox();
                downchartContainer.getChildren().addAll(charts.get(0), charts.get(1));

        VBox chartContainer = new VBox(12, upchartContainer, downchartContainer);
        chartContainer.setPadding(new Insets(16));
        chartContainer.getStyleClass().add("card");
        downchartContainer.setSpacing(20);
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
            dataLbls[1].setText(String.format("Portcoin: %.5f USD", ordacoin.get()));
            dataLbls[2].setText(String.format("Sensecoin: %.5f USD", virtuacoin.get()));
        })); guiUpdate.setCycleCount(Animation.INDEFINITE); guiUpdate.play();

        bp.setCenter(guiContainer); bp.setTop(menuBar);
        Scene scene = new Scene(bp);
        applyTradingTheme(scene);
        stg.setScene(scene);
        stg.show();
        
        // The simulation would typically run within a JavaFX Application thread.
        // Here we just set up the models. Actual rendering and application loop is omitted.
    }


    StackedAreaChart<Number, Number> createChart(String varName, AtomicDouble val) {
         
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        StackedAreaChart<Number, Number> StackedAreaChart = new StackedAreaChart<>(xAxis, yAxis);
        StackedAreaChart.getStyleClass().add("card");
        StackedAreaChart.setAnimated(false);

        xAxis.setLabel("Time");
        yAxis.setLabel(varName);
        StackedAreaChart.setCreateSymbols(false);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        
        series.getData().add(new XYChart.Data<>(0, val.get()));
        StackedAreaChart.getData().add(series);
        StackedAreaChart.setTitle(varName + " over Time");
        
        yAxis.setAutoRanging(false);

        Timeline chartUpdater = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            
                series.getData().add(new XYChart.Data<>(series.getData().size(), val.get()));
                keepBoundsInRange(yAxis, series);
            
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
    
    void keepBoundsInRange(NumberAxis yax, XYChart.Series<Number,Number> ser){
        AtomicDouble sermax = new AtomicDouble((double)ser.getData().get(ser.getData().size()-1).getYValue());
        AtomicDouble sermin = new AtomicDouble((double)ser.getData().get(ser.getData().size()-1).getYValue());

        for (int i =0;i<ser.getData().size();i++){
            double val = (double)ser.getData().get(i).getYValue();
            if(sermax.get()<val) sermax.set(val);
            if(sermin.get()>val) sermin.set(val);

        }
        yax.setUpperBound(sermax.get()); yax.setLowerBound(sermin.get());
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
