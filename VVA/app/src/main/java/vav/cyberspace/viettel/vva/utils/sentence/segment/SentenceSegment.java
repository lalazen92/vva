/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vav.cyberspace.viettel.vva.utils.sentence.segment;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import vav.cyberspace.viettel.vva.utils.nomalizer.Filter;
import vav.cyberspace.viettel.vva.utils.sentence.detection.Attribute;
import vav.cyberspace.viettel.vva.utils.sentence.detection.AttributeFactory;
import vav.cyberspace.viettel.vva.utils.sentence.detection.J48Classifier;


/**
 *
 * @author phiha
 */
public class SentenceSegment {

    public  static String mResourcePath;


    public ArrayList<String> segment(String paragraph) {
        Filter.mResourcePath = mResourcePath;
        Filter filter = Filter.getInstanceOf();
        ArrayList<String> output = new ArrayList();
        for (String content : paragraph.split("\n")) {
            content = Filter.preprocessing(content);
            ArrayList<String> sentences;
            sentences = getSentences(content);
            for (String sentence : sentences) {
                sentence = filter.filter(sentence);
                output.add(sentence);
            }
        }
        return output;
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
//        String folderName = "C:\\Users\\phiha.CYBERSPACE\\Desktop\\dantri.com.vn";
//        String outputFolderName = "C:\\Users\\phiha.CYBERSPACE\\Desktop\\output";
//        File folder = new File(folderName);
//        Filter filter = Filter.getInstanceOf();
//        for (File file : folder.listFiles()) {
//            if (!file.getName().contains(".txt")) {
//                continue;
//            }
//            System.out.println(file.getName());
//            Scanner scanner = new Scanner(file);
//            String outputSentences = outputFolderName + "\\" + file.getName();
//            Writer sentWriter = new BufferedWriter(
//                    new OutputStreamWriter(
//                            new FileOutputStream(outputSentences), "utf-8"));
//            while (scanner.hasNextLine()) {
//                String content = scanner.nextLine();
//                content = removeUnknownContent(content);
//                content = Filter.preprocessing(content);
//                ArrayList<String> sentences = getSentences(content);
//                for (String sentence : sentences) {
////                    System.out.println("BEFORE: " + sentence);
//                    if (sentence.split(" ").length >= 4) {
//                        sentence = filter.filter(sentence);
//                        sentence = sentence.replaceAll("[ ]{2,}", " ");
////                        System.out.println("AFTER: " + sentence);
//                        sentWriter.write(sentence + "\n");
//                    }
//                }
//            }
//            sentWriter.close();
//        }
        SentenceSegment segment = new SentenceSegment();

        System.out.println(segment.segment("tôi đi học"));
    }

    static ArrayList<String> getSentences(String paragraph) {
        ArrayList<String> sentences = new ArrayList();
        AttributeFactory factory = new AttributeFactory(paragraph);
        for (int i = 0; i < factory.paragraphLength(); i++) {
            if (factory.isPunctuation(i)) {
                Attribute attribute = factory.getAttribute(i);

                if (J48Classifier.classify(attribute).equals("yes")) {

                    String sentence = factory.getSentenceEndAt(i);
                    sentences.add(sentence);
                }
            } else if (i == paragraph.length() - 1) {
                String sentence = factory.getSentenceEndAt(i);
                sentences.add(sentence);
            }
        }
        return sentences;
    }

    static String removeUnknownContent(String content) {

        String startString = "<!--";
        String endString = "-->";
        int startIndex = content.indexOf(startString);
        int endIndex = content.lastIndexOf(endString);
        try {
            if (startIndex != -1 && endIndex != -1) {
                String sub = content.substring(startIndex, endIndex + endString.length() + 1);
                content = content.replace(sub, "");
            }
        } catch (Exception e) {

        }
        return content;
    }

}
