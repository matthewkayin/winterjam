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

        public void update(){

            vx += ax;
            vy += ay;
            if(vy > 5){

                vy = 7;
            }
            if(vx > 5){

                vx = 7;
            }
            x += vx;
            y += vy;
        }

        public double getMass(){

            return (4.0 / 3.0) * Math.PI * (w / 2) * (w / 2) * (w / 3);
        }
    }

    private Entity planet;
    private Entity bigplanet;
    private Entity player;
    private Entity end;

    public Level(){

        planet = new Entity();
        planet.setSize(200, 200);
        planet.setPos(650, 450);
        bigplanet = new Entity();
        bigplanet.setSize(350, 350);
        bigplanet.setPos(400, 25);
        player = new Entity();
        player.setSize(40, 40);
        player.setPos(300, 650);
        player.setPos(550, 650);

        end = new Entity();
        end.setSize(50, 50);
        end.setPos(50, 50);
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

        player.update();

        double planetRadius = (planet.getWidth() / 2) * 1.5;
        double centerx = planet.getX() + (planet.getWidth() / 2);
        double centery = planet.getY() + (planet.getHeight() / 2);
        double playerx = player.getX() + (player.getWidth() / 2);
        double playery = player.getY() + (player.getHeight() / 2);
        double pdirx = Math.abs(playerx - centerx);
        double pdiry = Math.abs(playery - centery);
        double pdist = Math.sqrt((pdirx * pdirx) + (pdiry * pdiry));
        double vmag = Math.sqrt((player.getVx() * player.getVx()) + (player.getVy() * player.getVy()));
        double angle = Math.acos( (pdirx*player.getVx() + pdiry*player.getVy()) / (pdist * vmag) );
        angle *= (180 / Math.PI);

        if(pdist <= planetRadius && !player.orbiting){

            if(angle <= 105 && angle >= 75){

                double diff = pdist / planetRadius;
                double nxdist = pdirx / diff;
                double nydist = pdiry / diff;
                player.setX(-(nxdist - pdirx) + player.getX());
                player.setY(-(nydist - pdiry) + player.getY());

                double g = 0.00005 * player.getMass() * planet.getMass() / (planetRadius * planetRadius);
                playerx = player.getX() + (player.getWidth() / 2);
                playery = player.getY() + (player.getHeight() / 2);
                pdirx = playerx - centerx;
                pdiry = playery - centery;
                double vdirx = (pdirx * Math.cos(angle*(Math.PI/180))) - (pdiry * Math.sin(angle*(Math.PI/180)));
                double vdiry = (pdirx * Math.sin(angle*(Math.PI/180))) + (pdiry * Math.cos(angle*(Math.PI/180)));
                player.setVx(0);
                player.setVy(0);
                impulse(player.getX() + vdirx, player.getY() + vdiry, g);
            }

        }else if(player.orbiting){

            double g = 0.00005 * player.getMass() * planet.getMass() / (planetRadius * planetRadius);
            playerx = player.getX() + (player.getWidth() / 2);
            playery = player.getY() + (player.getHeight() / 2);
            pdirx = playerx - centerx;
            pdiry = playery - centery;
            double vdirx = (pdirx * Math.cos(angle*(Math.PI/180))) - (pdiry * Math.sin(angle*(Math.PI/180)));
            double vdiry = (pdirx * Math.sin(angle*(Math.PI/180))) + (pdiry * Math.cos(angle*(Math.PI/180)));
            player.setVx(0);
            player.setVy(0);
            impulse(player.getX() + vdirx, player.getY() + vdiry, g);
        }

        if
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

    public Entity getEnd(){

        return end;
    }
}