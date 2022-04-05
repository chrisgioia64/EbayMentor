package base;

public class CustomUtilities {

    public static boolean isDigit(char c) {
        return (c - '0') <= 9 && (c - '0') >= 0;
    }

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
