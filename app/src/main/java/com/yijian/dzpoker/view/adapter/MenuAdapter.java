package com.yijian.dzpoker.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.view.data.MenuItemData;

/**
 * Created by koyabr on 10/22/15.
 */
public class MenuAdapter extends BaseListAdapter<MenuItemData, MenuAdapter.ViewHolder> {

    private OnRecordSelectListener mListener;


    public MenuAdapter(Context context, OnRecordSelectListener listener) {
        super(context);
        mListener = listener;
    }

    //接口供外部调用
    public interface OnRecordSelectListener {
        void onRecordSelected(MenuItemData menuItemData);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);

        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MenuItemData menuItemData = mData.get(position);
        if (menuItemData == null) return;

        /**
         Picasso.with(mContext)
         .load(menuItemData.menuImageId)
         .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
         .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
         .resize(100, 100)
         .error(R.drawable.default_club_level)
         .into(holder.mMenuPic);**/
        holder.mMenuPic.setImageDrawable(mContext.getResources().getDrawable(menuItemData.menuImageId));
        holder.mMenuname.setText(menuItemData.menuText);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mListener.onRecordSelected(menuItemData);
            }
        });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView mMenuname;
        public ImageView mMenuPic;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mMenuPic = (ImageView) itemView.findViewById(R.id.iv_menu_pic);
            mMenuname = (TextView) itemView.findViewById(R.id.tv_menu_name);

        }
    }

}