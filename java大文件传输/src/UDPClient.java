import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

//import com.bill.udp.util.UDPUtils;
/**
 * Test File transfer of Client
 * @author Bill QQ:593890231
 * @since v1.0 2014/09/21
 */
public class UDPClient {

    private static final String SEND_FILE_PATH = "example.jpg";

    public static void main(String[] args){
        long startTime = System.currentTimeMillis();

        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
        byte[] receiveBuf = new byte[80];

        RandomAccessFile accessFile = null;
        DatagramPacket dpk = null;
        DatagramSocket dsk = null;
        int readSize = -1;
        try {
            accessFile = new RandomAccessFile(SEND_FILE_PATH,"r");
            dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 4567));
            dsk = new DatagramSocket();
            int sendCount = 0;
            while((readSize = accessFile.read(buf,0,buf.length)) != -1){
                dpk.setData(buf, 0, readSize);
                dsk.send(dpk);
                // wait server response
                {
                    while(true){
                        dpk.setData(receiveBuf, 0, receiveBuf.length);
                        dsk.receive(dpk);

                        // confirm server receive
                        if(!UDPUtils.isEqualsByteArray(UDPUtils.successData,receiveBuf,dpk.getLength())){
                            System.out.println("resend ...");
                            dpk.setData(buf, 0, readSize);
                            dsk.send(dpk);
                        }else
                            break;
                    }
                }

                System.out.println("send count of "+(++sendCount)+"!");
            }
            // send exit wait server response
            while(true){
                System.out.println("client send exit message ....");
                dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);
                dsk.send(dpk);

                dpk.setData(receiveBuf,0,receiveBuf.length);
                dsk.receive(dpk);
                // byte[] receiveData = dpk.getData();
                if(!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())){
                    System.out.println("client Resend exit message ....");
                    dsk.send(dpk);
                }else
                    break;
            }
            dsk.receive(dpk);
            String backMes = new String(receiveBuf, 0, dpk.getLength());
            System.out.println(backMes);
        }catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if(accessFile != null)
                    accessFile.close();
                if(dsk != null)
                    dsk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("time:"+(endTime - startTime));
    }
}
