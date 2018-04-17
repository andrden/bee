package bumblebee.v1;

import javafx.geometry.Point2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 */
public class Bumblebee {

    final Color BGCOLOR = Color.WHITE;
    final double STEP = 0.5;
    final Random random = new Random(0);

    int width;
    int height;

    Point2D xy = new Point2D(50, 50);
    double rotation = 0;
    int stepsBeforeTurn = randomSteps();
    BufferedImage bufferedImage;

    public Bumblebee(int width, int height) {
        this.width = width;
        this.height = height;
        bufferedImage = makeScene();
    }

    void next() {
        Point2D newxy = direction(rotation, STEP);
        if (isEmpty(newxy)) { // move only along empty space
            xy = newxy;
        } else {
            newxy = findCrawlDestination();
            rotation = new Point2D(1, 0).angle(xy.subtract(newxy)) / 180 * Math.PI;
            xy = newxy;
        }
        if (--stepsBeforeTurn < 0) {
            stepsBeforeTurn = randomSteps();
            rotation += random.nextGaussian() * Math.PI / 3;
        }
    }

    Point2D findCrawlDestination() {
        double drotation = Math.min(1 / STEP, 0.1);
        double turn = drotation;
        while (turn < Math.PI * 2) {
            Point2D direction = direction(turn, STEP);
            Point2D directionMiniStep = direction(turn, STEP / 50);
            if (isEmpty(direction) && isEmpty(directionMiniStep)) {
                return directionMiniStep;
            }
            turn += drotation;
        }
        throw new IllegalStateException();
    }

    boolean isEmpty(Point2D point) {
        final int rgb = bufferedImage.getRGB((int) point.getX(), (int) point.getY());
        return (rgb == BGCOLOR.getRGB());
    }

    Point2D direction(double rotation, double step) {
        return new Point2D(xy.getX() + step * Math.cos(rotation), xy.getY() + step * Math.sin(rotation));
    }

    int randomSteps() {
        return 10 * (int) Math.max(10, 50 + random.nextGaussian() * 30);
    }

    private BufferedImage makeScene() {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
//        for (int i = 0; i < 100; i++) {
//            int rgb = new java.awt.Color(255, 100, 0).getRGB();
//            bufferedImage.setRGB(i, i * 2, rgb);
//        }
        final Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setPaint(BGCOLOR);
        graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        Random random = new Random(123);
        final Color rockColor = new Color(100, 80, 0);
        for (int object = 0; object < 15; object++) {
            if (random.nextDouble() > 0.25) {
                graphics.setPaint(Color.RED); // flower
            } else {
                graphics.setPaint(rockColor); // rock
            }
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            for (int petal = 0; petal < 10; petal++) {
                graphics.fillOval((int) (x + random.nextGaussian() * 10), (int) (y + random.nextGaussian() * 10),
                        (int) (40 + random.nextGaussian() * 5), (int) (40 + random.nextGaussian() * 5));
            }
        }
        graphics.setPaint(rockColor);
        final int BORDER = 5;
        graphics.fillRect(0, 0, bufferedImage.getWidth(), BORDER);
        graphics.fillRect(0, bufferedImage.getHeight() - BORDER, bufferedImage.getWidth(), BORDER);
        graphics.fillRect(0, 0, BORDER, bufferedImage.getHeight());
        graphics.fillRect(bufferedImage.getWidth() - BORDER, 0, BORDER, bufferedImage.getHeight());
        return bufferedImage;
    }
}
