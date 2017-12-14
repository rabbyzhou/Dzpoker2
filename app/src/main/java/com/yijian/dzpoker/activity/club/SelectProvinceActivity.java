package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.adapter.SelectProvinceAdapter;
import com.yijian.dzpoker.view.data.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SelectProvinceActivity extends BaseToolbarActivity {
    private List<String> mlistProvince = new ArrayList<String>();
    private RecyclerView lv_province;
    private LinearLayoutManager mLayoutManager ;
    private SelectProvinceAdapter mAdapter;
    private LinearLayout exitLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_province);

        exitLayout = (LinearLayout)findViewById(R.id.exit);
        exitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setToolbarTitle("地区选择");
        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if ( backText != null && !backText.isEmpty()){
            TextView exitText = (TextView)findViewById(R.id.tv_exit);
            exitText.setText(backText);
        }

        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();

                    String strURL=getString(R.string.url_remote);
                    strURL+="func=getprovince&param="+jsonObj.toString();

                    URL url = new URL(strURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
                    conn.setConnectTimeout(5 * 1000);   // 单位是毫秒，设置超时时间为5秒
                    conn.setRequestMethod("GET");       // HttpURLConnection是通过HTTP协议请求path路径的，所以需要设置请求方式,可以不设置，因为默认为GET
                    if (conn.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
                        InputStream is = conn.getInputStream(); // 获取输入流
                        byte[] data = Util.readStream(is);   // 把输入流转换成字符数组
                        String result = new String(data);        // 把字符数组转换成字符串

                        if (result.equals("")){
                            ToastUtil.showToastInScreenCenter(SelectProvinceActivity.this,"取省份数据列表失败!");
                            return;
                        }
                        JSONArray arrProvince=new JSONArray(result);

                        mlistProvince=new ArrayList<String>(arrProvince.length());
                        for (int i=0;i<arrProvince.length();i++){
                            JSONObject json=new JSONObject(arrProvince.getString(i));
                            mlistProvince.add(i,json.getString("province"));
                        }
                        handler.sendEmptyMessage(0x1001);

                    }

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(SelectProvinceActivity.this,"取省份数据列表失败，请稍后重试!");
                }

            }
        });
        thread.start();



    }

    private void initListView(){
        lv_province = (RecyclerView)findViewById(R.id.rv_province);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        lv_province.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        lv_province.setHasFixedSize(true);
        lv_province.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        //创建并设置Adapter

        List<Province> mProvince= new ArrayList<Province>();
        for(int i=0;i<mlistProvince.size();i++){
            Province provicne=new Province();
            provicne.provinceName=mlistProvince.get(i);
            mProvince.add(provicne);

        }

        //mAdapter = new SelectCityAdapter(mCity);
        mAdapter = new SelectProvinceAdapter(this, new SelectProvinceAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(Province province) {
                Intent intent = new Intent();
                intent.putExtra("province",province.provinceName );
                intent.setClass(SelectProvinceActivity.this, SelectCityActivity.class);
                startActivityForResult(intent,1);
                //finish();


            }


        });
        lv_province.setAdapter(mAdapter);
        mAdapter.setData(mProvince);

    }

    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == 0x1001) {
                initListView();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==1) {
            //刷新界面的数据
            Intent intent=new Intent();
            intent.putExtra("province",data.getExtras().getString("province") );
            intent.putExtra("city",data.getExtras().getString("city") );
            intent.putExtra("location",data.getExtras().getString("location") );
            setResult(1,intent);
            finish();

        }
    }
}
