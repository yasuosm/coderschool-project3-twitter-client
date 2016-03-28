package xyz.annt.twitterclient.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.annt.twitterclient.R;
import xyz.annt.twitterclient.adapters.TweetsAdapter;
import xyz.annt.twitterclient.applications.TwitterApplication;
import xyz.annt.twitterclient.fragments.ComposeFragment;
import xyz.annt.twitterclient.listeners.EndlessRecyclerViewScrollListener;
import xyz.annt.twitterclient.models.Tweet;
import xyz.annt.twitterclient.models.User;
import xyz.annt.twitterclient.networks.TwitterClient;

public class HomeActivity extends AppCompatActivity
        implements ComposeFragment.ComposeFragmentListener {

    private static final String TAG = "HomeActivity";
    private User user;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsAdapter tweetsAdapter;
    private LinearLayoutManager layoutManager;

    @Bind(R.id.fabCompose) FloatingActionButton fabCompose;
    @Bind(R.id.rvTweets) RecyclerView rvTweets;
    @Bind(R.id.srlContainer) SwipeRefreshLayout srlContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.ic_logo_white);
        ButterKnife.bind(this);

        user = getIntent().getParcelableExtra("user");
        client = TwitterApplication.getRestClient();
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsAdapter(tweets);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvTweets.setAdapter(tweetsAdapter);
        rvTweets.setLayoutManager(layoutManager);

        setListeners();

        srlContainer.setColorSchemeResources(R.color.colorPrimary);
        srlContainer.post(new Runnable() {
            @Override
            public void run() {
                srlContainer.setRefreshing(true);
                loadMoreTweets();
            }
        });
    }

    private void setListeners() {
        srlContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNewTweets();
            }
        });

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeFragment();
            }
        });

        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadMoreTweets();
            }
        });
    }

    private void showComposeFragment() {
        ComposeFragment fragment = new ComposeFragment();
        // Set fullscreen
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        // Set arguments
        Bundle args = new Bundle();
        args.putParcelable("user", user);
        fragment.setArguments(args);
        // Show
        FragmentManager manager = getSupportFragmentManager();
        fragment.show(manager, "fragment_compose");
    }

    private void loadNewTweets() {
        RequestParams params = new RequestParams();
        if (0 < tweets.size()) {
            long sinceId = tweets.get(0).getId();
            params.put("since_id", sinceId);
        }

        client.getHomeTimeline(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i(TAG, response.toString());
                ArrayList<Tweet> newTweets = Tweet.fromJSONArray(response);
                if (0 < newTweets.size()) {
                    for (int i = newTweets.size() - 1; i >= 0; i--) {
                        tweets.add(0, newTweets.get(i));
                    }
                }
                int curSize = tweetsAdapter.getItemCount();
                tweetsAdapter.notifyItemRangeInserted(curSize, tweets.size() - 1);

                srlContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.w(TAG, errorResponse.toString());
                srlContainer.setRefreshing(false);

                try {
                    JSONArray errors = errorResponse.getJSONArray("errors");
                    JSONObject error = errors.getJSONObject(0);
                    Toast.makeText(HomeActivity.this, error.getString("message"),
                            Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMoreTweets() {
        RequestParams params = new RequestParams();
        if (0 < tweets.size()) {
            long maxId = tweets.get(tweets.size() - 1).getId() - 1;
            params.put("max_id", maxId);
        }

        client.getHomeTimeline(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.i(TAG, response.toString());
                tweets.addAll(Tweet.fromJSONArray(response));
                int curSize = tweetsAdapter.getItemCount();
                tweetsAdapter.notifyItemRangeInserted(curSize, tweets.size() - 1);

                // @todo display progress bar on bottom of items
                srlContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject errorResponse) {
                Log.w(TAG, errorResponse.toString());
                srlContainer.setRefreshing(false);

                try {
                    JSONArray errors = errorResponse.getJSONArray("errors");
                    JSONObject error = errors.getJSONObject(0);
                    Toast.makeText(HomeActivity.this, error.getString("message"),
                            Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTweetSuccess(JSONObject response) {
        Tweet tweet = Tweet.fromJSONObject(response);
        tweets.add(0, tweet);
        tweetsAdapter.notifyItemInserted(0);
    }
}
