package cc.soham.timberutils.output.file;

import android.content.Context;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

import timber.log.Timber;

/**
 * Created by sohammondal on 17/08/16.
 * Our custom TimberTree that lets us output to a file via an {@link android.app.IntentService}
 * like {@link TimberService}
 */
public class FileTimberTree extends Timber.DebugTree {
    /**
     * we have to keep a reference to the application context so that we can launch the service which
     * writes to the file
     */
    private WeakReference<Context> contextWeakReference;

    public FileTimberTree(final Context context) {
        setContext(context);
    }

    /**
     * Overriden so that we can add the line number to the logs!
     *
     * @return
     */
    @Override
    protected String createStackElementTag(StackTraceElement element) {
        return super.createStackElementTag(element) + ":" + element.getLineNumber();
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        try {
            if (contextWeakReference != null && contextWeakReference.get() != null)
                TimberService.log(contextWeakReference.get(), priority, System.currentTimeMillis() + ", " + tag, message, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Context getContext() {
        return contextWeakReference == null ? null : contextWeakReference.get();
    }

    public void setContext(@NonNull Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context is null when setting Context in TimberWrapper");
        if (contextWeakReference != null) {
            contextWeakReference.clear();
            contextWeakReference = null;
        }
        this.contextWeakReference = new WeakReference<>(context);
    }
}
