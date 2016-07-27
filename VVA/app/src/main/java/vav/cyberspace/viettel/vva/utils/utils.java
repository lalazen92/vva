package vav.cyberspace.viettel.vva.utils;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by thanhtn10 on 4/4/16.
 */
public class utils {

    public  static Map<String, String> getParameters(String input){
        Map<String, String> result = new HashMap<>();

        String[] list = input.split("&");
        for (int i = 0; i< list.length; i++){
            String []items = list[i].split("=");
            if(items.length == 2){
                result.put(items[0], items[1]);
            }
        }

        return  result;
    }
    public static String stringToHTMLString(String string) {
        StringBuffer sb = new StringBuffer(string.length());
        // true if last char was blank
        boolean lastWasBlankChar = false;
        int len = string.length();
        char c;

        for (int i = 0; i < len; i++)
        {
            c = string.charAt(i);
            if (c == ' ') {
                // blank gets extra work,
                // this solves the problem you get if you replace all
                // blanks with &nbsp;, if you do that you loss
                // word breaking
                if (lastWasBlankChar) {
                    lastWasBlankChar = false;
                    sb.append("&nbsp;");
                }
                else {
                    lastWasBlankChar = true;
                    sb.append(' ');
                }
            }
            else {
                lastWasBlankChar = false;
                //
                // HTML Special Chars
                if (c == '"')
                    sb.append("&quot;");
                else if (c == '&')
                    sb.append("&amp;");
                else if (c == '<')
                    sb.append("&lt;");
                else if (c == '>')
                    sb.append("&gt;");
                else if (c == '\n')
                    // Handle Newline
                    sb.append("&lt;br/&gt;");
                else {
                    int ci = 0xffff & c;
                    if (ci < 160 )
                        // nothing special only 7 Bit
                        sb.append(c);
                    else {
                        // Not 7 Bit use the unicode system
                        sb.append("&#");
                        sb.append(new Integer(ci).toString());
                        sb.append(';');
                    }
                }
            }
        }
        return sb.toString();
    }
    public static String removeAccent(String s) {
        s = s.replaceAll("đ", "d");
        s = s.replaceAll("Đ", "d");
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
    public static String[] SplitUsingTokenizer(String subject, String delimiters) {
        StringTokenizer strTkn = new StringTokenizer(subject, delimiters);
        ArrayList<String> arrLis = new ArrayList<String>(subject.length());

        while(strTkn.hasMoreTokens())
            arrLis.add(strTkn.nextToken());

        return arrLis.toArray(new String[0]);
    }
    public static String mappingstringtoapp(String text, ArrayList<String> mapping){
        text = text.toLowerCase();
        for (int i = 0; i < mapping.size(); i++){
            String []strMappingList = mapping.get(i).split("->");
            if(strMappingList.length > 1){
                text = text.replaceAll("\\b" +  strMappingList[0].trim().toLowerCase()+"\\b", strMappingList[1].trim());
            }
        }
      /*  text = text.replaceAll("trình duyệt", "chrome");
        text = text.replaceAll("trình nghe nhạc", "music zing nhaccuatui");
        text = text.replaceAll("thư viện", "photos gallery");
        text = text.replaceAll("bản đồ", "maps");
        text = text.replaceAll("đồng hồ", "clock");
        text = text.replaceAll("danh bạ", "contacts");
        text = text.replaceAll("tin nhắn", "message");
        text = text.replaceAll("thư điện tử", "gmail");
        text = text.replaceAll("mạng xã hội", "facebook");
        text = text.replaceAll("ghi nhớ", "note supernote");

        text = text.replaceAll("máy ảnh", "camera");

        text = text.replaceAll("từ điển", "dict dictionary");

        text = text.replaceAll("tùy chọn", "settings option");
        text = text.replaceAll("thu âm", "audio recorder");
        text = text.replaceAll("xem lịch", "calendar");
        text = text.replaceAll("tính toán", "calculator");
        text = text.replaceAll("thời tiết", "weather");
        text = text.replaceAll("quản lý file", "filemanager");
        text = text.replaceAll("flash", "flashlight");*/
        text = text.replaceAll("mở", "");

        return text;
    }

    public static String mappingstringtonumber(String number){
        number = number.replaceAll("khong ", "0");
        number = number.replaceAll("không ", "0");
        number = number.replaceAll("mot ", "1");
        number = number.replaceAll("một ", "1");
        number = number.replaceAll("hai ", "2");
        number = number.replaceAll("ba ", "3");
        number = number.replaceAll("bon ", "4");
        number = number.replaceAll("bốn ", "4");
        number = number.replaceAll("nam ", "5");
        number = number.replaceAll("năm ", "5");
        number = number.replaceAll("sau ", "6");
        number = number.replaceAll("sáu ", "6");
        number = number.replaceAll("bay ", "7");
        number = number.replaceAll("bảy ", "7");
        number = number.replaceAll("bẩy ", "7");
        number = number.replaceAll("tam ", "8");
        number = number.replaceAll("tám ", "8");
        number = number.replaceAll("chin ", "9");
        number = number.replaceAll("chín ", "9");
        return number;
    }

}
