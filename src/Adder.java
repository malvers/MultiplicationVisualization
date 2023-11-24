import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Adder {

    private final Font carryOverFont = new Font("Arial", Font.PLAIN, 18);
    private ArrayList numbersToAdd = new ArrayList<>();
    private ArrayList<Integer> allCarryOver = new ArrayList<>();
    private String result = "";
    private int numbersToWrite = 0;
    private String digitToWrite = "";
    private boolean isDone = false;
    private int carryOverToWrite = 0;

    public void setMyDebug(boolean myDebug) {
        this.myDebug = myDebug;
    }

    private boolean myDebug = false;

    public String getResult() {
        return result.toString();
    }

    protected int doAdditionManually(ArrayList<String> lines) {

//        System.out.println("Adder.doAdditionManually ...");

        isDone = false;
        result = "";
        digitToWrite = "";
        allCarryOver.clear();

        ArrayList<String> columns = new ArrayList<>();
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
            while (tmp.length() < 2 * lines.size()) {
                tmp.insert(0, "0");
            }

            theLine = new StringBuilder(tmp.toString());

            columns.add(theLine.toString());

        }
        if (myDebug) {
            printColumns(columns);
        }

        int carryOver = 0;
        for (int i = columns.get(0).length() - 1; i >= 0; i--) {

            int sum = 0;
            sum = getSumOneColumn(columns, i, sum);

            int toWrite;
            if (sum + carryOver >= 10) {
                sum += carryOver;
                toWrite = sum % 10;
                carryOver = sum / 10;
            } else {
                toWrite = sum + carryOver;
                carryOver = 0;
            }
            allCarryOver.add(carryOver);

            result += toWrite;
        }

        Collections.reverse(allCarryOver);

        StringBuilder reversedStringBuilder = new StringBuilder(result).reverse();
        result = reversedStringBuilder.toString();

        if (result.substring(0, 1).contains("0")) {
            result = result.substring(1);
        }

        numbersToWrite = result.length();
        return Integer.parseInt(result.toString());
    }

    protected String oneStep() {

        if (numbersToWrite <= 0) {
            return digitToWrite;
        }
        digitToWrite = result.substring(numbersToWrite - 1);
        carryOverToWrite = allCarryOver.get(numbersToWrite - 1);
        System.out.println(numbersToWrite + " carry over: " + carryOverToWrite);
        numbersToWrite--;
        return digitToWrite;
    }

    private int getSumOneColumn(ArrayList<String> linesLocal, int columNum, int sum) {

        for (int i = linesLocal.size() - 1; i >= 0; i--) {

            int digit = Character.getNumericValue(linesLocal.get(i).charAt(columNum));
            sum += digit;
            numbersToAdd.add(sum);
        }
        return sum;
    }

    protected void paint(Graphics2D g2d, int xPos, int yPos) {

        Font font = g2d.getFont();
        Color color = g2d.getColor();
        FontMetrics fontMetrics = g2d.getFontMetrics(font);

        int lengthResult = fontMetrics.stringWidth(result);
        int lengthToWrite = fontMetrics.stringWidth(digitToWrite);

        g2d.drawString(digitToWrite, xPos + lengthResult - lengthToWrite, yPos + font.getSize());

        if (myDebug) {
            printAllCarryOvers();
        }

        if (numbersToWrite - 1 < 0) {
            isDone = true;
            return;
        }

        g2d.setFont(carryOverFont);

        int myXPos;
        if (myDebug) {
            myXPos = xPos + lengthResult - 70;
            for (int i = allCarryOver.size() - 1; i >= 0; i--) {
                int carryOver = allCarryOver.get(i);
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawString(carryOver + "", myXPos, yPos - 4);
                myXPos -= 44;
            }
        }

        myXPos = xPos + lengthResult - lengthToWrite - 26;

        if (carryOverToWrite > 0) {
            g2d.setColor(MyColors.myRed);
            g2d.drawString(carryOverToWrite + "", myXPos, yPos - 4);
        }

        g2d.setFont(font);
        g2d.setColor(color);
    }

    private void printColumns(ArrayList<String> columns) {
        System.out.println("Columns printing");
        System.out.println("------------------");
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i);
            System.out.println(columns.get(i));
        }
        System.out.println("------------------");
    }

    private void printAllCarryOvers() {
        for (int i = 0; i < allCarryOver.size(); i++) {
            int carryOver = allCarryOver.get(i);
            System.out.print(carryOver);
        }
        System.out.println(" carry overs\n------------------");
    }

    public boolean isDone() {
        return isDone;
    }
}
