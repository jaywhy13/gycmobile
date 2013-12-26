package jaywhy13.gycweb.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.renderscript.Font;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import jaywhy13.gycweb.R;
import jaywhy13.gycweb.util.Utils;

/**
 * TODO: document your custom view class.
 */
public class NowPlayingView extends LinearLayout {

    Paint nowPlayingPaint = new Paint();
    Paint sermonTitlePaint = new Paint();
    String nowPlayingText = "Now playing";
    private int viewHeight = 0;
    public String sermonTitle = "---";
    private String fontFamily;
    private Typeface typeFace;

    public NowPlayingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        // Load the font
        fontFamily = context.getString(R.string.default_font);
        typeFace = Typeface.createFromAsset(getContext().getAssets(), fontFamily);

        nowPlayingPaint.setColor(0xFF151717);
        nowPlayingPaint.setTypeface(typeFace);

        sermonTitlePaint.setColor(0xFFc5d1d0);
        sermonTitlePaint.setTypeface(typeFace);

        Rect rect = new Rect();
        nowPlayingPaint.setTextSize(28);
        nowPlayingPaint.setAntiAlias(true);
        nowPlayingPaint.getTextBounds(nowPlayingText, 0, nowPlayingText.length(), rect);
        viewHeight = (rect.width() + rect.left) * 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw Now playing rotated
        int tenPx = Utils.dpToPx(20);
        canvas.save();

        // Draw now playing
        canvas.translate(0, getMeasuredHeight() - tenPx);
        canvas.rotate(-90, 0, 0);
        canvas.drawText(nowPlayingText, 0, tenPx, nowPlayingPaint);

        Rect nowPlayingBounds = new Rect();
        nowPlayingPaint.getTextBounds(nowPlayingText, 0, nowPlayingText.length(), nowPlayingBounds);

        // Draw text title
        canvas.drawText(sermonTitle, 0, tenPx + nowPlayingBounds.height() + tenPx, sermonTitlePaint);

        canvas.restore();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, viewHeight);
    }
}
