package com.google.developer.colorvalue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.developer.colorvalue.data.Card;
import com.google.developer.colorvalue.data.CardAdapter;
import com.google.developer.colorvalue.data.CardProvider;
import com.google.developer.colorvalue.service.NotificationJobService;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, CardAdapter.OnColorClickedHandler{

    private static final String TAG = "Main Activity";
    public static final String EXTRA_CARD = "com.google.developer.colorvalue_extra_card";

    private static final int COLOR_LOADER_ID = 28;

    private CardAdapter mCardAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        mCardAdapter = new CardAdapter(this);
        recycler.setAdapter(mCardAdapter);
        recycler.setHasFixedSize(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(COLOR_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mColorData = null;

            @Override
            protected void onStartLoading() {
                if (mColorData != null){
                    deliverResult(mColorData);
                }else {
                    forceLoad();

                }
            }
            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(CardProvider.Contract.CONTENT_URI,
                            null,
                            null,
                            null,
                            CardProvider.Contract.Columns._ID);
                }catch (Exception e){
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable Cursor data) {
                mColorData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCardAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardAdapter.swapCursor(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(COLOR_LOADER_ID, null, this);

    }

    @Override
    public void onColorClicked(Card card) {
        Intent colorDetailIntent = new Intent(this, CardActivity.class);
        colorDetailIntent.putExtra(EXTRA_CARD, card);
        startActivity(colorDetailIntent);
    }


}
