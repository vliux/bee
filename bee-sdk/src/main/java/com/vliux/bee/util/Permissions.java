package com.vliux.bee.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by vliux on 17/5/19.
 * @author vliux
 */

public class Permissions {
    public static boolean has(final Context context, final String permission){
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }
}
