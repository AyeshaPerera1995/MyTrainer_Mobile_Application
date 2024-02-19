package com.aye.mytrainer.Databases;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DB {

    private static SQLiteDatabase database;


    public  static SQLiteDatabase createConnection(Context ct){
        if (database==null){
            database = ct.openOrCreateDatabase("ContactListDB",Context.MODE_PRIVATE,null);

            //create tables
            i_u_d("CREATE TABLE IF NOT EXISTS contact_details(id int NOT NULL,image VARCHAR,fname VARCHAR,lname VARCHAR,email VARCHAR,number int,PRIMARY KEY (id));");

        }

        return database;
    }


    public static void i_u_d(String query){
        database.execSQL(query);
    }

    public static Cursor search(String query){

        return database.rawQuery(query,null);
    }


    /******************************************************/


    private static SharedPreferences sp;
    private  static  SharedPreferences.Editor esp;

    public  static  SharedPreferences getSpConnection(Context ct){
        if(sp==null){
            sp = ct.getSharedPreferences("my_preference",Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static SharedPreferences.Editor getSpEditor(){
        if (sp!=null){
            if (esp==null){
                esp = sp.edit();
            }
        }
        return esp;
    }

}
