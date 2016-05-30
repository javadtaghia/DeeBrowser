package co.zew.browser.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;
import android.widget.Toast;

import co.zew.deebrowser.R;
import butterknife.ButterKnife;
import co.zew.browser.app.BrowserApp;
import co.zew.browser.bus.BrowserEvents;
import co.zew.browser.constant.Constants;
import co.zew.browser.preference.PreferenceManager;
import co.zew.browser.utils.Utils;

@SuppressWarnings("deprecation")


public class IncognitoActivity extends BrowserActivity {

    @Override
    public void updateCookiePreference() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this);
        }
        cookieManager.setAcceptCookie(PreferenceManager.getInstance().getIncognitoCookiesEnabled());
    }

    @Override
    public synchronized void initializeTabs() {
        newTab(null, true);


    }

    @Override
    public void onResume(){
        super.onResume();


    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent iin = getIntent();
//            if(iin != null ) {
//
//                String source = iin.getDataString();
//                newTab(source,true);
//                Toast.makeText(this, "error: " + source.toString(), Toast.LENGTH_SHORT);
//
//
//            }
            handleNewIntent(iin, 0);
        } catch (Exception e) {
            Log.e("Intent error", e.toString());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.incognito, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleNewIntent(intent, 1);
        super.onNewIntent(intent);
    }

    void handleNewIntent(Intent intent, int type) {

        String url = null;
        boolean isread = false;
        if (intent != null) {
            if (type == 0) {
                int lam =1;
                Bundle b = intent.getExtras();
                url ="";// (String) b.get("readurl");
                if (url.isEmpty()) {
                    url = intent.getDataString();
                    lam =0;
                }

                if (url != null && lam ==1)
                    isread = true;
            }
            if (!isread) {
                url = intent.getDataString();
                Toast.makeText(this, "error: " + url.toString(), Toast.LENGTH_SHORT);
//                if(url == null){
//                    Bundle b = intent.getExtras();
//                url = (String) b.get("http");}

            }
            Log.e("Intent Extra:", url.toString());
        }
        int num = 0;
        String source = null;
        if (intent != null && intent.getExtras() != null && !isread) {
            num = intent.getExtras().getInt(getPackageName() + ".Origin");
            source = intent.getExtras().getString("SOURCE");
            Log.e("Intent Extra2:", url.toString());
        }


        try {
            if (intent.getAction().equals("android.intent.action.VIEW"))
                outsideactivity = true;
            else {
                outsideactivity = false;
            }
        } catch (Exception e) {
            outsideactivity = false;
        }

        if (num == 1) {
            loadUrlInCurrentView(url);
        } else if (url != null) {
            if (url.startsWith(Constants.FILE)) {
                Utils.showSnackbar(this, R.string.message_blocked_local);
                url = null;
            }
            if (url != null) {
                newTab(url, true);
            }


            //  mIsNewIntent = (source == null);
        }




    }

    private void loadUrlInCurrentView(final String url) {
        if (mCurrentView == null) {
            return;
        }

        mCurrentView.loadUrl(url);
        mEventBus.post(new BrowserEvents.CurrentPageUrl(url));
    }


    @Override
    protected void onPause() {
        super.onPause();
      //  mSearchAdapter.refreshBookmarks();
        // saveOpenTabs();
    }



    @Override
    public void updateHistory(String title, String url) {
//        mSearchAdapter.refreshBookmarks();
//        mSearchAdapter.notifyDataSetChanged();
       // addItemToHistory(title, url);
    }

    @Override
    public boolean isIncognito() {

        return true;
    }

//    @Override
//    public void onBackPressed() {
//        finish();
//    super.onBackPressed();
//    }

    @Override
    public void closeActivity() {
//mBookmarkPage.notifyAll();
//        mSearchAdapter.mAllBookmarks.clear();
//        mSearchAdapter.mAllBookmarks.addAll(mBookmarkManager.getAllBookmarks(true));
        //moveTaskToBack(true);
      // closeDrawers();
//        if (!isIncognito())

          finish();
        // closeDrawers();
      //  finish();
    }
}
