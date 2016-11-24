package com.als.donetoday;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

// TODO: make this better
public class AboutDialogFragment extends DialogFragment {

    public final static String TAG = AboutDialogFragment.class.getName();

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final String license = getText(R.string.text_about).toString()
                .replaceAll("\\{app-name\\}", getString(R.string.app_name))
                .replaceAll("\\{app-name-encoded\\}", Uri.encode(getString(R.string.app_name)))//
                .replaceAll("\\{app-version\\}", BuildConfig.VERSION_NAME);

        final AlertDialog ad = new AlertDialog.Builder(getContext())
//                .setTitle(getString(R.string.title_about, getString(R.string.app_name)))
                .setMessage(Html.fromHtml(license)) // TODO: deprecated
                .create();

        // make links clickable
        final TextView message = (TextView) ad.findViewById(android.R.id.message);
        if (message != null) {
            message.setMovementMethod(LinkMovementMethod.getInstance());
            message.setClickable(true);
            message.setLinkTextColor(0x00000000);
        }
        return ad;
    }
}
