package fr.eseo.hervy.sopfe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.LocalizeFragment;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteFragment;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.DescriptionFragment;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.PosterFragment;
import fr.eseo.hervy.sopfe.Models.Members;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.Supervisor;

public class ProjectActivity extends AppCompatActivity {


    private Project projet;
    private int projectId;
    private Supervisor supervisor;
    private List<Members> membersList;
    private String usernameSupervisor;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_test);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        navigation = (BottomNavigationView) findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SharedPreferences pref = this.getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        /* Récupère les informations contenues dans l'intent */
        final Intent intent = getIntent();
        if (intent != null) {
            projet = intent.getParcelableExtra("Project");
            projectId = intent.getIntExtra("projectId", 0);
            boolean poster = intent.getBooleanExtra("Poster", false);
            if (!username.equals("jpo")&& !username.equals("visiteur")) {
                supervisor = new Supervisor(intent.getStringExtra("Supervisor_forename"), intent.getStringExtra("Supervisor_surname"));
                membersList = intent.getParcelableArrayListExtra("MemberList");

                if(supervisor.getSurname().length()>4 && supervisor.getForename().length()>2){
                    usernameSupervisor  = supervisor.getSurname().toLowerCase().substring(0,5) + supervisor.getForename().toLowerCase().substring(0, 3);
                }else {
                    usernameSupervisor  = supervisor.getSurname().toLowerCase().substring(0,supervisor.getSurname().length()) + supervisor.getForename().toLowerCase().substring(0, 3);
                }

            }

        }
        setTitle(projet.getTitle());


        if (projet.getConfid() != 0 && !usernameSupervisor.equals(username)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setMessage(R.string.txt_confid_project_error)
                    .setNegativeButton(R.string.txt_back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        /* Affichage du fragment description du projet*/
        showFragment(new DescriptionFragment(projectId,projet, supervisor));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_description:
                    showFragment(new DescriptionFragment(projectId,projet, supervisor));
                    return true;
                case R.id.navigation_notes:
                    showFragment(new NoteFragment(projectId, getWindow()));
                    return true;
                case R.id.navigation_poster:
                    navigation.getMenu().findItem(R.id.navigation_description).setEnabled(false);
                    navigation.getMenu().findItem(R.id.navigation_notes).setEnabled(false);
                    showFragment(new PosterFragment(projet.getProjectId(), navigation));
                    return true;
                case R.id.navigation_localize:
                    showFragment(new LocalizeFragment(projet.getPoster()));
            }
            return false;
        }

    };

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
