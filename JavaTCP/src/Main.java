import java.io.*;
import java.net.Socket;

public class Main {


    public static void clientR() {
        byte[] tempbytes = new byte[2048];
        byte[] dd = new byte[2048];

        //File file = new File("./1.txt");
        try{
            FileInputStream fis = new FileInputStream("./1.bmp");
            //InputStreamReader isr = new InputStreamReader(fis);
            //创建Socket实例
            Socket socket = new Socket("127.0.0.1",8899);
            //获取输出流
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("./1.bmp".getBytes());
            while(fis.read(tempbytes) > 0){
                outputStream.write(tempbytes);
            }
            outputStream.write("EOF!".getBytes());
            //释放资源
            InputStream is = socket.getInputStream();
            int x = is.read(dd);
            System.out.println(new String(dd,0,x));
            outputStream.close();
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        clientR();
        return;
    }


}

