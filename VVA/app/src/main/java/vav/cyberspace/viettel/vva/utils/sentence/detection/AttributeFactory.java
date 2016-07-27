/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vav.cyberspace.viettel.vva.utils.sentence.detection;

/**
 *
 * @author phiha
 */
public class AttributeFactory {

    private String paragraph;
    private int start, end;

    public AttributeFactory() {
        this.start = 0;
        this.end = 0;
    }

    public AttributeFactory(String paragraph) {
        this.start = 0;
        this.end = 0;
        this.paragraph = paragraph;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public int paragraphLength() {
        return paragraph.length();
    }

    public Attribute getAttribute(int index) {
        String prefix = getPrefix(index);
        String suffix = getSuffix(index);
        String previousWord = getPreviousWord(index);
        String nextWord = getNextWord(index);

        String candidateType = getCandidateType(paragraph.charAt(index));
        String previousWordType = getType(previousWord);
        String prefixType = getType(prefix);

        String suffixType = getType(suffix);

        String nextWordType = getType(nextWord);
        int prefixLength = prefix.length();
        Attribute attribute = new Attribute(candidateType, prefixType, suffixType, nextWordType, previousWordType, prefixLength);
        return attribute;
    }

    public String getPrefix(int index) {
        String prefix = "";
        index--;
        while (index >= 0 && !isWhiteSpace(paragraph.charAt(index))) {
            prefix = paragraph.charAt(index) + prefix;
            index--;
        }
//        System.out.println(prefix);

        return prefix;
    }

    private String getSuffix(int index) {
        String suffix = "";
        index++;
        while (index < paragraph.length() && !isWhiteSpace(paragraph.charAt(index))) {
            suffix += paragraph.charAt(index);
            index++;
        }
        return suffix;
    }

    private String getNextWord(int index) {
        String next = "";
        while (index < paragraph.length() && !isWhiteSpace(paragraph.charAt(index))) {
            index += 1;
        }
        while (index < paragraph.length() && isWhiteSpace(paragraph.charAt(index))) {
            index += 1;
        }
        while (index < paragraph.length() && !isWhiteSpace(paragraph.charAt(index))) {
            next += paragraph.charAt(index);
            index += 1;
        }
        return next;
    }

    private String getPreviousWord(int index) {
        String previous = "";
        while (index >= 0 && !isWhiteSpace(paragraph.charAt(index))) {
            index--;
        }
        while (index >= 0 && isWhiteSpace(paragraph.charAt(index))) {
            index--;
        }
        while (index >= 0 && !isWhiteSpace(paragraph.charAt(index))) {
            previous = paragraph.charAt(index) + previous;
            index--;
        }
        return previous;
    }

    public String getType(String str) {
        while (str.length() > 0 && Character.isSpaceChar(str.charAt(0))) {
            str = str.substring(1);
        }
        if (str.length() < 1) {
            return Type.EMPTY;
        }
        if (str.matches("\\p{javaUpperCase}\\p{javaLowerCase}+")) {
            return Type.CAPWORD;

        }
        if (str.matches("\\p{javaUpperCase}{1,}")) {
            return Type.UPPER;
        }
        if (str.matches("\\p{javaLowerCase}{1,}")) {
            return Type.LOWER;
        }
//        if(str.matches("[A-Z]["))
        if (str.matches("\\d+")) {
            return Type.NUMERIC;
        }

        return Type.MISC;
    }

    private String getCandidateType(char ch) {
        if (ch == '?') {
            return Type.QUEST;
        }
        if (ch == '!') {
            return Type.EXCLAM;
        }
        if (ch == '.') {
            return Type.DOT;
        }
        if (ch == ':') {
            return Type.COLON;
        }
        return null;
    }

    private boolean isWhiteSpace(char charAt) {
        String whitespace;
        whitespace = "\t\n\r ";
        return whitespace.contains(charAt + "");
    }

    public boolean isPunctuation(char ch) {
        String punctuation = "?!.:";
        return punctuation.contains(ch + "");
    }

    public boolean isPunctuation(int index) {
        return isPunctuation(paragraph.charAt(index));
    }

    public String getSentenceEndAt(int i) {
        end = i + 1;
        String sentence = paragraph.substring(start, end);
        start = end;
        while (start < paragraphLength() && isWhiteSpace(paragraph.charAt(start))) {
            start++;
        }
        return sentence;
    }

//    public static void main(String[] args) {
//        AttributeFactory a = new AttributeFactory();
//        System.out.println(a.getType("độ"));
//    }

}
