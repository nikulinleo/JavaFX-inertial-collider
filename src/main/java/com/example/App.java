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

    ArrayList<Ball> balls = new ArrayList<Ball>();

    @Override
    public void start(Stage stage) throws IOException {

        Random rnd = new Random();

        Group group = new Group();
        Scene scene = new Scene(group, 800, 600);

        CollisionHandler collider = new CollisionHandler();

        Rectangle background = new Rectangle(0,0,800,600);
        background.setFill(Color.WHITE);
        group.getChildren().add(background);

        for(int i = 0; i < 50; i++){
            Ball t = new Ball(10 + rnd.nextInt(780), 10 + rnd.nextInt(580), rnd.nextInt(50), rnd.nextInt(50), Ball.R, collider);
            balls.add(t);
            group.getChildren().add(t);
        }

        Timer tim = new Timer();
        tim.scheduleAtFixedRate(collider, 0, 5);

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
    double x, y, vx, vy, r, k, m;
    public static final double R = 10;
    CollisionHandler collider;
    EventHandler<MouseEvent> clickHandler, dragHandler, releaseHandler;
    
    Ball(double x, double y, double vx, double vy, double r, CollisionHandler collider){
        super(r);
        this.r = r;
        this.k = 40;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.m = this.r/10;
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
                b.vx += (b.x < 400 ? b.r - b.x : (800 - b.r) - b.x) * (b.k * K) / (b.k + K) / b.m;
            }
            if(b.y < b.r || b.y > 600-b.r){
                b.vy += (b.y < 300 ? b.r - b.y : (600 - b.r) - b.y) * (b.k * K) / (b.k + K) / b.m;
            }

            //b.vy += 4;  // g implementation
            b.vx -= 0.00001 * b.vx * b.vx * (b.vx > 0? 1: -1); // special condition for stopping
            b.vy -= 0.00001 * b.vy * b.vy * (b.vy > 0? 1: -1);

            if(b.pressedForce != null){
                //action for pressed ball
            }
        }

        //Check collisions with other balls
        if(balls.size() >= 2){
            for(int i = 0; i < balls.size() - 1; i++){
                for(int j = i+1; j < balls.size(); j++){

                    Ball b1 = balls.get(i), b2 = balls.get(j);
                    
                    double
                    k = (b1.k * b2.k) / (b1.k + b2.k),
                    dx = b2.x-b1.x, 
                    dy = b2.y-b1.y,
                    dist = Math.sqrt(dx*dx + dy*dy);

                    if(b1.r + b2.r > dist){
                        double absF = (b1.r + b2.r - dist)  * k;
                        b1.vx -= absF * dx / dist /b1.m;
                        b1.vy -= absF * dy / dist /b1.m;
                        b2.vx += absF * dx / dist /b2.m;
                        b2.vy += absF * dy / dist /b2.m;
                    }
                }
            }
        }

        for(Ball b: balls){
            b.move(b.x + b.vx * 0.001, b.y + b.vy * 0.001);
        }
    }
}