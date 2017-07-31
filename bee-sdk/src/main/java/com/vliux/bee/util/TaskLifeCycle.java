package com.vliux.bee.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vliux on 17/7/31.
 * @author vliux
 */

public class TaskLifeCycle {
    public interface Callbacks {
        void onTaskStarted(Context context);

        void onTaskStopped(Context context);

        void onActivityStarted(Activity activity, @Nullable StartInfo startInfo);

        void onActivityStopped(Activity activity, @Nullable StopInfo stopInfo);
    }
    
    public static class StartInfo {
        public String lastActivity;
    
        public StartInfo(String lastActivity) {
            this.lastActivity = lastActivity;
        }
    }
    
    public static class StopInfo {
        public long duration;
    
        public StopInfo(long duration) {
            this.duration = duration;
        }
    }

    public void register(final Application application, final Callbacks callbacks) {
        mCallbacks = callbacks;
        mLifeCycleCallbacks = new _ActivityLifeCycleCallbacks();
        application.registerActivityLifecycleCallbacks(mLifeCycleCallbacks);
    }

    public void unregister(final Application application) {
        if (null != mLifeCycleCallbacks)
            application.unregisterActivityLifecycleCallbacks(mLifeCycleCallbacks);
        mCallbacks = null;
    }

    public boolean isTaskStarted() {
        return mLifeCycleCallbacks.mStartCount.get() > 0;
    }

    private class _ActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {
        final AtomicInteger mCreationCount = new AtomicInteger(0);
        final AtomicInteger mStartCount = new AtomicInteger(0);

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (mCreationCount.get() < 0) {
                mCreationCount.set(0);
            }
            mCreationCount.getAndIncrement();
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (mStartCount.get() < 0) {
                mStartCount.set(0);
            }
            boolean isNewSession = false;
            if (mStartCount.getAndIncrement() == 0) {
                isNewSession = true;
                if (null != mCallbacks) mCallbacks.onTaskStarted(activity.getApplication());
            }
    
            if (null != mCallbacks)
                mCallbacks.onActivityStarted(activity,
                        isNewSession ? null : new StartInfo(mLastActivity));
            
            mLastActivity = getActivityName(activity);
            mActivityStart.put(mLastActivity, System.currentTimeMillis());
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (null != mCallbacks) {
                final Long start = mActivityStart.remove(getActivityName(activity));
                mCallbacks.onActivityStopped(activity,
                        null != start ? new StopInfo(System.currentTimeMillis() - start) : null);
            }
            if (mStartCount.decrementAndGet() <= 0) {
                if (null != mCallbacks) {
                    mCallbacks.onTaskStopped(activity.getApplication());
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            mCreationCount.decrementAndGet();
        }

    }

    private static String getActivityName(final Activity activity){
        return activity.getClass().getSimpleName();
    }
    
    private Callbacks mCallbacks;
    private _ActivityLifeCycleCallbacks mLifeCycleCallbacks;
    private String mLastActivity;
    private Map<String, Long> mActivityStart = new android.support.v4.util.ArrayMap<>();
}
