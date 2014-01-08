package ru.ifmo.lesson6;

/**
 * Created by asus on 06.01.14.
 */
public class FeedItem {

    final static String[] tags = new String[]{"ID", "NAME", "URL"};
    final static int ID = 0;
    final static int NAME = 1;
    final static int URL = 2;
    String[] param = new String[tags.length];

    FeedItem makeCopy() {
        FeedItem a = new FeedItem() ;
        for (int i = 0; i < param.length; i++) {
            a.param[i] = this.param[i];
        }
        return a;
    }

    void clear() {
        param = new String[tags.length];
    }
}
