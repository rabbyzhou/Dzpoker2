package com.yijian.dzpoker.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.yijian.dzpoker.activity.club.ClubInfoActivity;
import com.yijian.dzpoker.activity.club.ClubInfoManageActivity;
import com.yijian.dzpoker.activity.club.SelectProvinceActivity;
import com.yijian.dzpoker.ui.SwitchButton;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.FileHelper;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.CircleTransform;

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

public class ModifyUserInfoActivity extends BaseBackActivity {
    private ImageView iv_user_head;
    private EditText et_user_nickname,et_user_account,et_user_location,et_user_personaltip;
    private SwitchButton sb_sex;
    private TextView tv_user_level;
    private Button btn_save;
    private String mProvince,mCity,mLocation;
    private LinearLayout layout_window;//弹出窗体需要一个父窗体做基准
    private PopupWindow popupWindow;
    private Uri mCurrentPhotoUri;//裁剪后的照片路径
    private Uri mCameraPhotoUri;//拍照后的路径


    public final static int REQUEST_IMAGE_CAPTURE = 1;//调用照相机
    public final static int REQUEST_LOCATION_SELECT = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProvince=application.getUser().province;
        mCity=application.getUser().city;
        //初始化界面的值
        et_user_nickname.setText(application.getUser().nickName);
        et_user_account.setText(application.getUser().userLoginName);

        //此处数据库默认性别为男
        if (application.getUser().sex==null){
            sb_sex.setOn(true);
        }else{
            if (application.getUser().sex.equals("女"))
            {
                sb_sex.setOn(false);
            }else{
                sb_sex.setOn(true);
            }
        }
        tv_user_level.setText(application.getUser().levelname);
        et_user_personaltip.setText(application.getUser().personalTip);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_user_info;
    }




    @Override
    protected void initViews() {
        super.initViews();
        iv_user_head=(ImageView)findViewById(R.id.iv_user_head);
        et_user_nickname=(EditText)findViewById(R.id.et_user_nickname);
        et_user_account=(EditText)findViewById(R.id.et_user_account);
        et_user_location=(EditText)findViewById(R.id.et_user_location);
        et_user_personaltip=(EditText)findViewById(R.id.et_user_personaltip);
        tv_user_level=(TextView)findViewById(R.id.tv_user_level);
        btn_save=(Button)findViewById(R.id.btn_save);
        layout_window=(LinearLayout)findViewById(R.id.layout_window);
        sb_sex=(SwitchButton)findViewById(R.id.sb_sex) ;
        sb_sex.setImage(R.drawable.switch_bg2);

        btn_save.setOnClickListener(this);
        et_user_location.setOnClickListener(this);
        iv_user_head.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_save:
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        modifyUserInfo();
                    }
                });
                thread.start();

                break;
            case R.id.et_user_location: //设置location
                Intent intent = new Intent();
                intent.putExtra("opType",1 );
//                        intent.putExtra("phonenumber", edUserName.getText().toString());
                intent.setClass(ModifyUserInfoActivity.this, SelectProvinceActivity.class);
                startActivityForResult(intent,  REQUEST_LOCATION_SELECT);
                break;
            case R.id.iv_user_head://替换头像
                showWindow(layout_window);
                break;
        }

    }

    private void modifyUserInfo(){

        try{
            String head_path=application.getUser().userHeadPic;
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
                    ToastUtil.showToastInScreenCenter(ModifyUserInfoActivity.this, "上传头像失败，请稍后重试!错误原因为：" + response.body().string());
                    return;
                }
                String result=response.body().string();
                JSONObject json = new JSONObject(result);
                int ret = json.getInt("ret");
                if (ret != 0) {
                    ToastUtil.showToastInScreenCenter(ModifyUserInfoActivity.this, "上传头像失败，请稍后重试!");
                    return;
                }
                head_path=json.getString("path");
            }

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("userid",application.getUserId());
            jsonParam.put("nickname",et_user_nickname.getText().toString());
            if (sb_sex.isOn()) {
                jsonParam.put("sex","男");
            }else{
                jsonParam.put("sex","女");
            }
            jsonParam.put("headpic",head_path);
            jsonParam.put("personaltip",et_user_personaltip.getText().toString());
            jsonParam.put("province",mProvince);
            jsonParam.put("city",mCity);

                    /*Int userid;
                        String nickname
                        String sex
                        String province
                        String city
                        String personaltip
                        String headpic
                    */
            String strURL=getString(R.string.url_remote)+"func=modifyuserinfo&param="+jsonParam.toString();
            URL url = new URL(strURL);
            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();
            String result=response.body().string();

            JSONObject jsonResult=new JSONObject(result);
            if (jsonResult.getInt("ret")!=0){
                ToastUtil.showToastInScreenCenter(ModifyUserInfoActivity.this,"修改用户信息出错，错误内容:"+jsonResult.getString("msg"));
            }else{
               ToastUtil.showToastInScreenCenter(ModifyUserInfoActivity.this,"修改用户信息成功");
            }
            //发送修改成功，主要是扣除钻石，展现钻石数



        }catch (Exception e){
            ToastUtil.showToastInScreenCenter(ModifyUserInfoActivity.this,"修改用户信息出错，请稍后重试!");
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
                        Crop.pickImage(ModifyUserInfoActivity.this);
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
            et_user_location.setText(data.getExtras().getString("location"));

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
                ToastUtil.showToastInScreenCenter(ModifyUserInfoActivity.this,"选择的图片不符合标准，建议使用照相机进行拍摄！");
            }else {
                Picasso.with(ModifyUserInfoActivity.this)
                        .load(mCurrentPhotoUri)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .resize(200, 200)
                        .error(R.drawable.default_male_head)
                        .transform(new CircleTransform())
                        .into(iv_user_head);
            }
        }
    }

    private void beginCrop(Uri source) {
        mCurrentPhotoUri = Uri.fromFile(FileHelper.getTempFile(getApplicationContext(),new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg"));
        Crop.of(source, mCurrentPhotoUri).asSquare().start(this);
    }
}
