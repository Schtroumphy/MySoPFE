package fr.eseo.hervy.sopfe.Models.database;

import androidx.annotation.NonNull;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Created on 08/10/2019 - 16:49
 *
 * @author : tiffa
 * @filename :
 */
public class SoPFEDatabaseCallback extends androidx.room.RoomDatabase.Callback {




    @Override
    public void onCreate(@NonNull SupportSQLiteDatabase db) {
        super.onCreate(db);

    }

        @Override
    public void onOpen(@NonNull SupportSQLiteDatabase db) {
        super.onOpen(db);
    }

}
