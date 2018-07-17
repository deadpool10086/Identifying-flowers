package com.example.neuron.camaertest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    private Button BCamer;
    private Button BPthoto;
    private Uri imageUri;
    private ImageView picture;
    private TextView textView;
    private MyTask myts;
    private String typeResult;
    private String imagePath = null;
    private Intent intent;
    @SuppressLint("HandlerLeak")
    private Handler mTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                textView.setText(typeResult); //View.ininvalidate()
                sendEmptyMessageDelayed(0, 1000);
            }
        }
    };
    public class MIntentService extends IntentService {

        public MIntentService(){
            super("MIntentService");
        }

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         * @param name Used to name the worker thread, important only for debugging.
         */
        public MIntentService(String name) {
            super(name);
        }

        @Override
        public void onCreate() {
            Log.e("MIntentService--", "onCreate");
            super.onCreate();
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e("MIntentService--", "onStartCommand");
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.e("MIntentService--", Thread.currentThread().getName() + "--" + intent.getStringExtra("info") );
            try {
                //setTitle(String.format("ninini%d", count++));
                InputStream inStream = null;
                inStream = getContentResolver().openInputStream(imageUri);
                tcpSender(inStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDestroy() {
            Log.e("MIntentService--", "onDestroy");
            super.onDestroy();
        }
    }
    public class MyRunnable implements Runnable{
        @Override
        public void run() {
            try {
                //setTitle(String.format("ninini%d", count++));
                InputStream inStream = null;
                inStream = getContentResolver().openInputStream(imageUri);
                tcpSender(inStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    class MyTask extends AsyncTask<Void, Void, Void> {
        @SuppressLint("DefaultLocale")
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //setTitle(String.format("ninini%d", count++));
                InputStream inStream = null;
                inStream = getContentResolver().openInputStream(imageUri);
                tcpSender(inStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("花卉识别系统");
        setContentView(R.layout.activity_main);
        BCamer = (Button) findViewById(R.id.BCamer);
        BCamer.setOnClickListener(this);
        BPthoto = (Button) findViewById(R.id.BPhoto);
        BPthoto.setOnClickListener(this);
        picture = (ImageView) findViewById(R.id.IPhoto);
        textView = (TextView) findViewById(R.id.type);
        //      a = new Thread(new MyRunnable());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BCamer:
                takePhoto(v);
                break;
            case R.id.BPhoto:
                openPhoto(v);
                break;
        }
        return;
    }

    @SuppressLint("Registered")
    class ExampleService extends Service
    {
        private static final String TAG = "Example";

        @Override
        public IBinder onBind(Intent intent)
        {
            return null;
        }

        @Override
        public void onCreate()
        {
            Log.i(TAG, "ExampleService===>>onCreate");
            super.onCreate();
        }

        @Override
        public void onStart(Intent intent, int startId)
        {
            Log.i(TAG, "ExampleService===>>onStart");
            InputStream inStream = null;
            try {
                inStream = getContentResolver().openInputStream(imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            tcpSender(inStream);
            super.onStart(intent, startId);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId)
        {
            Log.i(TAG, "ExampleService===>>onStartCommand");
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onDestroy()
        {
            Log.i(TAG, "ExampleService===>>onDestroy");
            super.onDestroy();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Time time = new Time();
                        time.setToNow();
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imagePath = time.toString() + ".jpg";
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
//                    if (myts != null && myts.getStatus() == AsyncTask.Status.RUNNING)
//                        myts.cancel(true);
//                    myts  = new MyTask();
//                    myts.execute();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }

                }
                break;
        }
        if (myts != null && myts.getStatus() == AsyncTask.Status.RUNNING)
            myts.cancel(true);
        myts  = new MyTask();
        myts.execute();
//        Intent intent = new Intent(this, MIntentService.class);
//        intent.putExtra("info", "good good study");
//        startService(intent);
       // intent = new Intent(MainActivity.this, ExampleService.class);
        //startService(intent);
//        a.start();
//        try{
//            InputStream inStream = null;
//            inStream = getContentResolver().openInputStream(imageUri);
//            udpSender(inStream);
//        }
//        catch(IOException e)
//        {
//            e.printStackTrace();
//        }
    }

    public void takePhoto(View v) {
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(MainActivity.this,
                    "com.example.cameraalbumtest.fileprovider", outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
        return;
    }

    public void openPhoto(View v) {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
        return;
    }

    public void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUti = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUti, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        imageUri = uri;
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection,
                null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
            //udpSender(imagePath);
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean tcpSender(InputStream inStream)
    {

        byte[] tempbytes = new byte[2048];
        byte[] dd = new byte[400];

        try{
            //InputStreamReader isr = new InputStreamReader(fis);
            //创建Socket实例
            Socket socket = new Socket("10.42.0.1",8899);
            //获取输出流
            OutputStream outputStream = socket.getOutputStream();
            outputStream.flush();
            outputStream.write((imagePath + "\0").getBytes());
            int n = 1;
            while(inStream.read(tempbytes) > 0){
                outputStream.write(tempbytes);
                System.out.println(String.format("asdasdasd%d\'", n++));
                //Toast.makeText(getApplicationContext(), String.format("你好世界%d", n++) ,Toast.LENGTH_LONG).show();
            }
            outputStream.flush();
            outputStream.flush();
            outputStream.write("EOF!".getBytes());
            System.out.println("I'm out of eof ");

            InputStream is = socket.getInputStream();
            System.out.println("I'm IN X");
            int x = is.read(dd);
            System.out.println("I'm out of X ");
            typeResult = new String(dd,0,x);
            System.out.println(typeResult);
            mTimeHandler.sendEmptyMessageDelayed(0, 1000);
            outputStream.close();
            is.close();
            socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public boolean udpSender(InputStream inStream) {
        long startTime = System.currentTimeMillis();

        byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
        byte[] receiveBuf = new byte[80];

        RandomAccessFile accessFile = null;
        DatagramPacket dpk = null;
        DatagramSocket dsk = null;
        int readSize = -1;
        try {
            //accessFile = new RandomAccessFile(imagePath,"r");
            dpk = new DatagramPacket(buf, buf.length, new InetSocketAddress(InetAddress.getByName("118.89.18.165"), 4567));
            dsk = new DatagramSocket();
            int sendCount = 0;
            //while((readSize = accessFile.read(buf,0,buf.length)) != -1){
            while ((readSize = inStream.read(buf)) != -1) {
                dpk.setData(buf, 0, readSize);
                dsk.send(dpk);
                // wait server response
                {
                    while (true) {
                        dpk.setData(receiveBuf, 0, receiveBuf.length);
                        dsk.receive(dpk);

                        // confirm server receive
                        if (!UDPUtils.isEqualsByteArray(UDPUtils.successData, receiveBuf, dpk.getLength())) {
                            dpk.setData(buf, 0, readSize);
                            dsk.send(dpk);
                        } else
                            break;
                    }
                }
            }
            // send exit wait server response
            while (true) {
                //System.out.println("client send exit message ....");
                dpk.setData(UDPUtils.exitData, 0, UDPUtils.exitData.length);
                dsk.send(dpk);

                dpk.setData(receiveBuf, 0, receiveBuf.length);
                dsk.receive(dpk);
                //byte[] receiveData = dpk.getData();
                if (!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())) {
                    //System.out.println("client Resend exit message ....");
                    dsk.send(dpk);
                } else
                    break;
            }
            dsk.receive(dpk);
            typeResult = new String(receiveBuf, 0, dpk.getLength());
            mTimeHandler.sendEmptyMessageDelayed(0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (accessFile != null)
                    accessFile.close();
                if (dsk != null)
                    dsk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        //System.out.println("time:" + (endTime - startTime));
        return true;
    }

}


