import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args)throws IOException{
        DatagramSocket client = new DatagramSocket();

        String sendStr = "Hello! I'm Client";
        byte[] sendBuf;
        sendBuf = sendStr.getBytes();
        InetAddress addr = InetAddress.getByName("127.0.0.1");
        int port = 8080;
        DatagramPacket sendPacket
                = new DatagramPacket(sendBuf ,sendBuf.length , addr , port);
        client.send(sendPacket);
        byte[] recvBuf = new byte[100];
        DatagramPacket recvPacket
                = new DatagramPacket(recvBuf , recvBuf.length);
        client.receive(recvPacket);
        client.send(new DatagramPacket(sendBuf ,0 , addr , port));
        String recvStr = new String(recvPacket.getData() , 0 ,recvPacket.getLength());
        System.out.println("收到:" + recvStr);
        client.close();
    }
}
