package com.matthewkayin.winjam;

import org.w3c.dom.css.Rect;

import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.util.*;

public class Level{

    public double playerangle;
    public double playeranglespeed;
    public double distfromplanet;
    public boolean freeing = false;
    public double currPlanetX;
    public double currPlanetY;
    public int blackHoleIndex = -1;
    private int state = 0; //0 is level is playing, 1 is level victory screen, 2 is level defeat screen, 3 is victory so return, 4 is loss so return
    public int gatex;
    public int gatey;
    public boolean haslaunched = false;
    public boolean clockwise = false;
    private Queue<String> soundcall;

    public ArrayList<Entity> planets;
    public ArrayList<Entity> blackHoles;
    public ArrayList<Entity> blocks;

    public boolean explosionPlayed = false;

    public class Entity {

        private double x;
        private double y;
        private double vx;
        private double vy;
        private double w;
        private double h;
        private double ax;
        private double ay;
        private final int totalNumberOfObjects = 4;
        public boolean orbiting = false;

        public Entity() {

            x = y = vx = vy = h = w = ax = ay = 0;
        }

        public double getX() {

            return x;
        }

        public double getY() {

            return y;
        }

        public double getVx() {

            return vx;
        }

        public double getVy() {

            return vy;
        }

        public double getWidth() {

            return w;
        }

        public double getHeight() {

            return h;
        }

        public void setX(double value) {

            x = value;
        }

        public void setY(double value) {

            y = value;
        }

        public void setVx(double value) {

            vx = value;
        }

        public void setVy(double value) {

            vy = value;
        }

        public void setWidth(double value) {

            w = value;
        }

        public void setHeight(double value) {

            h = value;
        }

        public void setSize(double width, double height) {

            w = width;
            h = height;
        }

        public void setPos(double tx, double ty) {

            x = tx;
            y = ty;
        }

        public void incX(double v) {

            x += v;
        }

        public void incY(double v) {

            y += v;
        }

        public double getMass(){

            return (4.0 / 3.0) * Math.PI * (w / 2) * (w / 2) * (w / 3);
        }

        public boolean getCollision(Entity e){

            return (x >= e.getX() && x <= e.getX() + e.getWidth() && y >= e.getY() && y <= e.getY() + e.getHeight() ||
                x + w >= e.getX() && x + w <= e.getX() + e.getWidth() && y >= e.getY() && y <= e.getY() + e.getHeight() ||
                    x >= e.getX() && x <= e.getX() + e.getWidth() && y + h >= e.getY() && y + h <= e.getY() + e.getHeight() ||
                    x + w >= e.getX() && x + w <= e.getX() + e.getWidth() && y + h >= e.getY() && y + h <= e.getY() + e.getHeight());
        }

        public boolean getCollision(Rectangle e){

            return (x >= e.getX() && x <= e.getX() + e.getWidth() && y >= e.getY() && y <= e.getY() + e.getHeight() ||
                    x + w >= e.getX() && x + w <= e.getX() + e.getWidth() && y >= e.getY() && y <= e.getY() + e.getHeight() ||
                    x >= e.getX() && x <= e.getX() + e.getWidth() && y + h >= e.getY() && y + h <= e.getY() + e.getHeight() ||
                    x + w >= e.getX() && x + w <= e.getX() + e.getWidth() && y + h >= e.getY() && y + h <= e.getY() + e.getHeight());
        }
    }

    private Entity player;
    private Entity end;

    public Level(){

        planets = new ArrayList<Entity>();

        planets.add(new Entity());
        planets.get(0).setPos(200, 200);
        planets.get(0).setSize(200, 200);
        planets.add(new Entity());
        planets.get(1).setPos(500, 500);
        planets.get(1).setSize(200, 200);
        player = new Entity();
        player.setSize(40, 40);
        player.setPos(400, 600);

        end = new Entity();
        end.setSize(50, 50);
        end.setPos(50, 50);
    }

    public Level(int instructions[][]){

        soundcall = new LinkedList<>();
        planets = new ArrayList<Entity>();
        blackHoles = new ArrayList<Entity>();
        blocks = new ArrayList<Entity>();
        int index = 0;
        int holeIndex = 0;
        int blockIndex = 0;

        for(int i = 0; i < instructions.length; i++){

            if(instructions[i][0] == 0){

                player = new Entity();
                player.setSize(40, 40);
                player.setPos(instructions[i][1], instructions[i][2]);
                gatex = (int)player.getX() - 30;
                gatey = (int)player.getY() - 80;
            }

            if(instructions[i][0] == 1){

                end = new Entity();
                end.setSize(50, 50);
                end.setPos(instructions[i][1], instructions[i][2]);
            }

            if(instructions[i][0] == 2){

                planets.add(new Entity());
                planets.get(index).setSize(150, 150);
                planets.get(index).setPos(instructions[i][1], instructions[i][2]);
                index += 1;
            }

            if(instructions[i][0] == 3){

                planets.add(new Entity());
                planets.get(index).setSize(250, 250);
                planets.get(index).setPos(instructions[i][1], instructions[i][2]);
                index += 1;
            }

            if(instructions[i][0] == 4){

                blackHoles.add(new Entity());
                blackHoles.get(holeIndex).setSize(50, 50);
                blackHoles.get(holeIndex).setPos(instructions[i][1], instructions[i][2]);
                holeIndex += 1;
            }

            if(instructions[i][0] == 5){

                blocks.add(new Entity());
                blocks.get(blockIndex).setSize(50, 200);
                blocks.get(blockIndex).setPos(instructions[i][1], instructions[i][2]);
                blockIndex += 1;
            }

            if(instructions[i][0] == 6){

                blocks.add(new Entity());
                blocks.get(blockIndex).setSize(200, 50);
                blocks.get(blockIndex).setPos(instructions[i][1], instructions[i][2]);
                blockIndex += 1;
            }
        }
    }

    public void impulse(double x, double y, double speed){

        double dirx = x - player.getX() + (player.getWidth() / 2);
        double diry = y - player.getY() + (player.getHeight() / 2);
        double hyp = Math.sqrt((dirx * dirx) + (diry * diry)); //pythagoras
        double diff = hyp / speed;
        double ndx = dirx / diff;
        double ndy = diry / diff;
        int xmod = 1;
        int ymod = 1;
        player.setVx(player.getVx() + ndx*xmod);
        player.setVy(player.getVy() + ndy*ymod);
    }

    public void update() {

        if(state == 1 || state == 2){

            return;
        }

        //player movement
        if(player.orbiting && !freeing){

            playerangle += playeranglespeed;
            player.setX(currPlanetX + distfromplanet*Math.cos(playerangle) - (player.getWidth() / 2));
            player.setY(currPlanetY + distfromplanet*Math.sin(playerangle) - (player.getHeight() / 2));

        }else{

            player.incX(player.getVx());
            player.incY(player.getVy());
        }

        if(player.getCollision(end)){

            state = 1;
            soundcall.add("finish");
            return;
        }

        for(Entity block : blocks){

            if(player.getCollision(block)){

                state = 2;
                soundcall.add("explosion");
                return;
            }
        }

        //checking if we hit blackhole
        if(freeing && blackHoleIndex != -1){

            if(!player.getCollision(blackHoles.get(blackHoleIndex))){

                freeing = false;
                blackHoles.get(blackHoleIndex).orbiting = false;
                blackHoleIndex = -1;
            }
        }else{

            for(int i = 0; i < blackHoles.size(); i++){

                double offset = ((blackHoles.get(i).getWidth() - (blackHoles.get(i).getWidth() / Math.sqrt(2))) / 2);

                Rectangle rect = new Rectangle((int)(blackHoles.get(i).getX() + offset),
                        (int)(blackHoles.get(i).getY() + offset),
                        (int)(blackHoles.get(i).getWidth() / Math.sqrt(2)),
                        (int)(blackHoles.get(i).getHeight() / Math.sqrt(2)));

                if(player.getCollision(blackHoles.get(i)) && !freeing){

                    if(i % 2 == 1){

                        player.setPos(blackHoles.get(i - 1).getX(), blackHoles.get(i - 1).getY());
                        blackHoleIndex = i - 1;
                    }else{

                        player.setPos(blackHoles.get(i + 1).getX(), blackHoles.get(i + 1).getY());
                        blackHoleIndex = i + 1;
                    }
                    freeing = true;
                    blackHoles.get(i).orbiting = true;
                }
            }
        }

        //orbiting shit dear god
        for(Entity planet : planets){


            double offset = ((planet.getWidth() - (planet.getWidth() / Math.sqrt(2))) / 2);

            Rectangle rect = new Rectangle((int)(planet.getX() + offset),
                    (int)(planet.getY() + offset),
                    (int)(planet.getHeight() / Math.sqrt(2)),
                    (int)(planet.getHeight() / Math.sqrt(2)));

            if(player.getCollision(rect)){

                state = 2;
                soundcall.add("explosion");
                return;
            }

            double centerx = planet.getX() + (planet.getWidth() / 2);
            double centery = planet.getY() + (planet.getHeight() / 2);
            double planetRadius = (planet.getWidth() / 2) * 2.0;
            double playerx = player.getX() + (player.getWidth() / 2);
            double playery = player.getY() + (player.getHeight() / 2);
            double pdirx = playerx - centerx;
            double pdiry = playery - centery;
            double pdist = Math.sqrt((pdirx * pdirx) + (pdiry * pdiry));
            double vmag = Math.sqrt((player.getVx() * player.getVx()) + (player.getVy() * player.getVy()));
            double angle = Math.acos( (pdirx*player.getVx() + pdiry*player.getVy()) / (pdist * vmag) );
            angle *= (180 / Math.PI);
            angle = 180 - angle;

            if(pdist <= planetRadius){

                if(!freeing){

                    if(angle >= 75){

                        double oldvy = player.getVy();
                        double oldvx = player.getVx();
                        player.setVx(0);
                        player.setVy(0);
                        playerangle = Math.atan(pdiry / pdirx);
                        playeranglespeed =  (2 * Math.PI) / ((2 * Math.PI * pdist) / vmag );
                        int pasmod = 1;
                        if(pdirx > 0){

                            if(pdiry < 0){

                                if( oldvy < 0 || (oldvx < 0 && Math.abs(oldvx) > Math.abs(oldvy)) ){

                                    pasmod = -1;
                                }

                            }else{

                                if( (Math.abs(oldvx) > Math.abs(oldvy) && oldvx > 0) || (Math.abs(oldvx) < Math.abs(oldvy) && oldvy < 0) ){

                                    pasmod = -1;
                                }
                            }

                        }if(pdirx < 0){

                            if(pdiry < 0){

                                if( (oldvy > 0 && oldvx < 0) || (oldvy > 0 && Math.abs(oldvy) > Math.abs(oldvx)) ){

                                    pasmod = -1;
                                }

                            }else{

                                if( (oldvy > 0 && oldvx > 0) || (oldvx > 0 && Math.abs(oldvx) < Math.abs(oldvy)) ){

                                    pasmod = -1;
                                }
                            }

                            playerangle += Math.PI;
                        }
                        clockwise = (pasmod == 1);
                        playeranglespeed *= pasmod;
                        distfromplanet = pdist;
                        currPlanetX = centerx;
                        currPlanetY = centery;
                        player.orbiting = true;
                        planet.orbiting = true;
                    }
                }

            }else if(planet.orbiting){

                if(freeing){

                    freeing = false;
                    player.orbiting = false;
                    planet.orbiting = false;
                }
            }
        }
    }

    public int isFinished(){

        return state;
    }

    public void setState(int v){

        state = v;
    }

    public Entity getPlanet(int i){

        return planets.get(i);
    }

    public int noPlanets(){

        return planets.size();
    }

    public Entity getBlackHole(int i){

        return blackHoles.get(i);
    }

    public int getBHSize(){

        return blackHoles.size();
    }

    public Entity getPlayer(){

        return player;
    }

    public Entity getEnd(){

        return end;
    }

    public int noBlocks(){

        return blocks.size();
    }

    public Entity getBlock(int i){

        return blocks.get(i);
    }

    public double getPlayerAngle(){

        if(player.getVx() == 0){

            return 0;

        }else{

            double ra = Math.atan(player.getVy() / player.getVx());
            if(player.getVx() > 0){

                ra += (Math.PI / 2);

            }else{

                ra -= (Math.PI / 2);
            }

            return ra;
        }
    }

    public double getPlayerRenderAngle(){

        if(player.orbiting){

            double ra = playerangle;
            if(clockwise){ ra += Math.PI; }
            return ra;

        }else{

            return getPlayerAngle();
        }
    }

    public void playSound(String s){

        soundcall.add(s);
    }

    public String getSound(){

        if(soundcall.isEmpty()){

            return "";

        }else{

            return soundcall.remove();
        }
    }
}