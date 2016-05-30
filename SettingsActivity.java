/*
 * Copyright 2014 A.C.R. Development
 */
package co.zew.browser.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import co.zew.deebrowser.R;
import co.zew.browser.utils.PermissionsManager;

public class SettingsActivity extends ThemableSettingsActivity {

    private static final List<String> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this is a workaround for the Toolbar in PreferenceActitivty
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        LinearLayout content = (LinearLayout) root.getChildAt(0);
        LinearLayout toolbarContainer = (LinearLayout) View.inflate(this, R.layout.toolbar_settings, null);

        root.removeAllViews();
        toolbarContainer.addView(content);
        root.addView(toolbarContainer);

        // now we can set the Toolbar using AppCompatPreferenceActivity
        Toolbar toolbar = (Toolbar) toolbarContainer.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
        fragments.clear();
        for (Header header : target) {
            fragments.add(header.fragment);
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return fragments.contains(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // PermissionsManager.getInstance().notifyPermissionsChange(permissions);
        finish();
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
