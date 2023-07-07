package Games.Breakout;

import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

import static java.awt.Color.*;

/**
 * Еhis class implements the Breakout game.
 *
 * It has:
 * 13 constants
 * ...
 * 5 variables declared inside the class
 * ...
 * 19 methods.
 * ...
 */
public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;

    /**
     * The amount of time to pause between frames (60fps).
     */
    private static final double PAUSE_TIME = 1000.0 / 60;

    /**
     * Array of colors.
     */
    private static final Color[] COLORS = {RED, ORANGE, YELLOW, GREEN, CYAN};

    private GObject rocket;
    private GObject ball;
    /**
     * Displacement factors.
     */
    private double vx = randomVX(), vy = 3.0;
    int countOfBricks = NBRICK_ROWS * NBRICKS_PER_ROW;


    public void run() {
        rocket = makeAndAddRocket();
        addMouseListeners();
        ball = makeAndAddBall();
        addBricks();
        moveBall(ball);
    }

    /**
     * This method implements the movement of the ball and its actions when in contact with other objects
     *
     * @param ball - ball which we are playing
     */
    private void moveBall(GObject ball) {
        for (int i = 0; i < NTURNS; ) {
            System.out.println("Click to start.");
            System.out.println("You have " + (NTURNS - i) + " attempts.");
            waitForClick();
            while (countOfBricks > 0) {
                ball.move(vx, vy);
                GObject collider = getCollidingObject();
                ballHitWall();
                ballHitCeiling();
                if (collider == rocket) {
                    ballHitRocket();
                }
                if (collider != rocket && collider != null) {
                    remove(collider);
                    vy = -vy;
                    countOfBricks--;
                }
                if (isBallUnderFloor()) {
                    ball.setLocation(getWidth() / 2.0 - BALL_RADIUS, getHeight() / 2.0 - BALL_RADIUS);
                    i++;
                    break;
                }
                pause(PAUSE_TIME);
            }
            if (countOfBricks == 0) {
                removeAll();
                addWinLabel();
            }
            if (i == NTURNS) {
                removeAll();
                addLoseLabel();
            }
        }
    }

    /**
     * This method print lose label
     */
    private void addLoseLabel() {
        GLabel win = new GLabel("You lose :.(");
        win.setLocation(getWidth() / 2.0 - win.getWidth() / 2.0, getHeight() / 2.0 - win.getDescent());
        add(win);
    }

    /**
     * This method print win label
     */
    private void addWinLabel() {
        GLabel win = new GLabel("You win!!!");
        win.setLocation(getWidth() / 2.0 - win.getWidth() / 2.0, getHeight() / 2.0 - win.getDescent());
        add(win);
    }

    /**
     * This method checks if ball touch the floor.
     *
     * @return - true/false
     */
    private boolean isBallUnderFloor() {
        return ball.getY() >= (getHeight() - BALL_RADIUS * 2);
    }

    /**
     * This method adds a grid of bricks.
     */
    private void addBricks() {
        double brickWidth = calculateBrickWidth();
        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                GRect brick = new GRect((brickWidth + BRICK_SEP) * j, BRICK_Y_OFFSET + (BRICK_SEP + BRICK_HEIGHT) * i, brickWidth, BRICK_HEIGHT);
                brickSetColorAndAdd(brick, COLORS[i / 2]);
            }
        }
    }

    /**
     * This method paints bricks and add it to the app.
     *
     * @param brick - Grect which we are painting.
     * @param color - the color we paint.
     */
    private void brickSetColorAndAdd(GRect brick, Color color) {
        brick.setColor(color);
        brick.setFilled(true);
        brick.setFillColor(color);
        add(brick);
    }

    /**
     * this method calculate brick width using width of app, from which is subtracted indents and divided by
     * count of bricks in row.
     *
     * @return - brick width.
     */
    private double calculateBrickWidth() {
        return (getWidth() - ((NBRICKS_PER_ROW - 1.0) * BRICK_SEP)) / NBRICKS_PER_ROW;

    }

    /**
     * This method to check if ball`s hitbox hit the rocket or brick
     *
     * @return - Gobject with which ball hits.
     */
    private GObject getCollidingObject() {
        //top left corner of ball hitbox
        if (getElementAt(ball.getX(), ball.getY()) != null) {
            return getElementAt(ball.getX(), ball.getY());
        }
        //top right corner of ball hitbox
        else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null) {
            return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY());
        }
        //bottom left corner of ball hitbox
        else if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null) {
            return getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS);
        }
        //bottom right corner of ball hitbox
        else if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS) != null) {
            return getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2 * BALL_RADIUS);
        }
        return null;
    }

    /**
     * This method change y to negative if ball hits the rocket.
     */
    private void ballHitRocket() {
        if (isBallHitRocket()) {
            vy = -vy;
        }
    }

    /**
     * This method check is ball hit the rocket
     *
     * @return - true of false;
     */
    private boolean isBallHitRocket() {
        return ball.getY() >= PADDLE_Y_OFFSET;
    }

    /**
     * This method change y to negative if ball hits the ceiling.
     */
    private void ballHitCeiling() {
        if (isBallHitCeiling(ball)) {
            vy = -vy;
        }
    }

    /**
     * This method change x to negative if ball hits the wall
     */
    private void ballHitWall() {
        if (isBallHitWall(ball)) {
            vx = -vx;
        }
    }

    /**
     * This method check is ball hit the wall
     *
     * @param ball - ball :)
     * @return - true of false;
     */
    private boolean isBallHitWall(GObject ball) {
        return ball.getX() <= 0 || ball.getX() >= (getWidth() - BALL_RADIUS * 2);
    }

    /**
     * This method check is ball hit the ceiling
     *
     * @param ball - ball :)
     * @return - true of false;
     */
    private boolean isBallHitCeiling(GObject ball) {
        return ball.getY() <= 0;
    }

    /**
     * This method generates a random number for vx in the range from 1 to 3 and with a probability of 50%
     * changes it to negative, then returns vx
     *
     * @return - random vx
     */
    private double randomVX() {
        RandomGenerator rgen = RandomGenerator.getInstance();
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;
        return vx;
    }

    /**
     * This method make, fill and add ball into the centre of window.
     *
     * @return - Ball in centre of window.
     */
    private GObject makeAndAddBall() {
        GOval ball = new GOval(getWidth() / 2.0 - BALL_RADIUS, getHeight() / 2.0 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        ball.setFilled(true);
        add(ball);
        return ball;
    }

    /**
     * This method make, fill and add rocket indented at the bottom PADDLE_Y_OFFSET of window.
     *
     * @return - Rocket which we are playing.
     */
    private GRect makeAndAddRocket() {
        GRect rocket = new GRect(getWidth() / 2.0 - PADDLE_WIDTH / 2.0, getHeight() - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        rocket.setFilled(true);
        add(rocket);
        return rocket;
    }

    /**
     * Repositions the dragged rocket to the mouse's location when the LMB pressed and mouse
     * is moved.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (rocket != null) {
            double newX = e.getX() - rocket.getWidth() / 2.0;
            if (newX > 0 && newX < getWidth() - PADDLE_WIDTH) {
                rocket.setLocation(newX, getHeight() - PADDLE_Y_OFFSET);
            }
        }
    }
}

