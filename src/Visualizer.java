import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Visualizer extends JButton implements KeyListener {

    private final JTextPane leftInput;
    private final JTextPane rightInput;
    private String numbersLeft = "9876";
    private String numbersRight = "5432";
    private final Color myBlueColor = new Color(0, 0, 100);
    private final Font myFont80 = new Font("Arial", Font.PLAIN, 80);
    private final Font mySmallFont = new Font("Arial", Font.PLAIN, 24);
    private int rightPos = 0;
    private final int numDigits = 4;
    private String toBeWritten = "";
    private String actualTaskStr = "";
    private int carryOver = 0;
    private final Color myGrayColor = new Color(200, 200, 200);
    private LeftPos leftPos = new LeftPos();
    private ArrayList<Color> myColors = new ArrayList();
    private final ArrayList<String> lines = new ArrayList<>();
    private int stepCounter = 0;
    private boolean multiplicationDone = false;
    private final int fontSize80 = 80;

    public Visualizer() {

        addKeyListener(this);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

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
        leftInput.setFont(myFont80);

        setNumbers(true);

        rightInput = new JTextPane();
        rightInput.addKeyListener(this);
        rightInput.setPreferredSize(presSize);
        rightInput.setBackground(myLightGrayColor);
        rightInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rightInput.setFont(myFont80);

        setNumbers(false);

        add(Box.createVerticalStrut(240));
        add(leftInput);
        add(Box.createHorizontalStrut(100));
        add(rightInput);

        init();
    }

    private void init() {

        leftPos.set(0);
        rightPos = 0;
        toBeWritten = "";
        actualTaskStr = "";
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

        int left = Integer.parseInt(numbersLeft);
        int right = Integer.parseInt(numbersRight);

        System.out.println("True solution: " + left + " * " + right + " = " + (left * right));

        return "" + left * right;
    }

    private int doAdditionManually() {

        System.out.println("doAdditionManually ...");

        ArrayList<String> linesLocal = new ArrayList<>();
        int num0right = lines.size() - 1;

        for (int i = 0; i < lines.size(); i++) {

            StringBuilder theLine = new StringBuilder(lines.get(i));

            for (int j = 0; j < num0right; j++) {
                theLine.append("0");
            }
            num0right--;

            String tmp = theLine.toString();
            for (int j = 0; j < i; j++) {
                tmp = "0" + tmp;
            }

            /// hack
            while (tmp.length() < 2 * numDigits) {
                System.out.println("tmp: " + tmp.length());
                tmp = "0" + tmp;
            }

            theLine = new StringBuilder(tmp);

            linesLocal.add(theLine.toString());
        }

        System.out.println("Print lines local ...");
        for (int i = 0; i < linesLocal.size(); i++) {
            System.out.println(linesLocal.get(i));
        }
        System.out.println("Calculate ... ");
        String result = "";
        int carryOver = 0;
        for (int i = linesLocal.get(0).length() - 1; i >= 0; i--) {

            int sum = 0;
            System.out.print("line: " + i + " -> ");
            for (int j = 0; j < linesLocal.size(); j++) {

                int digit = Character.getNumericValue(linesLocal.get(j).charAt(i));
                sum += digit;
                System.out.print(digit);
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
            System.out.print(" sum: " + sum + " write " + toWrite + " cary: " + carryOver);
            result += "" + toWrite;
            System.out.println();
        }

        System.out.println("");

        StringBuilder reversedStringBuilder = new StringBuilder(result).reverse();
        result = reversedStringBuilder.toString();

        int intResult = Integer.parseInt(result);
        System.out.println("int result: " + intResult);

        System.out.println("doAdditionManually done ...");
        return intResult;
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
        int yPos = (leftInput.getY() + /*(rightPos+1) * */leftInput.getHeight() + 6);
        int xPos = leftInput.getX();

        g2d.drawLine(xPos, yPos, rightInput.getX() + rightInput.getWidth(), yPos);

        if (multiplicationDone) {
            drawAfterMultiplication(g2d, yPos, xPos);
        }

        g2d.fillOval(getWidth() / 2, (int) (yPos - leftInput.getPreferredSize().height / 1.5), 10, 10);

        int shift = 45;

        drawArc(g2d, shift);

        drawMultiplicationsLines(g2d, yPos, shift);

        drawCarryOver(g2d, yPos, shift);
    }

    private void drawActualTask(Graphics2D g2d, int xPos, int yPos, int width) {

        g2d.setFont(new Font("Arial", Font.PLAIN, 26));
        FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());
        int stringWidth = fontMetrics.stringWidth(actualTaskStr);

        g2d.setColor(myColors.get(rightPos));
        //g2d.drawString(actualTaskStr, xPos + width / 2 - stringWidth / 2, yPos - 6);
    }

    private void drawArc(Graphics2D g2d, int shift) {

        if (multiplicationDone || (leftPos.get() == 0 && rightPos == 0)) {
            return;
        }

        int rightPosLeftInput = leftInput.getX() + leftInput.getWidth();
        int leftPosRightInput = rightInput.getX();

        int gapBetweenInputs = leftPosRightInput - rightPosLeftInput;

        int increment = leftInput.getWidth() / numDigits;
        int halfLetter = increment / 2;

        int xPosLeft = rightInput.getX() + halfLetter - (gapBetweenInputs + increment) - (increment * leftPos.get());

        int xPosRight = rightInput.getX() + halfLetter  + (increment * rightPos);

        int width = xPosRight - xPosLeft;

        System.out.println(width + " leftPos: " + leftPos.get() + " rightPos: " + rightPos);

        int arcHeight = (int) (fontSize80 / 1.2);
        int yPos = leftInput.getY() - arcHeight / 3;

        g2d.setColor(myColors.get(rightPos));
        g2d.fillRect(xPosLeft - 4, yPos - 4, 8, 8);
        g2d.fillRect(xPosRight - 4, yPos - 4, 8, 8);

        g2d.drawArc(xPosLeft, yPos, width, arcHeight, 0, 180);

//        drawActualTask(g2d, xPos, yPos, width);
    }

    private void oneMultiplicationStep() {

        if (multiplicationDone) {
            return;
        }

        String leftDigits = leftInput.getText();
        String rightDigits = rightInput.getText();

        String previousTaskStr = actualTaskStr;

        int digitLeft = leftDigits.charAt(3 - leftPos.get()) - '0';
        int digitRight = rightDigits.charAt(rightPos) - '0';

        int singleResult = digitLeft * digitRight;
        actualTaskStr = "" + digitLeft + "・" + digitRight;

        int sum = singleResult + carryOver;

        String digitToWrite = "" + (sum % 10);

        if (singleResult + carryOver >= 10) {
            carryOver = sum / 10;
        } else {
            carryOver = 0;
        }

        actualTaskStr += " = " + singleResult;

        String tmp = toBeWritten;
        toBeWritten = digitToWrite + tmp;

        if (leftPos.get() == numDigits - 1 && carryOver > 0) {
            toBeWritten = carryOver + toBeWritten;
        }

        /// multiplications are done now
        if (++stepCounter >= numDigits * numDigits) {
            lines.add(toBeWritten);
            multiplicationDone = true;
            carryOver = 0;
            actualTaskStr = "";
            return;
        }

        leftPos.inc();

//        System.out.println("leftPos: " + leftPos + " rightPos: " + rightPos + " actualTaskStr: " + actualTaskStr + " previousTask: " + previousTaskStr);

        if (leftPos.get() >= numDigits) {

//            System.out.println("in leftPos >= numDigits: " + " actualTask: " + " previousTask: " + previousTaskStr);
            if (rightPos < numDigits) {
                rightPos++;
            }
            leftPos.set(0);

            checkResult(leftDigits, digitRight);

            lines.add(toBeWritten);
            toBeWritten = "";
            carryOver = 0;
        }
    }

    private void drawAfterMultiplication(Graphics2D g2d, int yPos, int xPos) {

        int downYpos = (numDigits * fontSize80) + yPos + 20;
        g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
        g2d.setFont(myFont80);
        FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());
        String resultStr = calculateTrueSolution();
        int stringWidth = fontMetrics.stringWidth(resultStr);
        int localXpos = rightInput.getX() + rightInput.getWidth() - stringWidth;
        g2d.drawString(resultStr, localXpos, downYpos + fontSize80);

        downYpos += fontSize80 + 16;
        g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
        downYpos += 6;
        g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
    }

    private void drawMultiplicationsLines(Graphics2D g2d, int yPos, int shift) {

        g2d.setFont(myFont80);
        FontMetrics fontMetrics = g2d.getFontMetrics(myFont80);
        if (rightPos > 0) {

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int lineLength = fontMetrics.stringWidth(line);
                g2d.setColor(myColors.get(i));
                g2d.drawString(line, rightInput.getX() + ((i + 1) * shift) - lineLength, yPos + (i + 1) * fontSize80);
            }
        }

        int toBeWrittenLength = fontMetrics.stringWidth(toBeWritten);
        g2d.setColor(myColors.get(rightPos));
        g2d.drawString("" + toBeWritten, rightInput.getX() + ((rightPos + 1) * shift) - toBeWrittenLength, yPos + (rightPos + 1) * fontSize80);
    }

    private void drawCarryOver(Graphics2D g2d, int yPos, int shift) {

        if (carryOver > 0  /*&&leftPos < numDigits - 1*/) {
            g2d.setColor(Color.RED);
            g2d.setFont(mySmallFont);
            int carryPos;

            carryPos = leftInput.getX() + leftInput.getWidth() - (leftPos.get()) * shift - 8;
            g2d.drawString("" + carryOver, carryPos, yPos - 4);
        }
    }

    private void checkResult(String leftDigits, int digitRight) {
        int leftNumber = Integer.parseInt(leftDigits);
        int checkLineResult = leftNumber * digitRight;
        if (Integer.parseInt(toBeWritten) != checkLineResult) {
            System.out.println("ERROR: " + leftNumber + " * " + digitRight + " = " + checkLineResult + " != " + toBeWritten);
        } else {
            System.out.println("ALL FINE: " + leftNumber + " * " + digitRight + " = " + checkLineResult + " == " + toBeWritten);
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
                oneMultiplicationStep();
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
                oneMultiplicationStep();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_I:
                init();
                break;
            case KeyEvent.VK_D:
                init();
                for (int j = 0; j < numDigits * numDigits; j++) {
                    oneMultiplicationStep();
                }
                int r1 = doAdditionManually();
                int r2 = Integer.parseInt(calculateTrueSolution());
                if (r1 != r2) {
                    System.out.println("ERROR ADDITION: " + r1 + " != " + r2);
                }

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
