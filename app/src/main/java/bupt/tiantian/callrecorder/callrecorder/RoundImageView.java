package bupt.tiantian.callrecorder.callrecorder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by tiantian on 17-2-17.
 */
public class RoundImageView extends ImageView {
    private int width;
    private int height;

    public RoundImageView(Context context) {
        super(context);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        if (!(drawable instanceof BitmapDrawable)) {
            super.onDraw(canvas);
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if (null == bitmap) {
            return;
        }

//        int width = getWidth();
        Bitmap roundBitmap = getCroppedBitmap(bitmap, width);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    /**
     * 初始Bitmap对象的缩放裁剪过程
     *
     * @param bmp 初始Bitmap对象
     * @param dia 圆形图片直径大小
     * @return 返回一个圆形的缩放裁剪过后的Bitmap对象
     */
    public static Bitmap getCroppedBitmap(Bitmap bmp, int dia) {
        Bitmap sbmp;
        //比较初始Bitmap宽高和给定的圆形直径，判断是否需要缩放裁剪Bitmap对象
        if (bmp.getWidth() != dia || bmp.getHeight() != dia) {
            sbmp = Bitmap.createScaledBitmap(bmp, dia, dia, false);
        } else {
            sbmp = bmp;
        }
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        int radius = dia / 2;
        canvas.drawCircle(radius, radius, radius, paint);
        //核心部分，设置两张图片的相交模式，在这里就是上面绘制的Circle和下面绘制的Bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, 0, 0, paint);

        return output;
    }
}
