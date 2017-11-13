package com.yijian.dzpoker.view.data;


import com.yijian.dzpoker.activity.user.MenuItemOnClickListener;

public class PayMenuItem {
    public PayMenuItem() {

    }

    /**
     *
     * @param _item_name 菜单项名称
     * @param _text 菜单项显示内容
     * @param _style 菜单类型
     * @param _menuItemOnClickListener 菜单点击回调事件
     */
    public PayMenuItem(String _item_name, String _text, MenuItemStyle _style, MenuItemOnClickListener _menuItemOnClickListener){
        this.item_name = _item_name;
        this.text = _text;
        this.style = _style;
        this.menuItemOnClickListener = _menuItemOnClickListener;
    }


    private String item_name;
    private String text;
    private MenuItemStyle style;
    private int payType;

    public String getItem_name() {
        return item_name;
    }

    public String getText() {
        return text;
    }

    public MenuItemStyle getStyle() {
        return style;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPayType(){
        return  payType;
    }
    /**
     *
     * @param style 菜单类型
     */
    public void setStyle(MenuItemStyle style) {
        this.style = style;
    }

    public MenuItemOnClickListener getMenuItemOnClickListener() {
        return menuItemOnClickListener;
    }

    public void setMenuItemOnClickListener(MenuItemOnClickListener menuItemOnClickListener) {
        this.menuItemOnClickListener = menuItemOnClickListener;
    }

    private MenuItemOnClickListener menuItemOnClickListener;


    public  enum MenuItemStyle{
        COMMON , STRESS
    }

}
