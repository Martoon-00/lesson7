package ru.ifmo.lesson6;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 24.10.13
 * Time: 2:44
 * To change this template use File | Settings | File Templates.
 */
public class Reloader extends IntentService {
    final static private String TAG = "Reloader service";

    public Reloader() {
        super("RssLoader");
    }

    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MainDatabase mainDb = new MainDatabase(getApplicationContext());
        mainDb.open();
        ArticlesDatabase articlesDb = new ArticlesDatabase(getApplicationContext());
        articlesDb.open();
        ArrayList<FeedItem> feeds = mainDb.getAllItems();
        ArrayList<String> updatedFeeds = new ArrayList<String>();
        ArrayList<Article> articles;
        for (int i = 0; i < feeds.size(); i++) {
            try {
                ArrayList<Article> wasArticles = articlesDb.getAllItems(feeds.get(i).param[FeedItem.ID]);

                articles = SaxParser.parse(feeds.get(i).param[FeedItem.URL]);
                for (int j = 0; j < articles.size(); j++) {
                    articles.get(j).param[Article.FEED_ID] = feeds.get(i).param[FeedItem.ID];
                }
                articlesDb.replaceAllWith(feeds.get(i).param[FeedItem.ID], articles);

                if (wasArticles.size() == 0 || wasArticles.get(0).param[Article.TITLE] != articlesDb.getAllItems(feeds.get(i).param[FeedItem.ID]).get(0).param[Article.TITLE])
                    updatedFeeds.add(feeds.get(i).param[FeedItem.ID]);
            } catch (Exception ex) {
                Log.w(TAG, "Parser failed: " + ex.getMessage());
            }
        }
        Intent broadcastIntent = new Intent("ru.ifmo.action.RssUpdate");
        broadcastIntent.putExtra("updatedFeeds", updatedFeeds);
        sendBroadcast(broadcastIntent);
        mainDb.close();
        articlesDb.close();
        stopSelf();
    }
}

