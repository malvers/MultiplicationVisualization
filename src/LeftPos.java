public class LeftPos {

    private int leftPos = 0;

    public int get() {
        return leftPos;
    }

    public void set(int leftPos) {
//        System.out.println("set leftPos: " + leftPos);
        this.leftPos = leftPos;
    }

    public void inc() {
        leftPos++;
//        System.out.println("inc leftPos: " + leftPos);
    }
}
