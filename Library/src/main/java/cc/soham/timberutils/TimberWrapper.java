package cc.soham.timberutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import cc.soham.timberutils.output.crashreporting.CrashReporter;
import cc.soham.timberutils.output.crashreporting.CrashReporterTimberTree;
import cc.soham.timberutils.output.file.FileTimberTree;
import cc.soham.timberutils.output.file.FileWriterWrapper;
import timber.log.Timber;

/**
 * Created by sohammondal on 21/08/16.
 * Manages various Timber related activities
 * - Stores/Retrieves/Manages the state of debug and file logging (two types of logging currently)
 * - Handles initialisation (and re-initialisation) of Timber (planting the right trees)
 * - Handles calls from Settings/Preferences to store and update the individual (debug:on/off + file:on/off)
 * states
 */
public class TimberWrapper {
    // by default this product flavor + build type will log to file
    public static final boolean DEFAULT_LOGGING_LEVEL_DEBUG = false;
    public static final boolean DEFAULT_LOGGING_LEVEL_FILE = false;
    // this is the default logging state for this product_flavor+build_type for Debug logging
    public static boolean defaultDebugLoggingStateForFlavorBuild = false;
    // this is the default logging state for this product_flavor+build_type for File logging
    public static boolean defaultFileLoggingStateForFlavorBuild = false;
    // the key for storing the "overall timber logging state" in SharedPreferences
    public static final String PREF_TIMBER_LOGGING_DEBUG = "logging_level_debug";
    public static final String PREF_TIMBER_LOGGING_FILE = "logging_level_file";
    public static final String PREF_USER_EXPLICIT = "logging_user_explicit_decision_made";
    public static final String KEY_BOOLEAN_CRASH_REPORTING_LOGGING_ENABLED = "crash_reporting_logging";

    public static Timber.DebugTree debugTree;
    public static FileTimberTree fileTimberTree;
    public static CrashReporterTimberTree crashReporterTimberTree;

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     * @param defaultDebugLoggingLevel this is the default debug logging state for this product_flavor+build_type
     */
    public static void initTimberLoggingLevel(@NonNull final Context context, boolean defaultDebugLoggingLevel) {
        initTimberLoggingLevel(context, PreferenceManager.getDefaultSharedPreferences(context), null, defaultDebugLoggingLevel, defaultFileLoggingStateForFlavorBuild);
    }

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     * @param defaultDebugLoggingLevel this is the default debug logging state for this product_flavor+build_type
     */
    public static void initTimberLoggingLevel(@NonNull final Context context, @Nullable CrashReporter crashReporter, boolean defaultDebugLoggingLevel) {
        initTimberLoggingLevel(context, PreferenceManager.getDefaultSharedPreferences(context), crashReporter, defaultDebugLoggingLevel, DEFAULT_LOGGING_LEVEL_FILE);
    }

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     * @param defaultDebugLoggingLevel this is the default debug logging state for this product_flavor+build_type
     * @param defaultFileLoggingLevel  this is the default file logging state for this product_flavor+build_type
     */
    public static void initTimberLoggingLevel(@NonNull final Context context, boolean defaultDebugLoggingLevel, boolean defaultFileLoggingLevel) {
        initTimberLoggingLevel(context, PreferenceManager.getDefaultSharedPreferences(context), null, defaultDebugLoggingLevel, defaultFileLoggingLevel);
    }

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     * @param crashReporter            a {@link CrashReporter} (OPTIONAL)
     * @param defaultDebugLoggingLevel this is the default debug logging state for this product_flavor+build_type
     * @param defaultFileLoggingLevel  this is the default file logging state for this product_flavor+build_type
     */
    public static void initTimberLoggingLevel(@NonNull final Context context, @Nullable CrashReporter crashReporter, boolean defaultDebugLoggingLevel, boolean defaultFileLoggingLevel) {
        initTimberLoggingLevel(context, PreferenceManager.getDefaultSharedPreferences(context), crashReporter, defaultDebugLoggingLevel, defaultFileLoggingLevel);
    }

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     */
    public static void initTimberLoggingLevel(@NonNull final Context context,  @Nullable CrashReporter crashReporter) {
        initTimberLoggingLevel(context, PreferenceManager.getDefaultSharedPreferences(context), crashReporter, DEFAULT_LOGGING_LEVEL_DEBUG, DEFAULT_LOGGING_LEVEL_FILE);
    }

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     */
    public static void initTimberLoggingLevel(@NonNull final Context context) {
        initTimberLoggingLevel(context, PreferenceManager.getDefaultSharedPreferences(context), null, DEFAULT_LOGGING_LEVEL_DEBUG, DEFAULT_LOGGING_LEVEL_FILE);
    }

    /**
     * Changes the state of debugging to the one in sharedPreferences
     * Typically called by InitUtils to initialise Timber with a stored logging level
     *
     * @param context
     * @param sharedPreferences        use shared preferences to load the stored logging state
     * @param crashReporter            a {@link CrashReporter} (OPTIONAL)
     * @param defaultDebugLoggingLevel this is the default debug logging state for this product_flavor+build_type
     * @param defaultFileLoggingLevel  this is the default file logging state for this product_flavor+build_type
     */
    private static void initTimberLoggingLevel(@NonNull final Context context, @NonNull final SharedPreferences sharedPreferences, @Nullable CrashReporter crashReporter, boolean defaultDebugLoggingLevel, boolean defaultFileLoggingLevel) {
        // store the default logging level
        defaultDebugLoggingStateForFlavorBuild = defaultDebugLoggingLevel;
        defaultFileLoggingStateForFlavorBuild = defaultFileLoggingLevel;
        // remove the current trees in Timber if added
        removeAllTimberWrapperTrees(context);
        // get the state
        boolean debugState = getCurrentDebugLoggingState(sharedPreferences);
        boolean fileState = getCurrentFileLoggingState(sharedPreferences);
        updateTimberDebugLoggingLevel(debugState);
        updateTimberFileLoggingLevel(context, fileState);
        updateTimberCrashReportingLoggingLevel(fileState, crashReporter);
    }

    /**
     * Called when the user has explicitly switched on/off file logging in Preferences for the first time
     * If the user has not made any decision here, the app has to enable logging automatically for alpha/beta users for example
     *
     * @param context
     */
    public static void updateUserExplicitDecisionFirstTime(@NonNull final Context context) {
        updateUserExplicitDecisionFirstTime(PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Called when the user has explicitly switched on/off file logging in Preferences for the first time
     * If the user has not made any decision here, the app has to enable logging automatically for alpha/beta users for example
     *
     * @param sharedPreferences
     */
    private static void updateUserExplicitDecisionFirstTime(@NonNull final SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_USER_EXPLICIT, true);
        editor.apply();
    }

    /**
     *
     * @param context
     * @return
     */
    public static boolean getUserExplicitDecisionFirstTime(@NonNull final Context context) {
        return getUserExplicitDecisionFirstTime(PreferenceManager.getDefaultSharedPreferences(context));
    }

    private static boolean getUserExplicitDecisionFirstTime(@NonNull final SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(PREF_USER_EXPLICIT, false);
    }

    /**
     * Changes the state of debug logging to the one in params
     *
     * @param debugLoggingLevel
     */
    private static void updateTimberDebugLoggingLevel(boolean debugLoggingLevel) {
        if (debugLoggingLevel)
            plantDebugTree();
    }

    /**
     * Changes the state of file logging to the one in params
     *
     * @param context
     * @param fileLoggingLevel
     */
    private static void updateTimberFileLoggingLevel(@NonNull Context context, boolean fileLoggingLevel) {
        if (fileLoggingLevel)
            plantFileTree(context);
    }

    /**
     * Changes the state of file logging to the one in params
     *
     * @param fileLoggingLevel
     * @param crashReporter
     */
    private static void updateTimberCrashReportingLoggingLevel(boolean fileLoggingLevel, final CrashReporter crashReporter) {
        if (fileLoggingLevel)
            plantCrashReportingTree(crashReporter);
    }

    /**
     * Plant a debug tree
     */
    private static void plantDebugTree() {
        debugTree = new Timber.DebugTree();
        Timber.plant(debugTree);
    }

    /**
     * Plant a file tree
     *
     * @param context
     */
    private static void plantFileTree(@NonNull final Context context) {
        fileTimberTree = new FileTimberTree(context);
        Timber.plant(fileTimberTree);
    }

    /**
     * Plant a crash reporting tree
     *
     * @param crashReporter
     */
    private static void plantCrashReportingTree(@Nullable final CrashReporter crashReporter) {
        if (crashReporter != null) {
            crashReporterTimberTree = new CrashReporterTimberTree(crashReporter);
            Timber.plant(crashReporterTimberTree);
            crashReporter.set(KEY_BOOLEAN_CRASH_REPORTING_LOGGING_ENABLED, true);
        }
    }

    /**
     * Uproots a debug tree
     */
    private static void uprootDebugTree() {
        if (debugTree != null) {
            Timber.uproot(debugTree);
            debugTree = null;
        }
    }

    /**
     * Uproots a {@link FileTimberTree}
     */
    private static void uprootFileTimberTree() {
        if (fileTimberTree != null) {
            Timber.uproot(fileTimberTree);
            fileTimberTree = null;
        }
    }

    /**
     * Uproots a {@link CrashReporterTimberTree}
     */
    private static void uprootCrashReporterTimberTree() {
        if (crashReporterTimberTree != null) {
            Timber.uproot(crashReporterTimberTree);
            crashReporterTimberTree = null;
        }
    }

    /**
     * Remove all current trees and reset {@link FileWriterWrapper}
     *
     * @param context
     */
    private static void removeAllTimberWrapperTrees(@NonNull final Context context) {
        if (Timber.treeCount() > 0) {
            uprootDebugTree();
            uprootFileTimberTree();
            clearFileWriterWrapperWhenApplicable(context);
        }
    }

    /**
     * Clears FileWriterWrapper in certain conditions
     *
     * @param context
     */
    private static void clearFileWriterWrapperWhenApplicable(@NonNull final Context context) {
        try {
            // flush and close only when it has never been initialized before
            if (FileWriterWrapper.fileWriterWrapper != null) {
                FileWriterWrapper.getFileWriterWrapper(context).flushAndCloseFileWriter();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the debug logging state
     *
     * @param context
     * @param state
     */
    public static void storeDebugLoggingState(@NonNull final Context context, boolean state) {
        // store the state
        storeDebugLoggingState(PreferenceManager.getDefaultSharedPreferences(context), state);
    }

    /**
     * Stores the debug logging state
     *
     * @param sharedPreferences
     * @param state
     */
    private static void storeDebugLoggingState(@NonNull SharedPreferences sharedPreferences, boolean state) {
        // store the state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_TIMBER_LOGGING_DEBUG, state);
        editor.commit();
    }

    /**
     * Stores the file logging state
     *
     * @param context
     * @param state
     */
    public static void storeFileLoggingState(@NonNull final Context context, boolean state) {
        // store the state
        storeFileLoggingState(PreferenceManager.getDefaultSharedPreferences(context), state);
    }

    /**
     * Stores the file logging state
     *
     * @param sharedPreferences
     * @param state
     */
    private static void storeFileLoggingState(@NonNull SharedPreferences sharedPreferences, boolean state) {
        // store the state
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_TIMBER_LOGGING_FILE, state);
        editor.commit();
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param context
     * @param defaultDebugState
     * @return
     */
    public static boolean getCurrentDebugLoggingState(@NonNull final Context context, boolean defaultDebugState) {
        return getCurrentDebugLoggingState(context, defaultDebugState);
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param sharedPreferences
     * @param defaultDebugState
     * @return
     */
    private static boolean getCurrentDebugLoggingState(SharedPreferences sharedPreferences, boolean defaultDebugState) {
        return sharedPreferences.getBoolean(PREF_TIMBER_LOGGING_DEBUG, defaultDebugState);
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param context
     * @param defaultFileState
     * @return
     */
    public static boolean getCurrentFileLoggingState(@NonNull final Context context, boolean defaultFileState) {
        return getCurrentFileLoggingState(PreferenceManager.getDefaultSharedPreferences(context), defaultFileState);
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param sharedPreferences
     * @return
     */
    private static boolean getCurrentFileLoggingState(SharedPreferences sharedPreferences, boolean defaultFileState) {
        return sharedPreferences.getBoolean(PREF_TIMBER_LOGGING_FILE, defaultFileState);
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param context
     * @return
     */
    public static boolean getCurrentDebugLoggingState(@NonNull final Context context) {
        return getCurrentDebugLoggingState(PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param sharedPreferences
     * @return
     */
    private static boolean getCurrentDebugLoggingState(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(PREF_TIMBER_LOGGING_DEBUG, defaultDebugLoggingStateForFlavorBuild);
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param context
     * @return
     */
    public static boolean getCurrentFileLoggingState(@NonNull final Context context) {
        return getCurrentFileLoggingState(PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Returns the current state of Debug Timber Logging
     *
     * @param sharedPreferences
     * @return
     */
    private static boolean getCurrentFileLoggingState(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(PREF_TIMBER_LOGGING_FILE, defaultFileLoggingStateForFlavorBuild);
    }

    /**
     * Called by a Settings/Preferences element to just update the value of the debug logs (in console)
     * This will check the OVERALL state and based on the new debug log state, it will
     * a) calculate if we need to make any changes
     * b) store the new overall state in SharedPreferences
     * c) apply the debug logging state change in Timber
     *
     * @param context
     * @param debugLoggingEnabled the new state of Debug logging (is debug logging enabled or not)
     */
    public static void updateDebugLoggingState(@NonNull final Context context, boolean debugLoggingEnabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        updateDebugLoggingState(sharedPreferences, debugLoggingEnabled);
    }

    /**
     * Called by a Settings/Preferences element to just update the value of the debug logs (in console)
     * This will check the OVERALL state and based on the new debug log state, it will
     * a) calculate if we need to make any changes
     * b) store the new overall state in SharedPreferences
     * c) apply the debug logging state change in Timber
     *
     * @param sharedPreferences
     * @param debugLoggingEnabled the new state of Debug logging (is debug logging enabled or not)
     */
    private static void updateDebugLoggingState(final SharedPreferences sharedPreferences, boolean debugLoggingEnabled) {
        boolean isDebugLoggingEnabledCurrently = getCurrentDebugLoggingState(sharedPreferences);
        if (isDebugLoggingEnabledCurrently) {
            if (!debugLoggingEnabled) {
                // currently debug logging is enabled, but we need to disable it
                // store the disabled state
                storeDebugLoggingState(sharedPreferences, debugLoggingEnabled);
                // uproot the debug tree
                uprootDebugTree();
            }
        } else {
            if (debugLoggingEnabled) {
                // currently debug logging is not enabled, but we need to enable it
                // store the enabled state
                storeDebugLoggingState(sharedPreferences, debugLoggingEnabled);
                // plan the debug tree
                plantDebugTree();
            }
        }
    }

    /**
     * Called by a Settings/Preferences element to just update the value of the file logs (in file)
     * This will check the OVERALL state and based on the new file log state, it will
     * a) calculate if we need to make any changes
     * b) store the new overall state in SharedPreferences
     * c) apply the file logging state change in Timber
     *
     * @param context
     * @param crashReporter
     * @param fileLoggingEnabled the new state of File logging (is file logging enabled or not)
     */
    public static void updateFileLoggingState(@NonNull final Context context, final CrashReporter crashReporter, boolean fileLoggingEnabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        updateFileLoggingState(context, sharedPreferences, crashReporter, fileLoggingEnabled);
    }

    /**
     * Called by a Settings/Preferences element to just update the value of the file logs (in file)
     * This will check the OVERALL state and based on the new file log state, it will
     * a) calculate if we need to make any changes
     * b) store the new overall state in SharedPreferences
     * c) apply the file logging state change in Timber
     *
     * @param context
     * @param fileLoggingEnabled the new state of File logging (is file logging enabled or not)
     */
    public static void updateFileLoggingState(@NonNull final Context context, boolean fileLoggingEnabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        updateFileLoggingState(context, sharedPreferences, null, fileLoggingEnabled);
    }

    /**
     * Called by a Settings/Preferences element to just update the value of the file logs (in file)
     * This will check the OVERALL state and based on the new file log state, it will
     * a) calculate if we need to make any changes
     * b) store the new overall state in SharedPreferences
     * c) apply the file logging state change in Timber
     *
     * @param context
     * @param sharedPreferences
     * @param fileLoggingEnabled the new state of File logging (is file logging enabled or not)
     */
    private static void updateFileLoggingState(@NonNull final Context context, final SharedPreferences sharedPreferences, final CrashReporter crashReporter, boolean fileLoggingEnabled) {
        boolean isFileLoggingEnabledCurrently = getCurrentFileLoggingState(sharedPreferences);
        if (isFileLoggingEnabledCurrently) {
            if (!fileLoggingEnabled) {
                // currently file logging is enabled, but we need to disable it
                // store the disabled state
                storeFileLoggingState(sharedPreferences, fileLoggingEnabled);
                // uproot the file tree
                uprootFileTimberTree();
                // uproot the crash reporting tree
                uprootCrashReporterTimberTree();
            }
        } else {
            if (fileLoggingEnabled) {
                // currently file logging is not enabled, but we need to enable it
                // store the enabled state
                storeFileLoggingState(sharedPreferences, fileLoggingEnabled);
                // plant the file tree
                plantFileTree(context);
                // plant the crash reporting tree
                plantCrashReportingTree(crashReporter);
            }
        }
    }
}