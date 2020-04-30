package fr.eseo.hervy.sopfe.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.DefaultRetryPolicy;
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

import java.text.CollationElementIterator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteViewModel;
import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.Models.Annotation;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.Models.PseudoNotes;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.Supervisor;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.R;
import fr.eseo.hervy.sopfe.ui.allProjects.AllProjectsViewModel;

import static android.view.View.GONE;
import static android.view.View.getDefaultSize;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;
import static fr.eseo.hervy.sopfe.R.string.myGeneratedJury;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeFragment extends Fragment {

    private AllProjectsViewModel galleryViewModel;
    private CollationElementIterator txt_error;
    private ListView listeMesProjets;
    private List<Project> projectList;
    private List<String> projectTitleList;
    private ListView listView;
    private ListView listViewJury;
    private NoteViewModel noteViewModel;
    private List<PseudoProject> listePseudo;
    private HomeFragmentViewModel homeFragmentViewModel;
    private TextView txtView_MyProjects,txtView_myJuryTitle,txtView_titleAnnotations,txt_View_titlePseudoNotes;

    private ListView listViewAnnotations,listViewPseudoNotes;

    private ProgressBar progressBar_home, progressBar_home_jury;
    private View root;
    int nbClic = 0;
    int nbClicAnnotations = 0;
    int nbClicNotes = 0;





    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        noteViewModel = ViewModelProviders.of(getActivity()).get(NoteViewModel.class);
        homeFragmentViewModel = ViewModelProviders.of(getActivity()).get(HomeFragmentViewModel.class);

        root = inflater.inflate(R.layout.fragment_home, container, false);
        final SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        final String token = pref.getString("token", "tokenDefault");
        final String password = pref.getString("password", "passwordDefault");
        listProjects(username, password, token);
        progressBar_home = (ProgressBar) root.findViewById(R.id.progressBar_home);
        progressBar_home_jury = (ProgressBar) root.findViewById(R.id.progressBar_home_jury);
        listViewAnnotations = (ListView) root.findViewById(R.id.listViewAnnotations);
        listViewPseudoNotes = (ListView) root.findViewById(R.id.listViewPseudoNotes);


        listerJury();

        listView = (ListView) root.findViewById(R.id.listViewProjets);
        listViewJury = (ListView) root.findViewById(R.id.listViewJury);
        txtView_MyProjects = (TextView) root.findViewById(R.id.txtView_MyProjects);
        txtView_myJuryTitle = (TextView) root.findViewById(R.id.txtView_myJuryTitle);
        txt_View_titlePseudoNotes = (TextView) root.findViewById(R.id.txt_View_titlePseudoNotes);
        txtView_titleAnnotations = (TextView) root.findViewById(R.id.txtView_titleAnnotations);






        //Récupérer les annotations données
        if (username.equals("jpo")) {
            listView.setVisibility(GONE);
            listViewJury.setVisibility(GONE);
            listViewAnnotations.setVisibility(GONE);
            listViewPseudoNotes.setVisibility(GONE);
            txtView_myJuryTitle.setVisibility(GONE);
            txtView_MyProjects.setText(myGeneratedJury);
            txtView_MyProjects.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nbClic==0){
                        listView.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                        params.height=400;
                        listView.setLayoutParams(params);
                        nbClic+=1;

                    } else{
                        listView.setVisibility(GONE);
                        nbClic=0;
                    }
                }
            });
            txtView_titleAnnotations.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nbClicAnnotations==0){
                        listViewAnnotations.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params = listViewAnnotations.getLayoutParams();
                        params.height=400;
                        listViewAnnotations.setLayoutParams(params);
                        nbClicAnnotations+=1;

                    } else{
                        listViewAnnotations.setVisibility(GONE);
                        nbClicAnnotations=0;
                    }
                }
            });
            txt_View_titlePseudoNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(nbClicNotes==0){
                        listViewPseudoNotes.setVisibility(View.VISIBLE);
                        ViewGroup.LayoutParams params = listViewPseudoNotes.getLayoutParams();
                        params.height=400;
                        listViewPseudoNotes.setLayoutParams(params);
                        nbClicNotes+=1;

                    } else{
                        listViewPseudoNotes.setVisibility(GONE);
                        nbClicNotes=0;
                    }
                }
            });

            //On récup les id des projets du pseudo jury
            Log.d("tag","usernameJPO");
            final List<Integer> pseudoProjectIdList = new ArrayList<>();
            final List<String> pseudoProjectTitleList = new ArrayList<>();
            List<Annotation> annotationList = new ArrayList<>();
            final List<PseudoNotes> PseudoNotesList = new ArrayList<>();
            final List<String> annotationString = new ArrayList<>();
            final List<String> pseudoNotesString = new ArrayList<>();
            noteViewModel.getAllPseudoProject().observe(this, new Observer<List<PseudoProject>>() {
                @Override
                public void onChanged(List<PseudoProject> pseudoProjectList) {
                    if (pseudoProjectList.size() != 0) {
                        for (int k = 0; k < pseudoProjectList.size(); k++) {
                            pseudoProjectIdList.add(pseudoProjectList.get(k).getProjectId());
                            pseudoProjectTitleList.add(pseudoProjectList.get(k).getTitle());

                            final int finalK = k;
                            noteViewModel.getPseudoNotesByIdProjectList(pseudoProjectIdList.get(k)).observe(getViewLifecycleOwner(), new Observer<List<PseudoNotes>>() {
                                @Override
                                public void onChanged(final List<PseudoNotes> PseudoNotesList) {

                                    noteViewModel.getLiveDataAllAnnotationByUsernameAndProjectId("visiteur",  pseudoProjectIdList.get(finalK)).observe(getViewLifecycleOwner(), new Observer<List<Annotation>>() {
                                        @Override
                                        public void onChanged(List<Annotation> annotationList) {
                                            for (int j = 0; j < annotationList.size(); j++) {
                                                annotationString.add("Projet : "+pseudoProjectTitleList.get(finalK)+" "+annotationList.get(j).getMessage());
                                                pseudoNotesString.add("Projet : "+pseudoProjectTitleList.get(finalK)+" "+PseudoNotesList.get(j).getMessage());
                                            }
                                        }
                                    });
                                }
                            });

                        }

                    }
                }
            });

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, annotationString);
            listViewAnnotations.setAdapter(adapter);
            final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_list_item_1, pseudoNotesString);
            listViewPseudoNotes.setAdapter(adapter2);

        } else {
            txt_View_titlePseudoNotes.setVisibility(GONE);
            txtView_titleAnnotations.setVisibility(GONE);
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height=400;
            listViewJury.setLayoutParams(params);
            ViewGroup.LayoutParams params2 = listViewJury.getLayoutParams();
            params2.height=400;
            listViewJury.setLayoutParams(params2);
        }

        return root;
    }


    public void listProjects(final String username, final String password, final String token) {
        final String url;
        if (username.equals("jpo")) {
            url = SERVER_IP + "PORTE&user=" + username + "&token=" + token;
        } else {
            url = SERVER_IP + "MYPRJ&user=" + username + "&token=" + token;
        }

        Log.d("TAG [URL : PORTE/MYPRJ]",url);

        projectList = new ArrayList<>();
        projectTitleList = new ArrayList<>();

        final RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            /* Display the repsonse from the server */
                            Log.d("TAG [Response] ",response.toString());
                            String result = response.getString("result");
                            if (result.equals("OK")) {
                                Log.d("tag result ok","ok");
                                String title ="",descrip="",supervisorForename,supervisorSurname,studentsSurname,studentsForename,poster;
                                int projectId = 0,confid;

                                Supervisor supervisor;
                                List<Students> listStudents;

                                    if (username.equals("jpo")) {
                                        //On prend la liste de tous les projets non confidentiels

                                        noteViewModel.getAllPseudoProject().observe(getViewLifecycleOwner(), new Observer<List<PseudoProject>>() {
                                            @Override
                                            public void onChanged(List<PseudoProject> pseudoProjects) {
                                                listePseudo = pseudoProjects;
                                            }
                                        });
                                        //List<PseudoProject> listePseudo = SoPFEDatabase.getDatabase(getContext()).PseudoProjectsJuryDao().findAllPseudoProject();
                                        if (listePseudo.size() != 0) {
                                            //faire une liste des titres de
                                            final List<String> projectTitleList = new ArrayList<>();
                                            for (int k = 0; k < listePseudo.size(); k++) {
                                                projectTitleList.add(listePseudo.get(k).getTitle());
                                            }
                                            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                                    android.R.layout.simple_list_item_1, projectTitleList);
                                            listView.setAdapter(adapter);
                                            progressBar_home.setVisibility(GONE);
                                        } else {


                                            for (int i = 0; i < response.getJSONArray("projects").length(); i++) {
                                                projectId = response.getJSONArray("projects").getJSONObject(i).getInt("idProject");
                                                descrip = response.getJSONArray("projects").getJSONObject(i).getString("description");
                                                //poster=response.getBoolean("poster");


                                                title = response.getJSONArray("projects").getJSONObject(i).getString("title");
                                                poster = response.getJSONArray("projects").getJSONObject(i).getString("poster");

                                                    projectTitleList.add(response.getJSONArray("projects").getJSONObject(i).getString("title"));
                                                    projectList.add(new Project(projectId, title, descrip, poster));

                                                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                                            android.R.layout.simple_list_item_1, projectTitleList);
                                                    listView.setAdapter(adapter);

                                                progressBar_home.setVisibility(GONE);
                                                //On en fait des pseudos projets et On les insère dans ROOM
                                                for (int j = 0; j < projectList.size(); j++) {
                                                    final PseudoProject pseudo = new PseudoProject(projectList.get(j).getProjectId(),projectList.get(j).getTitle(),projectList.get(j).getDescrip(),projectList.get(j).getPoster());

                                                    homeFragmentViewModel.insertPseudoProject(pseudo);
                                                    //SoPFEDatabase.getDatabase(getContext()).PseudoProjectsJuryDao().insertProject(pseudo);
                                                }

                                            }
                                        }
                                    } else {
                                        if (response.getJSONArray("projects").length() == 0) {
                                            progressBar_home.setVisibility(GONE);
                                        } else {
                                        for (int i = 0; i < response.getJSONArray("projects").length();i++) {
                                            projectId = Integer.parseInt(response.getJSONArray("projects").getJSONObject(i).getString("projectId"));
                                            descrip = response.getJSONArray("projects").getJSONObject(i).getString("descrip");
                                            //poster=response.getBoolean("poster");

                                            supervisorSurname = response.getJSONArray("projects").getJSONObject(i).getJSONObject("supervisor").getString("surname");
                                            supervisorForename = response.getJSONArray("projects").getJSONObject(i).getJSONObject("supervisor").getString("forename");
                                            supervisor = new Supervisor(supervisorForename, supervisorSurname);

                                            confid = response.getJSONArray("projects").getJSONObject(i).getInt("confid");

                                            JSONArray studentsArray = response.getJSONArray("projects").getJSONObject(i).getJSONArray("students");
                                            int sizeStudent = studentsArray.length();
                                            listStudents = new ArrayList<>();
                                            String forename, surname;
                                            int userId;
                                            for (int k = 0; k < sizeStudent; k++) {
                                                userId = studentsArray.getJSONObject(k).getInt("userId");
                                                forename = studentsArray.getJSONObject(k).getString("forename");
                                                surname = studentsArray.getJSONObject(k).getString("surname");
                                                Students student = new Students(userId, forename, surname);
                                                listStudents.add(student);
                                            }
                                            title = response.getJSONArray("projects").getJSONObject(i).getString("title");

                                            projectTitleList.add(response.getJSONArray("projects").getJSONObject(i).getString("title"));
                                            projectList.add(new Project(projectId, title, descrip, "", supervisor, confid, listStudents));

                                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                                    android.R.layout.simple_list_item_1, projectTitleList);
                                            listView.setAdapter(adapter);
                                            progressBar_home.setVisibility(GONE);
                                        }
                                    }


                                }


                            } else if (username.equals("visiteur")) {
                                //On prend la liste de tous les projets non confidentiels

                                noteViewModel.getAllPseudoProject().observe(getViewLifecycleOwner(), new Observer<List<PseudoProject>>() {
                                    @Override
                                    public void onChanged(List<PseudoProject> pseudoProjects) {
                                        listePseudo = pseudoProjects;
                                    }
                                });
                                    //faire une liste des titres de
                                    final List<String> projectTitleList = new ArrayList<>();
                                    for (int k = 0; k < listePseudo.size(); k++) {
                                        projectTitleList.add(listePseudo.get(k).getTitle());
                                    }
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                            android.R.layout.simple_list_item_1, projectTitleList);
                                    listView.setAdapter(adapter);
                                    progressBar_home.setVisibility(GONE);
                            } else {
                                if (!username.equals("visiteur")) {
                                    final String error_text = response.getString("error");
                                    txt_error.setText(error_text);
                                    Log.i("Tag","ERROR");

                                    /* Reconnecter l'utilisateur afin d'avoir un token valide */
                                    /* Progress Bar "Reconnexion en cours"*/
                                    ConnexionManager.reconnectUser(username, password, getContext());
                                    listProjects(username,password,token);
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG : ", error.toString());
                        progressBar_home.setVisibility(GONE);

                    }
                });
       queue.add(jsonObjectRequest);

        if (username.equals("jpo")) {
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    public void listerJury() {
        /* Récupérer le username et le token pour pouvoir lancer la requête
         * On récupère également le pwd car si le token a expiré on relance la requête pour connecter l'utilisateur
         * */

        SharedPreferences pref = getContext().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        String token = pref.getString("token", "tokenDefault");
        final String password = pref.getString("password", "passwordDefault");

        /* Récupérer le token dans les sharedPreference ainsi que le username */
        String url = SERVER_IP+ "MYJUR&user="+ username + "&token="+ token;

        final List<String> listeJuryStr = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            /* Display the repsonse from the server */
                            Log.i("TAG RESPONSE ", ""+response.toString());
                            String result = response.getString("result");

                            /* Créer la hashmap */
                            if (result.equals("OK")) {

                                JSONArray jsonArray = response.getJSONArray("juries");
                                if (jsonArray.length() != 0) {
                                    /* Oon parcours le tableau juries de la réponse
                                     * Pour chaque jury, on récupère l'id et la date dans la prmeière boucle
                                     * */

                                    String sDate1="31/12/1998";


                                    for (int i=0; i < jsonArray.length(); i++) {
                                        int idJury = jsonArray.getJSONObject(i).getInt("idJury");
                                        String date = jsonArray.getJSONObject(i).getString("date");

                                        /* On change le format de la date récupérée en dd/MM/yyyy */
                                        final String OLD_FORMAT = "yyyy-MM-dd";
                                        final String NEW_FORMAT = "dd/MM/yyyy";
                                        SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                                        Date d = sdf.parse(date);
                                        sdf.applyPattern(NEW_FORMAT);
                                        String newDateString = sdf.format(d);

                                        /* Récupérer la date du jour au format dd/MM/yyyy*/
                                        Date dateOfTheDay = new Date();
                                        String dateOfTheDayStr = new SimpleDateFormat("dd/MM/yyyy").format(dateOfTheDay);

                                        if (newDateString.equals(dateOfTheDayStr)) {
                                            ConnexionManager.showNotification(root, 1254, "RAPPEL - Jury SoPFE", "Vous avez un jury aujourd'hui !");
                                            Log.i("TAG date", "Vous avez un jury aujourd'hui ! ");
                                        } else {
                                            //ConnexionManager.showNotification(root, 1253, "RAPPEL - Jury SoPFE", "Vous n'avez pas de jury aujourd'hui");
                                            Log.i("TAG date", "C'est PAS aujourd'hui ! ");
                                        }

                                        listeJuryStr.add("Jury n°" + idJury + " - le "+ newDateString);

                                    }
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                            android.R.layout.simple_list_item_1, listeJuryStr);
                                    listViewJury.setAdapter(adapter);
                                    progressBar_home_jury.setVisibility(GONE);

                                } else {
                                    List<String> listeErreur = new ArrayList<>();
                                    listeErreur.add("Vous n'appartenez à aucun jury");
                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                            android.R.layout.simple_list_item_1, listeErreur);
                                    listViewJury.setAdapter(adapter);
                                    progressBar_home_jury.setVisibility(GONE);
                                }

                            } else if(username.equals("visiteur")){
                                List<String> listeErreur = new ArrayList<>();
                                listeErreur.add("Vous n'appartenez à aucun jury");
                                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                        android.R.layout.simple_list_item_1, listeErreur);
                                listViewJury.setAdapter(adapter);
                                progressBar_home_jury.setVisibility(GONE);
                            }

                            else {
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
                        } catch (ParseException e) {
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


}