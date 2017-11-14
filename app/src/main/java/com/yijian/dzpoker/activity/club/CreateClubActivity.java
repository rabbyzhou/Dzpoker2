package com.yijian.dzpoker.activity.club;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.FileHelper;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.CircleTransform;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateClubActivity extends AppCompatActivity implements View.OnClickListener {
    private DzApplication myApp;
    private TextView tv_created_number;
    private EditText et_clubname;
    private Button btnCreateClub;
    private LinearLayout layout_location;
    private EditText et_location;
    private int canCreateClubNum;
    private String  strLoginName;
    private int userId;
    private int createdclub=0;//已经创建数
    private int maxclub=0;//已经创建数
    private String province;
    private String city;
    private PopupWindow popupWindow;
    private LinearLayout layout_window;
    private ImageView iv_club_head;
    private Context mContext;

    public final static int REQUEST_IMAGE_CAPTURE = 1;
    public final static int REQUEST_IMAGE_CROP = 2;
    public final static int REQUEST_LOCATION_SELECT = 3;

    public  final static int MESAGE_LOAD_OK=0x1001;
    public  final static int MESAGE_CREATECLUB_OK=0x1002;
    // 图片路径
    private Uri mCurrentPhotoUri;//裁剪后的照片路径
    private Uri mCameraPhotoUri;//拍照后的路径
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == MESAGE_LOAD_OK) {
                tv_created_number.setText("已建立俱乐部数"+createdclub+"/"+maxclub);
            }
            if (msg.what == MESAGE_CREATECLUB_OK) {
                tv_created_number.setText("已建立俱乐部数"+createdclub+"/"+maxclub);
                ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"创建俱乐部成功!");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        setContentView(R.layout.activity_create_club);
        initViews();
        SharedPreferences settings = getSharedPreferences("depoker", 0);
        strLoginName=settings.getString("username","");
        userId=settings.getInt("userid",0);


        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{

                    //拼装url字符串
                    JSONObject jsonObj = new JSONObject();
                    //jsonObj.put("loginname",strLoginName);
                    jsonObj.put("userid",userId);

                    String strURL=getString(R.string.url_remote);
                    strURL+="func=cancreateclub&param="+jsonObj.toString();

                    URL url = new URL(strURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();// 利用HttpURLConnection对象,我们可以从网络中获取网页数据.
                    conn.setConnectTimeout(5 * 1000);   // 单位是毫秒，设置超时时间为5秒
                    conn.setRequestMethod("GET");       // HttpURLConnection是通过HTTP协议请求path路径的，所以需要设置请求方式,可以不设置，因为默认为GET
                    if (conn.getResponseCode() == 200) {// 判断请求码是否是200码，否则失败
                        InputStream is = conn.getInputStream(); // 获取输入流
                        byte[] data = Util.readStream(is);   // 把输入流转换成字符数组
                        String result = new String(data);        // 把字符数组转换成字符串

                        if (result.equals("")){
                            ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"从后台取用户可创建俱乐部数报错，请稍后重试!");
                            return;
                        }
                        JSONObject jsonUser=new JSONObject(result);
                        //Int createdclub: 已经创建数
                       //Int maxclub: 可创建数

                        createdclub=jsonUser.getInt("createdclub");
                        maxclub=jsonUser.getInt("maxclub");
                        handler.sendEmptyMessage(MESAGE_LOAD_OK);


                    }

                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"从后台取用户可创建俱乐部数报错，请稍后重试!");
                }

            }
        });
        thread.start();
        //调用请求数据，

    }

    private void initViews(){

        Intent intent = getIntent();
        String backText = intent.getStringExtra(Constant.INTENT_KEY_BACKTEXT);
        TextView exitText = (TextView)findViewById(R.id.tv_exit);
        if ( backText != null && !backText.isEmpty()){
            exitText.setText(backText);
        }

        tv_created_number=(TextView)findViewById(R.id.tv_created_number);
        et_clubname=(EditText)findViewById(R.id.et_clubname);
        btnCreateClub=(Button)findViewById(R.id.btnCreateClub);
        btnCreateClub.setOnClickListener(this);
//        layout_location=(LinearLayout)findViewById(R.id.layout_location);
//        layout_location.setOnClickListener(this);
        et_location=(EditText)findViewById(R.id.et_location);
        et_location.setOnClickListener(this);
        layout_window=(LinearLayout)findViewById(R.id.layout_window);
        iv_club_head=(ImageView)findViewById(R.id.iv_club_head);
        iv_club_head.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateClub:
                if(createdclub>maxclub){
                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"俱乐部创建数目超过允许创建的数目!");
                    //这里该跳出用户等级购买界面
                    return;
                }

                if (et_clubname.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"俱乐部名称不为空!");

                    return;
                }
                if (et_clubname.getText().toString().length()>20)
                {
                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"俱乐部名称长度限制在20以内!");

                    return;
                }
                if (et_location.getText().toString().equals(""))
                {
                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"请选择俱乐部所在地！");
                    return;
                }

                //首先上传头像，利用okhttp
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try{

                            //此处，先要到极光IM创建群组，然后创建俱乐部，将群组ID写入俱乐部信息中


                            String head_path="";
                            if (mCurrentPhotoUri!=null) {
                                String ss = mCurrentPhotoUri.toString();
                                File file = new File(URI.create(mCurrentPhotoUri.toString()));

                                //File file=new File("http://seopic.699pic.com/photo/50054/0855.jpg_wh1200.jpg");
                                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.jpg\""), RequestBody.create(MediaType.parse("image/png"), file));
                                RequestBody body = builder.build();

                                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);

                                String file1Name = mCurrentPhotoUri.getPath();
                                String fileName = file.getName();
                                /* form的分割线,自己定义 */
                                String boundary = "xx--------------------------------------------------------------xx";
                                MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
                                /* 底下上传文件 */
                                        .addFormDataPart("file", fileName, fileBody)
                                        .build();

                             /* 下边的就和post一样了 */
                                Request request = new Request.Builder().url(getString(R.string.url_upload)).post(body).build();
                                //同步取数据

                                Response response = DzApplication.getHttpClient().newCall(request).execute();
                                if (!response.isSuccessful()) {
                                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this, "上传头像失败，请稍后重试!错误原因为：" + response.body().string());
                                    return;
                                }
                                String result=response.body().string();
                                JSONObject json = new JSONObject(result);
                                int ret = json.getInt("ret");
                                if (ret != 0) {
                                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this, "上传头像失败，请稍后重试!");
                                    return;
                                }
                                head_path=json.getString("path");
                            }
                            //头像校验完毕，开始创建俱乐部

                            //拼装url字符串
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("userid",userId);
                            jsonObj.put("clubname",et_clubname.getText().toString());
                            jsonObj.put("province",province);
                            jsonObj.put("city",city);
                            jsonObj.put("headpic",head_path);

                            String strURL=getString(R.string.url_remote);
                            strURL+="func=createclub&param="+jsonObj.toString();

                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();
                            JSONObject jsonResult=new JSONObject(result);
                            if (jsonResult.getInt("ret")==0){
                                     //创建成功之后，需要到IM创建群组，然后修改俱乐部信息
                                final int clubId=jsonResult.getInt("clubid");
                                JMessageClient.createGroup(et_clubname.getText().toString(), "", new CreateGroupCallback() {
                                    @Override
                                    public void gotResult(int i, String s, long l) {
                                        if (i==0){

                                                final long ClubGroupID = l;
                                                Thread thread=new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {

                                                            JSONObject jsonObj = new JSONObject();
                                                            jsonObj.put("userid", userId);
                                                            jsonObj.put("clubid", clubId);
                                                            jsonObj.put("imgroupid", ClubGroupID);

                                                            String strURL = getString(R.string.url_remote);
                                                            strURL += "func=modifyclubimgroupid&param=" + jsonObj.toString();

                                                            Request request = new Request.Builder().url(strURL).build();
                                                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                                                            String result = response.body().string();
                                                            JSONObject jsonResult = new JSONObject(result);
                                                            if (jsonResult.getInt("ret") == 0) {
                                                                //这里要传消息的
                                                                createdclub += 1;
                                                                handler.sendEmptyMessage(MESAGE_CREATECLUB_OK);

                                                            } else {
                                                                ToastUtil.showToastInScreenCenter(CreateClubActivity.this, "创建俱乐部失败，错误内容：" + jsonResult.getString("msg"));
                                                            }


                                                        } catch (Exception e) {
                                                            ToastUtil.showToastInScreenCenter(CreateClubActivity.this, "创建俱乐部失败，错误内容：" + e.toString());

                                                        }
                                                    }
                                                });
                                                thread.start();
                                        }else{
                                            ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"创建俱乐部失败，错误内容："+s);
                                        }
                                    }
                                });



//                                user.userId+"", getString(R.string.unite_password), new BasicCallback() {
//                                    @Override
//                                    public void gotResult(int i, String s) {
//                                        if (i==0){
//                                            ((DzApplication)getApplication()).setUser(user);
//                                            //存储登录成功的用户名到本地文件
//                                            SharedPreferences settings = getSharedPreferences("depoker", 0);
//                                            SharedPreferences.Editor editor = settings.edit();
//                                            editor.putString("username",user.userLoginName);
//                                            editor.putInt("userid", user.userId);
//                                            DzApplication myApp=(DzApplication)getApplication();
//                                            myApp.setLoginName(user.userLoginName);
//                                            myApp.setUserId(user.userId);
//                                            // Commit the edits!
//                                            editor.commit();
//                                            //跳转到主界面
//                                            Intent intent = new Intent();
//                                            intent.setClass(LoginActivity.this, MainActivity.class);
//                                            startActivity(intent);
//                                            ToastUtil.showToastInScreenCenter(LoginActivity.this,"注册成功！");
//                                            finish();
//                                        }else{
//                                            ToastUtil.showToastInScreenCenter(LoginActivity.this,"注册到IM失败，错误内容："+s);
//                                        }
//                                    }



                                    return;
                                }
                            if (jsonResult.getInt("ret")==1){
                                ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"俱乐部部名已存在!");
                                return;
                            }
                            if (jsonResult.getInt("ret")==2){
                                ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"创建俱乐部失败,错误原因："+jsonResult.getString("msg"));
                                return;
                            }



                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"创建俱乐部失败，请稍后重试!");
                        }

                    }
                });
                thread.start();


                    /*
                    //异步调用代码
                    DzApplication.getHttpClient().newCall(request).enqueue(new Callback() {
                        public void onResponse(Call call, Response response) throws IOException {
                            final  String bodyStr = response.body().string();
                            final boolean ok = response.isSuccessful();
                            runOnUiThread(new Runnable() {
                                public void run() {



                                }
                            });
                        }
                        public void onFailure(Call call, final IOException e) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String ss=e.toString();
                                    ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"上传头像失败，请稍后重试!错误原因为："+e.toString());
                                }
                            });
                        }
                    });
                }*/
                break;
            case R.id.et_location:
                //选择行政区划
                Intent intent = new Intent();
//                        intent.putExtra("opType",1 );
//                        intent.putExtra("phonenumber", edUserName.getText().toString());
                intent.setClass(CreateClubActivity.this, SelectProvinceActivity.class);
                startActivityForResult(intent,  REQUEST_LOCATION_SELECT);

                break;
            case R.id.iv_club_head:
                showWindow(layout_window);
                break;
            case R.id.tv_exit:
                setResult(1);
                finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_LOCATION_SELECT && resultCode==1 ) {
            //刷新界面的数据
            province=data.getExtras().getString("province");
            city=data.getExtras().getString("city");
            et_location.setText(data.getExtras().getString("location"));

        }else  if(requestCode==Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            //选择照片返回
            // 裁剪
            beginCrop(data.getData());
        } else if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //照相机返回，调用裁剪
            beginCrop(mCameraPhotoUri);
        } else if(requestCode==Crop.REQUEST_CROP) {
            //裁剪返回，更新界面
            //iv_club_head.setImageURI( Uri.fromFile(new File(getCacheDir(), "cropped")))
            if (resultCode == Crop.RESULT_ERROR){
                ToastUtil.showToastInScreenCenter(CreateClubActivity.this,"选择的图片不符合标准，建议使用照相机进行拍摄！");
            }else {
                Picasso.with(CreateClubActivity.this)
                        .load(mCurrentPhotoUri)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .resize(300, 300)
                        .error(R.drawable.default_club_head)
                        .transform(new CircleTransform())
                        .into(iv_club_head);
            }
        }
    }

    private void beginCrop(Uri source) {
       mCurrentPhotoUri = Uri.fromFile(FileHelper.getTempFile(mContext,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg"));
       Crop.of(source, mCurrentPhotoUri).asSquare().start(this);
    }



    private void showWindow(View parent) {

        View contentView = getPopupWindowContentView();
//        popupWindow = new PopupWindow(contentView,getResources().getDisplayMetrics().widthPixels-160,240
//                , true);
        popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.WRAP_CONTENT ,ViewGroup.LayoutParams.WRAP_CONTENT
                , true);
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show

        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int xOffset = parent.getWidth()  - contentView.getMeasuredWidth() ;
        //popupWindow.showAsDropDown(parent,xOffset,20);    // 在mButton2的中间显示
        popupWindow.showAtLocation(parent, Gravity.CENTER,0,0);
    }

    private View getPopupWindowContentView() {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.popupwindow_selectphoto;   // 布局ID
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);
        View.OnClickListener menuItemOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_camera:
                        //拍照,这里传照片路径过去,保存为临时文件
                        File file = Util.createImageFile();
                        mCameraPhotoUri = Uri.fromFile(file);
                        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraPhotoUri);
                        startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
                        break;
                    case R.id.tv_map:
                        //直接图库选择，进行裁剪
                        Crop.pickImage(CreateClubActivity.this);
                        break;

                }

                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        };
        contentView.findViewById(R.id.tv_camera).setOnClickListener(menuItemOnClickListener);
        contentView.findViewById(R.id.tv_map).setOnClickListener(menuItemOnClickListener);
        return contentView;
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
