package Helper;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class Helper {
    public static int skillStringToIndex(String binaryString) {
        int value = 0;
        for (int i = 0; i < binaryString.length(); i++) {
            value += (int) (Character.getNumericValue(binaryString.charAt(binaryString.length()- 1 - i)) * Math.pow(2,i));
        }
        return value;
    }
    
    public static String indexToBitString(int number) {
        String str = Integer.toBinaryString(number);
        return str.substring(str.length()-3, str.length() - 1);
    }
}
