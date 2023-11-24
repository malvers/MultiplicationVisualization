import com.sun.prism.impl.PrismSettings;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Visualizer extends JButton implements KeyListener {

    private AnimationObject animeWrite;
    private AnimationObject animeCarry;
    private final JTextPane leftInput;
    private final JTextPane rightInput;
    private String numbersLeft = "2931";
    private String numbersRight = "7203";
    protected final Font multiplicationLineFont = new Font("Arial", Font.PLAIN, 80);
    private final Font carryOverFont = new Font("Arial", Font.PLAIN, 24);
    protected final Font taskFont = new Font("Arial", Font.PLAIN, 24);
    private int leftPos = 0;
    protected int rightPos = 0;
    private final int numDigits = 4;
    private String toBeWritten = "";
    private int carryOver = 0;

    protected final ArrayList<Color> myColors = new ArrayList();
    private final ArrayList<String> lines = new ArrayList<>();
    private int stepCounter = 0;
    private boolean multiplicationDone = false;
    private final int fontSize80 = 80;
    private final Adder adder;
    private String trueSolution;
    private String resultFromAdder = "";
    private boolean myDebug = false;

    public Visualizer() {

        addKeyListener(this);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        adder = new Adder();

        animeWrite = new AnimationObject(40, this);
        animeCarry = new AnimationObject(40, this);

        myColors.add(MyStuff.myGreen);
        myColors.add(MyStuff.myOrange);
        myColors.add(MyStuff.myCyan);
        myColors.add(MyStuff.myMagenta);

        setLayout(new FlowLayout(FlowLayout.CENTER));
        leftInput = new JTextPane();
        leftInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        Dimension presSize = new Dimension(180, 86);
        leftInput.setPreferredSize(presSize);
        int g = 255;

        leftInput.setBackground(Color.WHITE);
        leftInput.addKeyListener(this);
        leftInput.setFont(multiplicationLineFont);

        setNumbers(true);

        rightInput = new JTextPane();
        rightInput.addKeyListener(this);
        rightInput.setPreferredSize(presSize);
        rightInput.setBackground(Color.WHITE);
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

        animeCarry = new AnimationObject(40, this);
        animeWrite = new AnimationObject(40, this);
        leftPos = 0;
        rightPos = 0;
        toBeWritten = "";
        carryOver = 0;
        lines.clear();
        stepCounter = 0;
        multiplicationDone = false;
        resultFromAdder = "";
    }

    private void setNumbers(boolean left) {

        StyledDocument doc;

        MutableAttributeSet blueStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(blueStyle, MyStuff.myBlueColor);

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

        int ours = adder.doAdditionManually(lines);
        out += " our solution: " + ours;
        if (ours != exactSolution) {
            out += " ERROR manual addition!";
        }
//        System.out.println(out);

        return "" + exactSolution;
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
        g2d.setColor(MyStuff.myBlueColor);

        int yPos = (leftInput.getY() + leftInput.getHeight() + 6);

        if (multiplicationDone) {
            int xPos = leftInput.getX();
            drawAfterMultiplication(g2d, yPos, xPos);
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

        if (animeWrite == null || animeCarry == null) {
            return;
        }
        animeWrite.paintZoomFont(g2d);
        animeCarry.paint(g2d);
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
        g2d.setColor(MyStuff.myBlueColor);
        g2d.drawString(draw, leftXStart, yTaskPos);

        /// draw dot
        draw = str.substring(1, 2);
        g2d.setColor(MyStuff.myGrayColor);
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

            g2d.setColor(MyStuff.myGrayColor);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

            /// the digit after the + sign in red
            myStr = draw.substring(plusPos + 1, plusPos + 3);

            g2d.setColor(MyStuff.myRed);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

            String helpStr = draw.substring(plusPos + 1);

            myStr = helpStr.substring(helpStr.indexOf("=") - 1, helpStr.indexOf("write") + 6);

            /// after the red digit in gray again
            g2d.setColor(MyStuff.myGrayColor);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

        } else {

            g2d.setColor(MyStuff.myGrayColor);
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
            if (animeWrite.hasPositions()) {
                animeWrite.setFromPosition(xTaskPos, yTaskPos);
                animeWrite.setValue(writeValue);
            }
        } else {
            draw = str.substring(pos, str.indexOf("carry"));
            g2d.setColor(myColors.get(rightPos));
            int xTaskPos = leftXStart + shift;
            g2d.drawString(draw, xTaskPos, yTaskPos);
            int writeValue = Integer.parseInt(draw.trim());

            if (animeWrite.hasPositions()) {
                animeWrite.setFromPosition(xTaskPos, yTaskPos);
                animeWrite.setValue(writeValue);
            }

            shift += fontMetrics.stringWidth(draw);

            draw = str.substring(str.indexOf("carry"), str.indexOf("carry") + 5);
            g2d.setColor(MyStuff.myGrayColor);
            g2d.drawString(draw, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(draw);

            draw = str.substring(str.indexOf("carry") + 5);
            g2d.setColor(MyStuff.myRed);
            g2d.drawString(draw, leftXStart + shift, yTaskPos);

            if (animeCarry.hasPositions()) {
                animeCarry.setFromPosition(leftXStart + shift, yTaskPos);
                animeCarry.setValue(Integer.parseInt(draw.trim()));
            }
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

        drawActualTask(g2d, xPosLeft, yPos - 16, width);
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
            carryOver = 0;
            trueSolution = calculateTrueSolution();
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

        int stringWidth = fontMetrics.stringWidth(trueSolution);
        int localXPos = rightInput.getX() + rightInput.getWidth() - stringWidth;

        g2d.setColor(MyStuff.myLightGray);
        if (myDebug) {
            g2d.drawString(trueSolution, localXPos, downYpos + fontSize80);
        }

        g2d.setColor(MyStuff.myBlueColor);
        int lengthResult = fontMetrics.stringWidth(adder.getResult());
        int lengthToWrite = fontMetrics.stringWidth(resultFromAdder);
//        g2d.drawString(resultFromAdder, localXPos + lengthResult - lengthToWrite, downYpos + fontSize80);

        for (int i = 2; i < 5; i++) {
            g2d.drawString("+", xPos, yPos + i * fontSize80);
        }

        g2d.drawLine(xPos, yPos - fontSize80 - 16, rightInput.getX() + rightInput.getWidth(), yPos - fontSize80 - 16);

        g2d.setColor(MyStuff.myBlueColor);
        adder.paint(g2d, localXPos, downYpos);

        if (adder.isDone()) {
            downYpos += fontSize80 + 16;
            g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
            downYpos += 6;
            g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
        }

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
        animeWrite.setToPosition(xPosLocal, yPosLocal);
        int carryPos = leftInput.getX() + leftInput.getWidth() - (leftPos) * shift - 8;
        animeCarry.setToPosition(carryPos, yPos - 4);
        g2d.drawString("" + toBeWritten, xPosLocal, yPosLocal);
    }

    private void drawCarryOver(Graphics2D g2d, int yPos, int shift) {

        if (carryOver > 0 && !multiplicationDone) {
            g2d.setColor(MyStuff.myRed);
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
            //System.out.println("LINE RESULT - ALL FINE: " + leftNumber + " * " + digitRight + " = " + checkLineResult + " == " + toBeWritten);
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
            int r1 = adder.doAdditionManually(lines);
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
                if (multiplicationDone) {
                    resultFromAdder = adder.oneStep();
                } else {
                    animeWrite.setHasPositions(false, false);
                    animeCarry.setHasPositions(false, false);
                    oneMultiplicationStep();
                    animeWrite.start();
                    animeCarry.start();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_I:
                init();
                break;
            case KeyEvent.VK_D:
                myDebug = !myDebug;
                System.out.println("Debug..." + myDebug);
                break;
            case KeyEvent.VK_P:
                for (int j = 0; j < numDigits * numDigits; j++) {
                    oneMultiplicationStep();
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
        f.setLocation(400, 00);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
