package com.gowhich.androidhandlerandmessage;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private ImageView imageView;
    private String imagePath = "http://d.hiphotos.baidu.com/image/pic/item/d0c8a786c9177f3e3ca2960c72cf3bc79f3d5618.jpg";
    private final int IS_FINISH = 1;
    ProgressDialog progressDialog = null;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            byte[] data = (byte[] )msg.obj;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            imageView.setImageBitmap(bitmap);
            if(msg.what == IS_FINISH){
                progressDialog.dismiss();
            }
        }
    };

    public class MyThread implements Runnable{

        @Override
        public void run() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(imagePath);
            HttpResponse httpResponse = null;
//            InputStream inputStream = null;

            try{
                httpResponse = httpClient.execute(httpGet);
                if(httpResponse.getStatusLine().getStatusCode() == 200){
//                    inputStream = httpResponse.getEntity().getContent();
                    byte[] data = EntityUtils.toByteArray(httpResponse.getEntity());
                    Message message = Message.obtain();
                    message.obj = data;
                    message.what = IS_FINISH;
                    handler.sendMessage(message);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) this.findViewById(R.id.button);
        imageView = (ImageView) this.findViewById(R.id.imageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("下载提示");
        progressDialog.setMessage("下载图片...");
        progressDialog.setCancelable(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new MyThread()).start();
                progressDialog.show();
            }
        });
    }
}
