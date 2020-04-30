package fr.eseo.hervy.sopfe.ui.Jury;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.hervy.sopfe.Adapters.ListJuryAdapter;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteViewModel;
import fr.eseo.hervy.sopfe.HomeActivity;
import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.Models.Jury;
import fr.eseo.hervy.sopfe.Models.Members;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.Supervisor;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.R;

import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

/**
 * Created on 25/09/2019 - 14:06.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : MyJuryFragment
 */
public class MyJuryFragment extends Fragment {

    private List<Project> listeProjects;
    private List<Jury> listeJury;
    private ListJuryAdapter mAdapter;
    private View root;
    private RecyclerView recyclerView;
    private ProgressDialog dialog;
    private NoteViewModel noteViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_jury, container, false);
        noteViewModel = ViewModelProviders.of(getActivity()).get(NoteViewModel.class);


        // set up the RecyclerView
        recyclerView = root.findViewById(R.id.fragment_main_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*Recup Sharepref */
        final SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        if (username.equals("jpo")) {

            noteViewModel.getAllPseudoProject().observe(this, new Observer<List<PseudoProject>>() {
                @Override
                public void onChanged(List<PseudoProject> pseudoProjectList) {
                    final List<Project> listProject = new ArrayList<>();
                    for (int i = 0; i < pseudoProjectList.size(); i++) {
                        listProject.add(new Project(pseudoProjectList.get(i).getProjectId(),pseudoProjectList.get(i).getTitle(),pseudoProjectList.get(i).getDescrip(),0));
                    }
                    //On crée le jury
                    final Jury pseudoJury = new Jury(10000,"",new ArrayList<Members>(),listProject);
                    listeJury = new ArrayList<>();
                    listeJury.add(pseudoJury);
                    final ListJuryAdapter myAdapter = new ListJuryAdapter(getContext(), listeJury);
                    //myAdapter.setClickListener(this);
                    recyclerView.setAdapter(myAdapter);
                }
            });
            //final List<PseudoProject> pseudoProjectList = SoPFEDatabase.getDatabase(getContext()).PseudoProjectsJuryDao().findAllPseudoProject();


        } else {
            listerJury();
        }
        //else listerJury
        return root;

    }

    public void listerJury() {
        /* Récupérer le username et le token pour pouvoir lancer la requête
         * On récupère également le pwd car si le token a expiré on relance la requête pour connecter l'utilisateur
         * */
        SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        String token = pref.getString("token", "tokenDefault");
        final String password = pref.getString("password", "passwordDefault");

        listeJury = new ArrayList<>();

        /* Récupérer le token dans les sharedPreference ainsi que le username */
        final String url = SERVER_IP + "MYJUR&user=" + username + "&token=" + token;


        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            /* Display the repsonse from the server */
                            Log.i("TAG RESPONSE ", "" + response.toString());
                            final String result = response.getString("result");

                            /* Créer la hashmap */
                            if (result.equals("OK")) {

                                final JSONArray jsonArray = response.getJSONArray("juries");
                                if (jsonArray.length() != 0) {
                                    /* Oon parcours le tableau juries de la réponse
                                     * Pour chaque jury, on récupère l'id et la date dans la prmeière boucle
                                     * */

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        final int idJury = jsonArray.getJSONObject(i).getInt("idJury");
                                        final String date = jsonArray.getJSONObject(i).getString("date");
                                        final JSONArray members = jsonArray.getJSONObject(i).getJSONObject("info").getJSONArray("members");

                                        /* Parcourir la liste de smembres du jury */
                                        final List<Members> membersList = new ArrayList<>();

                                        for (int j = 0; j < members.length(); j++) {
                                           final String forenamae = members.getJSONObject(j).getString("forename");
                                           final String surname = members.getJSONObject(j).getString("surname");
                                           final Members member = new Members(forenamae, surname);

                                           membersList.add(member);
                                        }

                                        /* On parcours la liste des projets pour chaque jury
                                         * Et pour chaque projet, on crée un projet avec un supervisor
                                         * */
                                        listeProjects = new ArrayList<Project>();
                                        final JSONArray jsonArray_projects = jsonArray.getJSONObject(i).getJSONObject("info").getJSONArray("projects");
                                        for (int k=0; k< jsonArray_projects.length(); k++) {

                                            String forename = jsonArray_projects.getJSONObject(k).getJSONObject("supervisor").getString("forename");
                                            String surname = jsonArray_projects.getJSONObject(k).getJSONObject("supervisor").getString("surname");
                                            Supervisor supervisor =new Supervisor (forename, surname);

                                            /* Création du projet */
                                            int projectId= jsonArray_projects.getJSONObject(k).getInt("projectId");
                                            String title = jsonArray_projects.getJSONObject(k).getString("title");
                                            int confid = jsonArray_projects.getJSONObject(k).getInt("confid");
                                            String poster = jsonArray_projects.getJSONObject(k).getString("poster");
                                            Log.d("[POSTER] "+ k, poster);
                                            Project project = new Project(projectId, title, "", confid, poster, idJury, supervisor);

                                            /* Ajout du projet créé à la liste de projets */
                                            listeProjects.add(project);
                                        }

                                        Jury jury = new Jury(idJury, date, membersList, listeProjects);

                                        /* J'ajoute mon jury à la liste que je dois retourner */
                                        listeJury.add(jury);
                                    }

                                    ListJuryAdapter myAdapter = new ListJuryAdapter(getContext(), listeJury);
                                    //myAdapter.setClickListener(this);
                                    recyclerView.setAdapter(myAdapter);

                                } else {
                                    /* Afficher un message disant que l'utilisateur n'appartient à aucun jury */
                                    Snackbar mySnackbar = Snackbar.make(getView(), "Vous n'appartenez à aucun jury !", Snackbar.LENGTH_INDEFINITE);
                                    mySnackbar.setAction("RETOUR", new MyUndoListener());
                                    mySnackbar.show();
                                }

                            }


                            else{
                                /* Afficher un message d'erreur */
                                String error_text = response.getString("error");

                                Snackbar bar = Snackbar.make(root, "Error : "+ error_text, Snackbar.LENGTH_SHORT);
                                ViewGroup contentLay = (ViewGroup) bar.getView().findViewById(R.id.snackbar_text).getParent();
                                ProgressBar item = new ProgressBar(getContext());
                                contentLay.addView(item,0);
                                bar.show();

                                /* Reconnecter l'utilisateur afin d'avoir un token valide */
                                /* Progress Bar "Reconnexion en cours"*/
                                ConnexionManager.reconnectUser(username, password, getContext());

                                /* Relancer listerJury()*/
                                listerJury();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG ERROR ", error.toString());

                    }
                });

        queue.add(jsonObjectRequest);
    }

    public class MyUndoListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            startActivity(new Intent(getContext(), HomeActivity.class));
        }
    }
}