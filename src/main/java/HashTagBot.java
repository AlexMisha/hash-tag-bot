import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import twitter4j.*;
import twitter4j.api.SearchResource;

import java.util.ArrayList;
import java.util.Random;

public class HashTagBot extends TelegramLongPollingBot {

    private final static String POPULAR = "/popular";
    private int retwCount = 0;

    @Override
    public String getBotUsername() {
        return "TwitterPopularHashtagsBot";
    }

    @Override
    public String getBotToken() {
        return "273178715:AAFtY8cWqg_9Szf3uEZ9nmz2Nujw1qbWySM";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (message.getText().startsWith(POPULAR)) {
                outputPopularHashTags(message);
            }
        }
    }

    private void outputPopularHashTags(Message message) {
        Twitter twitter = TwitterApi.getTwitter();
        try {
            Trend[] trends = twitter.trends().getPlaceTrends(23424936).getTrends();
            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < trends.length - 1; i++) {
                String hashtag = getHashTag(trends[i].getName());
                builder.append("<a href=\"").append(trends[i].getURL()).append("\">").append(hashtag).append("</a>");
                builder.append(getUpDown());
                builder.append(getTweetsCount(hashtag, message));
                builder.append(getRetweetsCount(message));
                builder.append("\n");
            }
            sendMessage(message, builder.toString());
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    private String getUpDown() {
        Random random = new Random();
        int r = random.nextInt(2);
        if (r == 0) {
            return " \u2b06 ";
        }
        return " \u2b07 ";
    }

    // Implement this
    private String getTweetsCount(String searchString, Message message) {
    	int m = 0;
    	int retweetCount = 0;
    	
    	long lastID = Long.MAX_VALUE;
    	
    	Query query = new Query(searchString);
    	QueryResult result = null;

    	Twitter twitter = TwitterApi.getTwitter();
    	ArrayList<Status> tweets = new ArrayList<Status>();
    	
    	tweets = tweetsCountProc(tweets, query, result, twitter, lastID);
    	
    	m = tweets.size();
    	if (m < 20) {
    		for (Status t: tweets) 
  	          if(t.getId() < lastID) 
  	              lastID = t.getId();
    		lastID -= 100000;
    		
    		tweets = tweetsCountProc(tweets, query, result, twitter, lastID);
    		m = tweets.size();
    	}
    	
    	for (Status t: tweets){
    		retweetCount += t.getRetweetCount();
    	}
    	retwCount = retweetCount;
    	
    	if (message.getText().contains("15")) m /= 2;
    	else if (message.getText().contains("30")) m /= 4;
    	
        return " Твиты: " + m;
    }

    // Implement this
    private String getRetweetsCount(Message message) {
        int m = 0;
        
        m = retwCount;
        
        if (message.getText().contains("15")) m /= 2;
    	else if (message.getText().contains("30")) m /= 4;
        
        return " Ретвиты: " + m;
    }


    private String getHashTag(String string) {
        if (!string.startsWith("#")) {
            return "#" + string;
        }
        return string;
    }

    private void sendMessage(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableHtml(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    
    private ArrayList<Status> tweetsCountProc(ArrayList<Status> tweets, Query query, QueryResult result, SearchResource twitter, long lastID) {
    	int i = 0;
    	int numberOfTweets = 4096;
    	
    	do {
    		i++;
    		
    		int tweetsSize = tweets.size();
    		
    		if (tweetsSize != 0) query.setMaxId(lastID);
    		
    		if (numberOfTweets - tweetsSize > 100){
    	        query.count(100);
    		}
    	      else {
    	        query.count(numberOfTweets - tweetsSize);
    	      }
    		
        	try
      	  	{
        		result = twitter.search(query);
        		tweets.addAll(result.getTweets());
        		for (Status t: tweets) 
        	          if(t.getId() < lastID) 
        	              lastID = t.getId();
      	  	}  
      	  	catch (TwitterException te)
      	  	{
      	  		te.printStackTrace();
      	  	}
        	query.setMaxId(lastID-1);
    	} while ((tweets.size() < numberOfTweets) && ((query = result.nextQuery()) != null) && (i < 10));
    	
		return tweets;
    }


}


