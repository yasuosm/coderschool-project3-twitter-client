package xyz.annt.twitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by annt on 3/27/16.
 */
public class Tweet implements Parcelable {
    long id;
    String text;
    String createdAt;
    boolean favorited;
    boolean retweeted;
    int favoritesCount;
    int retweetsCount;
    User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public int getRetweetsCount() {
        return retweetsCount;
    }

    public void setRetweetsCount(int retweetsCount) {
        this.retweetsCount = retweetsCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static Tweet fromJSONObject(JSONObject object) {
        Tweet tweet = new Tweet();

        try {
            tweet.setId(object.getLong("id"));
            tweet.setText(object.getString("text"));
            tweet.setCreatedAt(object.getString("created_at"));
            tweet.setFavorited(object.getBoolean("favorited"));
            tweet.setRetweeted(object.getBoolean("retweeted"));
            tweet.setFavoritesCount(object.getInt("favorite_count"));
            tweet.setRetweetsCount(object.getInt("retweet_count"));
            tweet.setUser(User.fromJSONObject(object.getJSONObject("user")));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray array) {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                Tweet tweet = Tweet.fromJSONObject(object);
                if (null != tweet) {
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.text);
        dest.writeString(this.createdAt);
        dest.writeByte(favorited ? (byte) 1 : (byte) 0);
        dest.writeByte(retweeted ? (byte) 1 : (byte) 0);
        dest.writeInt(this.favoritesCount);
        dest.writeInt(this.retweetsCount);
        dest.writeParcelable(this.user, flags);
    }

    public Tweet() {
    }

    protected Tweet(Parcel in) {
        this.id = in.readLong();
        this.text = in.readString();
        this.createdAt = in.readString();
        this.favorited = in.readByte() != 0;
        this.retweeted = in.readByte() != 0;
        this.favoritesCount = in.readInt();
        this.retweetsCount = in.readInt();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Parcelable.Creator<Tweet> CREATOR = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel source) {
            return new Tweet(source);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };
}
