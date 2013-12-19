package jaywhy13.gycweb.components;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.shapes.Shape;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;

/**
 * This Layout draws an image in the background and scales it up.
 * Created by Jay on 11/18/13.
 */
public class BannerLayout extends RelativeLayout {

    Paint cornerIconPaint = new Paint();

    /**
     * The background color to use for the corner icon
     */
    private int cornerIconColor = 0xFF299c9e;

    /**
     * If true, we draw the corner icon
     */
    private boolean drawCornerIcon = false;

    /**
     * If set to True, the background image will only fill half of the page
     */
    private boolean fillParent = true;

    /**
     * If true, a linear gradient will be drawn untop of the image so it fades out...
     */
    private boolean fadeImage = false;

    /**
     * If true, the green overlay will be drawn
     */
    private boolean drawGreenBackground = true;

    /**
     * The opacity for the paint for the background image
     */
    private int backgroundImageOpacity = 0xB5;

    /**
     * The height of the path that the share icon will be placed in
     */
    private int pathHeight = 0;

    private Path path = new Path();

    private LinearGradient linearGradient = null;

    private Bitmap linearGradientBitmap;

    private int greenOverlayColor = 0xAA006563;

    private int startColor = 0xFF000000;
    private int endColor = 0x00FFFFFF;

    /**
     * The resource ID of the image for the background
     */
    private int backgroundImageResource;

    private Paint greenPaint = new Paint();

    /**
     * A bitmap to hold the background image
     */
    private static Bitmap backgroundBitmap;

    /**
     * This will hold the resized version of the bitmap
     */
    private Bitmap scaledBackgroundBitmap;

    /**
     * A paint object for the background
     */
    private Paint backgroundBitmapPaint;

    /**
     * A shader for the background image bitmap
     */
    private BitmapShader bitmapShader = null;

    public static Bitmap createAlphaBitmap(Bitmap bitmap){
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return result;
    }

    public BannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init(context, attrs);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int requiredWidth, int requiredHeight){
        int height = options.outHeight;
        int width = options.outWidth;
        int sampleSize = 1;

        if(height > requiredHeight || width > requiredWidth){
            int halfHeight = height / 2;
            int halfWidth = width  / 2;

            while((halfHeight / sampleSize) > requiredHeight && (halfWidth / sampleSize) > requiredWidth){
                sampleSize *= 2;
            }
        }
        return sampleSize;
    }

    public static Bitmap decodeScaledBitmapFromResource(Resources res, int resourceId, int requiredWidth, int requiredHeight){
        // Decode first to check dims
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resourceId, options);

        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);

        // Now load it..
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(res, resourceId, options);
    }

    /**
     * Creates the shaders and bitmap necessary to draw our background
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs){

        long now = System.currentTimeMillis();
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BannerLayout, 0, 0);
        // Load in the resource from settings
        backgroundImageResource = ta.getInt(R.styleable.BannerLayout_bannerBg, R.drawable.presenter_bg);

        backgroundBitmapPaint = new Paint();
        backgroundBitmapPaint.setAntiAlias(true);
        int backgroundImageOpacity = ta.getInt(R.styleable.BannerLayout_backgroundImageOpacity, 0xFF);
        backgroundBitmapPaint.setAlpha(backgroundImageOpacity); // Set the opacity

        // Configure the green paint
        greenPaint.setColor(greenOverlayColor);
        greenPaint.setStyle(Paint.Style.FILL);

        // Check if we ned to draw it...
        drawGreenBackground = ta.getBoolean(R.styleable.BannerLayout_drawGreenBackground, true);

        // Draw a linear gradient??
        fadeImage = ta.getBoolean(R.styleable.BannerLayout_fadeBackgroundImage, false);

        // Fill the entire layout?
        fillParent = ta.getBoolean(R.styleable.BannerLayout_fillParent, true);

        // Draw the corner icon?
        drawCornerIcon = ta.getBoolean(R.styleable.BannerLayout_drawCornerIcon, false);

        ta.recycle();
        long after = System.currentTimeMillis();
        Log.d(GYCMainActivity.TAG, getClass() + " init took " + (after - now) + "ms");

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int width = getMeasuredWidth();
        int height = fillParent ? getMeasuredHeight() : getMeasuredHeight() / 3;

        long now = System.currentTimeMillis();

        // Declare a bitmap to hold the image and a bitmap shader
        //backgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundImageResource);
        if(backgroundBitmap == null){
            backgroundBitmap = decodeScaledBitmapFromResource(getResources(), backgroundImageResource, width, height);
        }
        Log.d(GYCMainActivity.TAG, getClass() + " scaled bitmap load took " + (System.currentTimeMillis() - now) + "ms");

        // Now initialize the linear gradient
        if(fadeImage){
            linearGradient = new LinearGradient(0, 0, 0, height, startColor, endColor, Shader.TileMode.CLAMP);
            // Make an alpha bitmap out of the linear gradient
            Bitmap.Config conf = Bitmap.Config.ALPHA_8;
            Bitmap tmpBitmap = Bitmap.createBitmap(width, height, conf);
            Canvas c = new Canvas(tmpBitmap);
            Paint linearGradientPaint = new Paint();
            linearGradientPaint.setShader(linearGradient);
            c.drawRect(0, 0, width, height, linearGradientPaint);
            linearGradientBitmap = createAlphaBitmap(tmpBitmap);
        }

        // Resize the bitmap
        int backgroundWidth = backgroundBitmap.getWidth();
        int backgroundHeight = backgroundBitmap.getHeight();


        float scaleX = (float) backgroundWidth / (float) width;
        float scaleY = (float) backgroundHeight / (float) height;

        float scaledDownHeightDiff = (width / backgroundWidth * backgroundHeight) - height;
        float scaledDownWidthDiff = (height / backgroundHeight * backgroundWidth) - width;

        float scale = scaledDownHeightDiff > scaledDownWidthDiff ? scaleX : scaleY;

        int newBitmapWidth = (int) ((float) backgroundWidth / scale);
        int newBitmapHeight = (int) ((float) backgroundHeight / scale);

        //Log.d(GYCMainActivity.TAG, "Resized bitmap from " + backgroundWidth + "x" + backgroundHeight + " to " + newBitmapWidth + "x" + newBitmapHeight + " with scale = " + scale);

        scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, newBitmapWidth, newBitmapHeight, true);
        bitmapShader = new BitmapShader(scaledBackgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        backgroundBitmapPaint.setShader(bitmapShader);

        // Draw the path
        float pixelDensity = getResources().getDisplayMetrics().density;
        pathHeight = (int) (50 * pixelDensity);
        int actualPathHeight = (int) (0.73 * pathHeight);

        path.moveTo(0, height - pathHeight);
        path.lineTo(width, height - pathHeight);
        path.lineTo(0, height - pathHeight + actualPathHeight);
        path.close();

        // Initialize the paint for the corner icon
        cornerIconPaint.setStyle(Paint.Style.FILL);
        cornerIconPaint.setColor(cornerIconColor);

        long after = System.currentTimeMillis();
        Log.d(GYCMainActivity.TAG, getClass() + " onLayout took " + (after - now) + "ms");

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        long now = System.currentTimeMillis();

        // Draw the linear gradient using the background bitmap as the shader
        if(fadeImage){
            canvas.drawBitmap(linearGradientBitmap, 0, 0, backgroundBitmapPaint);
        } else {
            Paint paint = new Paint();
            paint.setAlpha(backgroundImageOpacity);
            canvas.drawBitmap(scaledBackgroundBitmap, 0, 0, paint);
        }

        if(drawGreenBackground){
            // Draw the translucent green bar over it now...
            canvas.drawRect(0, 0, width, height - pathHeight, greenPaint);

            // Draw the path
            canvas.drawPath(path, greenPaint);
        }

        // Draw the corner icon ...
        if(drawCornerIcon){
            // calculate the point of rotation
            int angleOfRotation = -14;
            int cornerIconWidth = (int) (90 * Resources.getSystem().getDisplayMetrics().density);
            int cornerX = width - cornerIconWidth + (int) (cornerIconWidth /4.0f);
            int pivotX = cornerX + cornerIconWidth / 2;
            int pivotY = cornerIconWidth / 2;
            int marginTop = (int) (-40 * Resources.getSystem().getDisplayMetrics().density);

            canvas.save(Canvas.MATRIX_SAVE_FLAG);
            canvas.rotate(angleOfRotation, pivotX, pivotY);
            canvas.drawRect(cornerX, marginTop, cornerX + cornerIconWidth, cornerIconWidth, cornerIconPaint);
            canvas.restore();
        }

        long after = System.currentTimeMillis();
        Log.d(GYCMainActivity.TAG, getClass() + " onDraw took " + (after - now) + "ms");
    }

}
