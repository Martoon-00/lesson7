package ru.ifmo.lesson6;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

public class RssList extends Activity {
    /**
     * Called when the activity is first created.
     */

    private ArrayAdapter<String> adapter;
    private MainDatabase mDbHelper;
    private ArrayList<FeedItem> feeds;

    public void updateList(){
        feeds = mDbHelper.getAllItems();
        adapter.clear();
        for (int i = 0; i < feeds.size(); i++){
            adapter.add(feeds.get(i).param[FeedItem.NAME]);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_list);
        mDbHelper = new MainDatabase(this);
        mDbHelper.open();

        final ListView listView = (ListView) findViewById(R.id.rssList);
        final Button addButton = (Button) findViewById(R.id.addButton);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        listView.setAdapter(adapter);
        updateList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Intent intent = new Intent(RssList.this, ArticleList.class);
                intent.putExtra("feed", feeds.get(position).param);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                Intent intent = new Intent(RssList.this, ModifyFeedActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("id", Integer.parseInt(feeds.get(position).param[FeedItem.ID]));
                startActivityForResult(intent, 0);
                return true;
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RssList.this, ModifyFeedActivity.class);
                intent.putExtra("type", 0);
                startActivityForResult(intent, 0);
                updateList();
            }
        });


        UpdateBroadcastReceiver updateBroadCastReceiver = new UpdateBroadcastReceiver();
        this.registerReceiver(updateBroadCastReceiver, new IntentFilter(UpdateBroadcastReceiver.UpdateAction));

        Updater updater = new Updater();
        updater.start(RssList.this);
    }


    public class UpdateBroadcastReceiver extends BroadcastReceiver {
        final static public String UpdateAction = "ru.ifmo.action.RssUpdate";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> a = intent.getExtras().getStringArrayList("updatedFeeds");
            for (int i = 0; i < a.size(); i++){
                if (getIntent().getStringExtra("feedId") == a.get(i)){
                    updateList();
                    break;
                }
            }
            Toast.makeText(RssList.this, "Feeds were updated", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onStop();
        mDbHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            updateList();
        }
    }
}
