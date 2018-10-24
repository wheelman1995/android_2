package ru.wheelman.weather;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AboutFragment extends AppCompatDialogFragment {

    private Listener listener;

    public static AboutFragment newInstance() {
        AboutFragment f = new AboutFragment();

        return f;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (listener != null) {
            listener.onDismiss();
        }
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String version = null;
        try {
            version = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder
                .setPositiveButton(R.string.about_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onDismiss();
                        }
                    }
                })
                .setTitle(R.string.about_title)
                .setMessage(getString(R.string.about_message, version))
                .setIcon(R.drawable.ic_info_outline)
                .create();
        return alertDialog;
    }


    public interface Listener {
        void onDismiss();
    }
}
