package max.sander;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FontTranslator {
    static String[][] charBytes = {{"0111111010100000101000001010000001111110"},
            {"1111111010100010101000101010001001011100"},
            {"0111110010000010100000101000001001000100"},
            {"1111111010000010100000101000001001111100"},
            {"1111111010100010101000101000001010000010"},
            {"1111111010100000101000001000000010000000"},
            {"0111110010000010100000101010001010111100"},
            {"1111111000100000001000000010000011111110"},
            {"100000101111111010000010"},
            {"0000010000000010000000100000001011111100"},
            {"1111111000100000001000000101000010001110"},
            {"1111111000000010000000100000001000000010"},
            {"1111111001000000001000000100000011111110"},
            {"1111111001000000001000000001000011111110"},
            {"0111110010000010100000101000001001111100"},
            {"1111111010100000101000001010000001000000"},
            {"0111110010000010100000101000010001111010"},
            {"1111111010100000101000001010000001011110"},
            {"0100010010100010101000101010001010011100"},
            {"1000000010000000111111101000000010000000"},
            {"1111110000000010000000100000001011111100"},
            {"1111000000001100000000100000110011110000"},
            {"1111111000000100000010000000010011111110"},
            {"1000111001010000001000000101000010001110"},
            {"1000000001000000001111100100000010000000"},
            {"1000011010001010100100101010001011000010"},
            {"0000010000101010001010100010101000011110"},
            {"1111111000010010001000100010001000011100"},
            {"0001110000100010001000100010001000010100"},
            {"0001110000100010001000100001001011111110"},
            {"0001110000101010001010100010101000011010"},
            {"00100000011111101010000010100000"},
            {"0001100100100101001001010010010100111110"},
            {"1111111000010000001000000010000000011110"},
            {"10111110"},
            {"0000011000000001000000010000000110111110"},
            {"11111110000010000001010000100010"},
            {"1111110000000010"},
            {"0011111000100000000110000010000000011110"},
            {"0011111000100000001000000010000000011110"},
            {"0001110000100010001000100010001000011100"},
            {"0011111100010100001001000010010000011000"},
            {"0001100000100100001001000001010000111111"},
            {"0011111000010000001000000010000000010000"},
            {"0001001000101010001010100010101000100100"},
            {"001000001111110000100010"},
            {"0011110000000010000000100000001000111110"},
            {"0011100000000100000000100000010000111000"},
            {"0011110000000010000011100000001000111110"},
            {"0010001000010100000010000001010000100010"},
            {"0011100100000101000001010000010100111110"},
            {"0010001000100110001010100011001000100010"},
            {"0111110010001010100100101010001001111100"},
            {"0000001001000010111111100000001000000010"},
            {"0100011010001010100100101001001001100110"},
            {"0100010010000010100100101001001001101100"},
            {"0001100000101000010010001000100011111110"},
            {"1110010010100010101000101010001010011100"},
            {"0011110001010010100100101001001000001100"},
            {"1100000010000000100011101001000011100000"},
            {"0110110010010010100100101001001001101100"},
            {"0110000010010010100100101001010001111000"},
            {"00000010"},
            {"00000011"},
            {"00100010"},
            {"00100011"},
            {"0000000100000001000000010000000100000001"},
            {"0001000000010000000100000001000000010000"},
            {"0010100011111110001010001111111000101000"},
            {"0001000000010000011111000001000000010000"},
            {"101000000100000010100000"},
            {"010000001000000010000000010000000100000010000000"},
            {"001110000100010010000010"},
            {"100000100100010000111000"},
            {"111111101000001010000010"},
            {"100000101000001011111110"},
            {"000100000110110010000010"},
            {"100000100110110000010000"},
            {"11111010"},
            {"11000000"},
            {"0010010001010100110101100101010001001000"},
            {"1100001000001100000100000110000010000110"},
            {"0000110001010010101110100100110000010010"},
            {"0000001000001100000100000110000010000000"},
            {"0010010000100100001001000010010000100100"},
            {"0100000010000000100010101001000001100000"},
            {"1000000001100000000100000000110000000010"},
            {"001111100100000101011101010101010100010100111100"},
            {"00010000001010000100010010000010"},
            {"10000010010001000010100000010000"}};
    static char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,:;_-#+*~()[]{}!'$%&/=?\\@<>".toCharArray();
    static String translate (BufferedImage in) {
        StringBuilder result = new StringBuilder();
        Color color;
        int startY = findFirstLineY(in);
        StringBuilder characterString = new StringBuilder("");
        for (int line = 0; line < (in.getHeight()-startY)/9-1; line++) {
            int spaceCount = 0;
            characterString = new StringBuilder("");
            for (int x = 0; x < in.getWidth(); x++) {
                StringBuilder col = new StringBuilder();
                for (int y = 0; y < 8; y++) {
                    color = new Color(in.getRGB(x,startY+y+line*9));
                    if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                        col.append("1");
                    } else {
                        col.append("0");
                    }
                }
                if (col.toString().equals("00000000")) {
                    spaceCount += 1;
                    if (spaceCount == 4) {
                        result.append(" ");
                        spaceCount = 0;
                    }
                    if (!characterString.toString().equals("")) {
                        result.append(charStringToChar(characterString.toString()));
                    }
                    characterString = new StringBuilder("");
                } else {
                    spaceCount = 0;
                    characterString.append(col.toString());
                }
            }
            result.append("\n");
        }

        return result.toString();
    }
    static String translateVariableSpacing(BufferedImage in) {
        // 1 pixel minimum spacing required
        StringBuilder result = new StringBuilder();
        int startY = findFirstLineY(in);
        StringBuilder characterString;
        int spaceCount = 0;
        int color;
        characterString = new StringBuilder();
        while (startY + 8 < in.getHeight()) {
            for (int x = 0; x < in.getWidth(); x++) {
                StringBuilder col = new StringBuilder();
                for (int y = 0; y < 8; y++) {
                    color = in.getRGB(x, startY + y);
                    if (color == Color.BLACK.getRGB()) {
                        col.append("1");
                    } else {
                        col.append("0");
                    }
                }
                if (col.toString().equals("00000000")) {
                    spaceCount += 1;
                    if (spaceCount == 4) {
                        result.append(" ");
                        spaceCount = 0;
                    }
                    if (!characterString.toString().equals("")) {
                        result.append(charStringToChar(characterString.toString()));
                    }
                    characterString = new StringBuilder();
                } else {
                    spaceCount = 0;
                    characterString.append(col.toString());
                }
            }
            result.append("\n");
            boolean found = false;
            for (int y = startY + 9; y < in.getHeight(); y++) {
                for (int x = 0; x < in.getWidth(); x++) {
                    color = in.getRGB(x,y);
                    if (color == Color.BLACK.getRGB()) {
                        startY = y;
                        found = true;
                        break;
                    }
                }
                if (found) break;
            }
            if (!found) {
                break;
            }
//            System.out.println(startY);
        }

        return result.toString();
    }


    static int findFirstLineY (BufferedImage in) {
        Color color;


        for (int y = 0; y < in.getHeight(); y++) {
            for (int x = 0; x < in.getWidth(); x++) {
                color = new Color(in.getRGB(x,y));
                if (color.getRed() == 0 && color.getGreen() == 0 && color.getBlue() == 0) {
                    return y;
                }
            }
        }
        return 0;
    }
    static char charStringToChar (String in) {
        char result = '#';
        for (int i = 0; i < charBytes.length; i++) {
            if (in.equals(charBytes[i][0])) {
                result = chars[i];
                break;
            }
        }
        return result;
    }
}
