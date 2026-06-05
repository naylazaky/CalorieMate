package com.example.caloriemate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.caloriemate.model.FoodLog;
import com.example.caloriemate.model.User;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "caloriemate.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_CALORIE_TARGET = "calorie_target";

    private static final String TABLE_FOOD_LOG = "food_log";
    private static final String COL_LOG_ID = "id";
    private static final String COL_LOG_USER_ID = "user_id";
    private static final String COL_LOG_FOOD_NAME = "food_name";
    private static final String COL_LOG_CALORIES = "calories";
    private static final String COL_LOG_PROTEIN = "protein";
    private static final String COL_LOG_FAT = "fat";
    private static final String COL_LOG_CARBS = "carbs";
    private static final String COL_LOG_DATE = "date_logged";
    private static final String COL_LOG_TIME = "time_logged";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_CALORIE_TARGET + " INTEGER NOT NULL)";

        String createFoodLog = "CREATE TABLE " + TABLE_FOOD_LOG + " (" +
                COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LOG_USER_ID + " INTEGER NOT NULL, " +
                COL_LOG_FOOD_NAME + " TEXT NOT NULL, " +
                COL_LOG_CALORIES + " REAL, " +
                COL_LOG_PROTEIN + " REAL, " +
                COL_LOG_FAT + " REAL, " +
                COL_LOG_CARBS + " REAL, " +
                COL_LOG_DATE + " TEXT, " +
                COL_LOG_TIME + " TEXT)";

        db.execSQL(createUsers);
        db.execSQL(createFoodLog);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_LOG);
        onCreate(db);
    }

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, user.getName());
        values.put(COL_USER_EMAIL, user.getEmail());
        values.put(COL_USER_PASSWORD, user.getPassword());
        values.put(COL_USER_CALORIE_TARGET, user.getCalorieTarget());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
            user.setCalorieTarget(cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_CALORIE_TARGET)));
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean updateCalorieTarget(int userId, int newTarget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_CALORIE_TARGET, newTarget);
        int rows = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean addFoodLog(FoodLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LOG_USER_ID, log.getUserId());
        values.put(COL_LOG_FOOD_NAME, log.getFoodName());
        values.put(COL_LOG_CALORIES, log.getCalories());
        values.put(COL_LOG_PROTEIN, log.getProtein());
        values.put(COL_LOG_FAT, log.getFat());
        values.put(COL_LOG_CARBS, log.getCarbs());
        values.put(COL_LOG_DATE, log.getDateLogged());
        values.put(COL_LOG_TIME, log.getTimeLogged());
        long result = db.insert(TABLE_FOOD_LOG, null, values);
        db.close();
        return result != -1;
    }

    public List<FoodLog> getFoodLogsByDate(int userId, String date) {
        List<FoodLog> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FOOD_LOG, null,
                COL_LOG_USER_ID + "=? AND " + COL_LOG_DATE + "=?",
                new String[]{String.valueOf(userId), date}, null, null, COL_LOG_TIME + " ASC");
        while (cursor.moveToNext()) {
            FoodLog log = new FoodLog();
            log.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ID)));
            log.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_USER_ID)));
            log.setFoodName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_FOOD_NAME)));
            log.setCalories(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LOG_CALORIES)));
            log.setProtein(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LOG_PROTEIN)));
            log.setFat(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LOG_FAT)));
            log.setCarbs(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LOG_CARBS)));
            log.setDateLogged(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_DATE)));
            log.setTimeLogged(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_TIME)));
            list.add(log);
        }
        cursor.close();
        db.close();
        return list;
    }

    public boolean deleteFoodLog(int logId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_FOOD_LOG, COL_LOG_ID + "=?",
                new String[]{String.valueOf(logId)});
        db.close();
        return rows > 0;
    }
}