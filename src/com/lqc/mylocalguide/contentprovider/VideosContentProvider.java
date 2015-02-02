package com.lqc.mylocalguide.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class VideosContentProvider extends ContentProvider {

	public static final String AUTHORITY = "com.lqc.mylocalguide.videoscontentprovider.java";
	public static final String VIDEOS_PATH = "videos";
	public static final Uri VIDEOS_URI = Uri
			.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY + "/"
					+ VIDEOS_PATH);
	public static final int FULL_VIDEOS_TABLE = 0;
	public static final int SINGLE_VIDEO = 1;
	private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		mUriMatcher.addURI(AUTHORITY, VIDEOS_PATH, FULL_VIDEOS_TABLE);
		mUriMatcher.addURI(AUTHORITY, VIDEOS_PATH + "#/", SINGLE_VIDEO);
	}
	private DBHelper mDBHelper;
	
	@Override
	public boolean onCreate() {
		mDBHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		switch(mUriMatcher.match(uri)) {
		case FULL_VIDEOS_TABLE : 
			queryBuilder.setTables(ItemsHelper.TABLE_NAME);
			break;
		case SINGLE_VIDEO :
			queryBuilder.setTables(ItemsHelper.TABLE_NAME);
			queryBuilder.appendWhere(ItemsHelper._ID + "="
									+ uri.getLastPathSegment());
			break;
		}
		SQLiteDatabase mDB = mDBHelper.getReadableDatabase();
		Cursor mCursor = queryBuilder.query(mDB, projection, selection, selectionArgs, null, null, sortOrder);
		mCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return mCursor;
	}
	
	public static final String MIME_TYPE_VIDEOS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/videos";
	public static final String MIME_TYPE_VIDEO = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/video";

	@Override
	public String getType(Uri uri) {
		String mResult = "";
		switch(mUriMatcher.match(uri)) {
			case FULL_VIDEOS_TABLE:
				mResult = MIME_TYPE_VIDEOS;
				break;
			case SINGLE_VIDEO:
				mResult = MIME_TYPE_VIDEO;
				break;
		}
		return mResult;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
