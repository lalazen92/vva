package vav.cyberspace.viettel.vva.uploadrecord;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by thanhtn10 on 5/19/16.
 */
public class SendFileTCP {

    public String sendfile(String hostname, String port, String filePath){
        String result = "";
        Socket socket = null;
        try{
            socket = new Socket();
            int timeout = 15000;
            socket.connect(new InetSocketAddress(hostname, Integer.parseInt(port)), timeout);
            socket.setSoTimeout(timeout);
            File file = new File(filePath);
            // Get the size of the file
            long length = file.length();
            byte[] bytes = new byte[16*1024];
            InputStream in = new FileInputStream(file);
            OutputStream out = socket.getOutputStream();
            InputStream respone = socket.getInputStream();
            int count;
            String leng_str = "$LEN$"+Long.toString(length);
            out.write(leng_str.getBytes());

            count = respone.read(bytes);
            if(count > 0) {
                byte []lenghtlist = Arrays.copyOf(bytes, count);
                String str = new String(lenghtlist, "UTF-8");
                if(str.contains("OK")){
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    //  out.write('\n');
                    count = respone.read(bytes);
                    if(count > 0){
                        byte []data = Arrays.copyOf(bytes, count);
                        str = new String(data, "UTF-8");
                        result = str.trim();
                     /*   int pos = str.indexOf("$");
                        if(pos != -1){
                            result = str.substring(0, pos);
                            result = result.trim();
                        }else{

                            result =str.substring(0, count-2);
                            result = result.trim();
                        }*/
                    }
                }

            }


            out.close();
            in.close();
            socket.close();

        }catch (UnknownHostException ex){
            ex.printStackTrace();
            result =  "Error: "+ex.toString();
         //   result = "Đã xẩy ra lỗi!";
        }catch (IOException ex){
            ex.printStackTrace();
          //  result =  ex.toString();
        //    result = "Đã xẩy ra lỗi!";
            result =  "Error: "+ex.toString();
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
