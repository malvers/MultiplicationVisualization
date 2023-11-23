import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class Adder {

    private final Font carryOverFont = new Font("Arial", Font.PLAIN, 24);
    private ArrayList numbersToAdd = new ArrayList<>();
    private ArrayList<Integer> allCarryOver = new ArrayList<>();
    private String result = "";
    private int numbersToWrite = 0;
    private String toWrite = "";

    public String getResult() {
        return result.toString();
    }

    protected int doAdditionManually(ArrayList<String> lines, boolean verbose) {

        System.out.println("Adder.doAdditionManually ...");

        result = "";
        toWrite = "";
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

        printColumns(columns);

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
            result += toWrite;
        }

        Collections.reverse(allCarryOver);

        if (allCarryOver.size() >= 8) {
            allCarryOver.remove(0);
        }

        StringBuilder reversedStringBuilder = new StringBuilder(result.toString()).reverse();
        result = reversedStringBuilder.toString();

        if (result.substring(0, 1).contains("0")) {
            result = result.substring(1);
        }

        numbersToWrite = result.length();
        return Integer.parseInt(result.toString());
    }

    private void printColumns(ArrayList<String> columns) {
        System.out.println("Columns printing");
        for (int i = 0; i < columns.size(); i++) {
            columns.get(i);
            System.out.println(columns.get(i));
        }
        System.out.println("Columns done");
    }

    protected String oneStep() {

        if (numbersToWrite <= 0) {
            return toWrite;
        }
        toWrite = result.substring(numbersToWrite - 1);
        numbersToWrite--;
        return toWrite;
    }

    private int getSumOneColumn(ArrayList<String> linesLocal, int columNum, int sum) {

        for (int i = linesLocal.size() - 1; i >= 0; i--) {

            int digit = Character.getNumericValue(linesLocal.get(i).charAt(columNum));
            sum += digit;
            numbersToAdd.add(sum);
        }
        if (sum >= 10) {
            allCarryOver.add(sum / 10);
        } else {
            allCarryOver.add(0);
        }
        return sum;
    }

    protected void paint(Graphics2D g2d, int xPos, int yPos) {

        Font font = g2d.getFont();
        FontMetrics fontMetrics = g2d.getFontMetrics(font);

        int lengthResult = fontMetrics.stringWidth(result.toString());
        int lengthToWrite = fontMetrics.stringWidth(toWrite);

        g2d.drawString(toWrite, xPos + lengthResult - lengthToWrite, yPos + font.getSize());

        printAllCarryOvers();

        if (numbersToWrite - 1 < 0) {
            return;
        }

//        if (carryOver < 1) {
//            return;
//        }
        g2d.setFont(carryOverFont);

        int myXPos = xPos + lengthResult - 30;
        for (int i = 0; i < allCarryOver.size(); i++) {
            int carryOver = allCarryOver.get(i);
            String carryOverStr = carryOver + "";
            g2d.drawString(carryOverStr, myXPos, yPos - 4);
            myXPos -= 44;
        }

        g2d.setFont(font);
    }

    private void printAllCarryOvers() {
        System.out.println("all carry");
        for (int i = 0; i < allCarryOver.size(); i++) {

            int carryOver = allCarryOver.get(i);
            System.out.print(carryOver);
        }
        System.out.println("\nall carry done");
    }
}
