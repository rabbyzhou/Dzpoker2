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
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.adapter.SelectCityAdapter;
import com.yijian.dzpoker.view.data.City;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class SelectCityActivity extends BaseToolbarActivity {
    private String  mProvince;
    private List<String> mlistCity= new ArrayList<String>();
    //private ListView lv_city;
    private RecyclerView lv_city;
    private LinearLayoutManager mLayoutManager ;
    private SelectCityAdapter mAdapter;
    private LinearLayout exitLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        //获得传过来的province
        //mProvince="广东省";
        Intent intent = getIntent();
        exitLayout = (LinearLayout)findViewById(R.id.exit);
        exitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setToolbarTitle("地区选择");
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if ( backText != null && !backText.isEmpty()){
            TextView exitText = (TextView)findViewById(R.id.tv_exit);
            exitText.setText(backText);
        }

        mProvince = intent.getStringExtra("province");
        TextView tv_province=(TextView)findViewById(R.id.tv_province);
        tv_province.setText("已选择 "+mProvince);

        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("province",mProvince);

                    String strURL=getString(R.string.url_remote);
                    strURL+="func=getcity&param="+jsonObj.toString();



                    //创建okHttpClient对象
                    //OkHttpClient mOkHttpClient = new OkHttpClient();
                    //创建一个Request
                    final Request request = new Request.Builder().url(strURL).build();
                    //new call
                    Call call = DzApplication.getHttpClient().newCall(request);
                    //请求加入调度
                    call.enqueue(new Callback()
                    {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result=response.body().string();
                            try{
                            JSONArray arrCity=new JSONArray(result);

                            mlistCity=new ArrayList<String>(arrCity.length());
                            for (int i=0;i<arrCity.length();i++){
                                JSONObject json=new JSONObject(arrCity.getString(i));
                                mlistCity.add(i,json.getString("city"));
                            }
                            handler.sendEmptyMessage(0x1001);
                            }catch (Exception e){
                                ToastUtil.showToastInScreenCenter(SelectCityActivity.this,"取城市数据列表失败，请稍后重试!");
                            }
                        }


                    });




//                    URL url = new URL(strURL);
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
//                    conn.setConnectTimeout(5 * 1000);   // 单位是毫秒，设置超时时间为5秒
//                    conn.setRequestMethod("GET");       // HttpURLConnection是通过HTTP协议请求path路径的，所以需要设置请求方式,可以不设置，因为默认为GET
//                    if (conn.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
//                        InputStream is = conn.getInputStream(); // 获取输入流
//                        byte[] data = Util.readStream(is);   // 把输入流转换成字符数组
//                        String result = new String(data);        // 把字符数组转换成字符串
//
//                        if (result.equals("")){
//                            ToastUtil.showToastInScreenCenter(SelectCityActivity.this,"取城市数据列表失败!");
//                            return;
//                        }
//                        JSONArray arrCity=new JSONArray(result);
//
//                        mlistCity=new ArrayList<String>(arrCity.length());
//                        for (int i=0;i<arrCity.length();i++){
//                            JSONObject json=new JSONObject(arrCity.getString(i));
//                            mlistCity.add(i,json.getString("city"));
//                        }
//                        handler.sendEmptyMessage(0x1001);
//
//                    }

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(SelectCityActivity.this,"取城市数据列表失败，请稍后重试!");
                }

            }
        });
        thread.start();



    }

    private void initListView(){
        lv_city = (RecyclerView)findViewById(R.id.lv_city);
        //创建默认的线性LayoutManager
        mLayoutManager = new LinearLayoutManager(this);
        lv_city.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        lv_city.setHasFixedSize(true);
        lv_city.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        //创建并设置Adapter

        List<City> mCity= new ArrayList<City>();
        for(int i=0;i<mlistCity.size();i++){
            City city=new City();
            city.cityName=mlistCity.get(i);
            mCity.add(city);

        }

        //mAdapter = new SelectCityAdapter(mCity);
        mAdapter = new SelectCityAdapter(this, new SelectCityAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(City city) {

                Intent intent=new Intent();
                intent.putExtra("province",mProvince );
                intent.putExtra("city",city.cityName );
                intent.putExtra("location",mProvince+" "+city.cityName );
                setResult(1,intent);
                finish();

            }


        });
        lv_city.setAdapter(mAdapter);
        mAdapter.setData(mCity);
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

}
