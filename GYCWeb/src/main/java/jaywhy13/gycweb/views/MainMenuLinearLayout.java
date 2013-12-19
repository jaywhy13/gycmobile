package jaywhy13.gycweb.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import jaywhy13.gycweb.R;

/**
 * Created by jay on 10/17/13.
 */
public class MainMenuLinearLayout extends LinearLayout {

    private int menuBackgroundColor = 0xFFFF0000;

    public static final int UP = 0;
    public static final int DOWN = 1;
    private int direction = UP;

    private TextView menuCaption;

    public MainMenuLinearLayout(Context context, AttributeSet attr) {
        super(context, attr);
        setWillNotDraw(false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a rect first...
        menuCaption = (TextView) findViewById(R.id.menu_caption);
        int menuCaptionLeft = getRelativeLeft(this, menuCaption);

        Resources r = getResources();
        float triangleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, r.getDisplayMetrics());
        float dpPadding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, r.getDisplayMetrics()); // distance from the caption

        // Draw the rect first...
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(menuBackgroundColor);
        canvas.drawRect(0, 0, menuCaptionLeft - triangleWidth - dpPadding, getHeight(), paint);

        // Draw the triangle after ...
        Path path = new Path();
        if(direction == DOWN){
            path.moveTo(menuCaptionLeft - triangleWidth - dpPadding, 0);
            path.lineTo(menuCaptionLeft - dpPadding, getHeight());
            path.lineTo(menuCaptionLeft - triangleWidth - dpPadding, getHeight());
        } else {
            path.moveTo(menuCaptionLeft - triangleWidth - dpPadding, getHeight());
            path.lineTo(menuCaptionLeft - dpPadding, 0);
            path.lineTo(menuCaptionLeft - triangleWidth - dpPadding, 0);
        }
        path.close();
        canvas.drawPath(path, paint);

    }

    protected int getRelativeLeft(View parent, View child){
        int left = child.getLeft();
        if(child.getParent() == parent){
            return left;
        } else {
            return left + getRelativeLeft(parent, (View) child.getParent());
        }
    }


    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setMenuBackgroundColor(int menuBackgroundColor) {
        this.menuBackgroundColor = menuBackgroundColor;
    }
}
