package com.example.masayuki.test3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AnoterhActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView text = new TextView(this);
        setContentView(text);

        // 送られてきたIntentから起動パラメータを取り出す
        // 送られたIntentを取得
        Intent intent = getIntent();
        // IntentからBundleを取り出す
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            // Bundleからデータを取り出す
            String string = bundle.getString("foobar");
            // getIntent().getString(key) と
            // intent.getStringExtra(key) は等価。
            // 取得するデータが少ないなら Bundle を
            // 取り出すのはメンドウ。

            text.setText(string);
        }
    }
}
