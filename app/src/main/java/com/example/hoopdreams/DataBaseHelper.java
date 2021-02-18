package com.example.hoopdreams;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.util.Log;
//import android.support.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "SESSION_DATA";
    public static final String COLUMN_TIME_ELAPSED = "TIME_ELAPSED";
    public static final String COLUMN_SHOTS_MADE = "SHOTS_MADE";
    public static final String COLUMN_SHOTS_ATTEMPTED = "SHOTS_ATTEMPTED";
    public static final String COLUMN_SESSION_DATE = "DATE";
    public static final String COLUMN_SESSION_ID = "SESSION_ID";

    public DataBaseHelper(Context context){
        super(context, "shotdata", null, 1);
    }

    public void onCreate(SQLiteDatabase db){
        String createTableStatement = "CREATE TABLE "+ TABLE_NAME + " (" + COLUMN_TIME_ELAPSED + " TEXT," +
                " "+COLUMN_SHOTS_MADE+" INT, " +COLUMN_SHOTS_ATTEMPTED+ " INT, "+COLUMN_SESSION_DATE+" TEXT, "+ COLUMN_SESSION_ID+" TEXT)";

        db.execSQL(createTableStatement);
    }

    public void onUpgrade(SQLiteDatabase db,int OldVersion, int NewVersion){

    }



    public boolean addOne(String Time, int ShotsMade, int ShotsAttempted, String date, String SessionID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TIME_ELAPSED, Time);
        cv.put(COLUMN_SHOTS_MADE, ShotsMade);
        cv.put(COLUMN_SHOTS_ATTEMPTED, ShotsAttempted);
        cv.put(COLUMN_SESSION_DATE, date);
        cv.put(COLUMN_SESSION_ID, SessionID);

        long insert = db.insert(TABLE_NAME, null, cv);
        if (insert == -1){
            return false;
        }
        else{
            return true;
        }
    }

    public Cursor getData(){
         String TAG="Get Items";
        Log.d(TAG, "Enter get Items");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(DataBaseHelper.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DataBaseHelper.COLUMN_SESSION_DATE+" DESC");
        return c;
    }
}
