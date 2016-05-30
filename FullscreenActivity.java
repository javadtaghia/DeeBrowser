package co.zew.browser.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.RatingCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.zew.deebrowser.R;
import co.zew.browser.reading.HtmlFetcher;
import co.zew.browser.reading.JResult;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    int i = 0;
    private Timer mytimer;
    String hidTextr = "";
    ProgressBar prog;
    TextView progressval;
    int fontsize = 50;
    SeekBar seek;
    SeekBar seekfont;
    SeekBar seekword;
    SeekBar seekspeed;
    String edittextTime = "400";
    String nofwords = "0";
    String[] myStrA;
    long val = 1;
    long valt;
    int juststated = 1;
    long valtime = 500;
    int ishideflag = 1;
    int ispaste = -1;
    int isword = 1;
    Intent myint;
    String browsedText = "";
    Document doc;
    String oldurl = "firsttime";
    int wordval = 0;
    int selectedColorRT1 = 255;
    int selectedColorGT1 = 0;
    int selectedColorBT1 = 0;
    int selectedColorRT2 = 0;
    int selectedColorGT2 = 0;
    int selectedColorBT2 = 0;
    int selectedColorRB = 255;
    int selectedColorGB = 255;
    int selectedColorBB = 255; // #C2CBC4
    FrameLayout fl;
    TextView t;
    TextView t2;
    TextView t3;
    TextView t4;
    TextView pt;
    TextView td, tw;

    private ProgressDialog mProgressDialog;


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;

    private View mContentView;
    private View mControlsView;
    private boolean mVisible;
    private Button buttonp;
    private Button buttonm;

    String textSource = "http://www.google.com";
    TextView hidtext;
    TextView fulltext;
    final Context context = this;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//            builder1.setMessage("Zew Browser");
//            builder1.setCancelable(true);
//            builder1.setPositiveButton("Quit",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            //put your code that needed to be executed when okay is clicked
//                            dialog.cancel();
//                            //ishideflag = 0;
//                           // moveTaskToBack(true);
//                            finish();
//                        }
//                    });
//            builder1.setNeutralButton("Back",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            //put your code that needed to be executed when okay is clicked
//                            dialog.cancel();
//
//                        }
//                    });
//            builder1.setNegativeButton("Pause",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            ishideflag = 0;
//                            moveTaskToBack(true);
//                            // finish();
//                        }
//                    });
//
//            AlertDialog alert11 = builder1.create();
//            alert11.show();
//            return true;
//        }
//        return false;
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        else
            return  false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            //SharedPreferences.Editor editor = settings.edit();
            wordval = settings.getInt("Saveswords", 2);
            seekword = (SeekBar) findViewById(R.id.seekword);
            seekword.setProgress(wordval);
            fontsize = settings.getInt("Savefont", 50);
            seekfont = (SeekBar) findViewById(R.id.seekfont);
            seekfont.setProgress(fontsize);
            fulltext.setTextSize(fontsize);
            hidTextr = settings.getString("Savefulltext", "DeeBrowser!");
            juststated = 1;
            seek = (SeekBar) findViewById(R.id.seekBar);
            int seekval = settings.getInt("Saveseekval", 0);
            int seekmax = settings.getInt("Saveseekmax", 0);
            seek.setProgress(seekval);
            seek.setMax(seekmax);
            i = seekval;
            progressval = (TextView) findViewById(R.id.progressval);
            progressval.setText("  " + Integer.toString(seekval) + " / " + Integer.toString(seekmax));
            int myspeed = settings.getInt("Savespeed", 787);
            edittextTime = Integer.toString(myspeed);
            seekspeed = (SeekBar) findViewById(R.id.seekspeed);
            seekspeed.setProgress(myspeed);

            selectedColorRT1 = settings.getInt("selectedColorRT1", 255);
            selectedColorGT1 = settings.getInt("selectedColorGT1", 0);
            selectedColorBT1 = settings.getInt("selectedColorBT1", 0);
            selectedColorRT2 = settings.getInt("selectedColorRT2", 0);
            selectedColorGT2 = settings.getInt("selectedColorGT2", 0);
            selectedColorBT2 = settings.getInt("selectedColorBT2", 0);
            selectedColorRB  = settings.getInt("selectedColorRB", 255);
            selectedColorGB  = settings.getInt("selectedColorGB", 255);
            selectedColorBB  = settings.getInt("selectedColorBB", 255);// #C2CBC4



            t = (TextView) findViewById(R.id.textView);
            t2 = (TextView) findViewById(R.id.textView2);
            td = (TextView) findViewById(R.id.textdelay);
            tw = (TextView) findViewById(R.id.textword);
            t3 = (TextView) findViewById(R.id.textView3);
            t4 = (TextView) findViewById(R.id.textView4);
            pt = (TextView) findViewById(R.id.progressval);
            fulltext = (TextView) findViewById(R.id.fullscreen_content); // output

            td.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            tw.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            t.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            t2.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            t3.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            t4.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            pt.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            fulltext.setBackgroundColor(Color.rgb(selectedColorRB, selectedColorGB, selectedColorBB));
            fulltext.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            final Button textcolor = (Button) findViewById(R.id.textcolor);
            final Button textcolor2 = (Button) findViewById(R.id.textcolor2);
            final Button backcolor = (Button) findViewById(R.id.backcolor);

            textcolor.setBackgroundColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
            textcolor2.setBackgroundColor(Color.rgb(selectedColorRT2, selectedColorGT2, selectedColorBT2));
            backcolor.setBackgroundColor(Color.rgb(selectedColorRB, selectedColorGB, selectedColorBB));


            Button buttonplay = (Button) findViewById(R.id.pPlay);

            buttonplay.performClick();
        } catch (Exception ex) {
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Retrieve data in the intent
        String browsedTextnew = "";
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                browsedTextnew = data.getStringExtra("fileSelected");
            }
        }
//        else if (requestCode == 1) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                String userInput = data.getStringExtra("webselect");
//                oldurl = userInput;
//                userInput = userInput.replaceAll("http://", "");
//                userInput = userInput.replaceAll("https://", "");
//                if (isValidUrl("http://" + userInput.toString())) {
//                    textSource = "http://" + userInput.toString();
////                    fulltext.setText("Wait...");
//                    Toast.makeText(context, "url: " + textSource.toString(), Toast.LENGTH_SHORT)
//                            .show();
//                    new MyTask().execute();
//                } else {
//                    Toast.makeText(context, userInput + " is not a valid url ! ", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            }
//            else {
//                // Toast.makeText(context, "ErrorI ! ", Toast.LENGTH_SHORT)
//                //       .show();
//            }
//        }
        else {
            // Toast.makeText(context, "ErrorII ! ", Toast.LENGTH_SHORT)
            //    .show();
        }


        if (browsedText != browsedTextnew) {
            browsedText = browsedTextnew;

            File sdcard = Environment.getExternalStorageDirectory();
          if (browsedText.contains(".txt")) {
//                Toast.makeText(this, "Reading: " + browsedTextnew + " ...", Toast.LENGTH_SHORT)
//                        .show();
//Get the text file
              browsedText = browsedText.replace("/sdcard/", "");
              File file = new File(sdcard, browsedText);
//Read text from file
                StringBuilder text = new StringBuilder();
                try {
                        Document doc = Jsoup.parse(file, "UTF-8");
                    hidTextr = doc.title() + "  " + doc.body().text();
                        if(doc!=null && !doc.hasText() )
                            hidTextr = doc.toString();
                    i =0;
                    Toast.makeText(this, "file is:"+ browsedText.toString(), Toast.LENGTH_SHORT).show();

//                    BufferedReader br = new BufferedReader(new FileReader(file));
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        text.append(line);
//                        text.append('\n');
//                    }
//                    br.close();
                } catch (Exception e) {
            hidTextr = "Invalid File:"+ " "+ e.toString();;
                }


//              try {
//                  browsedText = "file://" + file.toString();
////            Toast.makeText(this, "file is:"+ browsedText.toString(), Toast.LENGTH_SHORT)
////                    .show();
//                  textSource = browsedText;
//                  Toast.makeText(context, "Wait... ", Toast.LENGTH_SHORT)
//                          .show();
//                  MyTask myweb = new MyTask();
//                  myweb.execute();
//              } catch (Exception ex) {
//                  //  Toast.makeText(context, "error: " + ex.toString(), Toast.LENGTH_SHORT);
//              }
//
//                Toast.makeText(this, "file is:"+ file.toString(), Toast.LENGTH_SHORT)
//                        .show();
              //hidTextr = text.toString();
              Button buttonplay = (Button) findViewById(R.id.pPlay);
              i = 0;
              buttonplay.performClick();
              buttonplay.performClick();
              try {
                  SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                  SharedPreferences.Editor editor = settings.edit();
                  editor.putString("Savefulltext", hidTextr);
                  editor.commit();
              } catch (Exception ex) {

              }
          } else {
                  Toast.makeText(this, "Only *.txt files are allowed ! ", Toast.LENGTH_SHORT)
                          .show();
              }

          }
        else {
            Toast.makeText(this, "Error in reading the file! ", Toast.LENGTH_SHORT)
                    .show();
        }
                // hidtext.setText(text.toString());
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (i >= 2) {
            i = i - 2;
        } else if (i >= 1) {
            i = i - 1;
        }
        ishideflag = 0;
        try {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("Saveswords", wordval);
            editor.commit();
            editor.putInt("Savespeed", Integer.parseInt(edittextTime));
            editor.commit();
            editor.putInt("Savefont", fontsize);
            editor.commit();
            editor.putInt("Saveseekval", i);
            editor.commit();
            editor.putInt("Saveseekmax", myStrA.length);
            editor.commit();
            editor.putString("Savefulltext", hidTextr);
            editor.commit();
            editor.putInt("selectedColorRT1", selectedColorRT1);
            editor.commit();
            editor.putInt("selectedColorGT1", selectedColorGT1);
            editor.commit();
            editor.putInt("selectedColorBT1", selectedColorBT1);
            editor.commit();
            editor.putInt("selectedColorRT2", selectedColorRT2);
            editor.commit();
            editor.putInt("selectedColorGT2", selectedColorGT2);
            editor.commit();
            editor.putInt("selectedColorBT2", selectedColorBT2);
            editor.commit();
            editor.putInt("selectedColorRB", selectedColorRB);
            editor.commit();
            editor.putInt("selectedColorGB", selectedColorGB);
            editor.commit();
            editor.putInt("selectedColorBB", selectedColorBB);// #C2CBC4
            editor.commit();
        }
        catch (Exception ex) {
        }


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        Intent iin = getIntent();
        try {
//             List< String >    m_fontPaths;
//             List< String >    m_fontNames;
//            List<String> spinnerArray =  new ArrayList<String>();
////            spinnerArray.add("item1");
////            spinnerArray.add("item2");
//            HashMap< String, String > fonts = FontManager.enumerateFonts();
//            m_fontPaths = new ArrayList< String >();
//            m_fontNames = new ArrayList< String >();
//// Get the current value to find the checked item
////            String selectedFontPath = getSharedPreferences().getString( getKey(), "");
//            int idx = 0, checked_item = 0;
//            for ( String path : fonts.keySet() )
//            {
////                if ( path.equals( selectedFontPath ) )
////                    checked_item = idx;
//
//                m_fontPaths.add( path );
//                m_fontNames.add( fonts.get(path) );
//                idx++;
//            }
//
//
//
//
//            spinnerArray.addAll(m_fontNames);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                    this, android.R.layout.simple_spinner_item, spinnerArray);
//
//
//            Spinner myfontspin = (Spinner) findViewById(R.id.spinner);
//            myfontspin.setAdapter(adapter);
//            Typeface mtypeface = Typeface.createFromAsset(this.getAssets(),
//                    m_fontPaths.get(Long.valueOf(myfontspin.getSelectedItemId()).intValue()).toString() + myfontspin.getSelectedItem().toString() + ".ttf");
//            fulltext.setTypeface(Typeface.createFromAsset(context.getAssets(),"DroidNaskh.ttf"));


            // context.setTheme(android.R.style.Theme_Translucent_NoTitleBar);
            Bundle b = iin.getExtras();
//            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//           setSupportActionBar(toolbar);777777777

            if (b != null) {
                // Toast.makeText(this, arry, Toast.LENGTH_SHORT);
                String j = (String) b.get("readurl");
                textSource = j;
                textSource.replaceAll("http://", "");
                try {
                    Toast.makeText(context, "Wait... ", Toast.LENGTH_SHORT)
                            .show();
                    // fulltext.setText("Loading...");
                    MyTask myweb = new MyTask();

                    myweb.execute();

                } catch (Exception ex) {
                    //  Toast.makeText(context, "error: " + ex.toString(), Toast.LENGTH_SHORT);
                }
                // myaddrees.replaceAll("wwww.", "");

            } else {
                hidTextr = "Try-again!";
            }

        } catch (Exception ex) {
            // Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT);
            //hidTextr = "Nothing-to-show!";
        }


        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.pPlay).setOnTouchListener(mDelayHideTouchListener);
        // findViewById(R.id.plus).setOnTouchListener(plusTouch);
        //findViewById(R.id.minus).setOnTouchListener(minusTouch);
        //findViewById(R.id.pPlay).setOnTouchListener(pPlay);


        final Button button = (Button) findViewById(R.id.pPlay);
        final Button fopen = (Button) findViewById(R.id.browse);
        final Button zewbr = (Button) findViewById(R.id.zewbrowser);


        hidtext = (TextView) findViewById(R.id.hidText);
        fulltext = (TextView) findViewById(R.id.fullscreen_content); // output

        //  final CheckBox cw = (CheckBox) findViewById(R.id.isword);
        //

        //

        seek = (SeekBar) findViewById(R.id.seekBar);
        seekfont = (SeekBar) findViewById(R.id.seekfont);
        seekword = (SeekBar) findViewById(R.id.seekword);
        seekspeed = (SeekBar) findViewById(R.id.seekspeed);
        final TextView wordshow = (TextView) findViewById(R.id.textword);
        final TextView wordspeed = (TextView) findViewById(R.id.textdelay);
        seekspeed.setMax(1000);
        seekspeed.setProgress(400);
        seekword.setMax(1 * (100 - fontsize) + 1);
        seekword.setProgress(1);
        seekfont.setMax(100);
        seekfont.setProgress(50);
        progressval = (TextView) findViewById(R.id.progressval);

        ///
//        t = (TextView) findViewById(R.id.textView);
//        t2 = (TextView) findViewById(R.id.textView2);
//        td = (TextView) findViewById(R.id.textdelay);
//        tw = (TextView) findViewById(R.id.textword);
//        t3 = (TextView) findViewById(R.id.textView3);
//        t4 = (TextView) findViewById(R.id.textView4);
//        pt = (TextView) findViewById(R.id.progressval);
//        fulltext = (TextView) findViewById(R.id.fullscreen_content); // output
//
//        td.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        tw.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        t.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        t2.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        t3.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        t4.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        pt.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
//        fulltext.setBackgroundColor(Color.rgb(selectedColorRB, selectedColorGB, selectedColorBB));

        ///










        seekword.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    wordval = progress;
                    if (progress < 1) {
                        seekword.setProgress(1);
                        wordshow.setText(Integer.toString(1));
                        seekspeed.setMax(1000);

                    } else {
                        wordshow.setText(Integer.toString(progress));
                        seekspeed.setMax(1000 + progress * 500);
                    }
                    if (juststated == 0) {
                        int x = 1;
                        if (progress < 1)
                            x = 1;
                        else
                            x = progress;

                        int x4 = x * x * x * x;
                        int x3 = x * x * x;
                        int x2 = x * x;
                        double p1 = 0.000483;
                        double p2 = -0.0483;
                        double p3 = 0.9303;
                        double p4 = 253.1;
                        double p5 = 277.3;
                        double formulaout = p1 * x4 + p2 * x3 + p3 * x2 + p4 * x + p5;
                        if (x < 1)
                            formulaout = 320;

                        seekspeed.setProgress((int) Math.round(formulaout));
                        edittextTime = Integer.toString((int) Math.round(formulaout));
                        wordspeed.setText(Integer.toString((int) Math.round(formulaout)));
                    } else if (juststated == 1) {
                        edittextTime = Integer.toString(seekspeed.getProgress());
                        wordspeed.setText(Integer.toString(seekspeed.getProgress()));

                    }
                } catch (Exception ex) {
                }


            }
        });
        seekspeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    int setv = 1;
                    if (progress < 10) {
                        seekspeed.setProgress(10);
                        edittextTime = Integer.toString(10);
                        wordspeed.setText(Integer.toString(10));
                        setv = 10;
                    } else {
                        edittextTime = Integer.toString(progress);
                        wordspeed.setText(Integer.toString(progress));
                        setv = progress;
                    }
                    long winf = 1;
                    if (i > 0) {
                        if (isword == 1) {
                            winf = myStrA[i - 1].length() < 5 ? 1 : myStrA[i - 1].length() / 5;

                        } else {
                            if (myStrA[i - 1] != null) {
                                winf = myStrA[i - 1].length() < 5 ? 1 : myStrA[i - 1].length() / 5;
                                winf = wordCount(myStrA[i - 1]) + winf;
                            }
                        }

                    }

                    valt = (setv * val + winf * setv / 10);
                    if (mytimer != null) {
                        mytimer.cancel();
                        mytimer.purge();
                        if (i > 0 && ishideflag == 1)
                            i = i - 1;
                        mytimer = new Timer();
                        mytimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                TimerMethod();
                            }

                        }, 0, valt);
                    }

                } catch (Exception ex) {
                }


            }
        });

        seekfont.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    if (progress < 1)
                        seekfont.setProgress(1);
                    fulltext.setTextSize(progress);
                    fontsize = progress;
                    seekword.setMax(1 * (100 - fontsize) + 1);
                } catch (Exception ex) {
                }


            }
        });


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //    edittextTime = Integer.toString(seekspeed.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //  edittextTime ="1";
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    i = progress;
                    progressval.setText(" " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));
                    String showme0 = myStrA[i];
                    String showme = "";
                    int iout = 0;
                    for (int ii = 1; ii < wordval; ii++) {
                        if ((i + ii) < myStrA.length) {
                            showme = showme + " " + myStrA[i + ii];
                            iout = iout + 1;
                        }

                    }

                    String text2 = showme0 + showme;

                    Spannable spannable = new SpannableString(text2);

                    spannable.setSpan(new ForegroundColorSpan(Color.rgb(selectedColorRT2, selectedColorGT2, selectedColorBT2)), showme0.length(), (showme0 + showme).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    fulltext.setText(spannable, TextView.BufferType.SPANNABLE);


                    ///fulltext.setText(Html.fromHtml("<font color=white>" + showme0 + "</font><br><br>" + showme));
                    //  fulltext.setText(showme);

                    prog.setProgress(i);
                } catch (Exception ex) {
                }


            }
        });


        //

        zewbr.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         // Toast toast = Toast.makeText(context, "ZewBrowser", Toast.LENGTH_SHORT);
                                         // toast.show();
                                         myint = new Intent("android.intent.action.MAIN");

                                         //  myint = new Intent(MainActivity.class);
                                         myint.putExtra("filterFileExtension", oldurl);
                                         //startActivity( myint);
                                         myint.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                         startActivityForResult(myint, 1);
                                     }
                                 }

        );


        final Button textcolor = (Button) findViewById(R.id.textcolor);
        final Button textcolor2 = (Button) findViewById(R.id.textcolor2);
        final Button backcolor = (Button) findViewById(R.id.backcolor);

        t = (TextView) findViewById(R.id.textView);
        t2 = (TextView) findViewById(R.id.textView2);
        td = (TextView) findViewById(R.id.textdelay);
        tw = (TextView) findViewById(R.id.textword);
        t3 = (TextView) findViewById(R.id.textView3);
        t4 = (TextView) findViewById(R.id.textView4);
        pt = (TextView) findViewById(R.id.progressval);
        textcolor.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             try {
                                                 final ColorPicker cp = new ColorPicker(FullscreenActivity.this, selectedColorRT1, selectedColorGT1, selectedColorBT1);
                                                 cp.show();

                                                /* On Click listener for the dialog, when the user select the color */
                                                 Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                                                 okColor.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                                                         selectedColorRT1 = cp.getRed();
                                                         selectedColorGT1 = cp.getGreen();
                                                         selectedColorBT1 = cp.getBlue();
                /* Or the android RGB Color (see the android Color class reference) */
                                                         // selectedColorRGBTEXT = cp.getColor();
                                                         fulltext.setTextColor(Color.rgb(cp.getRed(), cp.getGreen(), cp.getBlue()));
                                                         td.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         tw.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         textcolor.setBackgroundColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));

                                                         t.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         t2.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         t3.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         t4.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         pt.setTextColor(Color.rgb(selectedColorRT1, selectedColorGT1, selectedColorBT1));
                                                         cp.dismiss();
                                                     }
                                                 });

                                             } catch (Exception ex) {
                                             }

                                         }
                                     }
        );


        textcolor2.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {

                                              try {
                                                  final ColorPicker cp = new ColorPicker(FullscreenActivity.this, selectedColorRT2, selectedColorGT2, selectedColorBT2);
                                                  cp.show();

                                                /* On Click listener for the dialog, when the user select the color */
                                                  Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                                                  okColor.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {

                /* You can get single channel (value 0-255) */
//                                                       selectedColorR = cp.getRed();
//                                                       selectedColorG = cp.getGreen();
//                                                       selectedColorB = cp.getBlue();

                /* Or the android RGB Color (see the android Color class reference) */
                                                          selectedColorRT2 = cp.getRed();
                                                          selectedColorGT2 = cp.getGreen();
                                                          selectedColorBT2 = cp.getBlue();
                                                          textcolor2.setBackgroundColor(Color.rgb(selectedColorRT2, selectedColorGT2, selectedColorBT2));

                                                          cp.dismiss();
                                                      }
                                                  });

                                              } catch (Exception ex) {
                                              }

                                          }
                                      }
        );
//fl = (FrameLayout) findViewById(R.id.content_frame);
        backcolor.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             try {
                                                 final ColorPicker cp = new ColorPicker(FullscreenActivity.this, selectedColorRB, selectedColorGB, selectedColorBB);
                                                 cp.show();

                                                /* On Click listener for the dialog, when the user select the color */
                                                 Button okColor = (Button) cp.findViewById(R.id.okColorButton);
                                                 okColor.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {

                /* You can get single channel (value 0-255) */
                                                         selectedColorRB = cp.getRed();//selectedColorRT2
                                                         selectedColorGB = cp.getGreen();
                                                         selectedColorBB = cp.getBlue();
                                                         fulltext.setBackgroundColor(Color.rgb(selectedColorRB, selectedColorGB, selectedColorBB));

                /* Or the android RGB Color (see the android Color class reference) */

                                                         //   fl.setBackgroundColor(Color.rgb(selectedColorRB, selectedColorGB, selectedColorBB));

                                                         backcolor.setBackgroundColor(Color.rgb(selectedColorRB, selectedColorGB, selectedColorBB));
                                                         cp.dismiss();
                                                     }
                                                 });

                                             } catch (Exception ex) {
                                             }

                                         }
                                     }
        );


        buttonm = (Button) findViewById(R.id.buttonm);
        buttonp = (Button) findViewById(R.id.buttonp);
        prog = (ProgressBar) findViewById(R.id.progressBar);
        progressval = (TextView) findViewById(R.id.progressval);
        buttonm.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           long winf = 1;
                                           try {
                                               if (i > 0) {
                                                   i = i - 1;

                                                   //    wait(1000);
                                                   progressval.setText("  " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));
                                                   seek = (SeekBar) findViewById(R.id.seekBar);
                                                   seek.setProgress(i);
                                                   prog.setProgress(i);
                                               }
                                           } catch (Exception ex) {
                                           }

                                       }
                                   }
        );

        buttonm.setOnLongClickListener(new View.OnLongClickListener() {
                                           @Override
                                           public boolean onLongClick(View v) {
                                               {
                                                   long winf = 1;
                                                   try {
                                                       if (i > 10) {
                                                           i = i - 10;

                                                           //    wait(1000);
                                                           progressval.setText("  " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));
                                                           seek = (SeekBar) findViewById(R.id.seekBar);
                                                           seek.setProgress(i);
                                                           prog.setProgress(i);
                                                       }
                                                   } catch (Exception ex) {
                                                   }
                                                   return true;
                                               }
                                           }
                                       }
        );
//        buttonp.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
        buttonp.setOnLongClickListener(new View.OnLongClickListener() {
                                           @Override
                                           public boolean onLongClick(View v) {
                                               long winf = 1;
                                               try {
                                                   if (i < myStrA.length + 10) {
                                                       i = i + 10;

                                                       prog = (ProgressBar) findViewById(R.id.progressBar);
                                                       progressval.setText("  " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));
                                                       seek = (SeekBar) findViewById(R.id.seekBar);
                                                       seek.setProgress(i);
                                                       prog.setProgress(i);
                                                   }
                                               } catch (Exception ex) {
                                               }
                                               return true;
                                           }
                                       }
        );


        buttonp.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           long winf = 1;
                                           try {
                                               if (i < myStrA.length) {
                                                   i = i + 1;

                                                   //   wait(1000);
                                                   prog = (ProgressBar) findViewById(R.id.progressBar);
                                                   progressval.setText("  " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));
                                                   seek = (SeekBar) findViewById(R.id.seekBar);
                                                   seek.setProgress(i);
                                                   prog.setProgress(i);
                                               }
                                           } catch (Exception ex) {
                                           }

                                       }
                                   }
        );

        fopen.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         String inputLine = "";
//
                                         // get prompts.xml view

                                         LayoutInflater li = LayoutInflater.from(context);
                                         View promptsView = li.inflate(R.layout.prompts, null);

                                         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                 context);

                                         // set prompts.xml to alertdialog builder
                                         alertDialogBuilder.setView(promptsView);

                                         final EditText userInput = (EditText) promptsView
                                                 .findViewById(R.id.editTextDialogUserInput);

                                         // set dialog message
                                         alertDialogBuilder
                                                 .setCancelable(true)
                                                 .setPositiveButton("Ok",
                                                         new DialogInterface.OnClickListener() {
                                                             public void onClick(DialogInterface dialog, int id) {
                                                                 // get user input and set it to result
                                                                 // edit text
                                                                 ispaste = 0;
                                                                 String temptex = "";
                                                                 if (userInput.getText().toString().contains("http://") || userInput.getText().toString().contains("http://")) {
                                                                     temptex = userInput.getText().toString();
                                                                 } else {
                                                                     temptex = "https://" + userInput.getText().toString();
                                                                 }

                                                                 ispaste = 0;
                                                                 try {
                                                                     if (isValidUrl(temptex)) {
                                                                         textSource = temptex;
                                                                         //fulltext.setText("Wait...");
                                                                         Toast.makeText(context, "Wait... ", Toast.LENGTH_SHORT)
                                                                                 .show();
                                                                         try {
                                                                             // textSource = textSource.replaceAll()
                                                                             MyTask myweb = new MyTask();
                                                                             myweb.execute();
                                                                         } catch (Exception ex) {
                                                                             //  Toast.makeText(context, "error: " + ex.toString(), Toast.LENGTH_SHORT);
                                                                         }
                                                                     } else {
                                                                         // hidtext.setText(userInput.getText());
                                                                         String str = userInput.getText().toString();

                                                                         if( !str.isEmpty() ) {
                                                                             hidTextr = userInput.getText().toString();
                                                                             i = 0;
//                                                                         buttonp = (Button) findViewById(R.id.pPlay);
//                                                                         buttonp.performClick();
                                                                             //  fulltext.setText("Press \u25BA ");
                                                                             //show();
                                                                             // buttonp.performClick();
                                                                             // buttonp.performClick();
                                                                             Toast toast = Toast.makeText(context, "Press \u25BA", Toast.LENGTH_LONG);
                                                                             toast.show();
                                                                         }
                                                                         //fulltext.setText("(Re)Play");
                                                                     }
                                                                 } catch (Exception ex) {
                                                                     hidTextr = userInput.getText().toString();
                                                                     // fulltext.setText("(Re)Play");

                                                                 }


                                                             }
                                                         })
                                                 .setNeutralButton("Browse", new DialogInterface.OnClickListener() {
                                                     public void onClick(DialogInterface dialog, int id) {
                                                         // get user input and set it to result
                                                         // edit text
                                                         ispaste = 0;
                                                         //
                                                         Context context = getApplicationContext();
                /*Toast toast = Toast.makeText(context, "hi", Toast.LENGTH_LONG);
                toast.show();*/
                                                         myint = new Intent("co.zew.zewreader.filechooser");
                                                         myint.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                                                         myint.putExtra("filterFileExtension", ".txt");
                                                         //startActivity( myint);
                                                         startActivityForResult(myint, 0);


                                                         //
                               /* userInput.setText(userInput.getText().toString().replaceAll("http://", ""));
                                if (isValidUrl("http://" + userInput.getText().toString())) {
                                    textSource = "http://" + userInput.getText().toString();
                                    fulltext.setText("Wait...");

                                    new MyTask().execute();
                                } else {
                                    hidtext.setText(userInput.getText());
                                    fulltext.setText(userInput.getText());
                                }*/

                                                     }
                                                 })
                                                 .setNegativeButton("Paste",
                                                         new DialogInterface.OnClickListener() {
                                                             public void onClick(DialogInterface dialog, int id) {
                                                                 ispaste = 1;
                                                                 if (ispaste == 1) {
                                                                     ClipboardManager clipMan = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                                                     //hidtext.setText(clipMan.getText().toString());
                                                                     String str = clipMan.getText().toString();
                                                                     if( !str.isEmpty()){
                                                                         hidTextr = clipMan.getText().toString();
                                                                         i = 0;
                                                                         Toast toast = Toast.makeText(context, "Press \u25BA", Toast.LENGTH_LONG);
                                                                         toast.show();}
                                                                     //fulltext.setText("Press \u25BA ");
                                                                     //  fulltext.setText("Press \u25BA ");
                                                                     // buttonp = (Button) findViewById(R.id.pPlay);


                                                                 }
                                                                 dialog.cancel();
                                                             }
                                                         });

                                         // create alert dialog
                                         AlertDialog alertDialog = alertDialogBuilder.create();

                                         // show it
                                         alertDialog.show();
                                         //


                                     }


                                 }

        );


        button.setOnClickListener(new View.OnClickListener()

                                  {
                                      public void onClick(View v) {
               /* ClipboardManager clipMan = (ClipboardManager)getSystemService(v.getContext().CLIPBOARD_SERVICE);

               if(hidtext.getText()=="0451033770" ){
                hidtext.setText(clipMan.getText());
               }*/

                                          //hidtext.setText(edittext.getText());
                                          //  final CheckBox isw = (CheckBox) findViewById(R.id.isword);


                                          String myStrW = hidTextr;
                                          if (juststated == 0) {
                                              i = 0;
                                              myStrW = myStrW.replaceAll("\\n", " ");
                                              myStrW = myStrW.replaceAll("\\t", " ");
                                              myStrW = myStrW.replaceAll("\\r", " ");
                                              myStrW = myStrW.replaceAll("\\x0b", " ");
                                              myStrW = myStrW.replaceAll("[\r\n]+", ".");
                                              // myStrW = myStrW.replaceAll("...", " ");
                                              myStrW = myStrW.replaceAll("\\s\\s+", " ").trim();

                                          } else if (juststated == 1) {
                                              myStrW = hidTextr;//hidtext.getText().toString();
                                              myStrW = myStrW.replaceAll("\\n", " ");
                                              myStrW = myStrW.replaceAll("\\t", " ");
                                              myStrW = myStrW.replaceAll("\\r", " ");
                                              myStrW = myStrW.replaceAll("\\x0b", " ");
                                              myStrW = myStrW.replaceAll("[\r\n]+", ".");
                                              // myStrW = myStrW.replaceAll("...", " ");
                                              myStrW = myStrW.replaceAll("\\s\\s+", " ").trim();
                                              juststated = 0;
                                          }


                                          // myStrW.
                                          // myStrW= myStrW.trim();
                                          long winf = 1;
                                          try {
                                              if (true) {
                                                  myStrA = myStrW.split(" ");
                                              } else {
                                                  myStrA = myStrW.split("\\.");
                                              }
                                              if (mytimer != null) {
                                                  mytimer.cancel();
                                                  mytimer.purge();
                                              }
                                              winf = 1;
                                              if (i >= 0) {
                                                  if (isword == 1) {
                                                      winf = myStrA[i].length() < 5 ? 1 : myStrA[i].length() / 5;

                                                  } else {
                                                      if (myStrA[i] != null) {
                                                          winf = myStrA[i].length() < 5 ? 1 : myStrA[i].length() / 5;
                                                          winf = wordCount(myStrA[i]) + winf;
                                                      }
                                                  }

                                              }
                                          } catch (Exception ex) {
                                              //   Toast.makeText(context, "Error: " + ex.toString(), Toast.LENGTH_SHORT);

                                          }
                                          if (true) {
                                              valt = ((Integer.parseInt(edittextTime.toString())) * val + winf * (Integer.parseInt(edittextTime.toString())) / 10);
                                          } else {
                                              valt = ((Integer.parseInt(edittextTime.toString())) * val + winf * (Integer.parseInt(edittextTime.toString())) / 10);
                                          }
//
//                final Button pbut = (Button) findViewById(R.id.plus);
//                pbut.performClick();
//
//                final Button nbut = (Button) findViewById(R.id.minus);
//                nbut.performClick();
                                          prog = (ProgressBar) findViewById(R.id.progressBar);
                                          seek = (SeekBar) findViewById(R.id.seekBar);

                                          seek.setMax(myStrA.length);
                                          seek.setProgress(i);
                                          // progressval = (TextView) findViewById(R.id.progressval);
                                          progressval.setText(" " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));

                                          prog.setMax(myStrA.length);
                                          prog.setProgress(i);
                                          mytimer = new Timer();
                                          mytimer.schedule(new TimerTask() {
                                              @Override
                                              public void run() {
                                                  TimerMethod();
                                              }

                                          }, 0, valt);
                                      }
                                  }

        );
        show();
    } // END OF ON CREATE


    private boolean isValidUrl(String url) {
        try {
            Pattern p = Patterns.WEB_URL;
            Matcher m = p.matcher(url);
            if (m.matches())
                return true;
            else
                return false;
        } catch (Exception ex) {
            Toast.makeText(context, "error: " + ex.toString(), Toast.LENGTH_SHORT);
            return false;
        }
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        String textResult;

        @Override
        protected Void doInBackground(Void... params) {


            try {
//                doc = Jsoup.connect("http://google.com").get();
//                Elements newsHeadlines = doc.select("#mp-itn b a");


                String url = textSource.toString();
                String outtext = getmyText(url);
                i = 0;
//                if (outtext == "Error,TryAgain!") {
//                    Document doc = Jsoup.connect(url).get();
//
////                    if (textSource.contains("en.wikipedia.org/")) {
////                        Elements paragraphs = doc.select(".mw-content-ltr p, .mw-content-ltr li");
////
////                        Element firstParagraph = paragraphs.first();
////                        Element lastParagraph = paragraphs.last();
////                        Element p;
////                        int i = 1;
////                        p = firstParagraph;
////                        textResult = p.text();
////                        while (p != lastParagraph) {
////                            p = paragraphs.get(i);
////                            textResult = textResult + (p.text());
////                            i++;
////                        }
////                    } else {
//                    textResult = doc.body().text();
//
//                    //  }
//                } else {
//                    textResult = outtext;
//                }
                textResult = outtext;
            } catch (Exception e) {
                e.printStackTrace();
                //   Toast.makeText(context, "error: " + e.toString(), Toast.LENGTH_SHORT);
                textResult = "Error,TryAgain!";
                // textResult = e.toString();
            }
            String url = textSource;
            if (textResult == "Error,TryAgain!" ) {
                  textResult = "";
                url = url.replaceAll("file:///", "/");
                File in = new File(url);

                ArrayList<String> mBodyText = new ArrayList<>();
                try {
                    Document doc = Jsoup.parse(in, "UTF-8");

                    if (doc != null) {
                        Elements paragraphs = doc.select("h1,h2,h3,h4,h5,h6,p");
                        for (Element p : paragraphs)
                            mBodyText.add(p.text());
                        for(String p : mBodyText) {
                           p= p.replaceAll("<a(.*)>","");
                            p= p.replaceAll("<img(.*)>","");
                            p= p.replaceAll("<img(.*)</img>","");
                            p = p.replaceAll("</a>","");
                            p = p.replaceAll("<b>","");
                            p = p.replaceAll("</b>","");
                            textResult = textResult + p + "-|- ";
                        }
                    }
                }

            catch(Exception e) {
                textResult = "Invalid File/Url:"+ " "+ url.toString();;
            }

            }
            return null;
        }


        private String getmyText(String myurl) {
            String returntext = "";
            if (myurl.startsWith("file:///"))
            {
                return "Error,TryAgain!";
            }

            HtmlFetcher fetcher = new HtmlFetcher();
            try {
                Document doc = Jsoup.parse(new URL(myurl).openStream(), "UTF-8", myurl);
                ArrayList<String> mBodyText = new ArrayList<>();
                if (doc != null) {
                    Elements paragraphs = doc.select("h1,h2,h3,h4,h5,h6,p");
                    for (Element p : paragraphs)
                        mBodyText.add(p.text());

                    for(String p : mBodyText) {
                       p= p.replaceAll("<a(.*)>","");
                        p = p.replaceAll("</a>","");
                        p= p.replaceAll("<img(.*)>","");
                        p= p.replaceAll("<img(.*)</img>","");
                        p = p.replaceAll("<b>","");
                        p = p.replaceAll("</b>","");
                        returntext = returntext + p + "-|- ";
                    }




                }
            }
            catch (Exception ex) {




                try {
                    JResult result = fetcher.fetchAndExtract(myurl, 4500, true);
                    //mTitleText = result.getTitle();
                    //  returntext = result.getTitle();//.replaceAll("."," ") ;
                    if (result.getText().length() > 5)
                        returntext =  result.getTitle().toString() + " " + result.getText().toString();
                } catch (Exception e) {
                    textResult = "Error,TryAgain!";
                    returntext= "Error,TryAgain!";

                } catch (OutOfMemoryError e) {
                    textResult = "Error,TryAgain!";
                    returntext= "Error,TryAgain!";
                }
            }



            return returntext;
        }

        //        Activity mActivity;
//        @Override
//        protected void onPreExecute() {
//
//            super.onPreExecute();
//            mProgressDialog = new ProgressDialog(mActivity);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setIndeterminate(true);
//            mProgressDialog.setMessage("Loading ...");
//            mProgressDialog.show();
//        }
        @Override
        protected void onPostExecute(Void result) {
//            Toast.makeText(context, "Wait... ", Toast.LENGTH_SHORT)
//                    .show();


            String textFromHtml = "";
            try {
                textResult = textResult.replaceAll("\\n", " ");
                textResult = textResult.replaceAll("\\t", " ");
                textResult = textResult.replaceAll("\\r", " ");
                textResult = textResult.replaceAll("\\x0b", " ");
                textResult = textResult.replaceAll("[\r\n]+", ".");
                textResult = textResult.replaceAll("\\s\\s+", " ").trim();
                // Elements newsHeadlines = doc.select("#mp-itn b a");
                // textFromHtml = doc.body().text();
                textFromHtml = textResult;// Jsoup.parse(textResult).text();

            } catch (Exception ex) {
                //  Toast.makeText(context, "Error !" + ex.toString(), Toast.LENGTH_SHORT);
                // textFromHtml = textResult;

            }
            //TextView desc = (TextView) dialog.findViewById(R.id.description);
            Toast.makeText(context, "Done ! ", Toast.LENGTH_SHORT)
                    .show();
            try {
                // hidtext.setText(textFromHtml.toString());
                hidTextr = textFromHtml.toString();
                Button buttonplay = (Button) findViewById(R.id.pPlay);
                // fulltext.setText("Press \u25BA ");
                show();
                buttonplay.performClick();
                buttonplay.performClick();
//                if (textFromHtml.length() > 22) {
//                    fulltext.setText("(Re)Play");
//                } else {
//                    if (textFromHtml.length() > 0) {
//                        fulltext.setText("(Re)Play");
//                    }
//
//                }
            } catch (Exception ex) {

                //Toast.makeText(context, ex.toString(), Toast.LENGTH_SHORT);
            }


            super.onPostExecute(result);
        }
    }


    private void TimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {

            //This method runs in the same thread as the UI.
            TextView fulltext = (TextView) findViewById(R.id.fullscreen_content); // output
            // final CheckBox isw = (CheckBox) findViewById(R.id.isword);
            if (i < myStrA.length) {
                String temp = myStrA[i].replaceAll(" ", "");
//                if (isw.isChecked()) {
//
//                    fulltext.setTextSize(50);
//                } else {
//                    fulltext.setTextSize(20);
//                }
                try {
                    //hidTextr0 = "stopped";
                    if (temp != "") {
                        String showme0 = myStrA[i];
                        String showme = "";
                        int iout = 0;
                        for (int ii = 1; ii < wordval; ii++) {
                            if ((i + ii) < myStrA.length) {
                                showme = showme + " " + myStrA[i + ii];
                                iout = iout + 1;
                            }

                        }

                        String text2 = showme0 + showme;

                        Spannable spannable = new SpannableString(text2);

                        spannable.setSpan(new ForegroundColorSpan(Color.rgb(selectedColorRT2, selectedColorGT2, selectedColorBT2)), showme0.length(), (showme0 + showme).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        fulltext.setText(spannable, TextView.BufferType.SPANNABLE);


                        if (ishideflag == 1) {
                            i = i + iout + 1;
                            prog = (ProgressBar) findViewById(R.id.progressBar);
                            progressval = (TextView) findViewById(R.id.progressval);
                            progressval.setText("  " + Integer.toString(i) + " / " + Integer.toString(myStrA.length));
                            seek = (SeekBar) findViewById(R.id.seekBar);
                            seek.setProgress(i);
                            prog.setProgress(i);

                        }
                        // Toast.makeText(context, Integer.toString(myStrA[i].length()), Toast.LENGTH_SHORT)
                        //       .show();
                    } else {
                        mytimer.cancel();
                        mytimer.purge();

                        long winf = 1;
                        if (i > 1) {
                            if (isword == 1) {
                                winf = myStrA[i].length() < 5 ? 1 : myStrA[i].length() / 5;

                            } else {
                                if (myStrA[i] != null) {
                                    winf = myStrA[i].length() < 5 ? 1 : myStrA[i].length() / 5;
                                    winf = wordCount(myStrA[i]) + winf;
                                }
                            }

                        }

                        valt = ((Integer.parseInt(edittextTime.toString())) * val + winf * (Integer.parseInt(edittextTime.toString())) / 10);
                        if (mytimer != null) {
                            mytimer.cancel();
                            mytimer.purge();
                        }
                        mytimer = new Timer();
                        mytimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                TimerMethod();
                            }

                        }, 0, valt);
                    }
                } catch (Exception ex) {

                    // Toast.makeText(context, "Error Show: " + ex.toString(), Toast.LENGTH_SHORT);

                }


            }
            //Do something to the UI thread here

        }
    };


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);


    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {


            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }

            return false;
        }

    };

    private void toggle() {
        if (mVisible) {

            hide();
        } else {

            show();
        }
    }

    private void hide() {

        ishideflag = 1;

        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @SuppressLint("InlinedApi")
    private void show() {
        ishideflag = 0;


        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public class addListenerOnTextChange implements TextWatcher {
        private Context mContext;
        EditText mEdittextview;

        public addListenerOnTextChange(Context context, EditText edittextview) {
            super();
            this.mContext = context;
            this.mEdittextview = edittextview;
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            this.mContext = context;
//            Toast.makeText(mContext, "time changed", Toast.LENGTH_SHORT)
//                    .show();
        }
    }

    public static int wordCount(String s) {
        if (s == null)
            return 1;
        return s.trim().split("\\s+").length;
    }


}


