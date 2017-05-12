package cc.soham.timberutilssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cc.soham.timberutils.TimberWrapper;
import timber.log.Timber;

/**
 * TODO: add example for crashlytics
 * TODO: add example for updating debug logging based on build types
 * TODO: add example for updating file logging based on preferences
 * TODO: add example of user explicitly disabling logging
 * TODO: add example of zipping all files to share with customer care
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimberWrapper.initTimberLoggingLevel(this, true, true);
        Timber.w("hello!!!!!!!!!!!!!!!!!!!!!!!!");
        Timber.tag("MainActivity").d("hello %s", "world");
    }
}
