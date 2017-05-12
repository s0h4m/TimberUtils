package cc.soham.timberutilssample;

import com.crashlytics.android.Crashlytics;
import cc.soham.timberutils.output.crashreporting.CrashReporter;

/**
 * Created by sohammondal on 27/09/16.
 * An implementation of {@link CrashReporter} that logs to {@link Crashlytics}
 * We must initialize {@link Crashlytics} before creating an instance of this to make sure
 * crashlytics is initialized before we start logging!
 * {@link CrashlyticsCrashReporter}
 */
public class CrashlyticsCrashReporter implements CrashReporter {
    @Override
    public void logException(Exception exception) {
        Crashlytics.logException(exception);
    }

    @Override
    public void log(int priority, String tag, String msg) {
        Crashlytics.log(msg);
    }

    @Override
    public void logException(Throwable throwable) {
        Crashlytics.logException(throwable);
    }

    @Override
    public void set(String key, boolean value) {
        if (key != null)
            Crashlytics.setBool(key, value);
    }
}
