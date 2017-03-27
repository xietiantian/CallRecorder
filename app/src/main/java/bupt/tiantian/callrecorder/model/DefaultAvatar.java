package bupt.tiantian.callrecorder.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import bupt.tiantian.callrecorder.R;

/**
 * Created by tiantian on 3/4/17.
 */

public class DefaultAvatar {

    public static final int[] colorsId = new int[]{
            R.color.red, R.color.pink, R.color.purple, R.color.deep_purple,
            R.color.indigo, R.color.blue, R.color.light_blue, R.color.cyan,
            R.color.teal, R.color.green, R.color.light_green, R.color.lime,
            R.color.yellow, R.color.amber, R.color.orange, R.color.deep_orange
    };

    public static final int[] avatarsId = new int[]{
            R.drawable.ic_account_circle_48dp, R.drawable.ic_account_circle_48dp_1,
            R.drawable.ic_account_circle_48dp_2, R.drawable.ic_account_circle_48dp_3,
            R.drawable.ic_account_circle_48dp_4, R.drawable.ic_account_circle_48dp_5,
            R.drawable.ic_account_circle_48dp_6, R.drawable.ic_account_circle_48dp_7
    };

    private static int calcHash(String phoneNum) {
        int tmp;
        if (phoneNum.length() > 5) {
            tmp = Integer.parseInt(phoneNum.substring(phoneNum.length() - 5));
        }else{
            tmp =Integer.parseInt(phoneNum)* 7;
        }
        return tmp % avatarsId.length;
    }

    public static Drawable getDefaultDrawable(String phoneNum, Context context) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawable = context.getDrawable(avatarsId[calcHash(phoneNum)]);
        } else {
            drawable = context.getResources().getDrawable(avatarsId[calcHash(phoneNum)]);
        }
        return drawable;
    }
}
