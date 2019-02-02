package com.matthewkayin.winjam;

import java.lang.Math;

public class Player{
    private double xPos;
    private double yPos;
    private double playerWidth;
    private double playerHeight;
    private double xSpeed;
    private double ySpeed;
    private double maxPlayerSpeed;

    public double getxPos(){

        return xPos;
    }

    public double getyPos(){

        return yPos;
    }

    public void setxPos(double par){

        xPos = par;
    }

    public void setyPos(double par){

        yPos = par;
    }

    public double getPlayerWidth(){

        return playerWidth;
    }

    public double getPlayerHeight(){

        return playerHeight;
    }

    public double getxSpeed(){

        return xSpeed;
    }

    public double getySpeed(){

        return ySpeed;
    }

    public void setxSpeed(double par){

        if((Math.sqrt((par * par) + (ySpeed * ySpeed)) <= maxPlayerSpeed)){

            xSpeed = par;
        }

        else{

            xSpeed = Math.sqrt((maxPlayerSpeed * maxPlayerSpeed) - (ySpeed * ySpeed));
        }
    }

    public void setySpeed(double par){

        if(Math.sqrt((par * par) + (xSpeed * xSpeed)) < maxPlayerSpeed){
            xSpeed = par;
        }

        else{

            xSpeed = Math.sqrt((maxPlayerSpeed * maxPlayerSpeed) - (xSpeed * xSpeed));
        }
    }

    public Player(){
        xPos = 50;
        yPos = 50;
        playerWidth = 50;
        playerHeight = 64;
        xSpeed = 0;
        ySpeed = 0;
        maxPlayerSpeed = 100;
    }
}