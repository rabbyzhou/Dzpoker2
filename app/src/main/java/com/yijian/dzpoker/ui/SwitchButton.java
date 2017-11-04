package com.yijian.dzpoker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yijian.dzpoker.R;


public class SwitchButton extends LinearLayout {

	/**
	 * 开关图片
	 */
	private LinearLayout switchParent;
	/**
	 * 滑块图片
	 */
	private ImageView switchButton;
	/**
	 * 按钮状态，默认关闭
	 */
	private boolean isOn = false;
	/**
	 * 滑块需要滑动的距离
	 */
	private int scrollDistance;
	/**
	 * 开关按钮监听器
	 */
	private SwitchChangedListner listner;

	public SwitchButton(Context context) {
		super(context);
		initWedgits(context);
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWedgits(context);
	}

	/**
	 * 初始化组件
	 * 
	 * @param context
	 *            上下文环境
	 */
	private void initWedgits(Context context) {
		try {
			View view = LayoutInflater.from(context).inflate(
					R.layout.switch_button, this);
			switchParent = (LinearLayout) view.findViewById(R.id.switch_parent);
			switchButton = (ImageView) view.findViewById(R.id.switch_button);
			addListeners();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加事件监听器
	 */
	private void addListeners() {
		try {
			switchParent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isOn = !isOn;
					scrollSwitch();
					if (null != listner) {
						// 开关开发或者关闭的回调方法
						listner.switchChanged(getId(), isOn);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 滑动开关
	 */
	private void scrollSwitch() {
		// 获取滑块需要滑动的距离，滑动距离等于父组建的宽度减去滑块的宽度
		scrollDistance = switchParent.getWidth() - switchButton.getWidth();
		// 初始化滑动事件
		Animation animation = null;
		if (isOn) {
			animation = new TranslateAnimation(0, scrollDistance, 0, 0);
		} else {
			animation = new TranslateAnimation(scrollDistance, 0, 0, 0);
		}
		// 设置滑动时间
		animation.setDuration(200);
		// 滑动之后保持状态
		animation.setFillAfter(true);
		// 开始滑动
		switchButton.startAnimation(animation);
	}

	/**
	 * 获取开关状态
	 * 
	 * @return 【true:打开】【false:关闭】
	 */
	public boolean isOn() {
		return isOn;
	}

	public void setImage(int resourceId){
		switchParent.setBackgroundResource(resourceId);

	}

	/**
	 * 设置开关状态
	 * 
	 * @param isOn
	 *            开关状态【true:打开】【false:关闭】
	 */
	public void setOn(boolean isOn) {
		if (this.isOn == isOn) {
			return;
		}
		this.isOn = isOn;
		post(new Runnable() {
			@Override
			public void run() {
				scrollSwitch();
			}
		});
	}

	/**
	 * 设置开关状态监听器
	 * 
	 * @param listner
	 *            开关状态监听器
	 */
	public void setOnSwitchListner(SwitchChangedListner listner) {
		this.listner = listner;
	}

	/**
	 * 开关状态监听器
	 * 
	 * @author llew
	 * 
	 */
	public interface SwitchChangedListner {
		/**
		 * 开关状态改变
		 * 
		 * @param viewId
		 *            当前开关ID
		 * @param isOn
		 *            开关是否打开【true:打开】【false:关闭】
		 */
		public void switchChanged(Integer viewId, boolean isOn);
	}
}
