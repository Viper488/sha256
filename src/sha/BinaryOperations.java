package sha;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperations {
    public static String not(String number) {
        char[] n = number.toCharArray();
        List<String> x = new ArrayList<>();

        for (char c : n) {
            if(c == '1') {
                x.add("0");
            } else {
                x.add("1");
            }
        }

        return String.join("", x);
    }

    public static String and(String first, String second) {
        char[] f = first.toCharArray();
        char[] s = second.toCharArray();

        List<String> x = new ArrayList<>();

        for (int i = 0; i < f.length; i++) {
            if(f[i] == '1' && s[i] == '1')
                x.add("1");
            else
                x.add("0");
        }

        return String.join("", x);
    }

    public static String xor(String first, String second) {
        char[] f = first.toCharArray();
        char[] s = second.toCharArray();
        List<String> x = new ArrayList<>();

        for (int i = 0; i < f.length; i++) {
            if(f[i] != s[i])
                x.add("1");
            else
                x.add("0");
        }

        return String.join("", x);
    }

    public static String add(String b1, String b2) {
        int len1 = b1.length();
        int len2 = b2.length();
        int carry = 0;

        StringBuilder res = new StringBuilder();

        int maxLen = Math.max(len1, len2);
        for (int i = 0; i < maxLen; i++) {
            int p = i < len1 ? b1.charAt(len1 - 1 - i) - '0' : 0;
            int q = i < len2 ? b2.charAt(len2 - 1 - i) - '0' : 0;
            int tmp = p + q + carry;
            carry = tmp / 2;
            res.insert(0, tmp % 2);
        }

        return res.toString();
    }

    // Rotate right - Characters should be moved by distance indexes to the right and
    // characters from the end of String should fill the first X characters of String until char[distance] is met
    public static String rotateRight(String integer, int distance) {
        char[] original = integer.toCharArray();
        char[] rotated = new char[original.length];

        for(int i = 0; i < original.length; i++) {
            rotated[(i + distance) % rotated.length] = original[i];
        }

        return String.valueOf(rotated);
    }

    // Shift right - Insert distance amount of 0, and then return string from 0 to 32
    public static String shiftRight(String integer, int distance) {
        StringBuilder sb = new StringBuilder(integer);

        for (int i = 0; i < distance; i++) {
            sb.insert(0, "0");
        }

        return sb.substring(0, 32);
    }
}
