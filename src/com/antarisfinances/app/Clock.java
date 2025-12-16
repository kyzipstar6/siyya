package com.antarisfinances.app;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;  
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;


public class Clock{
    int day = 0;
    int hours = 0;
    int minutes = 0;
    int seconds = 0;
    int millis = 0;

    
    public void model(AtomicInteger accel,AtomicDouble[] prices, String [] symbols){
        Timeline clckupd = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            seconds+=5*accel.get();
            if (seconds == 60) {
                seconds = 0;
                minutes++;
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                    if (hours == 24) {
                        hours = 0;
                        day++;
                        writeLastDayPrice(AtomicDouble[] prices, String [] symbols);
                    }
                }

            }
            if(seconds>60){
                minutes += (int) Math.round(seconds/60);
                seconds=0;
            }
        }));clckupd.setCycleCount(Animation.INDEFINITE);clckupd.play();
    }
    void writeLastDayPrice(AtomicDouble[] prices, String [] symbols){
        new File("\\files").mkdirs();
        try {
            FileWriter wr = new FileWriter("files\\prices_log.txt", true);
            for(int i=0;i<prices.length;i++){
                wr.write("Day " + day + " - " + symbols[i] + " price: " + prices[i].get() + ";");
            }
            wr.write("\n");
            wr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getHours(){
        return hours;
    }
    public int getMinutes(){
        return minutes;
    }
    public int getSeconds(){
        return seconds;
    }   
    public int getDay(){
        
        return day;
    }
    public String getTimeString(){
        String hr = (hours < 10) ? "0" + hours : String.valueOf(hours);
        String min = (minutes < 10) ? "0" + minutes : String.valueOf(minutes);
        String sec = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);
        return hr + ":" + min + ":" + sec;
    }public void setTime(int hr, int min, int sec){
        hours = hr;
        minutes = min;
        seconds = sec;
    }
}
