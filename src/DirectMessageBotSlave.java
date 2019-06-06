import twitter4j.DirectMessage;
import twitter4j.DirectMessageList;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DirectMessageBotSlave implements Runnable {

    private Twitter twitter;
    private DirectMessage message;
    private String baseURL;


    public DirectMessageBotSlave(Twitter twitter, DirectMessage directMessage, String url) {
        this.twitter = twitter;
        this.message = directMessage;
        this.baseURL = url;
    }

    @Override
    public void run() {
        var hashtagsText = new ArrayList<String>();
        var hashtags = message.getHashtagEntities();
        for (var index : hashtags) {
            hashtagsText.add(index.getText());
        }
        if (!hashtagsText.isEmpty()) {
                try {
                    var feedReader = new FeedReader();
                    var feed = feedReader.read(baseURL + hashtagsText.get(0));
                    var articles = feed.collect(Collectors.toList());
                    var currentMessageID = twitter.sendDirectMessage(message.getSenderId(), articles.get(0).getLink().orElse("No News")).getId();
                    twitter.destroyDirectMessage(message.getId());
                    twitter.destroyDirectMessage(currentMessageID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        } else {
            try {
                twitter.destroyDirectMessage(message.getId());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
        }
    }
}