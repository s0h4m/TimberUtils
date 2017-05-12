package cc.soham.timberutils.output.crashreporting;

import android.support.annotation.NonNull;

import timber.log.Timber;

/**
 * Created by sohammondal on 27/09/16.
 * Our custom TimberTree that lets us output to a Crash Reporting tool
 */
public class CrashReporterTimberTree extends Timber.DebugTree {
    private CrashReporter crashReporter;

    public CrashReporterTimberTree(@NonNull final CrashReporter crashReporter) {
        if (crashReporter == null)
            throw new NullPointerException("Null Crash Reporter, could not initialize CrashReporterTimberTree");
        this.crashReporter = crashReporter;
    }

    public CrashReporter getCrashReporter() {
        return crashReporter;
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
        if (t != null) {
            getCrashReporter().logException(t);
        }
        if (tag != null && message != null)
            getCrashReporter().log(priority, System.currentTimeMillis() + ", " + tag, message);
    }
}
