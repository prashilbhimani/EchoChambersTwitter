package communitydetection.restapi;

public class TweetClass {

    private final long tweetId;
    private final String tweetText;

    public TweetClass(long tweetId, String tweettext) {
        this.tweetId = tweetId;
        this.tweetText = tweettext;
    }

    public long getTweetId() {
        return tweetId;
    }

    public String getTweetText() {
        return tweetText;
    }
}