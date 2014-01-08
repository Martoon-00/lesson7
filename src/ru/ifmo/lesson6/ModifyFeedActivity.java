package ru.ifmo.lesson6;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by asus on 07.01.14.
 */
public class ModifyFeedActivity extends Activity {

    MainDatabase mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_feed);
        mDbHelper = new MainDatabase(this);
        mDbHelper.open();


        final Button modifyButton = (Button) findViewById(R.id.modifyButton);
        final Button deleteButton = (Button) findViewById(R.id.deleteButton);
        final TextView title = (TextView) findViewById(R.id.modifyTitle);
        final EditText nameText = (EditText) findViewById(R.id.modifyNameText);
        final EditText urlText = (EditText) findViewById(R.id.modifyUrlText);

        int type = getIntent().getExtras().getInt("type");

        if (type == 0) {
            modifyButton.setText("Add");
            deleteButton.setText("Cancel");
            title.setText("New feed");

            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nameText.getText().toString().isEmpty() || urlText.getText().toString().isEmpty()) return;
                    FeedItem feed = new FeedItem();
                    feed.param[FeedItem.NAME] = nameText.getText().toString();
                    feed.param[FeedItem.URL] = urlText.getText().toString();
                    mDbHelper.addItem(feed);
                    Intent intentService = new Intent(ModifyFeedActivity.this, Reloader.class);
                    ModifyFeedActivity.this.startService(intentService);
                    setResult(RESULT_OK);
                    finish();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            modifyButton.setText("Modify");
            deleteButton.setText("Delete");
            title.setText("Change feed");
            final int id = getIntent().getExtras().getInt("id");
            final FeedItem feed = mDbHelper.getItem(id);

            nameText.setText(feed.param[FeedItem.NAME]);
            urlText.setText(feed.param[FeedItem.URL]);

            modifyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (nameText.getText().toString().isEmpty() || urlText.getText().toString().isEmpty()) return;
                    feed.param[FeedItem.NAME] = nameText.getText().toString();
                    feed.param[FeedItem.URL] = urlText.getText().toString();
                    mDbHelper.updateItem(feed, id);
                    Intent intentService = new Intent(ModifyFeedActivity.this, Reloader.class);
                    ModifyFeedActivity.this.startService(intentService);
                    setResult(RESULT_OK);
                    finish();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDbHelper.deleteItem(feed);
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }
}
