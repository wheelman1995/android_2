package ru.wheelman.weather.presentation.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ru.wheelman.weather.R;

public class FeedbackFragment extends Fragment {

    private static final String TAG = FeedbackFragment.class.getSimpleName();
    private static final String DEVELOPER_EMAIL = "anikinvitya@gmail.com";

    private TextInputEditText subject;
    private TextInputEditText messageBody;
    private MaterialButton send;
    private InputMethodManager imm;

    public static FeedbackFragment newInstance() {
        return new FeedbackFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        initVariables(view);

        initListeners();

        return view;
    }

    private void initListeners() {
        send.setOnClickListener((view) -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{DEVELOPER_EMAIL});
            intent.putExtra(Intent.EXTRA_SUBJECT, String.format(Locale.UK, "%s, %s", getActivity().getPackageName(), subject.getText().toString()));
            intent.putExtra(Intent.EXTRA_TEXT, messageBody.getText().toString());
            intent.setData(Uri.parse("mailto:"));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(this)
                        .commit();
                getActivity().getSupportFragmentManager().popBackStack("FeedbackFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    private void initVariables(View view) {
        subject = view.findViewById(R.id.tiet_subject);
        messageBody = view.findViewById(R.id.tiet_message_body);
        send = view.findViewById(R.id.b_send);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (subject.requestFocus()) {
            imm.showSoftInput(subject, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onStop() {
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onStop();
    }
}
