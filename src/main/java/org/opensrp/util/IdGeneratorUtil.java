package org.opensrp.util;

public class IdGeneratorUtil {

    /**
     * Converts a String back to an long based on the passed base characters
     *
     * @should convert from string in base character set to long
     */
    public static long convertFromBase(String s, char[] baseCharacters) {
        long ret = 0;
        char[] inputChars = s.toCharArray();
        long multiplier = 1;
        for (int i = inputChars.length - 1; i >= 0; i--) {
            int index = -1;
            for (int j = 0; j < baseCharacters.length; j++) {
                if (baseCharacters[j] == inputChars[i]) {
                    index = j;
                }
            }
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character " + inputChars[i] + " found in " + s + " - First Identifier Base");
            }
            ret = ret + multiplier * index;
            multiplier *= baseCharacters.length;
        }
        return ret;
    }

    /**
     * Converts a long to a String given the passed base characters
     *
     * @should convert from long to string in base character set
     */
    public static String convertToBase(long n, char[] baseCharacters, int padToLength, int minLength) {
        StringBuilder base = new StringBuilder();
        long numInBase = (long) baseCharacters.length;
        long i = n;
        while (i > 0) {
            int index = (int) (i % numInBase);
            base.insert(0, baseCharacters[index]);
            i = i / numInBase;
        }
        while (base.length() < padToLength) {
            base.insert(0, baseCharacters[0]);
        }

        return base.toString();
    }

}
