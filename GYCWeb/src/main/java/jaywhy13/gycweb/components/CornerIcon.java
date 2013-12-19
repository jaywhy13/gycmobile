package jaywhy13.gycweb.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import jaywhy13.gycweb.R;

/**
 * Created by Jay on 10/28/13.
 */
public class CornerIcon extends ImageView {

    private int iconBgColor = Color.BLUE;

    public CornerIcon(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs){
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CornerIcon, 0, 0);
        int bgColor = ta.getInt(R.styleable.CornerIcon_iconBgColor, iconBgColor);
        setIconBgColor(bgColor);
        ta.recycle();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint paint = new Paint();
        paint.setColor(iconBgColor);

        Path path = new Path();
        path.moveTo(0,0);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.close();

        canvas.drawPath(path, paint);
    }

    @Override
    public Drawable getBackground() {
        return super.getBackground();
    }

    public void setIconBgColor(int iconBgColor) {
        this.iconBgColor = iconBgColor;
    }
}
