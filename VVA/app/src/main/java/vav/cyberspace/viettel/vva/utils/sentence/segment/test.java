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
public class test {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        String folderName = "data\\tn";
        String outputFolder = "data\\output";
        File folder = new File(folderName);
        Filter filter = Filter.getInstanceOf();

        for (File file : folder.listFiles()) {
            if (!file.getName().contains(".txt")) {
                continue;
            }
            Scanner scanner = new Scanner(file);
            String outputSentences1 = outputFolder + "\\" +  file.getName() + ".1";
            String outputSentences2 = outputFolder + "\\" + file.getName();
            Writer sentWriter1 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputSentences1), "utf-8"));
            Writer sentWriter2 = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(outputSentences2), "utf-8"));
            while (scanner.hasNextLine()) {
                String content = scanner.nextLine();
                ArrayList<String> sentences = getSentences(content);
                
                for (String sentence : sentences) {
                    if (sentence.split(" ").length >= 4) {
                        sentWriter1.write(sentence + "\n");
                        sentence = filter.filter(sentence);
                        sentWriter2.write(sentence + "\n");
                    }
                }
            }
            sentWriter1.close();
            sentWriter2.close();
        }
    }

    private static ArrayList<String> getSentences(String paragraph) {
        ArrayList<String> sentences = new ArrayList();
        AttributeFactory factory = new AttributeFactory(paragraph);
        for (int i = 0; i < factory.paragraphLength(); i++) {
            if (factory.isPunctuation(i)) {
                Attribute attribute = factory.getAttribute(i);
                if (J48Classifier.classify(attribute).equals("yes")) {
                    String sentence = factory.getSentenceEndAt(i);
                    sentences.add(sentence);
                }
            }
        }
        return sentences;
    }

}
