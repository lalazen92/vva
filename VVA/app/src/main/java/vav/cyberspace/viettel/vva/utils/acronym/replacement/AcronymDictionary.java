/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vav.cyberspace.viettel.vva.utils.acronym.replacement;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phiha
 */
public class AcronymDictionary {

    static AcronymDictionary instance = null;
    private Map<String, ArrayList<String>> dict = null;

    public AcronymDictionary() {
        BufferedReader reader = null;
        try {
            dict = new HashMap();
            reader = new BufferedReader(new FileReader("data\\accro\\acronyms.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] phases = line.split("\\|");
                String key = phases[0].split("\\s+")[0];
                ArrayList<String> values = new ArrayList();
                for (int i = 1; i < phases.length; i++) {
                    String value = "";
                    String phase = phases[i];
                    String s[] = phase.split("\\s+");
                    if(key.length() > s.length) continue;
                    for (int j = 0; j < key.length(); j++) {
                        value += s[j] + " ";
                    }
                    values.add(value);
                }
                dict.put(key, values);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AcronymDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AcronymDictionary.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(AcronymDictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public ArrayList<String> disambuation(String key) {
        return dict.get(key);
    }

    public static AcronymDictionary getInstanceOf() {
        if (instance == null) {
            instance = new AcronymDictionary();
        }
        return instance;
    }

}
