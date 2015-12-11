package jaywhy13.gycweb.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import jaywhy13.gycweb.R;

/**
 * Created by Jay on 12/19/13.
 */
public class BlurredImageLayout extends RelativeLayout {

    Bitmap backgroundBitmap;
    Paint backgroundPaint;

    public BlurredImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init();
    }

    private void init(){
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.presenter_bg);
        backgroundPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        backgroundPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
        canvas.drawBitmap(backgroundBitmap, 0, 0, backgroundPaint);
    }
}
