package vav.cyberspace.viettel.vva.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

import vav.cyberspace.viettel.vva.utils.sentence.segment.SentenceSegment;
import vav.cyberspace.viettel.vva.utils.sentence.segment.Unicode2Telex;

/**
 * Created by thanhtn10 on 6/2/16.
 */
public class NormalizerSynthesis {

    public HashMap<Integer,String> mapTone = new HashMap<>();

    public NormalizerSynthesis() {
        mapTone.put(768,"f");
        mapTone.put(769,"s");
        mapTone.put(777,"r");
        mapTone.put(771, "x");
        mapTone.put(803, "j");
    }
    public String convertToken(String input){
        input = input.toLowerCase();
        String tone = "";
        StringBuilder sb = new StringBuilder();
        String punctuation = "";
        if (input.endsWith(".") || input.endsWith(",")){
            punctuation=input.substring(input.length()-1,input.length());
            input = input.substring(0,input.length()-1);
        }

        String decompose = Normalizer.normalize(input, Normalizer.Form.NFD);
        ArrayList<Integer> codes = new ArrayList<Integer>();
        for( int i = 0; i < decompose.length(); i++){
            codes.add(decompose.codePointAt(i));
        }

        for (int t:mapTone.keySet()){
            if (codes.contains(t)){
                tone = mapTone.get(t);
            }
            codes.remove((Object)t);
        }
        int i  = 0;
        int len = codes.size();

        while (i < len){
            switch (codes.get(i)){
                case 97:
                    if (i+1 < len){
                        if (codes.get(i+1) == 770){
                            sb.append("aa");
                            i=i+2;
                            break;
                        } else if (codes.get(i+1) == 774){
                            sb.append("aw");
                            i=i+2;
                            break;
                        }
                    }
                    sb.append("a");
                    i++;
                    break;
                case 101:
                    if (i+1 < len && codes.get(i+1) == 770){
                        sb.append("ee");
                        i=i+2;
                    } else {
                        sb.append("e");
                        i++;
                    }
                    break;
                case 111:
                    if (i+1 < len) {
                        if (codes.get(i + 1) == 770 && i + 1 < len) {
                            sb.append("oo");
                            i = i + 2;
                            break;
                        } else if (codes.get(i + 1) == 795 && i + 1 < len) {
                            sb.append("ow");
                            i = i + 2;
                            break;
                        }
                    }
                    sb.append("o");
                    i++;
                    break;
                case 117:
                    if (i+1 < len && codes.get(i+1) == 795){
                        sb.append("uw");
                        i=i+2;
                    } else {
                        sb.append("u");
                        i++;
                    }
                    break;
                case 273:
                    sb.append("dd");
                    i++;
                    break;
                default:
                    sb.append(Character.toChars(codes.get(i)));
                    i++;
                    break;
            }
        }
        if (punctuation.isEmpty()) {
            return sb.append(tone).toString();
        } else {
            return sb.append(tone).append(punctuation).toString();
        }
    }
    public String nomarlize(String paragraph, String resourePath, ArrayList<String> mapping) {
        paragraph = paragraph.toLowerCase();
        for (int i = 0; i < mapping.size(); i++){
            String []strMappingList = mapping.get(i).split("->");
            if(strMappingList.length > 1){
                paragraph = paragraph.replaceAll("\\b" + strMappingList[0].trim().toLowerCase() +"\\b", strMappingList[1].trim().toLowerCase());
            }
        }
        SentenceSegment.mResourcePath = resourePath;
        SentenceSegment segment = new SentenceSegment();
        Unicode2Telex u2t = new Unicode2Telex();
        ArrayList<String> al = segment.segment(paragraph);
        return u2t.convertParagraph(al);
    }

}
