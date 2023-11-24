import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

class AnimationObject {

    private final Visualizer vis;
    private double incX;
    private double incY;
    private final int stepsToRun;
    private Point2D from;
    private final Point2D runningPoint = new Point2D.Double(0, 0);
    private Point2D to;
    private int animeCounter = 0;
    private String toWrite = "not set yet";
    private boolean hasFromPosition;
    private boolean hasToPosition;
    private Timer timer = new Timer(10, e -> onStep());

    public AnimationObject(Visualizer vis) {

        this.vis = vis;
        from = new Point2D.Double();
        runningPoint.setLocation(from);
        to = new Point2D.Double();
        stepsToRun = 40;
        hasFromPosition = false;
        hasToPosition = false;
        initTimer();
    }

    private void initTimer() {
        timer = new Timer(10, e -> {
            onStep();
        });
    }

    public void setHasPositions(boolean fromPos, boolean toPos) {

        hasFromPosition = fromPos;
        hasToPosition = toPos;
    }

    private void calculateIncrements() {

        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        incX = dx / stepsToRun;
        incY = dy / stepsToRun;
    }

    public boolean hasPositions() {
        return !hasFromPosition || !hasToPosition;
    }

    public void setValue(int writeValue) {
        toWrite = "" + writeValue;
    }

    protected void onStep() {

        animeCounter++;
        if (animeCounter >= stepsToRun) {
            timer.stop();
        }
        vis.repaint();
        runningPoint.setLocation(runningPoint.getX() + incX, runningPoint.getY() + incY);
    }

    public void paintZoomFont(Graphics2D g2d) {

        if (!hasToPosition || !hasFromPosition) {
            return;
        }

        g2d.setColor(vis.myColors.get(vis.rightPos));
        g2d.setFont(vis.taskFont);

        int startSize = vis.taskFont.getSize();
        int endSize = vis.multiplicationLineFont.getSize();
        double delta = (double) (endSize - startSize) / (double) stepsToRun;

        int newFontSize = (int) (startSize + delta * animeCounter);

        g2d.setFont(new Font("Arial", Font.PLAIN, newFontSize));
        int xShift = g2d.getFontMetrics().stringWidth(toWrite);

        double deltaShift = xShift / (double) stepsToRun;
        double actualShift = deltaShift * animeCounter;

        g2d.drawString(toWrite, (int) ((int) runningPoint.getX() - actualShift), (int) runningPoint.getY());
    }

    public void paint(Graphics2D g2d) {

        if (!hasToPosition || !hasFromPosition) {
            return;
        }
        int xShift = g2d.getFontMetrics().stringWidth(toWrite);
        g2d.setColor(MyStuff.myRed);
        g2d.setFont(vis.taskFont);
        g2d.drawString(toWrite, (int) runningPoint.getX() - xShift, (int) runningPoint.getY());
    }

    public void setFromPosition(int xFrom, int yFrom) {

        from = new Point2D.Double(xFrom, yFrom);
        runningPoint.setLocation(from);
        hasFromPosition = true;

        animeCounter = 0;

        calculateIncrements();
    }

    public void setToPosition(int xTo, int yTo) {

        to = new Point2D.Double(xTo, yTo);
        hasToPosition = true;

        calculateIncrements();
    }

    public void start() {
        if (stepsToRun > 0) {
            timer.start();
        }
    }
}