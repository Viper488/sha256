package sha;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperations {
    // NOT
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

    // AND
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

    // XOR
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
}
