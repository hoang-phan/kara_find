package vn.hoangphan.karafind.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Hoang Phan on 1/31/2016.
 */
public class NPALinearLayoutManager extends LinearLayoutManager {
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public NPALinearLayoutManager(Context context) {
        super(context);
    }

    public NPALinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public NPALinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("index", "out of bound");
        }
    }
}
