package jaywhy13.gycweb.screens;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import jaywhy13.gycweb.GYCMainActivity;

/**
 * Created by jay on 10/5/13.
 */
public class HomeScreenBanner extends LinearLayout {

    public HomeScreenBanner(Context context, AttributeSet attr) {
        super(context, attr);
        setWillNotDraw(false);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // Fill the background first...
        paint.setColor(0xFFD4DEDE);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(0xFF003938);
        int stopY = (int) (0.75 * getHeight());
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        path.lineTo(0, stopY);
        path.close();
        canvas.drawPath(path, paint);
    }
}
