package vav.cyberspace.viettel.vva.utils.sentence.segment;
import java.io.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by minhtrung on 5/30/16.
 */
public class Unicode2Telex {

    public HashMap<Integer, String> mapTone = new HashMap<>();
    public HashMap<Integer, Integer> mapHead = new HashMap<>();
    public List<Integer> listConsonant = new ArrayList<>();
    public List<Integer> listVowel = new ArrayList<>(Arrays.asList(97, 101, 105, 111, 117, 121));

    public Unicode2Telex() {
        for (int i = 98; i < 123; i++) {
            listConsonant.add(i);
        }
        listConsonant.removeAll(listVowel);

        mapTone.put(768, "f");
        mapTone.put(769, "s");
        mapTone.put(777, "r");
        mapTone.put(771, "x");
        mapTone.put(803, "j");

        mapHead.put(770, 1); // ^
        mapHead.put(774, 2); // aw
        mapHead.put(795, 3); // ow
    }

    private String convertToken(String input) {
        input = input.toLowerCase();
        String tone = "";
        StringBuilder sb = new StringBuilder();

        String punctuation = "";
        if (input.endsWith(".") || input.endsWith(",") || input.endsWith("?") || input.endsWith("!")) {
            punctuation = input.substring(input.length() - 1, input.length());
            input = input.substring(0, input.length() - 1);
        }

        String decompose = Normalizer.normalize(input, Normalizer.Form.NFD);
        ArrayList<Integer> codes = new ArrayList<Integer>();
        for (int i = 0; i < decompose.length(); i++) {
            codes.add(decompose.codePointAt(i));
        }

        for (int t : mapTone.keySet()) {
            if (codes.contains(t)) {
                tone = mapTone.get(t);
            }
            codes.remove((Object) t);
        }
        int i = 0;
        int len = codes.size();

        while (i < len) {
            switch (codes.get(i)) {
                case 97:
                    if (i + 1 < len) {
                        if (codes.get(i + 1) == 770) {
                            sb.append("aa");
                            i = i + 2;
                            break;
                        } else if (codes.get(i + 1) == 774) {
                            sb.append("aw");
                            i = i + 2;
                            break;
                        }
                    }
                    sb.append("a");
                    i++;
                    break;
                case 101:
                    if (i + 1 < len && codes.get(i + 1) == 770) {
                        sb.append("ee");
                        i = i + 2;
                    } else {
                        sb.append("e");
                        i++;
                    }
                    break;
                case 111:
                    if (i + 1 < len) {
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
                    if (i + 1 < len && codes.get(i + 1) == 795) {
                        sb.append("uw");
                        i = i + 2;
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

    public String convertSentence(String input) {
        StringBuilder sb = new StringBuilder();
        String[] parts = input.split(" ");
        for (String part : parts) {
            sb.append(this.convertToken(part));
            sb.append(" ");
        }
        return sb.toString();
    }


    public String convertParagraph(ArrayList<String> lines) {
        String parts[];
        String ans = "";
        for (String line : lines) {
//                    System.out.println(line);
            if (!line.trim().isEmpty()) {
                line = line.trim().replaceAll(",$", "").replaceAll(":$", "").replaceAll("-", "");
                parts = line.trim().split("\\.");
                for (String part : parts) {
//                    writer.write(this.convertSentence(part.replaceAll("\"", "")));
                    ans += this.convertSentence(part.replaceAll("\"", ""));
//                    writer.write(". ");
                    ans += ". ";
                }
            }
//            writer.write(" ");
            ans += " ";
        }
        return ans;
    }

    public static void main(String[] argv) {
        Unicode2Telex u2t = new Unicode2Telex();
        System.out.println(u2t.convertSentence("Trên thực tế, người dân vẫn thắc mắc những tuyến đường cải tạo! nâng cấp không phải tốn quá nhiều tiền vào việc giải phóng mặt bằng? đắp nền đường, mở rộng vỉa hè… như xây dựng mới nhưng lại có mức phí tương đương với các đường được đầu tư mới."));

    }

}