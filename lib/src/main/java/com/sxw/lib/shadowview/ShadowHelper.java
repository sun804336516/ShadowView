package com.sxw.lib.shadowview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sxw.lib.R;


/**
 * 作者：sxw on 2019/7/29 16:04
 * 绘制阴影Bitmap
 */
public class ShadowHelper {

    private int mShadowColor;
    private float mShadowRadius;
    private float mCornerRadius;
    private float mDx;
    private float mDy;
    private int mBgColor;
    private int mPressColor;
    private int mCheckColor;

    private Bitmap mShadowBitmap;
    private RectF mShadowRect;
    private Paint mPaint;
    private Paint mShadowPaint;
    private View mView;
    private boolean mChecked;

    int[] press = new int[]{android.R.attr.state_pressed};
    int[] checked = new int[]{android.R.attr.state_checked};

    public ShadowHelper(View view, Context context, AttributeSet attrs) {
        this.mView = view;
        this.mView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        Resources resources = context.getResources();
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.ShadowView);
        try {
            mCornerRadius = attr.getDimension(R.styleable.ShadowView_sv_cornerRadius, resources.getDimension(R.dimen.default_corner_radius));
            mShadowRadius = attr.getDimension(R.styleable.ShadowView_sv_shadowRadius, resources.getDimension(R.dimen.default_shadow_radius));
            mDx = attr.getDimension(R.styleable.ShadowView_sv_dx, 0);
            mDy = attr.getDimension(R.styleable.ShadowView_sv_dy, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowView_sv_shadowColor, ContextCompat.getColor(context, R.color.shadow_color));
//            mBgColor = attr.getColor(R.styleable.ShadowView_sv_background, ContextCompat.getColor(context, R.color.shadow_backgroud));
//            mPressColor = attr.getColor(R.styleable.ShadowView_sv_pressbackground, ContextCompat.getColor(context, R.color.shadow_press));

            ColorStateList colorStateList = attr.getColorStateList(R.styleable.ShadowView_sv_colors);

            if (colorStateList != null) {

                mBgColor = colorStateList.getDefaultColor();
                if (mBgColor == 0) {
                    mBgColor = ContextCompat.getColor(context, R.color.shadow_backgroud);
                }

                mPressColor = colorStateList.getColorForState(press, ContextCompat.getColor(context, R.color.shadow_press));
                mCheckColor = colorStateList.getColorForState(checked, ContextCompat.getColor(context, R.color.shadow_checked));
            } else {
                mBgColor = ContextCompat.getColor(context, R.color.shadow_backgroud);

                mPressColor = ContextCompat.getColor(context, R.color.shadow_backgroud);
                mCheckColor = ContextCompat.getColor(context, R.color.shadow_backgroud);
            }

        } finally {
            attr.recycle();
        }
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(mShadowColor);
        mShadowPaint.setStyle(Paint.Style.FILL);

        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPaint.setColor(mPressColor);
                        mView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPaint.setColor(mBgColor);
                        mView.invalidate();
                        break;
                }
                return false;
            }
        });

    }

    public void createShadowBitmap(int shadowWidth, int shadowHeight) {
        if (shadowWidth == 0 || shadowHeight == 0 || mShadowBitmap != null) {
            return;
        }
        mShadowBitmap = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mShadowBitmap);

        mShadowRect = new RectF(
                mShadowRadius,
                mShadowRadius,
                shadowWidth - mShadowRadius,
                shadowHeight - mShadowRadius);

        mShadowRect.left += Math.abs(mDx);
        mShadowRect.right -= Math.abs(mDx);
        mShadowRect.top += Math.abs(mDy);
        mShadowRect.bottom -= Math.abs(mDy);

        if (!mView.isInEditMode()) {
            mShadowPaint.setShadowLayer(mShadowRadius, mDx, mDy, mShadowColor);
        }

        canvas.drawRoundRect(mShadowRect, mCornerRadius, mCornerRadius, mShadowPaint);
    }

    public void onDraw(Canvas canvas) {
        if (mShadowBitmap != null) {
            canvas.save();
            canvas.drawBitmap(mShadowBitmap, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
            canvas.drawRoundRect(mShadowRect, mCornerRadius, mCornerRadius, mPaint);
            canvas.restore();
        }
    }

    public void reDraw() {
        mShadowBitmap = null;
        mView.requestLayout();
    }

    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            mPaint.setColor(mChecked ? mCheckColor : mBgColor);
            mView.invalidate();
        }
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }
}
