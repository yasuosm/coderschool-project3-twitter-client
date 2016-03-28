package xyz.annt.twitterclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.annt.twitterclient.R;
import xyz.annt.twitterclient.applications.TwitterApplication;
import xyz.annt.twitterclient.models.User;
import xyz.annt.twitterclient.networks.TwitterClient;

/**
 * Created by annt on 3/27/16.
 */
public class ComposeFragment extends DialogFragment {
    private static final String TAG = "ComposeFragment";

    @Bind(R.id.ibtnClose) ImageButton ibtnClose;
    @Bind(R.id.ivUserProfileImage) ImageView ivUserProfileImage;
    @Bind(R.id.etStatus) EditText etStatus;
    @Bind(R.id.btnTweet) Button btnTweet;
    @Bind(R.id.tvRemaining) TextView tvRemaining;

    public ComposeFragment() {}

    public interface ComposeFragmentListener {
        void onTweetSuccess(JSONObject response);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        User user = args.getParcelable("user");
        if (user != null) {
            Glide.with(getContext()).load(user.getProfileImageUrl()).into(ivUserProfileImage);
        }

        tvRemaining.setText(String.valueOf(140));

        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int length = etStatus.getText().length();
                int remaining = 140 - length;
                tvRemaining.setText(String.valueOf(remaining));
                btnTweet.setActivated(0 < length);
            }
        });

        // Show soft keyboard automatically and request focus to field
        etStatus.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @OnClick(R.id.ibtnClose)
    public void closeFragment() {
        dismiss();
    }

    @OnClick(R.id.btnTweet)
    public void tweet() {
        if (0 == etStatus.getText().length()) {
            return;
        }

        TwitterClient client = TwitterApplication.getRestClient();
        RequestParams params = new RequestParams();
        params.put("status", etStatus.getText().toString());
        client.updateStatus(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                ComposeFragmentListener listener = (ComposeFragmentListener) getActivity();
                listener.onTweetSuccess(response);
                dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.w(TAG, errorResponse.toString());
                Toast.makeText(getContext(), "An error has occurred. Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
