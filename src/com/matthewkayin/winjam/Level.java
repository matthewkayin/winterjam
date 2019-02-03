package com.matthewkayin.winjam;

import org.w3c.dom.css.Rect;

import javax.swing.text.html.parser.Entity;
import java.awt.*;
import java.util.ArrayList;

public class Level{

    public double playerangle;
    public double playeranglespeed;
    public double distfromplanet;
    public boolean freeing = false;
    public double currPlanetX;
    public double currPlanetY;
    private int state = 0; //0 is level is playing, 1 is level victory screen, 2 is level defeat screen, 3 is victory so return, 4 is loss so return

    public ArrayList<Entity> planets;

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

        planets = new ArrayList<Entity>();
        int index = 0;

        for(int i = 0; i < instructions.length; i++){

            if(instructions[i][0] == 0){

                player = new Entity();
                player.setSize(40, 40);
                player.setPos(instructions[i][1], instructions[i][2]);
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

                //create blackhole
                //set xpos = instructions[i][1];
                //set ypos = instruction[i][2];
            }
        }
    }

    public void impulse(double x, double y, double speed){

        double dirx = Math.abs(x - player.getX() + (player.getWidth() / 2));
        double diry = Math.abs(y - player.getY() + (player.getHeight() / 2));
        double hyp = Math.sqrt((dirx * dirx) + (diry * diry)); //pythagoras
        double diff = hyp / speed;
        double ndx = dirx / diff;
        double ndy = diry / diff;
        int xmod = 1;
        int ymod = 1;
        if(x < player.getX()){

            xmod = -1;
        }
        if(y < player.getY()){

            ymod = -1;
        }
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
            return;
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
                return;
            }

            double centerx = planet.getX() + (planet.getWidth() / 2);
            double centery = planet.getY() + (planet.getHeight() / 2);
            double planetRadius = (planet.getWidth() / 2) * 1.5;;
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

    public Entity getPlayer(){

        return player;
    }

    public Entity getEnd(){

        return end;
    }

    public double getPlayerAngle(){

        if(player.getVx() == 0){

            return 0;
        }

        double ra = Math.atan(player.getVy() / player.getVx());
        if(player.getVx() > 0){

            ra += (Math.PI / 2);

        }else{

            ra -= (Math.PI / 2);
        }

        return ra;
    }
}