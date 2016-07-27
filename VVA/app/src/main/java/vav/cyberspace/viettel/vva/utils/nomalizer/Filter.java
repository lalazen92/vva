/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vav.cyberspace.viettel.vva.utils.nomalizer;

import java.text.Normalizer;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author phiha
 */
public class Filter {

    public  static String mResourcePath;

    private static Filter instance = null;
    private final Dictionary dictionary;

    public Filter() {
        Dictionary.mResourcePath = mResourcePath;
        dictionary = Dictionary.getInstanceOf();
    }

    public static Filter getInstanceOf() {
        if (instance == null) {
            instance = new Filter();
        }
        return instance;
    }

    public String filter(String sentence) {
        try {
            sentence = filterByDate(sentence);
            sentence = filterByTime(sentence);
            sentence = filterByPhoneNumber(sentence);
            sentence = filterByFloat(sentence);
            sentence = filterBySymbol(sentence);
            sentence = dictionary.filterByUnitOfMeasure(sentence);
            sentence = filterByNumber(sentence);
            sentence = dictionary.filterByDictionary(sentence);
            sentence = sentence.replaceAll("\\s+", " ");
            sentence = sentence.replaceAll("★", "");
            sentence = sentence.replaceAll("☆", "");
        } catch (Exception e) {

        }
        return sentence;
    }

    static String filterByDate(String text) throws ParseException {
        Pattern pattern = Pattern.compile("(\\d{1,2})\\s*([.\\/])\\s*(\\d{1,4})\\s*(([.\\/])\\s*(\\d{2,4}))*");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String old = matcher.group();
            String replacement = "";
            String ss[] = old.split("[\\-.\\/]");
            int first = Integer.parseInt(ss[0].trim());
            int second = Integer.parseInt(ss[1].trim());
            if (ss.length == 3) {
                int third = Integer.parseInt(ss[2]);
                if (second == 4) {
                    replacement = String.format("%d, tháng tư, năm %d ", first, third);
                } else {
                    replacement = String.format("%d, tháng %d, năm %d ", first, second, third);
                }
            } else {
                if (second > 12) {
                    if (first == 4) {
                        replacement = String.format("tư, năm %d ", second);
                    } else {
                        replacement = String.format("%d, năm %d ", first, second);
                    }
                } else {
                    if (second == 4) {
                        replacement = String.format("%d, tháng tư ", first);
                    } else {
                        replacement = String.format("%d, tháng %d, ", first, second);
                    }
                }
            }
            text = text.replaceFirst(old, replacement);
        }
        return text;
    }

    static String filterByNumber(String text) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String temp = matcher.group();
            text = text.replaceFirst(temp, " " + ReadNumber2.readNum(temp) + " ");

        }
        text = text.trim();
        return text;

    }

    static String filterByPhoneNumber(String sentence) {
        Pattern pattern = Pattern.compile("(\\+84|0)\\d{9,10}");
        Matcher matcher = pattern.matcher(sentence);
        while (matcher.find()) {
            String temp = matcher.group();
            if (temp.charAt(0) == '+') {
                sentence = sentence.replaceFirst(temp, "cộng " + readByDigits(temp.substring(1)));
            } else {

                sentence = sentence.replaceFirst(temp, readByDigits(temp));
            }
        }
        return sentence;
    }

    static String filterBySpecialWord(String sentence) {
        return sentence.replaceAll("\\(|\\)|\\'|;|‘", "").replaceAll("  ", " ").replaceAll("\\.", "");
    }

    static String filterByFloat(String sentence) {

        Pattern pattern = Pattern.compile("[-+]?([0-9]*\\,[0-9]+)");
        Matcher matcher = pattern.matcher(sentence);
        while (matcher.find()) {
            String temp = matcher.group();
            if (temp.charAt(0) == '-') {
                String text_replace = temp.replaceFirst("-", "âm ").replaceAll(",", " phẩy ");
                sentence = sentence.replaceFirst(temp, text_replace);

            } else {
                String text_replace = temp.replaceFirst(",", " phẩy ");
                sentence = sentence.replaceFirst(temp, text_replace);
            }
        }
        return sentence;
    }

    static String readByDigits(String number) {
        String digits[] = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        String ans = "";
        for (int i = 0; i < number.length(); i++) {
            int index = (int) number.charAt(i) - (int) '0';
            ans += digits[index] + " ";

        }
        return ans.trim();
    }

    public static String preprocessing(String content) {
        content = Normalizer.normalize(content, Normalizer.Form.NFC);
        content = content.replaceAll(":", ",");
        content = content.replaceAll(" ", " ");
        content = content.replaceAll("[‘'\"“”\\)\\(]", "");
        content = content.replaceAll(" {2,}", ".\n");
        content = content.replaceAll("…", "");
        content = content.replaceAll("\\.{3}", "");
        String newContent = "";
        for (int i = 0; i < content.length(); i++) {
            if (i > 0 && content.charAt(i) == '.' && Character.isDigit(content.charAt(i - 1)) && i + 1 < content.length() && Character.isDigit(content.charAt(i + 1))) {
                continue;
            }
            newContent += content.charAt(i);
        }
        content = newContent;
        int index = content.indexOf(">>");
        if (index >= 0) {
            content = content.substring(0, index);
        }
        String ans = "";
        int spaceIndex = 0;
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == ' ') {
                spaceIndex = i;
            }
            char first = content.charAt(i);
            if (i + 1 < content.length()) {
                char second = content.charAt(i + 1);
                ans += first;
                if (Character.isAlphabetic(first) && Character.isLowerCase(first) && i - spaceIndex > 2 && Character.isUpperCase(second)) {
                    ans += "\n";
                }
            } else {
                ans += first;
            }
        }
        return ans;
    }

    static String filterBySymbol(String sentence) {
        sentence = sentence.replaceAll("(\\d+)\\s*([-–])\\s*(\\d+)", "$1 đến $3");
        sentence = sentence.replaceAll("(\\d+)%", "$1 phần trăm");
        sentence = sentence.replaceAll("(\\d+)\\s*([\\p{javaLowerCase}\\p{javaUpperCase}]+)(/)([\\p{javaLowerCase}\\p{javaUpperCase}]+)",
                "$1 $2 trên $4");
        return sentence;
    }

    static String filterByTime(String sentence) {
        sentence = sentence.replaceAll("(\\d+)(')", "$1 phút");
        sentence = sentence.replaceAll("(\\d+)([gh:])", "$1 giờ ");
        return sentence;
    }

    public static void main(String[] args) throws ParseException {
        String sentence = "Today is Saturday. Im working...";
        sentence = sentence.replaceAll("\\.", "");
        System.out.println(sentence);
    }
    
}
