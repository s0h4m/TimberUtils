package cc.soham.timberutils.output.file;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;

/**
 * Created by sohammondal on 17/08/16.
 * The {@link IntentService} that handles all logging operations
 * This delegates all the tasks to the {@link LogFileWriter}
 */
public class TimberService extends IntentService {
    public static final String NAME = "TimberService";
    public static final String KEY_PRIROITY = "key_priority";
    public static final String KEY_TAG = "key_tag";
    public static final String KEY_MESSAGE = "key_message";
    public static final String KEY_THROWABLE = "key_throwable";

    public static final String KEY_METHOD = "key_method";

    public static final int METHOD_LOG = 0;
    public static final int METHOD_ZIP = 1;

    public static final int PRIORITY_INVALID = -1;
    public static final int PRIORITY_DEFAULT = Log.DEBUG;

    // used to put a "debuglogger started at _____" message at the top of the log
    private static boolean started = false;

    public TimberService() {
        super(NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                int method = intent.getIntExtra(KEY_METHOD, METHOD_LOG);
                switch (method) {
                    case METHOD_ZIP:
                        String zipPath = LogFileWriter.handleZip(this);
                        // TODO: send broadcast to update that zip files have been generated
                        // include the zip path in the broadcast
                        break;
                    case METHOD_LOG:
                    default:
                        LogFileWriter.handleLog(this,
                                getFileWriterWrapper(),
                                TimberServiceIntentHelper.getPriorityFromIntent(intent),
                                TimberServiceIntentHelper.getTagFromIntent(intent),
                                TimberServiceIntentHelper.getMessageFromIntent(intent),
                                TimberServiceIntentHelper.getThrowableFromIntent(intent));
                        break;
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public FileWriterWrapper getFileWriterWrapper() {
        return FileWriterWrapper.getFileWriterWrapper(this);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        TimberService.started = started;
    }

    /**
     * Called by a {@link FileTimberTree}
     * to write things to a file in a service
     *
     * @param context
     * @param priority
     * @param tag
     * @param message
     * @param t
     */
    public static void log(final Context context, int priority, String tag, String message, Throwable t) {
        Intent intent = new Intent(context, TimberService.class);
        if (priority != PRIORITY_INVALID)
            intent.putExtra(KEY_PRIROITY, priority);
        else
            intent.putExtra(KEY_PRIROITY, PRIORITY_DEFAULT);
        if (tag != null)
            intent.putExtra(KEY_TAG, tag);
        if (message != null)
            intent.putExtra(KEY_MESSAGE, message);
        if (t != null)
            intent.putExtra(KEY_THROWABLE, t);
        // this is the log method
        intent.putExtra(KEY_METHOD, METHOD_LOG);
        context.startService(intent);
    }

    /**
     * Called to zip all the contents of the log folder into a zip file
     *
     * @param context
     */
    public static void zip(final Context context) {
        Intent intent = new Intent(context, TimberService.class);
        // this is the zip method
        intent.putExtra(KEY_METHOD, METHOD_ZIP);
        context.startService(intent);
    }

    /**
     * Intent related helper function for {@link TimberService}
     */
    static class TimberServiceIntentHelper {
        public static int getPriorityFromIntent(Intent intent) {
            return intent.getIntExtra(KEY_PRIROITY, PRIORITY_DEFAULT);
        }

        public static String getTagFromIntent(Intent intent) {
            return intent.getStringExtra(KEY_TAG);
        }

        public static String getMessageFromIntent(Intent intent) {
            return intent.getStringExtra(KEY_MESSAGE);
        }

        public static Throwable getThrowableFromIntent(Intent intent) {
            Object object = intent.getSerializableExtra(KEY_THROWABLE);
            if (object != null)
                return (Throwable) object;
            return null;
        }
    }
}
