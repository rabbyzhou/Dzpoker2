package com.yijian.dzpoker.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.user.MenuItemOnClickListener;
import com.yijian.dzpoker.view.data.PayMenuItem;

import java.util.List;


public class MenuItemAdapter extends BaseAdapter{


    private Context context;//运行上下文

    private LayoutInflater listContainer;  //视图容器

    private List<PayMenuItem> menuItems;

    public MenuItemAdapter(Context _context, List<PayMenuItem> _menuItems){
        this.context = _context;
        this.listContainer = LayoutInflater.from(_context);
        this.menuItems = _menuItems;
    }
    @Override
    public int getCount() {
        return this.menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        if(position >= menuItems.size() || position < 0) {
            return null;
        } else {
            return menuItems.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if(convertView == null) {
            view = listContainer.inflate(R.layout.menu_item, null);
        }

        PayMenuItem menuItem = menuItems.get(position);

        TextView textView = (TextView) view.findViewById(R.id.menu_item);
        TextView wayText = (TextView) view.findViewById(R.id.item_way);

        wayText.setVisibility(View.INVISIBLE);
        wayText.setTextSize(0);

        textView.setText(menuItem.getText());
        if(menuItems.size() == 1) {
            view.setBackgroundResource(R.drawable.bottom_menu_btn_selector);
        } else if(position == 0) {
            view.setBackgroundResource(R.drawable.bottom_menu_top_btn_selector);
            wayText.setVisibility(View.VISIBLE);
            wayText.setTextSize(10);

        } else if(position < menuItems.size() - 1) {
            view.setBackgroundResource(R.drawable.bottom_menu_mid_btn_selector);
        } else {
            view.setBackgroundResource(R.drawable.bottom_menu_bottom_btn_selector);
        }
        if(menuItem.getStyle() == PayMenuItem.MenuItemStyle.COMMON) {
            textView.setTextColor(ContextCompat.getColor(context, R.color.bottom_menu_btn_text_commom_color));
        } else {
            textView.setTextColor(ContextCompat.getColor(context, R.color.bottom_menu_btn_text_stress_color));
        }
        MenuItemOnClickListener _menuItemOnClickListener =menuItem.getMenuItemOnClickListener();
        if(_menuItemOnClickListener != null) {
            view.setOnClickListener(_menuItemOnClickListener);
        }
        return view;
    }
}
