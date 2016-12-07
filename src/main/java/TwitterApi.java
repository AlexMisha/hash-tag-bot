import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public final class TwitterApi {

    private TwitterApi() {}

    private static Twitter instance;

    public static Twitter getTwitter() {
        if (instance == null) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("qFmu9qMGBM0CEuuw6ZADUdEaQ")
                    .setOAuthConsumerSecret("8nhCDe8MpiNDPSSyIbwzKtkbswsxoznmnkEDh4Uqg8kA6TFWc4")
                    .setOAuthAccessToken("773282210459815937-1pi1eS63gZMO8QbwgE3zixpSVYK2jrs")
                    .setOAuthAccessTokenSecret("MyU6QeZWcn2boL5HVGMVZ6UgH80GeT0jL27WOyjJjnpK6");
            TwitterFactory tf = new TwitterFactory(cb.build());
            instance = tf.getInstance();
        }
        return instance;
    }


}
