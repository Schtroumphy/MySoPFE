package fr.eseo.hervy.sopfe.ui.home;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import fr.eseo.hervy.sopfe.Models.Mark;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.dao.MarkDao;
import fr.eseo.hervy.sopfe.Models.dao.PseudoNotesDao;
import fr.eseo.hervy.sopfe.Models.dao.PseudoProjectsJuryDao;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.PopViewModel;

/**
 * Created on 22/10/2019 - 14:25.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : HomeFfragmentViewModel
 */
public class HomeFragmentViewModel extends AndroidViewModel {
    private SoPFEDatabase soPFEDatabase;

    private PseudoNotesDao pseudoNotesDao;
    private PseudoProjectsJuryDao pseudoProjectsJuryDao;


    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);

        soPFEDatabase = SoPFEDatabase.getDatabase(application.getApplicationContext());
        pseudoProjectsJuryDao = soPFEDatabase.PseudoProjectsJuryDao();


    }

    public void insertPseudoProject(PseudoProject pseudoProject) {
        //pseudoProjectsJuryDao.insertProject(pseudoProject);
        new insertPseudoProjectAsync(pseudoProjectsJuryDao).execute(pseudoProject);
    }

    private static class insertPseudoProjectAsync extends AsyncTask<PseudoProject, Void, Void> {

        private PseudoProjectsJuryDao pseudoProjectsJuryDao;

        insertPseudoProjectAsync(PseudoProjectsJuryDao pseudoProjectsJuryDao) {
            this.pseudoProjectsJuryDao = pseudoProjectsJuryDao;
        }

        @Override
        protected Void doInBackground(final PseudoProject... params) {
            pseudoProjectsJuryDao.insertProject(params[0]);
            return null;
        }
    }


}
