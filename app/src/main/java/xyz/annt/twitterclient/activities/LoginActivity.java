package xyz.annt.twitterclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.annt.twitterclient.R;
import xyz.annt.twitterclient.models.User;
import xyz.annt.twitterclient.networks.TwitterClient;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {
	private static final String TAG = "LoginActivity";

    @Bind(R.id.btnLogin) Button btnLogin;
    @Bind(R.id.pbLoading) ProgressBar pbLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
	}

	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
        // Get user credentials before start home
        btnLogin.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);

        RequestParams params = new RequestParams();
        getClient().getUserCredentials(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(TAG, response.toString());
                User user = User.fromJSONObject(response);
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.putExtra("user", user);
                startActivity(i);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.w(TAG, errorResponse.toString());
                pbLoading.setVisibility(View.INVISIBLE);
                btnLogin.setVisibility(View.VISIBLE);

                Toast.makeText(LoginActivity.this,
                        "Unable to retrieve user information. Please try again.",
                        Toast.LENGTH_LONG).show();
            }
        });
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
        pbLoading.setVisibility(View.INVISIBLE);
        btnLogin.setVisibility(View.VISIBLE);

        Toast.makeText(LoginActivity.this, "An error has occurred. Please try again.",
                Toast.LENGTH_LONG).show();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
        btnLogin.setVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);

        getClient().connect();
	}

}
