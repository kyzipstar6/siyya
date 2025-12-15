package com.antarisfinances.app;

import javafx.beans.property.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Simulator {
    public enum EvolutionType{
        ST, R1, R2,R3,F1,F2,F3
    }
    ObjectProperty <EvolutionType> evolution = new SimpleObjectProperty<>(EvolutionType.ST);
    public void evolveSymbols(com.antarisfinances.app.Siyya.AtomicDouble symVal, ObjectProperty <EvolutionType> evol, AtomicInteger vol){
        double acval = symVal.get();
        switch(evol.get()){
            case EvolutionType.ST ->{
                
                double nval= acval * (1+ (-0.5+ (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
            case EvolutionType.R1 ->{
                
                double nval= acval * (1+(-0.42+ (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
            case EvolutionType.R2 ->{
                
                double nval= acval * (1+(-0.33+ (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
            case EvolutionType.R3 ->{
                
                double nval= acval * (1+(-0.25+ (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
            case EvolutionType.F1 ->{
                
                double nval= acval * (1+(-0.58+ (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
            case EvolutionType.F2 ->{
                
                double nval= acval * (1+(-0.66 + (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
            case EvolutionType.F3 ->{
                
                double nval= acval *(1+ (-0.75+ (1*(Math.random())))/vol.get());
                symVal.set(nval);
            }
        }
    }

}
