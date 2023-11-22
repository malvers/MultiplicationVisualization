import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Visualizer extends JButton implements KeyListener {

    class AnimationObject {

        private double incX;
        private double incY;
        private Timer timer = new Timer(10, e -> {
            anime.onStep();
            repaint();
        });
        private final int stepsToRun;
        private Point2D from;
        private final Point2D runningPoint = new Point2D.Double(0, 0);
        private Point2D to;
        private int animeCounter = 0;
        private String toWrite = "not set yet";
        private boolean hasFromPosition;
        private boolean hasToPosition;

        public AnimationObject(int steps) {

            from = new Point2D.Double();
            runningPoint.setLocation(from);
            to = new Point2D.Double();
            stepsToRun = steps;
            hasFromPosition = false;
            hasToPosition = false;
            initTimer();
        }

        public void setHasPositions(boolean fromPos, boolean toPos) {

            hasFromPosition = fromPos;
            hasToPosition = toPos;
        }

        private void initTimer() {
            timer = new Timer(10, e -> {
                anime.onStep();
                repaint();
            });
        }

        public void start() {
            timer.start();
        }

        private void calculateIncrements() {

            double dx = to.getX() - from.getX();
            double dy = to.getY() - from.getY();
            incX = dx / stepsToRun;
            incY = dy / stepsToRun;
        }

        public boolean hasPositions() {
            return hasFromPosition && hasToPosition;
        }

        public void setValue(int writeValue) {
            toWrite = "" + writeValue;
        }

        private void onStep() {

            animeCounter++;
            if (animeCounter >= stepsToRun) {
                timer.stop();
            }
            runningPoint.setLocation(runningPoint.getX() + incX, runningPoint.getY() + incY);
        }

        public void paint(Graphics2D g2d) {

            if (!hasToPosition || !hasFromPosition) {
                return;
            }

            g2d.setColor(myColors.get(rightPos));
            g2d.setFont(taskFont);

            int startSize = taskFont.getSize();
            int endSize = multiplicationLineFont.getSize();
            double delta = (double) (endSize - startSize) / (double) stepsToRun;

            int newFontSize = (int) (startSize + delta * animeCounter);

            g2d.setFont(new Font("Arial", Font.PLAIN, newFontSize));
            int xShift = g2d.getFontMetrics().stringWidth(toWrite);

            double deltaShift = xShift / (double) stepsToRun;
            double actualShift = deltaShift * animeCounter;

            g2d.drawString(toWrite, (int) ((int) runningPoint.getX() - actualShift), (int) runningPoint.getY());
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
    }

    private AnimationObject anime;
    private final JTextPane leftInput;
    private final JTextPane rightInput;
    private String numbersLeft = "9876";
    private String numbersRight = "9876";
    private final Color myBlueColor = new Color(0, 0, 100);
    private final Font multiplicationLineFont = new Font("Arial", Font.PLAIN, 80);
    private final Font carryOverFont = new Font("Arial", Font.PLAIN, 24);
    private final Font taskFont = new Font("Arial", Font.PLAIN, 26);
    private int leftPos = 0;
    private int rightPos = 0;
    private final int numDigits = 4;
    private String toBeWritten = "";
    private int carryOver = 0;
    private final int g = 100;
    private final Color myGrayColor = new Color(g, g, g);
    private final ArrayList<Color> myColors = new ArrayList();
    private final ArrayList<String> lines = new ArrayList<>();
    private int stepCounter = 0;
    private boolean multiplicationDone = false;
    private final int fontSize80 = 80;
    private String resultStr = "";

    public Visualizer() {

        addKeyListener(this);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        anime = new AnimationObject(40);

        Color myGreenColor = new Color(140, 180, 42);
        myColors.add(myGreenColor);
        Color myOrangeColor = new Color(255, 190, 0);
        myColors.add(myOrangeColor);
        Color myCyanColor = new Color(0, 150, 200);
        myColors.add(myCyanColor);
        Color myRedColor = new Color(180, 0, 0);
        myColors.add(myRedColor);

        setLayout(new FlowLayout(FlowLayout.CENTER));
        leftInput = new JTextPane();
        leftInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        Dimension presSize = new Dimension(180, 86);
        leftInput.setPreferredSize(presSize);
        int g = 255;
        Color myLightGrayColor = new Color(g, g, g);
        leftInput.setBackground(myLightGrayColor);
        leftInput.addKeyListener(this);
        leftInput.setFont(multiplicationLineFont);

        setNumbers(true);

        rightInput = new JTextPane();
        rightInput.addKeyListener(this);
        rightInput.setPreferredSize(presSize);
        rightInput.setBackground(myLightGrayColor);
        rightInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rightInput.setFont(multiplicationLineFont);

        setNumbers(false);

        add(Box.createVerticalStrut(240));
        add(leftInput);
        add(Box.createHorizontalStrut(100));
        add(rightInput);

        init();
    }

    private void init() {

        anime = new AnimationObject(40);
        leftPos = 0;
        rightPos = 0;
        toBeWritten = "";
        carryOver = 0;
        lines.clear();
        stepCounter = 0;
        multiplicationDone = false;
    }

    private void setNumbers(boolean left) {

        StyledDocument doc;

        MutableAttributeSet blueStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(blueStyle, myBlueColor);

        MutableAttributeSet greenStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(greenStyle, myColors.get(0));

        MutableAttributeSet orangeStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(orangeStyle, myColors.get(1));

        MutableAttributeSet cyanStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(cyanStyle, myColors.get(2));

        MutableAttributeSet redStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(redStyle, myColors.get(3));

        if (left) {
            leftInput.setText("");
            doc = leftInput.getStyledDocument();
            try {
                doc.insertString(doc.getLength(), "" + numbersLeft.charAt(0), blueStyle);
                doc.insertString(doc.getLength(), "" + numbersLeft.charAt(1), blueStyle);
                doc.insertString(doc.getLength(), "" + numbersLeft.charAt(2), blueStyle);
                doc.insertString(doc.getLength(), "" + numbersLeft.charAt(3), blueStyle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            rightInput.setText("");
            doc = rightInput.getStyledDocument();

            try {
                doc.insertString(doc.getLength(), "" + numbersRight.charAt(0), greenStyle);
                doc.insertString(doc.getLength(), "" + numbersRight.charAt(1), orangeStyle);
                doc.insertString(doc.getLength(), "" + numbersRight.charAt(2), cyanStyle);
                doc.insertString(doc.getLength(), "" + numbersRight.charAt(3), redStyle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String calculateTrueSolution() {

        int left = Integer.parseInt(leftInput.getText());
        int right = Integer.parseInt(rightInput.getText());

        int exactSolution = left * right;
        String out = "exact solution: " + left + " * " + right + " = " + exactSolution;

        int ours = doAdditionManually();
        out += " our solution: " + ours;
        if (ours != exactSolution) {
            out += " ERROR manual addition!";
        }
        System.out.println(out);

        return "" + exactSolution;
    }

    private int doAdditionManually() {

//        System.out.println("doAdditionManually ...");

        ArrayList<String> linesLocal = new ArrayList<>();
        int num0right = lines.size() - 1;

        for (int i = 0; i < lines.size(); i++) {

            StringBuilder theLine = new StringBuilder(lines.get(i));

            for (int j = 0; j < num0right; j++) {
                theLine.append("0");
            }
            num0right--;

            StringBuilder tmp = new StringBuilder(theLine.toString());
            for (int j = 0; j < i; j++) {
                tmp.insert(0, "0");
            }

            /// hack
            while (tmp.length() < 2 * numDigits) {
//                System.out.println("tmp: " + tmp.length());
                tmp.insert(0, "0");
            }

            theLine = new StringBuilder(tmp.toString());

            linesLocal.add(theLine.toString());
        }

//        System.out.println("Print lines local ...");
//        for (int i = 0; i < linesLocal.size(); i++) {
//            System.out.println(linesLocal.get(i));
//        }
//        System.out.println("Calculate ... ");

        StringBuilder result = new StringBuilder();
        int carryOver = 0;
        for (int i = linesLocal.get(0).length() - 1; i >= 0; i--) {

            int sum = 0;
//            System.out.print("line: " + i + " -> ");
            for (String s : linesLocal) {

                int digit = Character.getNumericValue(s.charAt(i));
                sum += digit;
//                System.out.print(digit);
            }

            int toWrite;
            if (sum + carryOver >= 10) {
                sum += carryOver;
                toWrite = sum % 10;
                carryOver = sum / 10;
            } else {
//                carryOver = 0;
                toWrite = sum + carryOver;
            }
//            System.out.print(" sum: " + sum + " write " + toWrite + " cary: " + carryOver);
            result.append(toWrite);
//            System.out.println();
        }

//        System.out.println("");

        StringBuilder reversedStringBuilder = new StringBuilder(result.toString()).reverse();
        result = new StringBuilder(reversedStringBuilder.toString());

//        int intResult = Integer.parseInt(result.toString());

//        System.out.println("int result: " + intResult);
//        System.out.println("doAdditionManually done ...");

        return Integer.parseInt(result.toString());
    }

    private void randomNumbers() {

        init();

        Random random = new Random();
        numbersLeft = "" + random.nextInt(9000) + 1000;
        numbersRight = "" + random.nextInt(9000) + 1000;

        setNumbers(true);
        setNumbers(false);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(myBlueColor);

        int yPos = (leftInput.getY() + leftInput.getHeight() + 6);

        if (multiplicationDone) {
            drawAfterMultiplication(g2d, yPos, leftInput.getX());
        }

        // draw task line
        g2d.drawLine(leftInput.getX(), yPos, rightInput.getX() + rightInput.getWidth(), yPos);
        // draw multiplication dot
        g2d.fillOval(getWidth() / 2, (int) (yPos - leftInput.getPreferredSize().height / 1.5), 10, 10);

        drawArcAndTask(g2d);

        int shift = 45;

        drawMultiplicationsLines(g2d, yPos, shift);

        drawCarryOver(g2d, yPos, shift);

        drawAnimation(g2d);
    }

    private void drawAnimation(Graphics2D g2d) {

        if (anime == null) {
            return;
        }
        anime.paint(g2d);
    }

    private void drawActualTask(Graphics2D g2d, int xPos, int yTaskPos, int width) {

        g2d.setFont(taskFont);
        FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());

        String leftDigits = leftInput.getText();
        String rightDigits = rightInput.getText();

        int digitLeft = leftDigits.charAt(3 - leftPos) - '0';
        int digitRight = rightDigits.charAt(rightPos) - '0';

        int result = digitLeft * digitRight;
        String str = digitLeft + "・" + digitRight + " = " + result;

        int carryOverNew = result / 10;
        int write = (result + carryOver) % 10;

        if (carryOver > 0) {
            str += " + " + carryOver + " = " + (result + carryOver);
        }

        if (leftPos >= numDigits - 1 && carryOverNew > 0) {
            str += " ➙ write " + (result + carryOver);
        } else {
            str += " ➙ write " + write;
            if (carryOverNew > 0) {
                str += " carry " + carryOverNew;
            }
        }

        int stringWidth = fontMetrics.stringWidth(str);

        int leftXStart = xPos + width / 2 - stringWidth / 2;

        /// draw left digit
        String draw = str.substring(0, 1);
        int shift = fontMetrics.stringWidth(draw);
        g2d.setColor(myBlueColor);
        g2d.drawString(draw, leftXStart, yTaskPos);

        /// draw dot
        draw = str.substring(1, 2);
        g2d.setColor(myGrayColor);
        g2d.drawString(draw, leftXStart + shift, yTaskPos);
        shift += fontMetrics.stringWidth(draw);

        /// draw right digit
        draw = str.substring(2, 3);
        g2d.setColor(myColors.get(rightPos));
        g2d.drawString(draw, leftXStart + shift, yTaskPos);
        shift += fontMetrics.stringWidth(draw);

        /// paint in gray up to the number to be written
        int pos = str.indexOf("write") + 6;
        draw = str.substring(3, pos);

        /// draw the digit after the + sign in red
        int plusPos = draw.indexOf("+");
        if (plusPos >= 0) {

            /// up to the + sign in gray
            String myStr = draw.substring(0, plusPos + 1);

            g2d.setColor(myGrayColor);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

            /// the digit after the + sign in red
            myStr = draw.substring(plusPos + 1, plusPos + 3);

            g2d.setColor(Color.RED);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

            String helpStr = draw.substring(plusPos + 1);

            myStr = helpStr.substring(helpStr.indexOf("=") - 1, helpStr.indexOf("write") + 6);

            /// after the red digit in gray again
            g2d.setColor(myGrayColor);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

        } else {

            g2d.setColor(myGrayColor);
            g2d.drawString(draw, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(draw);
        }

        /// paint the number to be written in its color
        if (!str.contains("carry")) {
            draw = str.substring(pos);
            g2d.setColor(myColors.get(rightPos));
            int xTaskPos = leftXStart + shift;
            g2d.drawString(draw, xTaskPos, yTaskPos);

            int writeValue = Integer.parseInt(draw.trim());
            if (!anime.hasPositions()) {
                anime.setFromPosition(xTaskPos, yTaskPos);
                anime.setValue(writeValue);
            }
        } else {
            draw = str.substring(pos, str.indexOf("carry"));
            g2d.setColor(myColors.get(rightPos));
            int xTaskPos = leftXStart + shift;
            g2d.drawString(draw, xTaskPos, yTaskPos);
            int writeValue = Integer.parseInt(draw.trim());

            if (!anime.hasPositions()) {
                anime.setFromPosition(xTaskPos, yTaskPos);
                anime.setValue(writeValue);
            }

            shift += fontMetrics.stringWidth(draw);

            draw = str.substring(str.indexOf("carry"), str.indexOf("carry") + 5);
            g2d.setColor(myGrayColor);
            g2d.drawString(draw, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(draw);

            draw = str.substring(str.indexOf("carry") + 5);
            g2d.setColor(Color.RED);
            g2d.drawString(draw, leftXStart + shift, yTaskPos);
        }
    }

    private void drawArcAndTask(Graphics2D g2d) {

        if (multiplicationDone || (leftPos == 0 && rightPos == 0)) {
            return;
        }

        int rightPosLeftInput = leftInput.getX() + leftInput.getWidth();
        int leftPosRightInput = rightInput.getX();

        int gapBetweenInputs = leftPosRightInput - rightPosLeftInput;

        int increment = leftInput.getWidth() / numDigits;
        int halfLetter = increment / 2;

        int xPosLeft = rightInput.getX() + halfLetter - (gapBetweenInputs + increment) - (increment * leftPos);

        int xPosRight = rightInput.getX() + halfLetter + (increment * rightPos);

        int width = xPosRight - xPosLeft;

        int arcHeight = (int) (fontSize80 / 1.2);
        int yPos = leftInput.getY() - arcHeight / 3;

        g2d.setColor(myColors.get(rightPos));
//        g2d.fillRect(xPosLeft - 4, yPos - 4, 8, 8);
//        g2d.fillRect(xPosRight - 4, yPos - 4, 8, 8);

        g2d.drawArc(xPosLeft, yPos, width, arcHeight, 0, 180);

        drawActualTask(g2d, xPosLeft, yPos - 10, width);
    }

    private void oneMultiplicationStep() {

        if (multiplicationDone) {
            return;
        }

        String leftDigits = leftInput.getText();
        String rightDigits = rightInput.getText();

        int digitLeft = leftDigits.charAt(3 - leftPos) - '0';
        int digitRight = rightDigits.charAt(rightPos) - '0';

        int singleResult = digitLeft * digitRight;

        int sum = singleResult + carryOver;

        String digitToWrite = "" + (sum % 10);

        if (singleResult + carryOver >= 10) {
            carryOver = sum / 10;
        } else {
            carryOver = 0;
        }

        toBeWritten = digitToWrite + toBeWritten;

        if (leftPos == numDigits - 1 && carryOver > 0) {
            toBeWritten = carryOver + toBeWritten;
        }

        /// multiplications are done now
        if (++stepCounter >= numDigits * numDigits) {
            lines.add(toBeWritten);
            multiplicationDone = true;
            resultStr = calculateTrueSolution();
            carryOver = 0;
            return;
        }

        leftPos++;

        if (leftPos >= numDigits) {

            if (rightPos < numDigits) {
                rightPos++;
            }
            leftPos = 0;

            checkLineResult(leftDigits, digitRight);

            lines.add(toBeWritten);
            toBeWritten = "";
            carryOver = 0;
        }
    }

    private void drawAfterMultiplication(Graphics2D g2d, int yPos, int xPos) {

        int downYpos = (numDigits * fontSize80) + yPos + 20;
        g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
        g2d.setFont(multiplicationLineFont);
        FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());

        int stringWidth = fontMetrics.stringWidth(resultStr);
        int localXPos = rightInput.getX() + rightInput.getWidth() - stringWidth;
        g2d.drawString(calculateTrueSolution(), localXPos, downYpos + fontSize80);

        for (int i = 2; i < 5; i++) {
            g2d.drawString("+", xPos, yPos + i * fontSize80);
        }

        downYpos += fontSize80 + 16;
        g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
        downYpos += 6;
        g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);

        g2d.drawLine(xPos, yPos - fontSize80 - 16, rightInput.getX() + rightInput.getWidth(), yPos - fontSize80 - 16);
    }

    private void drawMultiplicationsLines(Graphics2D g2d, int yPos, int shift) {

        g2d.setFont(multiplicationLineFont);
        FontMetrics fontMetrics = g2d.getFontMetrics(multiplicationLineFont);
        if (rightPos > 0) {

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineLength = fontMetrics.stringWidth(line);
                g2d.setColor(myColors.get(i));
                g2d.drawString(line, rightInput.getX() + ((i + 1) * shift) - lineLength, yPos + (i + 1) * fontSize80);
            }
        }

        int toBeWrittenLen = fontMetrics.stringWidth(toBeWritten);
        g2d.setColor(myColors.get(rightPos));
        int xPosLocal = rightInput.getX() + ((rightPos + 1) * shift) - toBeWrittenLen;
        int yPosLocal = yPos + (rightPos + 1) * fontSize80;
        anime.setToPosition(xPosLocal, yPosLocal);
        g2d.drawString("" + toBeWritten, xPosLocal, yPosLocal);
    }

    private void drawCarryOver(Graphics2D g2d, int yPos, int shift) {

        if (carryOver > 0) {
            g2d.setColor(Color.RED);
            g2d.setFont(carryOverFont);
            int carryPos;
            carryPos = leftInput.getX() + leftInput.getWidth() - (leftPos) * shift - 8;
            g2d.drawString("" + carryOver, carryPos, yPos - 4);
        }
    }

    private void checkLineResult(String leftDigits, int digitRight) {

        int leftNumber = Integer.parseInt(leftDigits);
        int checkLineResult = leftNumber * digitRight;
        if (Integer.parseInt(toBeWritten) != checkLineResult) {
            System.out.println("LINE RESULT - ERROR:    " + leftNumber + " * " + digitRight + " = " + checkLineResult + " != " + toBeWritten);
        } else {
            System.out.println("LINE RESULT - ALL FINE: " + leftNumber + " * " + digitRight + " = " + checkLineResult + " == " + toBeWritten);
        }
    }

    private void runTesting() {

        for (int i = 0; i <= 1000; i++) {
            if (i % 100 == 0) {
                System.out.println("i: " + i);
            }
            randomNumbers();
            for (int j = 0; j < numDigits * numDigits; j++) {
                oneMultiplicationStep();
            }
            int r1 = doAdditionManually();
            int r2 = Integer.parseInt(calculateTrueSolution());

            if (r1 != r2) {
                System.out.println("ERROR ADDITION: " + r1 + " != " + r2);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                randomNumbers();
                init();
                break;
            case KeyEvent.VK_ENTER:

                break;
            case KeyEvent.VK_W:
                if (e.isMetaDown()) {
                    System.exit(0);
                }
                break;
            case KeyEvent.VK_UP:
                rightPos++;
                if (rightPos > 3) {
                    rightPos = 3;
                }
                break;
            case KeyEvent.VK_DOWN:
                rightPos--;
                if (rightPos < 0) {
                    rightPos = 0;
                }
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                anime.setHasPositions(false, false);
                oneMultiplicationStep();
                anime.start();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_I:
                init();
                break;
            case KeyEvent.VK_D:
                System.out.println("Debug ...");
                break;
            case KeyEvent.VK_P:
                for (int j = 0; j < numDigits * numDigits; j++) {
                    oneMultiplicationStep();
                }
                for (int i = 0; i < lines.size(); i++) {
                    System.out.println("line " + i + ": " + lines.get(i));
                }
                break;

            case KeyEvent.VK_T:
                runTesting();
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public static void main(String[] args) {

        JFrame f = new JFrame();
        Visualizer v = new Visualizer();
        f.add(v);
        f.setSize(1200, 700);
        f.setLocation(200, 40);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
