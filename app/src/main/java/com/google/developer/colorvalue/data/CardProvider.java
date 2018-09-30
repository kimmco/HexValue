package com.google.developer.colorvalue.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import static com.google.developer.colorvalue.data.CardProvider.Contract.CONTENT_URI;
import static com.google.developer.colorvalue.data.CardProvider.Contract.TABLE_NAME;

public class CardProvider extends ContentProvider {

    /** Matcher identifier for all cards */
    private static final int CARD = 100;
    /** Matcher identifier for one card */
    private static final int CARD_WITH_ID = 102;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.google.developer.colorvalue/cards
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME, CARD);
        // content://com.google.developer.colorvalue/cards/#
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME + "/#", CARD_WITH_ID);
    }

    private CardSQLite mCardSQLite;

    @Override
    public boolean onCreate() {
        mCardSQLite = new CardSQLite(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        return null;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
            @Nullable String selection, @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {
        // TODO Implement query function by Uri all cards or single card by id
        final SQLiteDatabase db = mCardSQLite.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor returnCursor;

        switch (match) {
            case CARD:
                returnCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CARD_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{id};

                returnCursor = db.query(TABLE_NAME,
                        projection,
                        Contract.Columns._ID + "=?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // TODO Implement insert new color and return Uri with ID

        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri retUri;

        switch (match){
            case CARD:
                long id = db.insert(TABLE_NAME,
                        null,
                        values);
                if (id > 0){
                    retUri = ContentUris.withAppendedId(CONTENT_URI, id);
                }else {
                    throw new android.database.SQLException("Failed to insert into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return  retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
            @Nullable String[] selectionArgs) {
        // TODO delete card by Uri
        final SQLiteDatabase db = mCardSQLite.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int colorDeleted;

        switch (match){
            case CARD_WITH_ID:
                String id = uri.getLastPathSegment();
                colorDeleted = db.delete(TABLE_NAME ,
                        Contract.Columns._ID + " =? ",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (colorDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return colorDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
            @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("This provider does not support updates");
    }

    /**
     * Database contract
     */
    public static class Contract {
        public static final String TABLE_NAME = "cards";
        public static final String CONTENT_AUTHORITY = "com.google.developer.colorvalue";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
                .authority(CONTENT_AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        public static final class Columns implements BaseColumns {
            public static final String COLOR_HEX = "question";
            public static final String COLOR_NAME = "answer";
        }
    }

}
