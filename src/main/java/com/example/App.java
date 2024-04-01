package com.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Random;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Random rnd = new Random();

        Group group = new Group();
        Scene scene = new Scene(group, 800, 600);

        ArrayList<Ball> balls = new ArrayList<Ball>();
        CollisionHandler collider = new CollisionHandler();

        Rectangle background = new Rectangle(0,0,800,600);
        background.setFill(Color.WHITE);
        group.getChildren().add(background);

        for(int i = 0; i < 50; i++){
            balls.add(new Ball(10 + rnd.nextInt(780), 10 + rnd.nextInt(580), rnd.nextInt(20), rnd.nextInt(20), Ball.R, collider));
            group.getChildren().add(balls.get(i));
        }

        Timer tim = new Timer();
        tim.scheduleAtFixedRate(collider, 0, 1);

        background.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

class Ball extends Circle {
    Nvect pressedForce;
    double x, y, r, er, k, m;
    Nvect V;
    public static final double R = 10;
    CollisionHandler collider;
    EventHandler<MouseEvent> clickHandler, dragHandler, releaseHandler;
    
    Ball(double x, double y, double vx, double vy, double r, CollisionHandler collider){
        super(r);
        this.r = r;
        this.k = 25;
        this.x = x;
        this.y = y;
        this.m = this.r/10;
        this.V = new Nvect(vx, vy);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.pressedForce = null;
        this.clickHandler = new BallClickHandler(this);
        this.releaseHandler = new BallReleaseHandler(this);
        this.dragHandler = new BallDragHandler(this);
        this.setOnMousePressed(clickHandler);
        this.setOnMouseDragged(dragHandler);
        this.setOnMouseReleased(releaseHandler);
        collider.addBall(this);
        this.collider = collider;
    }

    void move(double x, double y){
        this.setLayoutX(this.x = x);
        this.setLayoutY(this.y = y);
    }
}

class BallClickHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallClickHandler(Ball ball){
        this.ball = ball;
    }
    
    @Override
    public void handle(MouseEvent arg0) { 
        ball.pressedForce = null;
    }
}

class BallDragHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallDragHandler(Ball ball){
        this.ball = ball;
    }

    @Override
    public void handle(MouseEvent event) {
        ball.pressedForce = null;
    }
}

class BallReleaseHandler implements EventHandler<MouseEvent>{
    Ball ball;

    BallReleaseHandler(Ball ball){
        this.ball = ball;
    }
    
    @Override
    public void handle(MouseEvent arg0) { 
        ball.pressedForce = null;
    }
}

class CollisionHandler extends TimerTask{
    ArrayList<Ball> balls = new ArrayList<Ball>();
    double K=50;

    void addBall(Ball ball){
        balls.add(ball);
    }
    
    @Override
    public void run(){

        //Check collisions with walls
        for(Ball b : balls){
            if(b.x < b.r || b.x > 800-b.r){
                b.V = b.V.add(new Nvect((b.x < 400 ? b.r - b.x : (800 - b.r) - b.x) * (b.k * K) / (b.k + K) / b.m, 0));
            }
            if(b.y < b.r || b.y > 600-b.r){
                b.V = b.V.add(new Nvect(0, (b.y < 300 ? b.r - b.y : (600 - b.r) - b.y) * (b.k * K) / (b.k + K) / b.m));
            }

            //b.V = b.V.add(new Nvect(0, 5));  // g implementation
            b.V = b.V.mul(0.99999); // special condition for stopping

            if(b.pressedForce != null){
                b.V = b.V.add(b.pressedForce.mul(1/b.m)); //doesn't work =(
            }
        }

        //Check collisions with other balls
        if(balls.size() >= 2){
            for(int i = 0; i < balls.size() - 1; i++){
                for(int j = i+1; j < balls.size(); j++){
                    Ball b1 = balls.get(i), b2 = balls.get(j);
                    Nvect axe = new Nvect(b2.x-b1.x, b2.y-b1.y);
                    double
                    shift = b1.r + b2.r - axe.len(),
                    k = (b1.k * b2.k) / (b1.k + b2.k);
                    if(b1.r + b2.r > axe.len()){
                        double absF = k * shift;
                        axe = axe.mul(absF/axe.len());
                        b1.V = b1.V.sub(new Nvect(axe.mul(1/b1.m)));
                        b2.V = b2.V.add(new Nvect(axe.mul(1/b2.m)));
                        
                    }
                }
            }
        }

        for(Ball b: balls){
            b.move(b.x + b.V.coords[0] * 0.01, b.y + b.V.coords[1] * 0.01);
        }
    }
}