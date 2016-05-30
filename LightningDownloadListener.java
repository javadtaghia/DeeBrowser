/*
 * Copyright 2014 A.C.R. Development
 */
package co.zew.browser.download;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import co.zew.deebrowser.R;
import co.zew.browser.constant.Constants;

public class LightningDownloadListener implements DownloadListener {

    private final Activity mActivity;

    public LightningDownloadListener(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onDownloadStart(final String url, final String userAgent,
            final String contentDisposition, final String mimetype, long contentLength) {
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        DownloadHandler.onDownloadStart(mActivity, url, userAgent,
                                contentDisposition, mimetype);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity); // dialog
        builder.setTitle(fileName)
                .setMessage(mActivity.getResources().getString(R.string.dialog_download))
                .setPositiveButton(mActivity.getResources().getString(R.string.action_download),
                        dialogClickListener)
                .setNegativeButton(mActivity.getResources().getString(R.string.action_cancel),
                        dialogClickListener).show();
        Log.i(Constants.TAG, "Downloading" + fileName);

    }
}
