package vn.hoangphan.karafind.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import vn.hoangphan.karafind.R;

/**
 * Created by Hoang Phan on 1/18/2016.
 */
public class OutlinedTextView extends TextView {
    private Paint mTextPaint;
    private Paint mTextPaintOutline;

    public OutlinedTextView(Context context) {
        super(context);
        init();
    }

    public OutlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OutlinedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(getTextSize());
        mTextPaint.setColor(getCurrentTextColor());
        mTextPaint.setStyle(Paint.Style.FILL);

        mTextPaintOutline = new Paint();
        mTextPaintOutline.setAntiAlias(true);
        mTextPaintOutline.setTextSize(getTextSize());
        mTextPaintOutline.setColor(0xFFFFFF);
        mTextPaintOutline.setStyle(Paint.Style.STROKE);
        mTextPaintOutline.setStrokeWidth(4);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = getPaint();
        p.setStyle(Paint.Style.FILL);
        super.onDraw(canvas);
        int currentTextColor = getCurrentTextColor();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(1);
        setTextColor(Color.WHITE);
        super.onDraw(canvas);
        setTextColor(currentTextColor);
    }
}
