package bumblebee.v2;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import static java.util.Collections.emptyMap;

/**
 */
public class World {
    static public int SIZE = 200;
    final Random random = new Random(0);
    static int DOT_RADIUS = 5;
    double viewAngle = 0.2;
    List<Dot> dots = new ArrayList();
    Actor actor = new Actor();

    Map<String, Runnable> commands = ImmutableMap.of(
            "fwd", this::commandFwd,
            "right", this::commandRight,
            "left", this::commandLeft
    );

    Bumblebee bumblebee = new Bumblebee(commands.keySet());

    static class Dot {
        double x, y;
        Color color;

        public Dot(double x, double y) {
            this.x = x;
            this.y = y;
            this.color = Color.GRAY;
        }

        public Dot(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        void draw(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillOval(getX() - DOT_RADIUS, getY() - DOT_RADIUS, DOT_RADIUS * 2, DOT_RADIUS * 2);
        }

        public double distance(double x1, double y1) {
            double a = getX() - x1;
            double b = getY() - y1;
            return Math.sqrt(a * a + b * b);
        }

        double distance(Point2D p){
            return distance(p.getX(), p.getY());
        }
    }

    public World() {
        for (int pos = DOT_RADIUS * 2; pos < SIZE - DOT_RADIUS * 2; pos += DOT_RADIUS * 3) {
            dots.add(new Dot(pos, DOT_RADIUS * 2));
            dots.add(new Dot(pos, SIZE - DOT_RADIUS * 2));
            dots.add(new Dot(DOT_RADIUS * 2, pos));
            dots.add(new Dot(SIZE - DOT_RADIUS * 2, pos));
        }
        dots.add(new Dot(50,100,Color.RED));
    }

    void draw(GraphicsContext gc) {
        dots.forEach(dot -> dot.draw(gc));

        drawBumblebee(gc);
    }

    void next() {
        
        commands.get(bumblebee.next(emptyMap())).run();
    }

    void commandLeft() {
        actor.rotation -= 0.3;
    }

    void commandRight() {
        actor.rotation += 0.3;
    }

    void commandFwd() {
        double RIGHT_BIAS = 0.03;
        //double RIGHT_BIAS = 0;
        actor.rotation += RIGHT_BIAS + random.nextGaussian() / 10;
        Point2D to = actor.direction(actor.rotation, 5 + 2 * random.nextGaussian());
        double minDistance = dots.stream().mapToDouble(dot -> dot.distance(to)).min().getAsDouble();
        if (minDistance > DOT_RADIUS * 5) {
            actor.x = to.getX();
            actor.y = to.getY();
        }
    }

    void drawBumblebee(GraphicsContext gc) {
        int radius = 10;
        gc.setFill(Color.BLACK);
        gc.fillOval(actor.x - radius, actor.y - radius, radius * 2, radius * 2);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);
        Point2D to = actor.direction(actor.rotation, 15);
        gc.strokeLine(actor.x, actor.y, to.getX(), to.getY());

        gc.setLineDashes(1,10);
        gc.setLineWidth(1);
        gc.setStroke(Color.BLUE);
        for (int i = -3; i <= 3; i += 2) {
            Point2D viewTo = actor.direction(actor.rotation + i * viewAngle / 2, 100);
            gc.strokeLine(actor.x, actor.y, viewTo.getX(), viewTo.getY());
        }
    }

    class Actor {
        double x = SIZE / 2;
        double y = SIZE / 3;
        double rotation = Math.PI / 8;

        Point2D direction(double rotation, double step) {
            return new Point2D(x + step * Math.cos(rotation), y + step * Math.sin(rotation));
        }
    }
}
