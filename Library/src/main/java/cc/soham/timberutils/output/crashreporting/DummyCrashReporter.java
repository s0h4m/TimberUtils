package cc.soham.timberutils.output.crashreporting;

/**
 * Created by sohammondal on 27/09/16.
 * Dummy implementation of Crash reporting (in case there is no specific implementation of CrashReporter_
 */
public class DummyCrashReporter implements CrashReporter {
    @Override
    public void logException(Exception exception) {

    }

    @Override
    public void log(int priority, String tag, String msg) {

    }

    @Override
    public void logException(Throwable throwable) {

    }

    @Override
    public void set(String key, boolean value) {

    }
}
