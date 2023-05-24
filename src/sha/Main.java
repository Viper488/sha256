package sha;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static sha.BinaryOperations.*;

public class Main {
    public static void main(String[] args) {
        String message = "hello world";
        String hash = sha256(message);

        System.out.println("Original message: " + message);
        System.out.println("SHA-256 hash: " + hash);
    }

    // Initialize hash values
    private static final String[] H0 = {"01101010000010011110011001100111",
                                        "10111011011001111010111010000101",
                                        "00111100011011101111001101110010",
                                        "10100101010011111111010100111010",
                                        "01010001000011100101001001111111",
                                        "10011011000001010110100010001100",
                                        "00011111100000111101100110101011",
                                        "01011011111000001100110100011001"};
    // Initialize constants
    private static final String[] K = {"01000010100010100010111110011000",
            "01110001001101110100010010010001",
            "10110101110000001111101111001111",
            "11101001101101011101101110100101",
            "00111001010101101100001001011011",
            "01011001111100010001000111110001",
            "10010010001111111000001010100100",
            "10101011000111000101111011010101",
            "11011000000001111010101010011000",
            "00010010100000110101101100000001",
            "00100100001100011000010110111110",
            "01010101000011000111110111000011",
            "01110010101111100101110101110100",
            "10000000110111101011000111111110",
            "10011011110111000000011010100111",
            "11000001100110111111000101110100",
            "11100100100110110110100111000001",
            "11101111101111100100011110000110",
            "00001111110000011001110111000110",
            "00100100000011001010000111001100",
            "00101101111010010010110001101111",
            "01001010011101001000010010101010",
            "01011100101100001010100111011100",
            "01110110111110011000100011011010",
            "10011000001111100101000101010010",
            "10101000001100011100011001101101",
            "10110000000000110010011111001000",
            "10111111010110010111111111000111",
            "11000110111000000000101111110011",
            "11010101101001111001000101000111",
            "00000110110010100110001101010001",
            "00010100001010010010100101100111",
            "00100111101101110000101010000101",
            "00101110000110110010000100111000",
            "01001101001011000110110111111100",
            "01010011001110000000110100010011",
            "01100101000010100111001101010100",
            "01110110011010100000101010111011",
            "10000001110000101100100100101110",
            "10010010011100100010110010000101",
            "10100010101111111110100010100001",
            "10101000000110100110011001001011",
            "11000010010010111000101101110000",
            "11000111011011000101000110100011",
            "11010001100100101110100000011001",
            "11010110100110010000011000100100",
            "11110100000011100011010110000101",
            "00010000011010101010000001110000",
            "00011001101001001100000100010110",
            "00011110001101110110110000001000",
            "00100111010010000111011101001100",
            "00110100101100001011110010110101",
            "00111001000111000000110010110011",
            "01001110110110001010101001001010",
            "01011011100111001100101001001111",
            "01101000001011100110111111110011",
            "01110100100011111000001011101110",
            "01111000101001010110001101101111",
            "10000100110010000111100000010100",
            "10001100110001110000001000001000",
            "10010000101111101111111111111010",
            "10100100010100000110110011101011",
            "10111110111110011010001111110111",
            "11000110011100010111100011110010"};
    private static final int BLOCK_BITS = 512;
    private static final int SUB_BLOCK_BITS = 32;
    private static final int NUMBER_OF_WORDS = 64;
    private static final String BINARY_ZERO = "00000000000000000000000000000000";

    private static String sha256(String message) {
        String padded = padMessage(message);

        List<String> chunked = chunk(padded);

        return compress(chunked);
    }

    // Compression
    private static String compress(List<String> words) {
        // Initialize variables
        String a = H0[0],
               b = H0[1],
               c = H0[2],
               d = H0[3],
               e = H0[4],
               f = H0[5],
               g = H0[6],
               h = H0[7];

        // Mutate the values of a to h
        for (int i = 0; i < 64; i++) {
            String s0 = compressS0(a);
            String s1 = compressS1(e);
            String ch = xor(and(e, f), and(not(e), g));
            String maj = xor(and(a, b), xor(and(a, c), and(b, c)));
            String temp1 = add(h, add(s1, add(ch, add(K[i], words.get(i)))));
            String temp2 = add(s0, maj);

            h = g;
            g = f;
            f = e;
            e = add(d, temp1);
            d = c;
            c = b;
            b = a;
            a = add(temp1, temp2);
        }

        List<String> finalA = Arrays.asList(a, b, c, d, e, f, g, h);
        List<String> finalH = new ArrayList<>();

        // Add hashes to values a to h
        for (int i = 0; i < H0.length; i++) {
            finalH.add(add(H0[i], finalA.get(i)));
        }

        // Concat and return final hash
        return finalH.stream().map(binary -> new BigInteger(binary, 2).toString(16).toUpperCase()).collect(Collectors.joining());
    }

    // Chunk loop
    private static List<String> chunk(String binary) {
        // Split binary into even 512 bit pieces
        List<String> words512 = splitEvenly(binary, BLOCK_BITS);

        // Split 512 bit words into 32 bit words
        List<String> words32 = new ArrayList<>();
        for (String piece:words512
             ) {
            words32.addAll(splitEvenly(piece, SUB_BLOCK_BITS));

        }

        // Extend 16 x 32 bit words into 64 x 32 bit words
        while(words32.size() < NUMBER_OF_WORDS) {
            words32.add(BINARY_ZERO);
        }

        // Modify zeroed indexes from 16 to 63
        for(int i = 16; i < NUMBER_OF_WORDS; i++) {
            String s0 = chunkS0(words32.get(i - 15)),
                   s1 = chunkS1(words32.get(i - 2));

            words32.set(i, add(words32.get(i - 16), add(s0, add(words32.get(i - 7), s1))));
        }

        return words32;
    }

    // Calculate s0
    private static String compressS0(String number) {
        return xor(xor(rotateRight(number, 2), rotateRight(number, 13)), rotateRight(number, 22));
    }
    private static String chunkS0(String word) {
        return xor(xor(rotateRight(word, 7), rotateRight(word, 18)), shiftRight(word, 3));
    }

    // Calculate s1
    private static String compressS1(String number) {
        return xor(xor(rotateRight(number, 6), rotateRight(number, 11)), rotateRight(number, 25));
    }
    private static String chunkS1(String word) {
        return xor(xor(rotateRight(word, 17), rotateRight(word, 19)), shiftRight(word, 10));
    }

    // Rotate right - Characters should be moved by distance indexes to the right and
    // characters from the end of String should fill the first X characters of String until char[distance] is met
    private static String rotateRight(String integer, int distance) {
        char[] original = integer.toCharArray();
        char[] rotated = new char[original.length];

        for(int i = 0; i < original.length; i++) {
            rotated[(i + distance) % rotated.length] = original[i];
        }

        return String.valueOf(rotated);
    }

    // Shift right - Insert distance amount of 0, and then return string from 0 to 32
    private static String shiftRight(String integer, int distance) {
        StringBuilder sb = new StringBuilder(integer);

        for (int i = 0; i < distance; i++) {
            sb.insert(0, "0");
        }

        return sb.substring(0, 32);
    }

    // Split string into equal length words
    private static List<String> splitEvenly(String text, int length) {
        List<String> splitString = new ArrayList<>();

        for (int start = 0; start < text.length(); start += length) {
            splitString.add(text.substring(start, Math.min(text.length(), start + length)));
        }

        return splitString;
    }

    // Preparing message to be always divisible by 512
    private static String padMessage(String message) {
        String binary = toBinaries(message);
        int ogSize = binary.length();
        String ogSizeS = getBinary(ogSize);

        StringBuilder sb = new StringBuilder();
        sb.append(binary);

        // Add bit 1
        sb.append("1");

        // Pad with 0 until length mod 512 is 448
        while (sb.length() % BLOCK_BITS != 448) {
            sb.append("0");
        }

        // Pad with original length
        while ((sb.length() + ogSizeS.length()) % BLOCK_BITS != 0) {
            sb.append("0");
        }
        sb.append(ogSizeS);

        return sb.toString();
    }

    // Transform message to binary
    public static String toBinaries(String message) {
        StringBuilder sb = new StringBuilder();
        for (byte b:message.getBytes()) {
            String binary = Integer.toBinaryString(b);
            int zeros = 8 - binary.length();
            while (zeros > 0) {
                sb.append("0");
                zeros--;
            }
            sb.append(binary);
        }
        return sb.toString();
    }

    // Get Binary append with 0 to match 32 bit length
    public static String getBinary(int integer) {
        String binary = BigInteger.valueOf(integer).toString(2);
        StringBuilder result = new StringBuilder();

        while ((binary.length() + result.length()) < 32) {
            result.insert(0,"0");
        }
        result.append(binary);

        return result.toString();
    }
}
