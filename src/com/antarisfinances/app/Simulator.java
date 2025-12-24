package com.antarisfinances.app;

import javafx.beans.property.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulator {
    public enum EvolutionType {
        ST, R1, R2, R3, F1, F2, F3, NULL
    }

    public enum CryptoEvolutionType {
        ST, R1, R2, R3, F1, F2, F3, NULL
    }

    ObjectProperty<EvolutionType> evolution = new SimpleObjectProperty<>(EvolutionType.ST);

    public void evolveSymbols(com.antarisfinances.app.Siyya.AtomicDouble symVal,
            ObjectProperty<EvolutionType> generalEvol, boolean generalsentiment,
            ObjectProperty<EvolutionType> criptoSentiment, boolean xsentiment,
            ObjectProperty<EvolutionType> coinsSentiment, boolean coinsentiment, AtomicInteger vol) {
        double acval = symVal.get();
        if (!generalsentiment && !xsentiment && !coinsentiment) {
            double nval = acval * (1 + (-0.5 + (1 * (Math.random()))) / (vol.get() * 50));
            symVal.set(nval);
            System.out.println("Not any evolution trend readed");
        }
        if (generalsentiment == true) {
            switch (generalEvol.get()) {
                case EvolutionType.ST -> {

                    double nval = acval * (1 + (-0.5 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.R1 -> {

                    double nval = acval * (1 + (-0.42 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.R2 -> {

                    double nval = acval * (1 + (-0.33 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.R3 -> {

                    double nval = acval * (1 + (-0.25 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.F1 -> {

                    double nval = acval * (1 + (-0.58 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.F2 -> {

                    double nval = acval * (1 + (-0.66 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.F3 -> {

                    double nval = acval * (1 + (-0.75 + (1 * (Math.random()))) / (vol.get() * 10));
                    symVal.set(nval);
                }
                case EvolutionType.NULL -> {
                    symVal.set(acval);
                }
                default -> {
                    symVal.set(acval);
                }
            }
        } else if (coinsentiment == true) {
            switch (coinsSentiment.get()) {
                case EvolutionType.ST -> {

                    double nval = acval * (1 + (-0.5 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.R1 -> {

                    double nval = acval * (1 + (-0.42 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.R2 -> {

                    double nval = acval * (1 + (-0.33 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.R3 -> {

                    double nval = acval * (1 + (-0.25 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.F1 -> {

                    double nval = acval * (1 + (-0.58 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.F2 -> {

                    double nval = acval * (1 + (-0.66 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.F3 -> {

                    double nval = acval * (1 + (-0.75 + (1 * (Math.random()))) / (vol.get() * 50));
                    symVal.set(nval);
                }
                case EvolutionType.NULL -> {
                    symVal.set(acval);
                }
                default -> {
                    symVal.set(acval);
                }
            }
        } else if (xsentiment == true) {
            switch (criptoSentiment.get()) {
                case EvolutionType.ST -> {

                    double nval = acval * (1 + (-0.5 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.R1 -> {

                    double nval = acval * (1 + (-0.42 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.R2 -> {

                    double nval = acval * (1 + (-0.33 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.R3 -> {

                    double nval = acval * (1 + (-0.25 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.F1 -> {

                    double nval = acval * (1 + (-0.58 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.F2 -> {

                    double nval = acval * (1 + (-0.66 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.F3 -> {

                    double nval = acval * (1 + (-0.75 + (1 * (Math.random()))) / vol.get());
                    symVal.set(nval);
                }
                case EvolutionType.NULL -> {
                    symVal.set(acval);
                }
                default -> {
                    symVal.set(acval);
                }

            }
        }
    }

}
