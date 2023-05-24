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
    private static final int[] iH = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372,  0xa54ff53a,
            0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19};
    // Convert hash values to binary
    private static final String[] H0 = Arrays.stream(iH).mapToObj(Main::hexToBin).toArray(String[]::new);


    // Initialize constants
    private static final int[] iK = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2};
    // Convert constants values to binary
    private static final String[] K = Arrays.stream(iK).mapToObj(Main::hexToBin).toArray(String[]::new);

    private static final int BLOCK_BITS = 512;
    private static final int SUB_BLOCK_BITS = 32;
    private static final int NUMBER_OF_WORDS = 64;
    private static final String BINARY_ZERO = "00000000000000000000000000000000";

    private static String sha256(String message) {
        return compress(chunk(padMessage(message)));
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
        return finalH.stream()
                .map(binary -> new BigInteger(binary, 2).toString(16).toUpperCase())
                .collect(Collectors.joining());
    }

    // Chunk loop
    private static List<String> chunk(String binary) {
        // Split binary into even 512 bit pieces
        List<String> words512 = splitEvenly(binary, BLOCK_BITS);

        // Split 512 bit words into 32 bit words
        List<String> words32 = new ArrayList<>();
        for (String piece:words512)
            words32.addAll(splitEvenly(piece, SUB_BLOCK_BITS));

        // Extend 16 x 32 bit words into 64 x 32 bit words
        while(words32.size() < NUMBER_OF_WORDS)
            words32.add(BINARY_ZERO);

        // Modify zeroed indexes from 16 to 63
        for(int i = 16; i < NUMBER_OF_WORDS; i++) {
            String s0 = chunkS0(words32.get(i - 15)),
                   s1 = chunkS1(words32.get(i - 2));
            words32.set(i, add(words32.get(i - 16), add(s0, add(words32.get(i - 7), s1))));
        }

        return words32;
    }

    // Preparing message to be always divisible by 512
    private static String padMessage(String message) {
        String binary = msgToBin(message);
        String messageLength = getSize(binary.length());

        StringBuilder sb = new StringBuilder();
        sb.append(binary);

        // Add bit 1
        sb.append("1");

        // Pad with 0 until length mod 512 is 448
        while (sb.length() % BLOCK_BITS != 448) {
            sb.append("0");
        }

        // Pad with original length
        while ((sb.length() + messageLength.length()) % BLOCK_BITS != 0) {
            sb.append("0");
        }
        sb.append(messageLength);

        return sb.toString();
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

    // Split string into equal length words
    private static List<String> splitEvenly(String text, int length) {
        List<String> splitString = new ArrayList<>();

        for (int start = 0; start < text.length(); start += length) {
            splitString.add(text.substring(start, Math.min(text.length(), start + length)));
        }

        return splitString;
    }

    // Get original size append with 0 to match 32 bit length
    public static String getSize(int integer) {
        return padBinary32(BigInteger.valueOf(integer).toString(2));
    }

    // Transform message to binary
    public static String msgToBin(String message) {
        StringBuilder sb = new StringBuilder();
        for (byte b : message.getBytes())
            sb.append(padBinary8(Integer.toBinaryString(b)));

        return sb.toString();
    }

    // Hex to bin conversion used for hashes and constants
    private static String hexToBin(int hex) {
        StringBuilder binary = new StringBuilder();

        for (char c : Integer.toHexString(hex).toUpperCase().toCharArray())
            binary.append(switch (c) {
                case '1' -> "0001";
                case '2' -> "0010";
                case '3' -> "0011";
                case '4' -> "0100";
                case '5' -> "0101";
                case '6' -> "0110";
                case '7' -> "0111";
                case '8' -> "1000";
                case '9' -> "1001";
                case 'A' -> "1010";
                case 'B' -> "1011";
                case 'C' -> "1100";
                case 'D' -> "1101";
                case 'E' -> "1110";
                case 'F' -> "1111";
                default -> "0000";
            });

        return padBinary32(binary.toString());
    }

    // Pad binary to 8 bits
    private static String padBinary8(String binary) {
        return padBinary(binary, 8);
    }

    // Pad binary to 32 bits
    private static String padBinary32(String binary) {
        return padBinary(binary, 32);
    }

    // Pad binary to specified number of bits
    private static String padBinary(String binary, int length) {
        StringBuilder result = new StringBuilder(binary);
        while (result.length() < length)
            result.insert(0,"0");

        return result.toString();
    }
}
