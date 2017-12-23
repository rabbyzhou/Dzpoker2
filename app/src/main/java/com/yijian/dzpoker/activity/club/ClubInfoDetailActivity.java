package com.yijian.dzpoker.activity.club;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.baselib.widget.CustomCircleImageView;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.ClubInfo;

/**
 * Created by QIPU on 2017/12/23.
 */

public class ClubInfoDetailActivity extends BaseBackActivity {

    private static final String TAG = "ClubInfoDetailActivity";
    private CustomCircleImageView clubIcon;
    private TextView clubName;
    private TextView clubIdAndLoc;
    private CustomCircleImageView masterIcon;
    private TextView masterName;
    private TextView clubLevel;
    private ImageView clubLevelNarrow;
    private RecyclerView membersRecyclerView;
    private TextView clubDesc;
    private TextView members;

    @Override
    protected int getLayoutId() {
        return R.layout.club_info_page;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        setClubInfo();
    }

    private void initToolbar() {
        setToolbarTitle("俱乐部管理");
        showToolbarRightTextView("解散", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:release club
            }
        });
    }

    @Override
    protected void initViews() {
        clubIcon = (CustomCircleImageView) findViewById(R.id.club_info_page_icon);
        clubIcon.setOnClickListener(this);
        clubName = (TextView) findViewById(R.id.club_info_page_club_name);
        clubIdAndLoc = (TextView) findViewById(R.id.club_info_page_id_and_loc);
        masterIcon = (CustomCircleImageView) findViewById(R.id.club_info_page_master_icon);
        masterIcon.setOnClickListener(this);
        masterName = (TextView) findViewById(R.id.club_info_page_master_name);
        clubLevel = (TextView) findViewById(R.id.club_info_page_club_level_tv);
        clubLevelNarrow = (ImageView) findViewById(R.id.club_info_page_club_level_narrow);
        clubLevelNarrow.setOnClickListener(this);
        membersRecyclerView = (RecyclerView) findViewById(R.id.club_info_page_club_mem_list);
        clubDesc = (TextView) findViewById(R.id.club_info_page_club_desc);
        members = (TextView) findViewById(R.id.club_info_page_club_mem);
    }

    private void setClubInfo() {
        ClubInfo clubInfo = application.getClubInfo();
        if (null == clubInfo) {
            return;
        }
        if (!TextUtils.isEmpty(clubInfo.clubHeadPic)) {
            Picasso.with(this)
                    .load(clubInfo.clubHeadPic)
                    .placeholder(R.drawable.default_club_head)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_club_head)
                    .transform(new CircleTransform())
                    .into(clubIcon);
        }
        clubName.setText(clubInfo.clubName);
        clubIdAndLoc.setText(clubInfo.clubID + " " + clubInfo.province + " " + clubInfo.city);
        if (!TextUtils.isEmpty(clubInfo.headpic1)) {
            Picasso.with(this)
                    .load(clubInfo.headpic1)
                    .placeholder(R.drawable.default_male_head)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_male_head)
                    .transform(new CircleTransform())
                    .into(masterIcon);
        }
        masterName.setText(clubInfo.createuserid + "");
        clubLevel.setText(clubInfo.clubLevelName);
        clubDesc.setText(clubInfo.clubCreatetime);
        members.setText(clubInfo.clubMemberNumber + "/" + clubInfo.maxCLubMemberNumber);
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        switch (resId) {
            case R.id.club_info_page_icon:
                break;
            case R.id.club_info_page_master_icon:
                break;
            case R.id.club_info_page_club_level_narrow:
                break;
            default:
                break;
        }
    }
}
