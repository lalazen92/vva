/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vav.cyberspace.viettel.vva.utils.nomalizer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author phiha
 */
public class Dictionary {

    public  static String mResourcePath;
    static Dictionary instance = null;
    private Map<String, String> acronymDict = null;
    private Map<String, String> UMDict = null;

    public Dictionary() {
        System.err.println("Building dictionary");

        acronymDict = build(mResourcePath+"acronym.txt");
        UMDict = build(mResourcePath+"unit-of-measure.txt");

    }

    public static Dictionary getInstanceOf() {
        if (instance == null) {
            instance = new Dictionary();
        }
        return instance;
    }

    public String getWord(String word) {
        while (word.length() > 1 && !Character.isLetter(word.charAt(0)) && !Character.isDigit(word.charAt(0))) {
            word = word.substring(1);
        }
        while (word.length() > 1 && !Character.isLetter(word.charAt(word.length() - 1)) && !Character.isDigit(word.charAt(word.length() - 1))) {
            word = word.substring(0, word.length() - 1);
        }
        return word;
    }

    String filterByDictionary(String sentence) {
//        System.out.println("BEFORE: " + sentence);
        Dictionary dictionary = Dictionary.getInstanceOf();
        for (String word : sentence.split("[\\s+\\.]")) {

            word = getWord(word);
            if (isAcronym(word)) {
                String disAmWord = dictionary.disambuation(acronymDict, word);

                if (disAmWord != null) {
                    sentence = sentence.replace(word, disAmWord);
                }
            }
        }
//        System.out.println("AFTER: " + sentence);
        return sentence;
    }

    private boolean isAcronym(String word) {
        return word.equals(word.toUpperCase());
    }

    private boolean isUnitOfMeasure(String word) {
        return word.matches("[qwrtpsdfghjklzxcvbnm]+");
    }

    String filterByUnitOfMeasure(String sentence) {
        Dictionary dictionary = Dictionary.getInstanceOf();
        Pattern pattern = Pattern.compile("(\\d+)\\s*([a-z]+)(\\d+)*");
        Matcher matcher = pattern.matcher(sentence);
        while (matcher.find()) {
            String phrase = matcher.group();
            String number = matcher.group(1);
            String word = matcher.group(2);
            String disAm = disambuation(UMDict, word);
            if (disAm == null) {
                continue;
            }
            String other = matcher.group(3);
            String replacement = number + " " + disAm + " ";
            if (other != null) {
                replacement += other + " ";
            }
            sentence = sentence.replace(phrase, replacement);
        }

        pattern = Pattern.compile("(trên)(\\s)*([a-z]+)");
        matcher = pattern.matcher(sentence);
        while (matcher.find()) {
            String phrase = matcher.group();
//            System.out.println(phrase);
            String word = matcher.group(3);
//            System.out.println(word);
            String disAm = disambuation(UMDict, word);
//            System.out.println(disAm);
            if (disAm == null) {
                continue;
            }
            String replacement = "trên " + disAm;
            sentence = sentence.replace(phrase, replacement);
        }
        return sentence;

    }

    private Map<String, String> build(String filePath) {
        Map<String, String> dict = null;
        BufferedReader reader = null;
        try {
            dict = new HashMap();
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                int index = line.indexOf(" ");
                String key = line.substring(0, index);
                String value = line.substring(index + 1);
//                System.out.println(key + " ========= " + value);
                dict.put(key.trim(), value.trim());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Dictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return dict;
    }

    private String disambuation(Map<String, String> acronymDict, String key) {
        return acronymDict.get(key);
    }

    public static void main(String[] args) {
        Dictionary dictionary = Dictionary.getInstanceOf();
        String sentence = "một kg";
        System.out.println(sentence.matches("(một)(\\s)*"));
        System.out.println(sentence.matches("(một)(\\s)*([a-z]+)"));
//        System.out.println(dictionary.filterByUnitOfMeasure(sentence));
    }

}
