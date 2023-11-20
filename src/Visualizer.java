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
    private String numbersRight = "6789";

    private final int numCols = 6;
    private final Dimension presSize = new Dimension(180, 86);
    private final Color myBlueColor = new Color(0, 0, 100);
    private final Font myFont80 = new Font("Arial", Font.PLAIN, 80);
    private final Font mySmallFont = new Font("Arial", Font.PLAIN, 24);
    private int g = 255;
    private final Color myGray = new Color(g, g, g);
    private int leftPos = 0;
    private int rightPos = 0;
    private boolean showResult = true;
    private int numDigits = 4;
    private String toBeWritten = "";
    private int singleResult = -1;
    private String singleResultStr = "";
    private int carryOver = 0;
    private Color myRedColor = new Color(180, 0, 0);
    private Color myOrangeColor = new Color(255, 190, 0);
    private Color myGreenColor = new Color(140, 180, 42);
    private Color myCyanColor = new Color(0, 150, 200);

    private ArrayList<Color> myColors = new ArrayList();
    private ArrayList<String> lines = new ArrayList<>();
    private int stepCounter = 0;
    private boolean multiplicationDone = false;
    private int fontSize80 = 80;

    public Visualizer() {

        addKeyListener(this);
        setOpaque(true);
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder());

        myColors.add(myGreenColor);
        myColors.add(myOrangeColor);
        myColors.add(myCyanColor);
        myColors.add(myRedColor);

        setLayout(new FlowLayout(FlowLayout.CENTER));
        leftInput = new JTextPane();
        leftInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        leftInput.setPreferredSize(presSize);
        leftInput.setBackground(myGray);
        leftInput.addKeyListener(this);
        leftInput.setFont(myFont80);

        setNumbers(true);

        rightInput = new JTextPane();
        rightInput.addKeyListener(this);
        rightInput.setPreferredSize(presSize);
        rightInput.setBackground(myGray);
        rightInput.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        rightInput.setFont(myFont80);

        setNumbers(false);

        add(Box.createVerticalStrut(240));
        add(leftInput);
        add(Box.createHorizontalStrut(100));
        add(rightInput);

        init();
        oneStep();
    }

    private void init() {
        leftPos = 0;
        rightPos = 0;
        toBeWritten = "";
        singleResultStr = "";
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

    private String createResultFromLines() {

        for (int i = 0; i < lines.size(); i++) {
        }

        int left = Integer.parseInt(numbersLeft);
        int right = Integer.parseInt(numbersRight);

        return "" + left * right;
    }

    private void randomNumbers() {

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
            int downYpos = (numDigits * fontSize80) + yPos + 20;
            g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
            g2d.setFont(myFont80);
            FontMetrics fontMetrics = g.getFontMetrics(g2d.getFont());
            String resultStr = createResultFromLines();
            int stringWidth = fontMetrics.stringWidth(resultStr);
            int localXpos = rightInput.getX() + rightInput.getWidth() - stringWidth;
            g2d.drawString(resultStr, localXpos, downYpos + fontSize80);

            downYpos += fontSize80 + 16;
            g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
            downYpos += 6;
            g2d.drawLine(xPos, downYpos, rightInput.getX() + rightInput.getWidth(), downYpos);
        }

        g2d.fillOval(getWidth() / 2, (int) (yPos - leftInput.getPreferredSize().height / 1.5), 10, 10);

        int pos1 = rightInput.getX();
        int width1 = 157;
        int gap = 26;
        int shift = 45;

        g2d.setFont(new Font("Arial", Font.PLAIN, 26));
        FontMetrics fontMetrics = g.getFontMetrics(g2d.getFont());
        int stringWidth = fontMetrics.stringWidth(singleResultStr);

        g2d.setColor(Color.GRAY);
        g2d.drawString(singleResultStr, getWidth() / 2 - stringWidth / 2, leftInput.getY() - 20);

        if (rightPos == 0) {
            width1 += (leftPos - 1) * shift;
        } else {
            width1 += (leftPos) * shift;
        }
        int addRight = rightPos * shift;
        int arcHeight = fontSize80;
//        g2d.drawArc(pos1 - width1 + gap, leftInput.getY() - arcHeight / 2, width1 + addRight, arcHeight, 0, 180);

        drawLineResults(g, g2d, yPos, shift);

        drawCarryOver(g2d, yPos, shift);
    }

    private void drawLineResults(Graphics g, Graphics2D g2d, int yPos, int shift) {

        FontMetrics fontMetrics;
        g2d.setFont(myFont80);
        fontMetrics = g.getFontMetrics(g2d.getFont());
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

            if (rightPos == 0) {
                carryPos = leftInput.getX() + leftInput.getWidth() - (leftPos) * shift - 8;
            } else {
                carryPos = leftInput.getX() + leftInput.getWidth() - (leftPos) * shift - 8;
            }
            g2d.drawString("" + carryOver, carryPos, yPos - 4);
        }
    }

    private void oneStep() {

        if (multiplicationDone) {
            return;
        }

        String leftDigits = leftInput.getText();
        String rightDigits = rightInput.getText();

        int digitLeft = leftDigits.charAt(3 - leftPos) - '0';
        int digitRight = rightDigits.charAt(rightPos) - '0';

        singleResult = digitLeft * digitRight;
        singleResultStr = "" + digitLeft + "・" + digitRight;

        int sum = singleResult + carryOver;

        String digitToWrite = "" + (sum % 10);

        if (singleResult >= 10) {
            carryOver = sum / 10;
        }

        singleResultStr += " = " + singleResult;
        singleResultStr += " + " + carryOver + " = " + (singleResult + carryOver); //+ " write " + digitToWrite + " carry " + carryOver;

        String s = digitLeft + " * " + digitRight + " = " + singleResult + " sum: " + sum + " write: " + digitToWrite + " carry: " + carryOver;
        s += " | leftPos: " + leftPos + " rightPos: " + rightPos + " step: " + stepCounter;
        System.out.println(s);

        String tmp = toBeWritten;
        toBeWritten = digitToWrite + tmp;

        if (leftPos == numDigits - 1) {
            System.out.println("add carry left ...");
            tmp = toBeWritten;
            toBeWritten = carryOver + tmp;
        }

        if (++stepCounter >= numDigits * numDigits) {
            multiplicationDone = true;
            carryOver = 0;
            singleResultStr = "";
            return;
        }

        leftPos++;

        if (leftPos > numDigits - 1) {
            if (rightPos < numDigits - 1) {
                rightPos++;
            }
            leftPos = 0;
            lines.add(toBeWritten);
            toBeWritten = "";
            carryOver = 0;
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
                oneStep();
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
                oneStep();
                break;
            case KeyEvent.VK_RIGHT:
                oneStep();
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_I:
                init();
                oneStep();
                break;
            case KeyEvent.VK_P:
                for (int i = 0; i < lines.size(); i++) {
                    System.out.println("line " + i + ": " + lines.get(i));
                }
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
