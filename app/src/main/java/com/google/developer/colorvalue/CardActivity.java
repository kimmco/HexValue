package com.google.developer.colorvalue;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.service.CardService;
import com.google.developer.colorvalue.ui.ColorView;



public class CardActivity extends AppCompatActivity {

    private ColorView mDetailsColorView;
    private TextView mDetailsColorName,
            mDetailsColorHex;

    private Card mCard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mDetailsColorView = findViewById(R.id.details_color_view);
        mDetailsColorName = findViewById(R.id.details_color_name);
        mDetailsColorHex = findViewById(R.id.details_hex_value);

        if (getIntent() != null) {
            mCard = getIntent().getParcelableExtra(MainActivity.EXTRA_CARD);
            mDetailsColorName.setText(mCard.getName());
            mDetailsColorHex.setText(mCard.getHex());
            mDetailsColorView.setColor(mCard.getColorInt());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            CardService.deleteCard(this, mCard.getUri());
            CardActivity.this.finish();
        } else if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);

//            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
//            } else {
//                NavUtils.navigateUpTo(this, upIntent);
//            }
        }

        return super.onOptionsItemSelected(item);
    }

}
