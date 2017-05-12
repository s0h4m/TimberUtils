package cc.soham.timberutils.output.crashreporting;

/**
 * Created by sohammondal on 27/09/16.
 * Interface that needs to be implemented by all build/flavour to enable crash reporting of any kind
 */
public interface CrashReporter {
    void logException(Exception exception);
    void log(int priority, String tag, String msg);
    void logException(Throwable throwable);
    void set(String key, boolean value);
}
