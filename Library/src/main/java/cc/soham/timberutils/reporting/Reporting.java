package cc.soham.timberutils.reporting;

import android.support.annotation.NonNull;

/**
 * Created by sohammondal on 16/08/16.
 * Responsible for formatting the log to a string message
 */
public class Reporting {
    /**
     * Converts a set of priority/tag/message and throwable into a single line message
     * Current Implementation: comma separated value
     *
     * @param priority Priority of the message
     * @param tag      TAG, context of the recording
     * @param message  the actual message
     * @param t        an optional exception
     * @return
     */
    public static String format(int priority, @NonNull String tag, @NonNull String message, Throwable t) {
        if (t == null) {
            return format(priority, tag, message);
        } else {
            return String.format("%d, %s, %s, %s", priority, tag, message, t.getMessage());
        }
    }

    /**
     * Converts a set of priority/tag/message and throwable into a single line message
     * Current Implementation: comma separated value
     *
     * @param priority
     * @param tag
     * @param message
     * @return
     */
    public static String format(int priority, @NonNull String tag, @NonNull String message) {
        return String.format("%d, %s, %s", priority, tag, message);
    }
}
