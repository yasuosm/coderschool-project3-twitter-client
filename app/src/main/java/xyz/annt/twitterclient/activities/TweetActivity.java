package xyz.annt.twitterclient.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.annt.twitterclient.R;
import xyz.annt.twitterclient.models.Tweet;

public class TweetActivity extends AppCompatActivity {
    private Tweet tweet;

    @Bind(R.id.ivUserProfileImage) RoundedImageView ivUserProfileImage;
    @Bind(R.id.tvCreatedAt) TextView tvCreatedAt;
    @Bind(R.id.tvUserName) TextView tvUserName;
    @Bind(R.id.tvUserScreenName) TextView tvUserScreenName;
    @Bind(R.id.tvText) TextView tvText;
    @Bind(R.id.tvRetweetsCount) TextView tvRetweetsCount;
    @Bind(R.id.tvFavoritesCount) TextView tvFavoritesCount;
    @Bind(R.id.ibtnReply) ImageButton ibtnReply;
    @Bind(R.id.ibtnRetweet) ImageButton ibtnRetweet;
    @Bind(R.id.ibtnFavorite) ImageButton ibtnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tweet = getIntent().getParcelableExtra("tweet");

        populateViews();
    }

    private void populateViews() {
        ivUserProfileImage.setImageResource(0);
        Glide.with(this).load(tweet.getUser().getProfileImageUrl()).into(ivUserProfileImage);

        tvUserName.setText(tweet.getUser().getName());

        String userScreenName = this.getResources().getString(R.string.user_screen_name_prefix)
                + tweet.getUser().getScreenName();
        tvUserScreenName.setText(userScreenName);

        tvText.setText(tweet.getText());

        String createdRelative = getDateString(tweet.getCreatedAt());
        tvCreatedAt.setText(createdRelative);

        DecimalFormat formatter = new DecimalFormat("#,###,###");
        String retweetsCount = formatter.format(tweet.getRetweetsCount());
        tvRetweetsCount.setText(retweetsCount);

        String favoritesCount = formatter.format(tweet.getFavoritesCount());
        tvFavoritesCount.setText(favoritesCount);

        if (tweet.isRetweeted()) {
            ibtnRetweet.setColorFilter(
                    this.getResources().getColor(R.color.colorRetweetActive));
        }

        if (tweet.isFavorited()) {
            ibtnFavorite.setColorFilter(
                    this.getResources().getColor(R.color.colorFavoriteActive));
        }
    }

    private String getDateString(String rawJsonDate){
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat fromFormat = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        fromFormat.setLenient(true);

        try {
            long dateMillis = fromFormat.parse(rawJsonDate).getTime();
            SimpleDateFormat toFormat = new SimpleDateFormat("h:m a · d MMM yy");
            Date date = (new Date(dateMillis));
            return toFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
