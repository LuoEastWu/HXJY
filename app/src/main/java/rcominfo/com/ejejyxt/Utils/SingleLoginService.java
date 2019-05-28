package rcominfo.com.ejejyxt.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.Header;

/**
 * Created by 王璐阳 on 2017/2/23.
 */
public class SingleLoginService extends android.app.Service{

    private Bundle extras;
    private boolean flag = true;
    private SyncHttpClient syncHttpClient;
    private PostTypeMsg mBean;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        syncHttpClient = new SyncHttpClient();

        netThread netThread = new netThread();
        netThread.start();
        super.onCreate();
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

                   extras = intent.getExtras();
                   return super.onStartCommand(intent, flags, startId);
    }
    private class netThread extends Thread{
        @Override
        public void run() {
            while (flag){
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final String url = "http://47.90.48.6:8888/PDA/AllTransfer";
                Log.e("url_base",url);
                final String id = extras.getString("id");
                String MD5Result = MD5.getMD5("{\"name\":\"lmq\",\"id\":" + id + "}"+WebAPI.KEY);
                final AsyncHttpClient client = new AsyncHttpClient();

                RequestParams params = new RequestParams();
                params.add("FunctionName","CheckUid");
                params.add("JsonData","{\"name\":\"lmq\",\"id\":" + id + "}");
                params.add("CusID", WebAPI.CUSID);
                params.add("KeyMd5",MD5Result);
                syncHttpClient.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        if(!new String(bytes).equals("")&&!(new String(bytes)==null)){
                            mBean = new Gson().fromJson(new String(bytes), PostTypeMsg.class);
                            if(mBean.State){
                                Log.e("PostError","CheckUid" +" "+ "{\"name\":\"lmq\",\"id\":" + id + "}");
                                Log.e("PostError","CheckUid" +" "+ new String(bytes));
                            }else if(!mBean.State){
                                Intent in = new Intent("com.rcominfo.broadcastReceiver");
                                sendBroadcast(in);
                                Log.e("123",123+"");
                                flag=false;
                                Log.e("PostError","CheckUid" +" "+ "{\"name\":\"lmq\",\"id\":" + id + "}");
                                Log.e("PostError","CheckUid" +" "+ new String(bytes));
                                Log.e("1","2");
                            }
                        }
                    }
                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }
                });
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
