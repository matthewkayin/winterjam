package com.matthewkayin.winjam;

public class Level{

    public class Entity {

        private double x;
        private double y;
        private double vx;
        private double vy;
        private double w;
        private double h;
        private double ax;
        private double ay;

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

        public void update(){

            vx += ax;
            vy += ay;
            if(vy > 5){

                //vy = 5;
            }
            if(vx > 5){

                //vx = 5;
            }
            x += vx;
            y += vy;
        }
    }

    private Entity planet;
    private Entity bigplanet;
    private Entity player;

    public Level(){

        planet = new Entity();
        planet.setSize(200, 200);
        planet.setPos(650, 450);
        bigplanet = new Entity();
        bigplanet.setSize(350, 350);
        bigplanet.setPos(400, 25);
        player = new Entity();
        player.setSize(40, 40);
        player.setPos(550, 650);
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

    public void update(){

        player.update();

        double planetRadius = (planet.getWidth() / 2) * 1.7;
        double pdirx = Math.abs(player.getX() + (player.getWidth() / 2) - (planet.getX() + (planet.getWidth() / 2)));
        double pdiry = Math.abs(player.getY() + (player.getHeight() / 2) - (planet.getY() + (planet.getHeight() / 2)));
        double pdist = Math.sqrt( (pdirx * pdirx) + (pdiry * pdiry) );
        if(pdist <= planetRadius){

            double ming = 0.1;
            double maxg = 0.4;
            double percentStrength = 1 - (pdist / planetRadius);
            //percentStrength = Math.max(percentStrength, 0.7);
            double gs = ming + ((maxg - ming) * percentStrength);
            impulse(planet.getX() + (planet.getWidth() / 2), planet.getY() + (planet.getHeight() / 2), gs);
        }
    }

    public Entity getPlanet(){

        return planet;
    }

    public Entity getPlayer(){

        return player;
    }

    public Entity getBigplanet(){

        return bigplanet;
    }
}
