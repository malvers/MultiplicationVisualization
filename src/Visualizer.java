import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class Visualizer extends JButton implements KeyListener {

    private BufferedImage image;
    private AnimationObject animeWrite;
    private AnimationObject animeCarry;
    private final JTextPane leftInput;
    private final JTextPane rightInput;
    private long numberLeft = 23l;
    private String digitsLeftStr = "" + numberLeft;
    private int numDigitsLeft = digitsLeftStr.length();
    private long numberRight = 42l;
    private String digitsRightStr = "" + numberRight;
    private int numDigitsRight = digitsRightStr.length();
    protected final Font multiplicationLineFont = new Font("Arial", Font.PLAIN, 80);
    private final Font carryOverFont = new Font("Arial", Font.PLAIN, 24);
    private final Font debugFont = new Font("Arial", Font.PLAIN, 14);
    protected final Font taskFont = new Font("Arial", Font.PLAIN, 24);
    private int leftPos = 0;
    protected int rightPos = 0;
    private String toBeWritten = "";
    private int carryOver = 0;
    protected final ArrayList<Color> myColors = new ArrayList();
    private final ArrayList<String> lines = new ArrayList<>();
    private final ArrayList<Point> tasks = new ArrayList<>();
    private int stepCounter = 0;
    private boolean multiplicationDone = false;
    private final int fontSize80 = 80;
    private final Adder adder;
    private String trueSolution;
    private String resultFromAdder = "";
    private boolean myDebug = false;
    private boolean drawHelp = false;
    private int actualTask = 0;

    public Visualizer() {

        try {
            image = ImageIO.read(new File("clouds.jpg"));
        } catch (IOException e) {
            System.out.println("clouds not found :-/");
        }

        addKeyListener(this);
        setBorder(BorderFactory.createEmptyBorder());

        adder = new Adder();

        animeWrite = new AnimationObject(this);
        animeCarry = new AnimationObject(this);

        setLayout(new FlowLayout(FlowLayout.CENTER));
        leftInput = new JTextPane();
        leftInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

        int g = 255;

        leftInput.setBackground(MyColors.myTransparentColor);
        leftInput.setEditable(false);
        leftInput.setCaret(null);
        leftInput.addKeyListener(this);
        leftInput.setFont(multiplicationLineFont);

        setNumbers(true);

        rightInput = new JTextPane();
        rightInput.addKeyListener(this);
        rightInput.setBackground(MyColors.myTransparentColor);
        rightInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rightInput.setEditable(false);
        rightInput.setCaret(null);
        rightInput.setFont(multiplicationLineFont);

        setNumbers(false);

        add(Box.createVerticalStrut(240));
        add(leftInput);
        add(Box.createHorizontalStrut(100));
        add(rightInput);

        init();

        readSettings();
        adder.setMyDebug(myDebug);

        setBackground(MyColors.myOrange);
    }

    private void writeSettings() {
        try {
            String uh = System.getProperty("user.home");
            FileOutputStream f = new FileOutputStream(uh + "/MultiplicationVisualization.bin");
            ObjectOutputStream os = new ObjectOutputStream(f);

            os.writeBoolean(myDebug);
            os.writeInt(actualTask);

            os.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readSettings() {

        try {
            String uh = System.getProperty("user.home");
            FileInputStream f = new FileInputStream(uh + "/MultiplicationVisualization.bin");
            ObjectInputStream os = new ObjectInputStream(f);

            myDebug = os.readBoolean();
            actualTask = os.readInt();
            setDataForTask();

            os.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {

        animeCarry = new AnimationObject(this);
        animeWrite = new AnimationObject(this);
        leftPos = 0;
        rightPos = 0;
        toBeWritten = "";
        carryOver = 0;
        lines.clear();
        stepCounter = 0;
        multiplicationDone = false;
        resultFromAdder = "";

        fillTasks();
    }

    private void runTesting() {

        for (int i = 0; i <= 1000; i++) {
            if (i % 100 == 0) {
                System.out.println("i: " + i);
            }
            createRandomTask();
            for (int j = 0; j < numDigitsLeft * numDigitsRight; j++) {
                oneMultiplicationStep();
            }
            long r1 = adder.doAdditionManually(lines);
            int r2 = Integer.parseInt(calculateTrueSolution());

            if (r1 != r2) {
                System.out.println("ERROR ADDITION: " + r1 + " != " + r2);
            }
        }
    }

    private void createRandomTask() {

        init();

        digitsLeftStr = "" + (int) (Math.random() * 10000 + 1);
        digitsRightStr = "" + (int) (Math.random() * 10000 + 1);

        numDigitsLeft = digitsLeftStr.length();
        numDigitsRight = digitsRightStr.length();

        setNumbers(true);
        setNumbers(false);
    }

    private void setNumbers(boolean left) {

        StyledDocument doc;

        ArrayList<MutableAttributeSet> styles = new ArrayList();

        MutableAttributeSet redStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(redStyle, MyColors.myRed);

        MutableAttributeSet greenStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(greenStyle, MyColors.myGreen);

        MutableAttributeSet blueStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(blueStyle, MyColors.myBlue);

        MutableAttributeSet cyanStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(cyanStyle, MyColors.myCyan);

        MutableAttributeSet magentaStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(magentaStyle, MyColors.myMagenta);

        MutableAttributeSet orangeStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(orangeStyle, MyColors.myOrange);

        MutableAttributeSet grayStyle = new SimpleAttributeSet();
        StyleConstants.setForeground(grayStyle, MyColors.myGray);

        myColors.add(MyColors.myGreen);
        myColors.add(MyColors.myGray);
        myColors.add(MyColors.myOrange);
        myColors.add(MyColors.myMagenta);
        myColors.add(MyColors.myCyan);
        myColors.add(MyColors.myBlue);
        myColors.add(MyColors.myRed);

        styles.add(greenStyle);
        styles.add(grayStyle);
        styles.add(orangeStyle);
        styles.add(magentaStyle);
        styles.add(cyanStyle);
        styles.add(blueStyle);
        styles.add(redStyle);

        int myWidth = 46;
        if (left) {
            Dimension presSize = new Dimension(numDigitsLeft * myWidth, 86);
            leftInput.setText("");
            doc = leftInput.getStyledDocument();
            try {
                for (int i = 0; i < numDigitsLeft; i++) {
                    /// left draw all in blue
                    doc.insertString(doc.getLength(), "" + digitsLeftStr.charAt(i), blueStyle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            leftInput.setPreferredSize(presSize);

        } else {

            Dimension presSize = new Dimension(numDigitsRight * myWidth, 86);
            rightInput.setText("");
            doc = rightInput.getStyledDocument();

            try {
                for (int i = 0; i < numDigitsRight; i++) {
                    AttributeSet style;

                    /// right loop through styles
                    style = styles.get(i);
                    doc.insertString(doc.getLength(), "" + digitsRightStr.charAt(i), style);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            rightInput.setPreferredSize(presSize);
        }
    }

    private String calculateTrueSolution() {

        long left = Long.parseLong(leftInput.getText());
        long right = Long.parseLong(rightInput.getText());

        long exactSolution = left * right;
        String out = "exact solution: " + left + " * " + right + " = " + exactSolution;

        long ours = adder.doAdditionManually(lines);
        out += " our solution: " + ours;
        if (ours != exactSolution) {
            out += " ERROR manual addition!";
        }

        return "" + exactSolution;
    }

    private void oneMultiplicationStep() {

        if (multiplicationDone) {
            return;
        }

        String leftDigits = leftInput.getText();
        String rightDigits = rightInput.getText();

        int digitLeft = leftDigits.charAt(numDigitsLeft - 1 - leftPos) - '0';
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

        if (leftPos == numDigitsLeft - 1 && carryOver > 0) {
            toBeWritten = carryOver + toBeWritten;
        }

        /// multiplications are done now
        if (++stepCounter >= numDigitsLeft * numDigitsRight) {
            lines.add(toBeWritten);
            multiplicationDone = true;
            carryOver = 0;
            trueSolution = calculateTrueSolution();
            return;
        }

        leftPos++;

        if (leftPos >= numDigitsLeft) {

            if (rightPos < numDigitsRight) {
                rightPos++;
            }
            leftPos = 0;

            checkLineResult(leftDigits, digitRight);

            lines.add(toBeWritten);
            toBeWritten = "";
            carryOver = 0;
        }
    }

    /// drawing section ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        if (drawHelp) {
            drawHelpPage(g2d);
            return;
        }

        super.paint(g2d);

        drawImage(g2d);

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2.0f));

        int yPos = (leftInput.getY() + leftInput.getHeight() + 6);

        if (multiplicationDone) {
            int xPos = leftInput.getX();
            drawAfterMultiplication(g2d, yPos, xPos);
        }

        if (myDebug) {
            g2d.setColor(MyColors.myLightGray);
            g2d.setFont(debugFont);
            g2d.drawString("DEBUG", 10, 18);
        }

        g2d.setColor(MyColors.myBlue);

        // draw task line
        g2d.drawLine(leftInput.getX(), yPos, rightInput.getX() + rightInput.getWidth(), yPos);
        // draw multiplication dot
        int dist = rightInput.getX() - (leftInput.getX() + leftInput.getWidth());
        g2d.fillOval(rightInput.getX() - dist / 2, (int) (yPos - leftInput.getPreferredSize().height / 1.5), 10, 10);

        drawArcAndTask(g2d);

        int shift = 45;

        drawMultiplicationsLines(g2d, yPos, shift);

        drawCarryOver(g2d, yPos, shift);

        drawAnimation(g2d);
    }

    private void drawHelpPage(Graphics2D g2d) {

        g2d.setColor(MyColors.mySandLikeColor);
        g2d.fillRect(0,0, getWidth(), getHeight());

        int fs = 26;
//        Font font = new Font("SansSerif", Font.PLAIN, fs);
        Font font = new Font("Arial", Font.PLAIN, fs);
        g2d.setFont(font);
        g2d.setColor(Color.DARK_GRAY);

        int xPos = 20;
        int dy = (int) (fs * 1.5);
        int yPos = dy;
        int tab = 350;

        g2d.drawString("Arrow left / right", xPos, yPos);
        g2d.drawString("Run one step forward", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("Arrow up / down", xPos, yPos);
        g2d.drawString("Choose predefined tasks", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("Space bar", xPos, yPos);
        g2d.drawString("create random task", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("Esc key", xPos, yPos);
        g2d.drawString("Quit the program", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("Numbers 0 - 9", xPos, yPos);
        g2d.drawString("Choose predefined tasks directly", xPos + tab, yPos);
        yPos += dy;

         g2d.drawString("D", xPos, yPos);
        g2d.drawString("Toggle debug mode", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("H", xPos, yPos);
        g2d.drawString("Show this help page", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("I", xPos, yPos);
        g2d.drawString("Init actual task", xPos + tab, yPos);
        yPos += dy;

        g2d.drawString("Cmd W", xPos, yPos);
        g2d.drawString("Quit the program", xPos + tab, yPos);
        yPos += dy;
    }

    private void drawImage(Graphics2D g2d) {

        if (image == null) {
            return;
        }
        float alpha = 0.10f; // Adjust this value between 0.0f (fully transparent) and 1.0f (fully opaque)
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);
        g2d.drawImage((Image) image, 0, 0, (int) (image.getHeight() * 1.618), (int) (image.getHeight()), Color.BLACK, this);

        alpha = 1.0f; // Adjust this value between 0.0f (fully transparent) and 1.0f (fully opaque)
        alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g2d.setComposite(alphaComposite);
    }

    private void drawMultiplicationsLines(Graphics2D g2d, int yPos, int shift) {

//        System.out.println("drawMultiplicationsLines ...");
        g2d.setFont(multiplicationLineFont);
        FontMetrics fontMetrics = g2d.getFontMetrics(multiplicationLineFont);

        int lengthShift = fontMetrics.stringWidth(rightInput.getText());

        if (rightPos > 0) {

            int inputPos = rightInput.getX() + rightInput.getWidth();

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineLength = fontMetrics.stringWidth(line);
                int xPosLocal = inputPos - lineLength - (numDigitsRight - i - 1) * shift + numDigitsRight;
                g2d.setColor(myColors.get(i));
                g2d.drawString(line, xPosLocal, yPos + (i + 1) * fontSize80);
            }
        }
        if (multiplicationDone) {
            return;
        }

        int toBeWrittenLen = fontMetrics.stringWidth(toBeWritten);

        int xPosLocal = rightInput.getX() + rightInput.getWidth() - toBeWrittenLen - lengthShift + (rightPos + 1) * shift;

        int yPosLocal = yPos + (rightPos + 1) * fontSize80;
        animeWrite.setToPosition(xPosLocal, yPosLocal);
        int carryPos = leftInput.getX() + leftInput.getWidth() - (leftPos) * shift - 8;
        animeCarry.setToPosition(carryPos, yPos - 4);

        g2d.setColor(myColors.get(rightPos));
//        g2d.setColor(Color.MAGENTA);

        g2d.drawString("" + toBeWritten, xPosLocal, yPosLocal);
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

        int digitLeft = leftDigits.charAt(numDigitsLeft - 1 - leftPos) - '0';
        int digitRight = rightDigits.charAt(rightPos) - '0';

        int result = digitLeft * digitRight;
        String str = digitLeft + "・" + digitRight + " = " + result;

        int carryOverNew = result / 10;
        int write = (result + carryOver) % 10;

        if (carryOver > 0) {
            str += " + " + carryOver + " = " + (result + carryOver);
        }

        if (leftPos >= numDigitsLeft - 1 && carryOverNew > 0) {
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
        g2d.setColor(MyColors.myBlue);
        g2d.drawString(draw, leftXStart, yTaskPos);

        /// draw dot
        draw = str.substring(1, 2);
        g2d.setColor(MyColors.myGray);
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

            g2d.setColor(MyColors.myGray);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

            /// the digit after the + sign in red
            myStr = draw.substring(plusPos + 1, plusPos + 3);

            g2d.setColor(MyColors.myRed);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

            String helpStr = draw.substring(plusPos + 1);

            myStr = helpStr.substring(helpStr.indexOf("=") - 1, helpStr.indexOf("write") + 6);

            /// after the red digit in gray again
            g2d.setColor(MyColors.myGray);
            g2d.drawString(myStr, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(myStr);

        } else {

            g2d.setColor(MyColors.myGray);
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
            g2d.setColor(MyColors.myGray);
            g2d.drawString(draw, leftXStart + shift, yTaskPos);
            shift += fontMetrics.stringWidth(draw);

            draw = str.substring(str.indexOf("carry") + 5);
            g2d.setColor(MyColors.myRed);
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

        int increment = leftInput.getWidth() / numDigitsLeft;
        int halfLetter = increment / 2;

        int xPosLeft = rightInput.getX() + halfLetter - (gapBetweenInputs + increment) - (increment * leftPos);

        int xPosRight = rightInput.getX() + halfLetter + (increment * rightPos);

        int width = xPosRight - xPosLeft;

        int arcHeight = (int) (fontSize80 / 1.2);
        int yPos = leftInput.getY() - arcHeight / 3;

        g2d.setColor(myColors.get(rightPos));

        g2d.drawArc(xPosLeft, yPos, width, arcHeight, 0, 180);

        drawActualTask(g2d, xPosLeft, yPos - 16, width);
    }

    private void drawAfterMultiplication(Graphics2D g2d, int yPos, int xPos) {

        int downYPos = (numDigitsRight * fontSize80) + yPos + 20;
        g2d.drawLine(xPos, downYPos, rightInput.getX() + rightInput.getWidth(), downYPos);
        g2d.setFont(multiplicationLineFont);
        FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());

        int stringWidth = fontMetrics.stringWidth(trueSolution);
        int localXPos = rightInput.getX() + rightInput.getWidth() - stringWidth;

        g2d.setColor(MyColors.myLightGray);
        if (myDebug) {
            g2d.drawString(trueSolution, localXPos, downYPos + fontSize80);
        }

        g2d.setColor(MyColors.myBlue);
        int lengthResult = fontMetrics.stringWidth(adder.getResult());
        int lengthToWrite = fontMetrics.stringWidth(resultFromAdder);
        g2d.drawString(resultFromAdder, localXPos + lengthResult - lengthToWrite, downYPos + fontSize80);

        for (int i = 1; i < numDigitsRight; i++) {
            g2d.drawString("+", xPos, yPos + (i + 1) * fontSize80);
        }

        g2d.drawLine(xPos, yPos - fontSize80 - 16, rightInput.getX() + rightInput.getWidth(), yPos - fontSize80 - 16);

        g2d.setColor(MyColors.myBlue);

        adder.paint(g2d, localXPos, downYPos, trueSolution);

        if (adder.isDone()) {
            downYPos += fontSize80 + 16;
            g2d.drawLine(xPos, downYPos, rightInput.getX() + rightInput.getWidth(), downYPos);
            downYPos += 6;
            g2d.drawLine(xPos, downYPos, rightInput.getX() + rightInput.getWidth(), downYPos);
        }
    }

    private void drawCarryOver(Graphics2D g2d, int yPos, int shift) {

        if (carryOver > 0 && !multiplicationDone) {
            g2d.setColor(MyColors.myRed);
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

    private void runAllMultiplicationSteps() {
        for (int j = 0; j < numDigitsLeft * numDigitsRight; j++) {
            oneMultiplicationStep();
        }
    }

    private void fillTasks() {

        tasks.clear();
        tasks.add(new Point(98465, 45678));
        tasks.add(new Point(9846, 2052));
        tasks.add(new Point(23, 42));
        tasks.add(new Point(234, 456));
        tasks.add(new Point(234, 45678));
        tasks.add(new Point(2345, 4567));
        tasks.add(new Point(2345, 42));
        tasks.add(new Point(42, 4567));
        tasks.add(new Point(2, 111));
        tasks.add(new Point(111, 2));
    }

    private void setTask(int t) {
        actualTask = t;
        setDataForTask();
    }

    private void prevTask() {

        actualTask--;
        if (actualTask < 0) {
            actualTask = tasks.size() - 1;
        }

        setDataForTask();
    }

    private void nextTask() {

        actualTask++;
        if (actualTask >= tasks.size()) {
            actualTask = 0;
        }
        setDataForTask();
    }

    private void setDataForTask() {

        numberLeft = tasks.get(actualTask).x;
        numberRight = tasks.get(actualTask).y;

        digitsLeftStr = "" + numberLeft;
        digitsRightStr = "" + numberRight;

        numDigitsLeft = digitsLeftStr.length();
        numDigitsRight = digitsRightStr.length();

        setNumbers(true);
        setNumbers(false);

        init();
    }

    /// handle key events //////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        System.out.println("code: " + code);
        if (code > 47 && code < 58) {
            handleNumberKeys(code);
            repaint();
            return;
        }

        switch (e.getKeyCode()) {

            case KeyEvent.VK_SPACE:
                createRandomTask();
                runAllMultiplicationSteps();
                init();
                break;
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_UP:
                nextTask();
                runAllMultiplicationSteps();
                break;
            case KeyEvent.VK_DOWN:
                prevTask();
                runAllMultiplicationSteps();
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

            /// letter keys ////////////////////////////////////////////////////////////////////////////////////////////
            case KeyEvent.VK_D:
                myDebug = !myDebug;
                adder.setMyDebug(myDebug);
                break;
            case KeyEvent.VK_H:
                drawHelp = !drawHelp;
                break;
            case KeyEvent.VK_I:
                init();
                break;
            case KeyEvent.VK_P:
                runAllMultiplicationSteps();
                break;
            case KeyEvent.VK_T:
                runTesting();
                break;
            case KeyEvent.VK_ESCAPE:
            case KeyEvent.VK_W:
                writeSettings();
                System.exit(0);
                break;
        }
        repaint();
    }

    private void handleNumberKeys(int code) {
        /// number key codes range between 48 (0) and 57 (9)
        setTask(code - 48);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /// last but not least /////////////////////////////////////////////////////////////////////////////////////////////
    public static void main(String[] args) {

        JFrame f = new JFrame();
        Visualizer v = new Visualizer();
        f.add(v);
        int height = 800;
        f.setSize((int) (height * 1.618), height);
        f.setLocation(500, 0);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
