package com.yijian.dzpoker.activity.club;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.base.BaseToolbarActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.adapter.SelectCityAdapter;
import com.yijian.dzpoker.view.adapter.SelectProvinceAdapter;
import com.yijian.dzpoker.view.data.City;
import com.yijian.dzpoker.view.data.ClubInfo;
import com.yijian.dzpoker.view.data.Province;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class AddIntoClubActivity extends BaseBackActivity implements View.OnClickListener{

    private TextView tv_exit;
    private RecyclerView rv_hotcity;
    private RecyclerView rv_province;
    private EditText et_search;
    private List<ClubInfo> mListClubInfo=new ArrayList<ClubInfo>();
    private List<City> mHotCity=new ArrayList<City>();
    private List<Province> mProvince=new ArrayList<Province>();
    private SelectCityAdapter mHotCityAdapter;
    private SelectProvinceAdapter mProvinceAdapter;
    private String strLoginName;
    private int userId;
    private  final static int MESAGE_LOAD_OK=0x1001;
    private List<ClubInfo> mClubInfoList=new ArrayList<ClubInfo>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences settings = getSharedPreferences("depoker", 0);
        strLoginName=settings.getString("username","");
        userId=settings.getInt("userid",0);
        setToolbarTitle("加入俱乐部");
        //从服务器获取数据，用来填充recyleview
        getInitData();

    }

    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == MESAGE_LOAD_OK) {
                initHotCityView();
                initProvinceView();
            }

        }
    };


    private void getInitData(){

        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                mHotCity = new ArrayList<City>();
                mProvince=new ArrayList<Province>();

                try{
                    //获取热门城市列表
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();

                    String strURL=getString(R.string.url_remote);
                    strURL+="func=gethotcity&param="+jsonObj.toString();

                    URL url = new URL(strURL);

                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();


                    JSONArray jsonHotCityList = new JSONArray(result);

                    for (int i = 0; i < jsonHotCityList.length(); i++) {
                        JSONObject json=new JSONObject(jsonHotCityList.getString(i));
                        City city = new City();
                        city.cityName =json.getString("city");
                        mHotCity.add(city);
                    }

                    //获取省份列表，这里搜索俱乐部是通过省份列表来搜索，展现俱乐部的时候展现城市
                    //拼装url字符串
                     jsonObj = new JSONObject();

                     strURL = getString(R.string.url_remote);
                     strURL += "func=getprovince&param=" + jsonObj.toString();

                     url = new URL(strURL);
                     request = new Request.Builder().url(strURL).build();
                     response = DzApplication.getHttpClient().newCall(request).execute();
                     result=response.body().string();
                    JSONArray  jsonProvinceList=new JSONArray(result);
                    for (int i=0;i<jsonProvinceList.length();i++){
                        JSONObject json=new JSONObject(jsonProvinceList.getString(i));
                        Province province=new Province();
                        province.provinceName=json.getString("province");
                        mProvince.add(province);
                    }

                     //获取数据后，通知主线程，更新界面
                    handler.sendEmptyMessage(MESAGE_LOAD_OK);


                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(AddIntoClubActivity.this,"从后台取数据报错，请稍后重试!");
                }

            }
        });
        thread.start();



    }

    private void initHotCityView(){

        //创建默认的GridLayoutManager,一行4列
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,4);
        rv_hotcity.setLayoutManager(gridLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rv_hotcity.setHasFixedSize(true);
//        rv_hotcity.addItemDecoration(new DividerItemDecoration(
//                this, DividerItemDecoration.VERTICAL));
        //创建并设置Adapter


        //mAdapter = new SelectCityAdapter(mCity);
        mHotCityAdapter = new SelectCityAdapter(this, new SelectCityAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(City city) {
                searchClub(city.cityName,2);
            }

        });
        rv_hotcity.setAdapter(mHotCityAdapter);
        mHotCityAdapter.setData(mHotCity);
    }

    private void initProvinceView(){
        rv_province = (RecyclerView)findViewById(R.id.rv_province);
        //创建默认的线性LayoutManager
        LinearLayoutManager  mLayoutManager = new LinearLayoutManager(this);
        rv_province.setLayoutManager(mLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rv_province.setHasFixedSize(true);
        rv_province.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        //创建并设置Adapter


        mProvinceAdapter = new SelectProvinceAdapter(this, new SelectProvinceAdapter.OnRecordSelectListener() {
            @Override
            public void onRecordSelected(Province province) {

                searchClub(province.provinceName,3);
            }

        });
        rv_province.setAdapter(mProvinceAdapter);
        mProvinceAdapter.setData(mProvince);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_into_club;
    }

    @Override
    public void initViews(){

        tv_exit=(TextView)findViewById(R.id.tv_exit);

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        if ( backText != null && !backText.isEmpty()){
            tv_exit.setText(backText);
        }

        rv_hotcity=(RecyclerView)findViewById(R.id.rv_hotcity);
        rv_province=(RecyclerView)findViewById(R.id.rv_province);
        et_search=(EditText)findViewById(R.id.et_search);
        et_search.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER && KeyEvent.ACTION_UP==event.getAction()) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(AddIntoClubActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    //进行搜索操作的方法，在该方法中可以加入mEditSearchUser的非空判断
                    if (et_search.getText().toString().equals(""))
                    {
                        ToastUtil.showToastInScreenCenter(AddIntoClubActivity.this,"搜索条件不为空：");
                        return false;
                    }
                    searchClub(et_search.getText().toString(),1);

                }
                return false;
            }
        });

    }


    //通用的搜索俱乐部列表方法  1：搜索框搜索 2：热门城市搜索  3：省份搜索
    private void searchClub(final String condition,final int serachType){
        //将输入的参数传递到列表显示activity

        Intent intent = new Intent();
//                        intent.putExtra("opType",1 );
//                        intent.putExtra("phonenumber", edUserName.getText().toString());

        intent.putExtra("condition",condition);
        intent.putExtra("serachtype",serachType);
        intent.setClass(AddIntoClubActivity.this, SelectClubToAddActivity.class);
        startActivity(intent);


        /**
         * searchclubbyhotcity  param :city
         * searchclubbyprovince param province
         * searchclubbynameorid param clubnameorid
         */






        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //获取热门城市列表
                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("userid",userId);
                    String funcName="";
                    switch (serachType){
                        case 1:
                            jsonObj.put("clubnameorid",condition);
                            funcName="searchclubbynameorid";
                            break;
                        case 2:
                            jsonObj.put("city",condition);
                            funcName="searchclubbyhotcity";
                            break;
                        case 3:
                            jsonObj.put("province",condition);
                            funcName="searchclubbyprovince";
                            break;

                    }
                    mClubInfoList=new ArrayList<ClubInfo>();

                    String strURL=getString(R.string.url_remote);
                    strURL+="func="+funcName+"&param="+jsonObj.toString();

                    URL url = new URL(strURL);

                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();
                    JSONArray jsonClubList=new JSONArray(result);
                    for(int i=0;i<jsonClubList.length();i++){
                        /*Int id
                         String clubname
                         String headpic
                         String clubprovince
                         String clubcity
                         String clubabstract
                         int maxpeople
                         int curpeople
                         int createuserid
                         string nickname //创建用户昵称
                         string headpic1 //创建用户头像
                         string useridclub //查询用户是否在此club中  true/false

*/
                        ClubInfo clubInfo=new ClubInfo();
                        JSONObject jsonObject=new JSONObject(jsonClubList.get(i).toString());
                        clubInfo.clubID=jsonObject.getInt("id");
                        clubInfo.clubName=jsonObject.getString("name");
                        clubInfo.clubHeadPic=jsonObject.getString("headpic");
                        clubInfo.maxCLubMemberNumber=jsonObject.getInt("maxpeople");
                        clubInfo.clubMemberNumber=jsonObject.getInt("curpeople");
                        clubInfo.createuserid=jsonObject.getInt("createuserid");
                        clubInfo.nickname=jsonObject.getString("nickname");
                        clubInfo.headpic1=jsonObject.getString("headpic");
                        clubInfo.location=jsonObject.getString("city");
                        //clubInfo.clubLevelName=jsonObject.getString("levelabstract");
                        if (jsonObject.getString("userinclub").equals("true"))
                        {
                            clubInfo.bInClub=true;
                        }else{
                            clubInfo.bInClub=false;
                        }
                        mClubInfoList.add(clubInfo);
                    }

                    //获取数据后，将俱乐部列表数据作为对象传入到下一activity，进行数据初始化




                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(AddIntoClubActivity.this,"从后台取数据报错，请稍后重试!");
                }

            }
        });
        thread.start();


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_exit:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
