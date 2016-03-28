package xyz.annt.twitterclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import xyz.annt.twitterclient.R;
import xyz.annt.twitterclient.activities.TweetActivity;
import xyz.annt.twitterclient.models.Tweet;

/**
 * Created by annt on 3/27/16.
 */
public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    ArrayList<Tweet> tweets;
    Context context;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.ivUserProfileImage) RoundedImageView ivUserProfileImage;
        @Bind(R.id.tvCreatedAt) TextView tvCreatedAt;
        @Bind(R.id.tvUserName) TextView tvUserName;
        @Bind(R.id.tvUserScreenName) TextView tvUserScreenName;
        @Bind(R.id.tvText) TextView tvText;
        @Bind(R.id.ibtnReply) ImageButton ibtnReply;
        @Bind(R.id.ibtnRetweet) ImageButton ibtnRetweet;
        @Bind(R.id.ibtnFavorite) ImageButton ibtnFavorite;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Tweet tweet = tweets.get(position);
            Intent intent = new Intent(context, TweetActivity.class);
            intent.putExtra("tweet", tweet);
            context.startActivity(intent);
        }
    }

    public TweetsAdapter(ArrayList<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(TweetsAdapter.ViewHolder holder, int position) {
        Tweet tweet = tweets.get(position);

        holder.ivUserProfileImage.setImageResource(0);
        Glide.with(context).load(tweet.getUser().getProfileImageUrl())
                .into(holder.ivUserProfileImage);

        String createdRelative = getRelativeTimeAgo(tweet.getCreatedAt());
        holder.tvCreatedAt.setText(createdRelative);

        holder.tvUserName.setText(tweet.getUser().getName());

        String userScreenName = context.getResources().getString(R.string.user_screen_name_prefix)
                + tweet.getUser().getScreenName();
        holder.tvUserScreenName.setText(userScreenName);

        holder.tvText.setText(tweet.getText());

        holder.ibtnRetweet.clearColorFilter();
        if (tweet.isRetweeted()) {
            holder.ibtnRetweet.setColorFilter(
                    context.getResources().getColor(R.color.colorRetweetActive));
        }

        holder.ibtnFavorite.clearColorFilter();
        if (tweet.isFavorited()) {
            holder.ibtnFavorite.setColorFilter(
                    context.getResources().getColor(R.color.colorFavoriteActive));
        }
    }

    @Override
    public int getItemCount() {
        if (null == tweets) {
            return 0;
        }

        return tweets.size();
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }
}
