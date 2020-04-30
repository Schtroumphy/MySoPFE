package fr.eseo.hervy.sopfe;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import fr.eseo.hervy.sopfe.Models.Mark;
import fr.eseo.hervy.sopfe.Models.dao.MarkDao;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;

/**
 * Created on 22/10/2019 - 10:19.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : PopViewModel
 */
public class PopViewModel extends AndroidViewModel {
    private SoPFEDatabase soPFEDatabase;
    private MarkDao markDao;

    public PopViewModel(@NonNull Application application) {
        super(application);

        soPFEDatabase = SoPFEDatabase.getDatabase(application.getApplicationContext());
        markDao = soPFEDatabase.markDao();

    }

    public void insertMark(Mark mark) {
        //insertMarkAsync(mark);
        new insertMarkAsync2(markDao).execute(mark);
    }

    private static class insertMarkAsync2 extends AsyncTask<Mark, Void, Void> {

        private MarkDao markDao;

        insertMarkAsync2(MarkDao dao) {
            this.markDao = dao;
        }

        @Override
        protected Void doInBackground(final Mark... params) {
            markDao.insertMark(params[0]);
            return null;
        }
    }

    private void insertMarkAsync(final Mark mark) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    soPFEDatabase.markDao().insertMark(mark);
                } catch (Exception e) {
                }
            }
        }).start();

    }
}
