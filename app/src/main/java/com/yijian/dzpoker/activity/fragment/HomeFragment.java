package com.yijian.dzpoker.activity.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.news.DetailNewsActivity;
import com.yijian.dzpoker.utils.NewsPageUtils;
import com.yijian.dzpoker.view.data.MainPageNews;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private int mDistance;

    private ViewPager mIn_vp;
    private LinearLayout mIn_ll;
    private ImageView mLight_dots;

    private ArrayList<MainPageNews> mNewsList= null;
    private ArrayList<View> mPagerViews = new ArrayList<View>();

    private ViewPagerAdatper mAdapter;
    public  final static int MESAGE_GETNEWSLIST_OK=0x1001;

    private ProgressDialog mProgressDialog;

    public class ViewPagerAdatper extends PagerAdapter {
        private ArrayList<View> mViewList ;

        public ViewPagerAdatper(ArrayList<View> mViewList ) {
            this.mViewList = mViewList;
        }

        /**
        private void setList(ArrayList<View> list){
            this.mViewList = list;
            notifyDataSetChanged();
        }**/

        @Override
        public int getCount() {
            return mViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = mViewList.get(position);
            container.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DetailNewsActivity.class);
                    MainPageNews news = mNewsList.get(position);
                    intent.putExtra("news", news);
                    getContext().startActivity(intent);
                }
            });
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initData();
        return view;
    }

    private void initView(View inflater){
        mIn_vp = (ViewPager) inflater.findViewById(R.id.in_viewpager);
        mIn_ll = (LinearLayout) inflater.findViewById(R.id.in_ll);
        mLight_dots = (ImageView) inflater.findViewById(R.id.iv_light_dots);
    }

    private void initData(){

        mNewsList= NewsPageUtils.getNewsList();
        if ( mNewsList == null || mNewsList.isEmpty()) {
            QueryDataTask task = new QueryDataTask();
            task.execute();
        } else {
            showData();
        }
    }

    private void showData(){

        initPagerViews();
        mAdapter = new ViewPagerAdatper(mPagerViews);
        mIn_vp.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        addDots();
        moveDots();
        mIn_vp.setPageTransformer(true,new DepthPageTransformer());
    }

    private class QueryDataTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setTitle("请稍后....");
            mProgressDialog.setMessage("加载中....");
            mProgressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            NewsPageUtils.prepareNewsData(getContext());
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }

            mNewsList= NewsPageUtils.getNewsList();
            showData();
            super.onPostExecute(s);
        }
    }

    private void initPagerViews(){

        mPagerViews.clear();
        for(MainPageNews news: mNewsList){
            Bitmap map = NewsPageUtils.getBitmap(news, getContext());
            View view = generateView(map);
            mPagerViews.add(view);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void addDots() {

        for (int i=0; i<mNewsList.size();i++){
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(R.drawable.gray_dot);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 40, 0);
            mIn_ll.addView(dot, layoutParams);
            final int index = i;
            dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIn_vp.setCurrentItem(index);
                }
            });
        }

        mLight_dots.setVisibility(View.VISIBLE);
    }

    private void moveDots() {
        mLight_dots.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获得两个圆点之间的距离
                if (mIn_ll.getChildCount() > 2) {
                    mDistance = mIn_ll.getChildAt(1).getLeft() - mIn_ll.getChildAt(0).getLeft();
                }
                mLight_dots.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
            }
        });
        mIn_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //页面滚动时小白点移动的距离，并通过setLayoutParams(params)不断更新其位置
                float leftMargin = mDistance * (position + positionOffset);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
                params.leftMargin = (int) leftMargin;
                mLight_dots.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //页面跳转时，设置小圆点的margin
                float leftMargin = mDistance * position;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLight_dots.getLayoutParams();
                params.leftMargin = (int) leftMargin;
                mLight_dots.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private View generateView(Bitmap map){
        if (map == null) return null;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_news_page, null);
        ImageView image = (ImageView)view.findViewById(R.id.news_page);
        image.setImageBitmap(map);
        return view;
    }

    class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // 页面远离左侧页面
                page.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // 页面在由中间页滑动到左侧页面 或者 由左侧页面滑动到中间页
                page.setAlpha(1);
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                //页面在由中间页滑动到右侧页面 或者 由右侧页面滑动到中间页
                // 淡出效果
                page.setAlpha(1 - position);
                // 反方向移动
                page.setTranslationX(pageWidth * -position);
                // 0.75-1比例之间缩放
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);
            } else { // (1,+Infinity]
                // 页面远离右侧页面
                page.setAlpha(0);
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
