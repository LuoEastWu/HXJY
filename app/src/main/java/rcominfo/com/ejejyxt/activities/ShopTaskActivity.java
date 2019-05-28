package rcominfo.com.ejejyxt.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.rcominfo.ejejyxt.R;

import java.util.ArrayList;

import rcominfo.com.ejejyxt.Bean.GetBean.PostTypeMsg;
import rcominfo.com.ejejyxt.Bean.PostBean.BeanAll;
import rcominfo.com.ejejyxt.Bean.ShopBean;
import rcominfo.com.ejejyxt.Utils.AsyncHttpPost;
import rcominfo.com.ejejyxt.Utils.InfoCode;
import rcominfo.com.ejejyxt.Utils.JsonCreate;
import rcominfo.com.ejejyxt.Utils.MediaPlayer;
import rcominfo.com.ejejyxt.Utils.MyAdapter_task;
import rcominfo.com.ejejyxt.Utils.ShareUtil;
import rcominfo.com.ejejyxt.Utils.SimpleAda2;
import rcominfo.com.ejejyxt.Utils.ToastUtil;
import rcominfo.com.ejejyxt.Utils.VibratorUtil;
import rcominfo.com.ejejyxt.Utils.WebAPI;

public class ShopTaskActivity extends BaseActivity {

    private ShopTaskActivity mContext;
    private ListView lv_task;
    private EditText ed_code;
    private TextView tv_num;
    private Button btn_stop;
    private CommonListener commonListener;
    private Gson gson;
    private BeanAll.pickingShelvesList bean;
    private ArrayList<BeanAll.OffShelfRuturn> container;
    private MyAdapter_task ada;
    private ProgressDialog pDialog;
    private String out_barcode;
    private Button btn_finish_task;
    private TextView orderid;
    private ListView lv;
    private ArrayList<ShopBean.tbim> cccccc;
    private SimpleAda2 ada2;
    private AlertDialog a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_shop);
        container = new ArrayList<BeanAll.OffShelfRuturn>();
        IntentFilter intentFilter = new IntentFilter(InfoCode.SCN_CUST_ACTION_SCODE);
        registerReceiver(mSamDataReceiver, intentFilter);
        gson = new Gson();
        mContext = this ;

        initView();
        getSetShopId();
        setOnClick();
        OnEditor();
        OnItemclick();

    }

    private void getSetShopId() {
        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
        ab.setTitle("请选择门店");
        cccccc = new ArrayList<>();
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_shoplist_alert,null);
        lv = (ListView)v.findViewById(R.id.lv);
        ada2 = new SimpleAda2(mContext, cccccc);
        lv.setAdapter(ada2);
        AsyncHttpPost.post(mContext, "ShopList", "{}", new AsyncHttpPost.Postinterface() {
            @Override
            public void onSuccess(PostTypeMsg mBean, AsyncHttpClient Client) {
                ShopBean shopBean = gson.fromJson(mBean.ReturnJson,ShopBean.class);
                cccccc.addAll(shopBean.tbiList);
                ada2.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String Msg) {
                ToastUtil.Show(mContext,Msg);
            }
        });
        ab.setView(v);
        ab.setCancelable(false);
        ab.setNegativeButton("取消",null);
        a = ab.create();
        a.show();
        a.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                requestTask(ShareUtil.getInstance(mContext).getNickName(),cccccc.get(i).ID);
                a.dismiss();
            }
        });
    }

    private void OnItemclick() {
        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    ToastUtil.Show(mContext,"客官，我是名称");
                }else{
                    AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                    View view1 = LayoutInflater.from(mContext).inflate(R.layout.ab_task_view, null);
                    ab.setTitle("详细信息");
                    ab.setView(view1);
                    TextView tv_weight = (TextView)view1.findViewById(R.id.tv_weight);
                    TextView tv_guige = (TextView)view1.findViewById(R.id.tv_guige);
                    tv_weight.setText("重量："+container.get(i-1).dd_weight2);
                    tv_guige.setText("长*宽*高："+container.get(i-1).guige);
                    ab.setNegativeButton("OK",null);
                    ab.create();
                    ab.show();
                }


            }
        });
    }

    private BroadcastReceiver mSamDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(InfoCode.SCN_CUST_ACTION_SCODE)) {
                String message = intent.getStringExtra(InfoCode.SCN_CUST_EX_SCODE);
                if (ed_code.hasFocus()) {
                    ed_code.setText(message);
                    setTextFocus(ed_code);
                    ed_code.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                }
            }
        }
    };


    private void OnEditor() {
        ed_code.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i==0&&keyEvent.getAction()==KeyEvent.ACTION_DOWN){
                    String code = ed_code.getText().toString();
                    postCode(code);
                }
                return true;
            }
        });
    }
//异常捡货
    private void postCode(final String code) {
        ed_code.setEnabled(false);
        AsyncHttpPost.post(mContext, WebAPI.ERROR_JHXJ, JsonCreate.JHXJfind(container.get(0).out_barcode, code, ShareUtil.getInstance(mContext).getNickName()), new AsyncHttpPost.Postinterface() {
            @Override
            public void onSuccess(PostTypeMsg mBean, AsyncHttpClient Client) {
                ed_code.setEnabled(true);
                MediaPlayer.getInstance(mContext).ok();
                VibratorUtil.Vibrate(mContext,500);
                ed_code.setText("");
               setTextFocus(ed_code);

                if(container.size()>1){
                    for(int i = 0;i<container.size();i++){
                        if(container.get(i).kd_billcode.equals(code)){
                            container.remove(i);
                            ada.notifyDataSetChanged();
                            ed_code.setText("");
                           setTextFocus(ed_code);
                        }
                    }
                }else if(container.size()==1){
                    container.clear();
                    ada.notifyDataSetChanged();
                    finishtask();

                }

            }

            @Override
            public void onFailure(String Msg) {
                MediaPlayer.getInstance(mContext).error();
                VibratorUtil.Vibrate(mContext,1000);
                ToastUtil.Show(mContext,Msg);
                ed_code.setEnabled(true);
                ed_code.setText("");
               setTextFocus(ed_code);
            }
        });
    }
//终止捡货
    private void finishtask() {
        AsyncHttpPost.post(mContext, WebAPI.SZJHXJ, JsonCreate.FinishTask(out_barcode,ShareUtil.getInstance(mContext).getNickName(),ShareUtil.getInstance(mContext).getWaveHouse()), new AsyncHttpPost.Postinterface() {
            @Override
            public void onSuccess(PostTypeMsg mBean, AsyncHttpClient Client) {
                ToastUtil.Show(mContext,"当前任务完成");
                MediaPlayer.getInstance(mContext).ok();
                isGetNewTask();
            }

            @Override
            public void onFailure(String Msg) {
                ToastUtil.Show(mContext, Msg);
                MediaPlayer.getInstance(mContext).error();
                AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
                ab.setTitle("拣货失败").setMessage("是否重试");
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finishtask();
                    }
                });
                ab.create();
                ab.show();

            }
        });
    }

    private void setOnClick() {
        btn_stop.setOnClickListener(commonListener);
        btn_finish_task.setOnClickListener(commonListener);
    }
//获取任务列表
    private void requestTask(String waveHouseID,int shopID) {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("加载中...请稍后...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(true);
        pDialog.show();
        AsyncHttpPost.post(mContext, WebAPI.JHXJ_PARENT,JsonCreate.requestPick(ShareUtil.getInstance(mContext).getareaCode(),waveHouseID,"",ShareUtil.getInstance(mContext).getWaveHouse(),ShareUtil.getInstance(mContext).getCountryId(),shopID), new AsyncHttpPost.Postinterface() {
            public String order_id;

            @Override
            public void onSuccess(PostTypeMsg mBean, AsyncHttpClient Client) {
                pDialog.dismiss();
                container.clear();
                MediaPlayer.getInstance(mContext).ok();
                VibratorUtil.Vibrate(mContext,500);
                bean = gson.fromJson(mBean.ReturnJson,BeanAll.pickingShelvesList.class);
                if(bean.OffShelfRuturn==null&&bean.OffShelfRuturn.size()==0){
                    btn_finish_task.setVisibility(View.VISIBLE);
                }else{
                    btn_finish_task.setVisibility(View.GONE);
                    container.addAll(bean.OffShelfRuturn);
                    out_barcode = container.get(0).out_barcode;
                    order_id = container.get(0).OrderId;
                    orderid.setText("订单号:"+order_id);
                    ada = new MyAdapter_task(mContext,container);
                    lv_task.setAdapter(ada);
                    ada.notifyDataSetChanged();
                    ed_code.setText("");
                    setTextFocus(ed_code);
                }

            }

            @Override
            public void onFailure(String Msg) {
                ToastUtil.Show(mContext,Msg);
                pDialog.dismiss();
                getSetShopId();
                MediaPlayer.getInstance(mContext).error();
                VibratorUtil.Vibrate(mContext,1000);
            }
        });
    }

    private void initView() {
        lv_task = (ListView)findViewById(R.id.lv_task);
        ed_code = (EditText)findViewById(R.id.ed_code);
        btn_finish_task = (Button)findViewById(R.id.btn_finish_task);
        btn_stop= (Button)findViewById(R.id.btn_stop);
        orderid = (TextView)findViewById(R.id.orderid);
        commonListener = new CommonListener();

    }
    //是否获取新任务
    private void isGetNewTask(){
        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
        ab.setTitle("提示:");
        ab.setMessage("当前任务完成是否要领取新任务?");
        ab.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               getSetShopId();
            }
        });
        ab.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        ab.setCancelable(false);
        ab.create();

        ab.show();
    }
    class CommonListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_stop:
                    VibratorUtil.Vibrate(mContext,2000);
                    if(container.size()>0){
                        AsyncHttpPost.post(mContext,WebAPI.PICKING_STOP, JsonCreate.StopPicking(container.get(0).out_barcode,ShareUtil.getInstance(mContext).getNickName()), new AsyncHttpPost.Postinterface() {
                            @Override
                            public void onSuccess(PostTypeMsg mBean, AsyncHttpClient Client) {
                                ToastUtil.Show(mContext,"成功");
                                MediaPlayer.getInstance(mContext).ok();
                                VibratorUtil.Vibrate(mContext,500);
                                container.clear();
                                ada.notifyDataSetChanged();
                                ed_code.setText("");
                                isGetNewTask();


                            }

                            @Override
                            public void onFailure(String Msg) {
                                ToastUtil.Show(mContext,Msg);
                                MediaPlayer.getInstance(mContext).error();
                                VibratorUtil.Vibrate(mContext,1000);
                            }
                        });
                    }else{
                        finish();
                    }

                    break;
                case R.id.btn_finish_task:
                    finishtask();
            }
        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("key",event+"");
        if(event.getKeyCode()==23&&event.getAction()==KeyEvent.ACTION_DOWN){
            if(ed_code.hasFocus()){
                ed_code.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                return false;
            }
            return false;
        }else if(event.getKeyCode()==23&&event.getAction()==KeyEvent.ACTION_UP){
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(mSamDataReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //TODO something
            AlertDialog.Builder a = new AlertDialog.Builder(mContext);
            a.setTitle("确认退出");
            a.setMessage("确认退出拣货下架么？");
            a.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String json = JsonCreate.TaskBack(bean.OffShelfRuturn.get(0).out_barcode);
                    AsyncHttpPost.post(mContext, "IStask", json, new AsyncHttpPost.Postinterface() {
                        @Override
                        public void onSuccess(PostTypeMsg mBean, AsyncHttpClient Client) {
                            finish();
                        }

                        @Override
                        public void onFailure(String Msg) {
                            ToastUtil.Show(mContext,"任务未完成");
                        }
                    });
                }
            });
            a.setNegativeButton("取消",null);
            a.create();
            a.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
