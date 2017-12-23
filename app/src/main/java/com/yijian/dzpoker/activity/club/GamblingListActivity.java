package com.yijian.dzpoker.activity.club;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.adapter.GamblingAdapter;

/**
 * Created by QIPU on 2017/12/24.
 */
public class GamblingListActivity extends BaseBackActivity {

    private RecyclerView gamblingRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("牌局");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.gambling_list;
    }

    @Override
    protected void initViews() {
        gamblingRecyclerView = (RecyclerView) findViewById(R.id.gambling_list);
        gamblingRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        gamblingRecyclerView.addItemDecoration(new SpaceItemDecoration(8));
        gamblingRecyclerView.setAdapter(new GamblingAdapter(this, null));
    }

    @Override
    public void onClick(View v) {

    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (0 == parent.getChildLayoutPosition(view)) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }
}
