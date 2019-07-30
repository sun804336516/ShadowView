package com.sxw.lib.shadowview;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
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

    private Bitmap mShadowBitmap;
    private RectF mShadowRect;
    private Paint mPaint;
    private Paint mMShadowPaint;
    private View mView;

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
            mShadowColor = attr.getColor(R.styleable.ShadowView_sv_shadowColor, resources.getColor(R.color.default_shadow_color));
            mBgColor = attr.getColor(R.styleable.ShadowView_sv_background, Color.WHITE);
        } finally {
            attr.recycle();
        }
        mPaint = new Paint();
        mPaint.setColor(mBgColor);
        mPaint.setStyle(Paint.Style.FILL);

        mMShadowPaint = new Paint();
        mMShadowPaint.setAntiAlias(true);
        mMShadowPaint.setColor(mShadowColor);
        mMShadowPaint.setStyle(Paint.Style.FILL);
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
            mMShadowPaint.setShadowLayer(mShadowRadius, mDx, mDy, mShadowColor);
        }

        canvas.drawRoundRect(mShadowRect, mCornerRadius, mCornerRadius, mMShadowPaint);
    }

    public void onDraw(Canvas canvas) {
        if (mShadowBitmap != null) {
            canvas.drawBitmap(mShadowBitmap, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));
            canvas.drawRoundRect(mShadowRect, mCornerRadius, mCornerRadius, mPaint);
        }
    }

    public void reDraw() {
        mShadowBitmap = null;
    }
}
