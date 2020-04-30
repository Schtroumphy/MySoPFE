package fr.eseo.hervy.sopfe.Models.database;

import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import fr.eseo.hervy.sopfe.Models.Annotation;
//import fr.eseo.hervy.sopfe.Models.dao.AnnotationDao;
import fr.eseo.hervy.sopfe.Models.Mark;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.PseudoNotes;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.dao.AnnotationDao;
import fr.eseo.hervy.sopfe.Models.dao.MarkDao;
import fr.eseo.hervy.sopfe.Models.dao.PseudoNotesDao;
import fr.eseo.hervy.sopfe.Models.dao.PseudoProjectsJuryDao;
import fr.eseo.hervy.sopfe.R;

/**
 * Created on 08/10/2019 - 16:46.
 *
 * @author : HERV
 * @filename : SoPFEDatabase
 */
@Database(entities = {Annotation.class, PseudoProject.class, PseudoNotes.class, Mark.class}, version = 1)
public abstract class SoPFEDatabase extends RoomDatabase {
    private static SoPFEDatabase INSTANCE;

    public abstract AnnotationDao annotationDao();
    public abstract PseudoProjectsJuryDao PseudoProjectsJuryDao();
    public abstract PseudoNotesDao PseudoNotesDao();
    public abstract MarkDao markDao();

    public static SoPFEDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, SoPFEDatabase.class, "sopfeDbTest")
                    .fallbackToDestructiveMigration()
                    //.allowMainThreadQueries()
                    .addCallback(new SoPFEDatabaseCallback())
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }


}