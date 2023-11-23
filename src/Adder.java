import java.awt.*;
import java.util.ArrayList;

public class Adder {

    public String getResult() {
        return result.toString();
    }

    private StringBuilder result;
    private int numbersToWrite = 0;
    private String toWrite;

    protected int doAdditionManually(ArrayList<String> lines, boolean verbose) {

        if (true) {
            System.out.println("Adder.doAdditionManually ...");
        }

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
            while (tmp.length() < 2 * lines.size()) {
                tmp.insert(0, "0");
            }

            theLine = new StringBuilder(tmp.toString());

            linesLocal.add(theLine.toString());
        }

        if (verbose) {
            System.out.println("Print lines local ...");
        }
        for (int i = 0; i < linesLocal.size(); i++) {
            if (verbose) {
                System.out.println(linesLocal.get(i));
            }
        }

        if (verbose) {
            System.out.println("Calculate ... ");
        }

        result = new StringBuilder();
        int carryOver = 0;
        for (int i = linesLocal.get(0).length() - 1; i >= 0; i--) {

            int sum = 0;
            if (verbose) {
                System.out.print("column: " + i + " -> \t");
            }

            sum = getSumOneColumn(linesLocal, i, sum);

            int toWrite;
            if (sum + carryOver >= 10) {
                sum += carryOver;
                toWrite = sum % 10;
                carryOver = sum / 10;
            } else {
                toWrite = sum + carryOver;
                carryOver = 0;
            }
            if (verbose) {
                if (sum < 10) {
                    System.out.print(" sum:  " + sum);
                } else {
                    System.out.print(" sum: " + sum);
                }
            }
            if (verbose) {
                System.out.print(" write " + toWrite + " cary: " + carryOver);
            }
            result.append(toWrite);
            if (verbose) {
                System.out.println(result);
            }
        }

        if (verbose) {
            System.out.println("");
        }

        StringBuilder reversedStringBuilder = new StringBuilder(result.toString()).reverse();
        result = new StringBuilder(reversedStringBuilder.toString());

        int intResult = Integer.parseInt(result.toString());

        if (verbose) {
            System.out.println("int result: " + intResult);
        }
        if (verbose) {
            System.out.println("doAdditionManually done ...");
        }

        numbersToWrite = result.length();
        return Integer.parseInt(result.toString());
    }

    protected String oneStep() {

        if (numbersToWrite < 0) {
            return toWrite;
        }
        System.out.println("ntw: " + numbersToWrite);
        toWrite = result.substring(numbersToWrite--);
        return toWrite;
    }

    private static int getSumOneColumn(ArrayList<String> linesLocal, int columNum, int sum) {

        for (String s : linesLocal) {

            int digit = Character.getNumericValue(s.charAt(columNum));
            sum += digit;
//            System.out.println("sum: " + sum);
        }
        return sum;
    }

    protected static void paint(Graphics2D g2d, int rightPos, int charWidth) {


    }
}
