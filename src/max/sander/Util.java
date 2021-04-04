package max.sander;

public class Util {
    public static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    static boolean arrayContainsString(String[] arrayIn, String in) {
        boolean answer = false;
        for (String i : arrayIn) {
            if (in.equals(i)) {
                answer = true;
                break;
            }
        }
        return answer;
    }
}
