package com.yijian.dzpoker.activity.club;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
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
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.activity.user.BuyDiamondActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.FileHelper;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.ClubInfo;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.yijian.dzpoker.constant.Constant.INTENT_KEY_BACKTEXT;

public class ClubInfoManageActivity extends BaseBackActivity {
    private EditText et_club_name,et_club_location,et_club_abstract;
    private TextView tv_user_diamonds;
    private ImageView iv_club_head;
    private Button btn_save;
    private ClubInfo mClubInfo=new ClubInfo();
    public final static int REQUEST_IMAGE_CAPTURE = 1;//调用照相机
    public final static int REQUEST_IMAGE_CROP = 2;
    public final static int REQUEST_LOCATION_SELECT = 3;
    public final static int REQUEST_BUY_DIAMONDS = 4;
    private LinearLayout layout_window;
    private PopupWindow popupWindow;
    private Uri mCurrentPhotoUri;//裁剪后的照片路径
    private Uri mCameraPhotoUri;//拍照后的路径
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private String mProvince,mCity,mLocation;
    private Context mContext;
    private String  mOldclubName;
    private User mUser;
    private static  final  int  CONST_RENAME_CLUB_PAY=40;
    private boolean bModifyName=false;

    private final int MESAGE_MODIFY_OK=0x1001;



    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESAGE_MODIFY_OK:
                    //修改，钻石
                    mOldclubName=mClubInfo.clubName;
                    if(bModifyName) {
                        application.setUser(mUser);
                        tv_user_diamonds.setText(mUser.diamond + "");
                        if (mUser.diamond < CONST_RENAME_CLUB_PAY) {
                            tv_user_diamonds.setTextColor(Color.RED);
                        }
                    }
                    break;

            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getApplicationContext();
        Intent intent=getIntent();
        mClubInfo=(ClubInfo) intent.getSerializableExtra("clubinfo");
        if (mClubInfo.clubabstract.equals("null")){
            mClubInfo.clubabstract="";
        }
        //这里记录原有俱乐部名字，在修改的时候如果名字发生变化，需要调用扣除钻石的接口
        mOldclubName=mClubInfo.clubName;
        mProvince=mClubInfo.province;
        mCity=mClubInfo.city;
        initData();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_club_info_manage;
    }

    private void initData(){
        et_club_name.setText(mClubInfo.clubName);
        et_club_abstract.setText(mClubInfo.clubabstract);
        et_club_location.setText(mClubInfo.location);
        if (mClubInfo.clubHeadPic!=null && !mClubInfo.clubHeadPic.equals("") ){
            Picasso.with(getApplicationContext())
                    .load(mClubInfo.clubHeadPic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(200, 200)
                    .error(R.drawable.default_club_head)
                    .transform(new CircleTransform())
                    .into(iv_club_head);
        }
        mUser=application.getUser();
        tv_user_diamonds.setText(mUser.diamond+"");
        if (mUser.diamond<CONST_RENAME_CLUB_PAY){
            tv_user_diamonds.setTextColor(Color.RED);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        tv_back.setOnClickListener(this);
        et_club_name=(EditText)findViewById(R.id.et_club_name);
        et_club_location=(EditText)findViewById(R.id.et_club_location);
        et_club_abstract=(EditText)findViewById(R.id.et_club_abstract);
        btn_save=(Button) findViewById(R.id.btn_save);
        iv_club_head=(ImageView)findViewById(R.id.iv_club_head);
        iv_club_head.setOnClickListener(this);
        et_club_location.setOnClickListener(this);
        layout_window=(LinearLayout)findViewById(R.id.layout_window);
        tv_user_diamonds=(TextView)findViewById(R.id.tv_user_diamonds);
        btn_save=(Button)findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.et_club_location:
                //选择行政区划
                Intent intent = new Intent();
//                        intent.putExtra("opType",1 );
//                        intent.putExtra("phonenumber", edUserName.getText().toString());
                intent.setClass(ClubInfoManageActivity.this, SelectProvinceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(INTENT_KEY_BACKTEXT, tv_title.getText());
                startActivityForResult(intent,  REQUEST_LOCATION_SELECT);
                break;
            case R.id.iv_club_head:
                showWindow(layout_window);
                break;
            case R.id.btn_save:
                //这里要判断是否改俱乐部名了，如果改了，就需要调用使用钻石接口
                if (et_club_name.getText().toString().equals("")){
                    ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"俱乐部名不为空");
                    return;
                }
                bModifyName=false;
                if (et_club_name.getText().toString().trim().equals(mOldclubName.trim())){
                    //名称未变化，说明只是修改别的信息
                    Thread thread=new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            modifyClubInfo();
                        }
                    });
                    thread.start();

                }else{
                    bModifyName=true;
                    //判断用户钻石数是否够，不够的话，买钻石
                    if (mUser.diamond<CONST_RENAME_CLUB_PAY){
                        Intent intentBuyDiamons=new Intent();
                        intentBuyDiamons.setClass(ClubInfoManageActivity.this, BuyDiamondActivity.class);
                        startActivityForResult(intentBuyDiamons,  REQUEST_BUY_DIAMONDS);

                    }else{
                        //先调用扣除钻石，然后，修改信息
                        Thread thread=new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                try{
                                    JSONObject jsonParam= new JSONObject();
                                    jsonParam.put("userid",application.getUserId());
                                    jsonParam.put("usediamond",CONST_RENAME_CLUB_PAY);
                                    jsonParam.put("tradetip","花费"+CONST_RENAME_CLUB_PAY+"进行俱乐部改名");



                                    /*public class UseDiamondParam
                                        {
                                            public int userid;
                                            public int usediamond;
                                            public string tradetip;
                                        }
                                    */
                                    String strURL=getString(R.string.url_remote)+"func=usediamond&param="+jsonParam.toString();
                                    URL url = new URL(strURL);
                                    Request request = new Request.Builder().url(strURL).build();
                                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                                    String result=response.body().string();

                                    JSONObject jsonResult=new JSONObject(result);
                                    if (jsonResult.getInt("ret")==0){
                                        mUser.diamond=mUser.diamond-CONST_RENAME_CLUB_PAY;
                                        modifyClubInfo();
                                    }else{
                                        ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"修改俱乐部信息出错，错误内容:"+jsonResult.getString("msg"));
                                    }



                                }catch (Exception e){
                                    ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"修改俱乐部信息出错，请稍后重试!");
                                }
                            }
                        });
                        thread.start();
                    }
                }
                break;
        }

    }

    private void modifyClubInfo(){

                try{
                    String head_path=mClubInfo.clubHeadPic;
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
                            ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this, "上传头像失败，请稍后重试!错误原因为：" + response.body().string());
                            return;
                        }
                        String result=response.body().string();
                        JSONObject json = new JSONObject(result);
                        int ret = json.getInt("ret");
                        if (ret != 0) {
                            ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this, "上传头像失败，请稍后重试!");
                            return;
                        }
                        head_path=json.getString("path");
                    }

                    JSONObject jsonParam= new JSONObject();
                    jsonParam.put("userid",application.getUserId());
                    jsonParam.put("clubid",mClubInfo.clubID);
                    jsonParam.put("name",et_club_name.getText().toString());
                    jsonParam.put("headpic",head_path);
                    jsonParam.put("clubabstract",et_club_abstract.getText().toString());
                    jsonParam.put("province",mProvince);
                    jsonParam.put("city",mCity);

                    /*public class ModifyClubInfoParam
                    {
                        public int userid;
                        public int clubid;
                        public string name;
                        public string headpic;
                        public string clubabstract;
                    }

                    */
                    String strURL=getString(R.string.url_remote)+"func=modifyclubinfo&param="+jsonParam.toString();
                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();

                    JSONObject jsonResult=new JSONObject(result);
                    if (jsonResult.getInt("ret")!=0){
                        ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"修改俱乐部信息出错，错误内容:"+jsonResult.getString("msg"));
                    }else{
                        handler.sendEmptyMessage(MESAGE_MODIFY_OK);
                        ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"修改俱乐部信息成功");
                    }
                    //发送修改成功，主要是扣除钻石，展现钻石数



                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"修改俱乐部信息出错，请稍后重试!");
                }

    }

    private void showWindow(View parent) {

        View contentView = getPopupWindowContentView();
//        popupWindow = new PopupWindow(contentView,getResources().getDisplayMetrics().widthPixels-160,240
//                , true);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT ,ViewGroup.LayoutParams.WRAP_CONTENT
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
                        Crop.pickImage(ClubInfoManageActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_LOCATION_SELECT && resultCode==1) {
            //刷新界面的数据
            mProvince=data.getExtras().getString("province");
            mCity=data.getExtras().getString("city");
            et_club_location.setText(data.getExtras().getString("location"));

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
                ToastUtil.showToastInScreenCenter(ClubInfoManageActivity.this,"选择的图片不符合标准，建议使用照相机进行拍摄！");
            }else {
                Picasso.with(ClubInfoManageActivity.this)
                        .load(mCurrentPhotoUri)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .resize(200, 200)
                        .error(R.drawable.default_club_head)
                        .transform(new CircleTransform())
                        .into(iv_club_head);
            }
        }else if (requestCode==REQUEST_BUY_DIAMONDS){
            //从购买钻石界面回来，更新界面上的钻石数目
            mUser=application.getUser();
            tv_user_diamonds.setText(mUser.diamond+"");
            if (mUser.diamond<CONST_RENAME_CLUB_PAY){
                tv_user_diamonds.setTextColor(Color.RED);
            }else{
                tv_user_diamonds.setTextColor(Color.GREEN);
            }

        }
    }

    private void beginCrop(Uri source) {
        mCurrentPhotoUri = Uri.fromFile(FileHelper.getTempFile(mContext,new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg"));
        Crop.of(source, mCurrentPhotoUri).asSquare().start(this);
    }
}
