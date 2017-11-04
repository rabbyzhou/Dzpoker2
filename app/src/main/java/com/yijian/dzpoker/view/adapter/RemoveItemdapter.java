package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.ui.RemoverViewHolder;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.data.User;

import java.util.ArrayList;

public class RemoveItemdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<User> mUserList;

    public RemoveItemdapter(Context context, ArrayList<User> list) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUserList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RemoverViewHolder(mInflater.inflate(R.layout.item_club_user_manage, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final RemoverViewHolder viewHolder = (RemoverViewHolder) holder;
        final User user=mUserList.get(position);
        viewHolder.mUserName.setText(user.nickName);
        if (user.userHeadPic!=null && !user.userHeadPic.equals("null") &&  !user.userHeadPic.equals("")) {
            Picasso.with(mContext)
                    .load(user.userHeadPic)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize(100, 100)
                    .error(R.drawable.default_club_head)
                    .transform(new CircleTransform())
                    .into(viewHolder.mUserHeadPic);
        }
    }

    @Override
    public int getItemCount() {
        return mUserList != null ? mUserList.size() : 0;
    }

    public void removeItem(int position) {
        mUserList.remove(position);
        notifyDataSetChanged();
    }
}
