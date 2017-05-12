package cc.soham.timberutilssample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cc.soham.timberutils.TimberWrapper;
import timber.log.Timber;

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
