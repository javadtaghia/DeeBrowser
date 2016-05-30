package co.zew.browser.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.zew.deebrowser.R;
import co.zew.browser.constant.Constants;
import co.zew.browser.offline.Database;
import co.zew.browser.preference.PreferenceManager;
import co.zew.browser.reading.HtmlFetcher;
import co.zew.browser.reading.JResult;
import co.zew.browser.utils.ThemeUtils;
import co.zew.browser.utils.Utils;
import co.zew.browser.utils.WebUtils;

public class ReadingActivity extends AppCompatActivity {

    private TextView mTitle;
    private WebView mBody;
    private boolean mInvert;
    private String mUrl = null;
    private PreferenceManager mPreferences;
    private int mTextSize;
    private ProgressDialog mProgressDialog;
    private Button zewbtn;
    private  int isdark;
    private static final int XXLARGE = 200;
    private static final int XLARGE = 170;
    private static final int LARGE = 120;
    private static final int MEDIUM = 100;
    private static final int SMALL = 50;
    private static final int XSMALL = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out_scale);
        mPreferences = PreferenceManager.getInstance();
        mInvert = mPreferences.getInvertColors();
        final int color;
        if (mInvert) {
            setTheme(R.style.Theme_SettingsTheme_Black);
            color = ThemeUtils.getPrimaryColorDark(this);
            getWindow().setBackgroundDrawable(new ColorDrawable(color));
           isdark = 1;
        } else {
            isdark = 0;
            setTheme(R.style.Theme_SettingsTheme);
            color = ThemeUtils.getPrimaryColor(this);
            getWindow().setBackgroundDrawable(new ColorDrawable(color));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_view);




//
//        zewbtn = (Button)findViewById(R.id.zewbrowser);
//        zewbtn.setText("hi");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//       toolbar.setNavigationIcon(R.drawable.ic_launcher);
        toolbar.setTitle(""); //✂
        toolbar.setSubtitle("");
//        getActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        mTitle = (TextView) findViewById(R.id.textViewTitle);
        mBody = (WebView) findViewById(R.id.textViewBody);

        //mBody.setWebClient(new myweb());



        mTextSize = mPreferences.getReadingTextSize();
        mBody.getSettings().setTextZoom(getTextSize(mTextSize));
        mTitle.setText(getString(R.string.untitled));
        //  mBody.setText(getString(R.string.loading));

        mTitle.setVisibility(View.INVISIBLE);
        mBody.setVisibility(View.INVISIBLE);




        Intent intent = getIntent();
        if (!loadPage(intent)) {
            ArrayList<String> a = new ArrayList<>();
            a.add(getString(R.string.loading_failed));
            setText(getString(R.string.untitled),a );
        }
    }



    private static int getTextSize(int size) {
        switch (size) {
            case 0:
                return XSMALL;
            case 1:
                return SMALL;
            case 2:
                return MEDIUM;
            case 3:
                return LARGE;
            case 4:
                return XLARGE;
            case 5:
                return XXLARGE;
            default:
                return MEDIUM;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reading, menu);
        MenuItem invert = menu.findItem(R.id.invert_item);
        MenuItem textSize = menu.findItem(R.id.text_size_item);
        int iconColor = mInvert ? ThemeUtils.getIconDarkThemeColor(this) : ThemeUtils.getIconLightThemeColor(this);
        if (invert != null && invert.getIcon() != null)
            invert.getIcon().setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        if (textSize != null && textSize.getIcon() != null)
            textSize.getIcon().setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean loadPage(Intent intent) {
        if (intent == null) {
            return false;
        }
        mUrl = intent.getStringExtra(Constants.LOAD_READING_URL);
        if (mUrl == null) {
            return false;
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(Utils.getDomainName(mUrl));
        new PageLoader(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
        return true;
    }

    private class PageLoader extends AsyncTask<String, Void, Void> {

        private final Activity mActivity;
        private String mTitleText;
       // private String [] mBodyText;
        ArrayList<String> mBodyText = new ArrayList<>();

        public PageLoader(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage(mActivity.getString(R.string.loading));
            mProgressDialog.show();
        }
        private boolean isValidUrl(String url) {
            try {
                Pattern p = Patterns.WEB_URL;
                Matcher m = p.matcher(url);
                if (m.matches())
                    return true;
                else
                    return false;
            } catch (Exception ex) {
                // Toast.makeText(this, "error: " + ex.toString(), Toast.LENGTH_SHORT);
                return false;
            }
        }
        @Override
        protected Void doInBackground(String... params) {

            HtmlFetcher fetcher = new HtmlFetcher();
            try {
               // JResult result= fetcher.fetchAndExtract(params[0], 4500, true);

                //int a =1;
                if(true/*result.getText().isEmpty() || result.getText().length() < 5*/){
                    try {
                        if(isValidUrl(mUrl)){
                           // Document doc = Jsoup.connect(mUrl).userAgent("desktop").get();
                            Document doc= Jsoup.parse(new URL(mUrl).openStream(), "UTF-8", mUrl);

                            Elements paragraphs = doc.select("h1,h2,h3,h4,h5,h6,p");
                            for(Element p : paragraphs){
                                String myst = p.html();
                                URI uri = new URI(mUrl);
                                String domain = uri.getHost();
                                String mydom = domain.startsWith("www.") ? domain.substring(4) : domain;
                                mydom = mydom.trim();
                                String myh = "";
                                if(mUrl.contains("https://"))
                                    myh = "https://";
                                else
                                myh = "http://";

//                                Matcher m = Pattern.compile(
//                                        Pattern.quote("href=")
//                                                + "(.*?)"
//                                                + Pattern.quote("</a>")
//                                ).matcher(myst);
//                                while(m.find()){
//                                    String match = m.group(1);
//                                    myst = myst.replaceAll("<a","");
//                                    myst = myst.replaceAll("</a>","");
//                                    myst = myst.replaceAll("href=\"/", "<a href="+myh+mydom+"/"+m.group(1).trim()+"</a>");
//                                }
                                   myst = myst.replaceAll("href=\"/", "href=\""+myh+mydom+"/");




                                int i = 1;
                                if (i == 1)
                                    Log.e("9999", myst.toString());
                                mBodyText.add(myst);
                            }
//                            Elements images = doc.select("img");
//                            for(Element ps : images){
//                                mBodyText.add("<img src=" + ps.absUrl("src") + ">");}



                            mTitleText = doc.title().toString();}
                        else
                        {
                            mTitleText = "Invalid Url !";
                        }
                    }
                    catch (Exception ex) {
                        mTitleText = "Invalid Url !";

                    }

                    if (mTitleText == "Invalid Url !" ) {
                        String url = mUrl.replaceAll("file:///", "/");
                        File in = new File(url);
                        try {
                            Document doc = Jsoup.parse(in, "UTF-8");
                            mTitleText = "Invalid File/URL!";
//                            if(doc!=null) {
//                                mBodyText.add(doc.title() + "  " + doc.html());
//                            }
                            if(doc!=null  ){
                                Elements paragraphs = doc.select("h1,h2,h3,h4,h5,h6,p");
//                                Elements images = doc.select("img");
//                                for(Element ps : images){
//                                    mBodyText.add("<img  src="+  "/ " + ps.toString() + ">");}

                                for(Element p : paragraphs){

                                    int i =1;
                                    if (i == 1){
                                        Log.e("9999", p.html().toString());}
                                    mBodyText.add(p.html());}

                            mTitleText = doc.title().toString();}
                        }
                        catch(Exception e) {
                            mTitleText = "Invalid File:"+ " "+ mUrl.toString();
                        }
                    }


                }
                else {
                    //mTitleText = result.getTitle();
                    //mBodyText.add(result.getHtml());
                }
            } catch (Exception e) {
                mTitleText = "";
                mBodyText.add("");
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                System.gc();
                mTitleText = "";
                mBodyText.add("");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            try {
                if (mTitleText.isEmpty() && mBodyText.isEmpty()) {
                    ArrayList<String> a = new ArrayList<>();
                    a.add(getString(R.string.loading_failed));
                     setText(getString(R.string.untitled),a );

                } else {
                    setText(mTitleText,  mBodyText);
                }
            }
            catch (Exception ex) {
                Log.e("bad array", ex.toString());}
            super.onPostExecute(result);
        }

    }

    private void setText(String title,  ArrayList<String> body) {
        if (mTitle == null || mBody == null)
            return;
        if (mTitle.getVisibility() == View.INVISIBLE) {
            mTitle.setAlpha(0.0f);
            mTitle.setVisibility(View.INVISIBLE);
            mTitle.setText(title);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mTitle, "alpha", 1.0f);
            animator.setDuration(300);
            animator.start();
        } else {
            mTitle.setText(title);
        }
        mTitle.setVisibility(View.INVISIBLE);
        if (mBody.getVisibility() == View.INVISIBLE) {
            mBody.setAlpha(0.0f);
            mBody.setVisibility(View.VISIBLE);
            String sb = "<html lang=\"fa\" id=\"responsive-news\" prefix=\"og: http://ogp.me/ns#\"><head dir=\"rtl\"> <meta charset=\"utf-8\"></head><body>";
            String bodytex = "<h2>" + title + "</h2>";
            for(String p : body){

                bodytex = bodytex +  p + "<p> " + " </p>";

            }
          sb = sb + bodytex;
            sb = sb + "</body></html>";
if(isdark==1)
{
    Paint mPaint = new Paint();
    float[] mNegativeColorArray = {
            -1.0f, 0, 0, 0, 255, // red
            0, -1.0f, 0, 0, 255, // green
            0, 0, -1.0f, 0, 255, // blue
            0, 0, 0, 1.0f, 0 // alpha
    };

    ColorMatrixColorFilter filterInvert = new ColorMatrixColorFilter(mNegativeColorArray);
    mPaint.setColorFilter(filterInvert);
    try {
        mBody.setLayerType(View.LAYER_TYPE_HARDWARE, mPaint);
    }
    catch (Exception ex) {Log.e("renprob:", ex.toString());}
}

            WebSettings websetting = mBody.getSettings();

            websetting.setDefaultTextEncodingName("utf-8");




            mBody.loadData(sb, "text/html; charset=utf-8", null);


//            mBody.getSettings().supportZoom();
//            mBody.getSettings().setBuiltInZoomControls(false);
//            mBody.getSettings().setDisplayZoomControls(true);

            //mBody.setText(body);
            ObjectAnimator animator = ObjectAnimator.ofFloat(mBody, "alpha", 1.0f);
            animator.setDuration(300);
            animator.start();
        } else {
            //mBody.setText(body);

            StringBuilder sb = new StringBuilder("<html><body>");
            sb.append(body);
            sb.append("</body></html>");


            mBody.loadData(sb.toString(), "text/html", "UTF-8");

        }
    }


    @Override
    protected void onDestroy() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(R.anim.fade_in_scale, R.anim.slide_out_to_right);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.zew_item:
                Intent read = new Intent(this, FullscreenActivity.class);
                read.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                read.putExtra("readurl", mUrl);
                startActivity(read);
               // finish();
                break;
            case R.id.invert_item:
                mPreferences.setInvertColors(!mInvert);
                Intent read2 = new Intent(this, ReadingActivity.class);
                read2.putExtra(Constants.LOAD_READING_URL, mUrl);
                startActivity(read2);
                finish();
                break;
            case R.id.text_size_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View view = inflater.inflate(R.layout.seek_layout, null);
                final SeekBar bar = (SeekBar) view.findViewById(R.id.text_size_seekbar);
                bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar view, int size, boolean user) {
                        mBody.getSettings().setTextZoom(getTextSize(size));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar arg0) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar arg0) {
                    }

                });
                bar.setMax(5);
                bar.setProgress(mTextSize);
                builder.setView(view);
                builder.setTitle(R.string.size);


                builder.setPositiveButton(android.R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mTextSize = bar.getProgress();
                        mBody.getSettings().setTextZoom(getTextSize(mTextSize));
                        mPreferences.setReadingTextSize(bar.getProgress());
                    }

                });
                builder.show();
                break;
            default:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
