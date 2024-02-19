package com.aye.mytrainer.Databases;

import android.content.Context;
import android.widget.ListView;

import com.aye.mytrainer.Adapters.ProfileAdapter;
import com.aye.mytrainer.Model.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseDB {

        private static DatabaseReference database;
        private static StorageReference storage;

        public static DatabaseReference getDbConnection(){
            if (database==null){
                database = FirebaseDatabase.getInstance().getReference();
            }
            return database;

        }

        public static StorageReference getStorageConnection(){
            if (storage==null){
                storage = FirebaseStorage.getInstance().getReference();
            }
            return storage;

        }

}
