package klx.app.sdleader_app.debug.decoration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import klx.app.sdleader_app.R;
import klx.app.sdleader_app.utils.UIUtils;

/**
 * Created by xjw on 2017/10/10 17:04
 * Email 1521975316@qq.com
 * <p>
 * onDraw onDrawOver getItemOffset这三个方法都是在RecyclerView滑动时触发
 * RecyclerView.getChildCount 获取当前屏幕可见的item的个数
 */

public class XItemDecoration extends RecyclerView.ItemDecoration {

    private final Context context;
    private final XDecorationCallBack callBack;
    private final Paint.FontMetrics mFontMertics;
    private Bitmap mBitmap;
    private int mWidth;
    private int mHeight;
    private int mItemHeight;
    private int mItemPadding;
    private Paint mPaint;
    private Paint mPaintText;
    private Paint mPaintDecoration;
    private Rect mRectText = new Rect();

    public XItemDecoration(Context context, XDecorationCallBack callBack) {

        this.context = context;
        this.callBack = callBack;

        mWidth = context.getResources().getDisplayMetrics().widthPixels;
        mHeight = context.getResources().getDisplayMetrics().heightPixels;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaintDecoration = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(Color.BLACK);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(UIUtils.dip2px(context, 19));
        mFontMertics = new Paint.FontMetrics();
        mPaintText.getFontMetrics(mFontMertics);
        mPaintDecoration.setColor(Color.BLACK);
        mItemHeight = UIUtils.dip2px(context, 46);
        mItemPadding = UIUtils.dip2px(context, 13);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.load_defult);
        scaleBitmap();
    }

    private void scaleBitmap() {
        Matrix matrix = new Matrix();
        float scale = mBitmap.getWidth() > mItemHeight ?
                Float.valueOf(mItemHeight) / Float.valueOf(mBitmap.getHeight()) :
                Float.valueOf(mBitmap.getHeight()) / Float.valueOf(mItemHeight);
        matrix.postScale(scale, scale);
        mBitmap = Bitmap.createBitmap(mBitmap,
                0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, false);
    }

    @Override
    /**
     * 画东西
     */
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            int top = child.getTop() - mItemHeight;
            int bottom = top + mItemHeight;
            String content = callBack.getContent(position);
            mPaintText.getTextBounds(content, 0, content.length(), mRectText);
            System.out.println("top>" + top + "; bottom>" + bottom);
            if (isFirstInGroup(position)) {//条目不一样
                c.drawRect(left, top, right, bottom, mPaint);
                c.drawBitmap(mBitmap, mItemPadding, bottom - mBitmap.getHeight(), mPaintText);
                c.drawText(content, mItemPadding + mBitmap.getWidth(), bottom - mFontMertics.descent, mPaintText);
            }
        }
    }

    /**
     * 判断当前position的文字 与 下一个position的文字是否相等
     *
     * @param position
     * @return
     */
    private boolean isFirstInGroup(int position) {
        if (position == 0) {
            return true;
        } else {
            String prevGroupId = callBack.getContent(position - 1);
            String groupId = callBack.getContent(position);
            return !prevGroupId.equals(groupId);
        }
    }

    @Override
    /**
     * 图层关系,item的内容和分割线是一层(在第一层画东西调用onDraw)
     * onDrawOver是第二层,位于onDraw的上面
     */
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount(); i++) {
            View view = parent.getChildAt(0);
            int position = parent.getChildAdapterPosition(view);
            int top = view.getTop();
            String content = callBack.getContent(position);
            mPaintText.getTextBounds(content, 0, content.length(), mRectText);
            System.out.println("position>" + position +
                    "; mItemHeight - view.getHeight()>" + (mItemHeight - view.getHeight())
                    + "; view.getBottom()>" + view.getBottom());
            if (view.getTop() <= mItemHeight - view.getHeight() && isFirstInGroup(position + 1)) {
                c.drawRect(left, top, right, view.getBottom(), mPaintDecoration);
                c.drawBitmap(mBitmap, mItemPadding, view.getBottom() - mBitmap.getHeight(), mPaintText);
                c.drawText(content, mItemPadding + mBitmap.getWidth(), view.getBottom() - mFontMertics.descent, mPaintText);
            } else {
                c.drawRect(left, 0, right, mItemHeight, mPaintDecoration);
                c.drawBitmap(mBitmap, mItemPadding, 0, mPaintText);
                c.drawText(content, mItemPadding + mBitmap.getWidth(),
                        mItemHeight - mFontMertics.descent, mPaintText);
            }

        }
    }

    @Override
    /**
     * 设置item偏移值
     */
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (isFirstInGroup(position))
            outRect.top = mItemHeight;
    }


}
