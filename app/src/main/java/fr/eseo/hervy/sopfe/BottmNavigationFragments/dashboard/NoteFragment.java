package fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
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

import fr.eseo.hervy.sopfe.Adapters.ListStudentsAdapter;
import fr.eseo.hervy.sopfe.Manager.ConnexionManager;
import fr.eseo.hervy.sopfe.Models.Annotation;
import fr.eseo.hervy.sopfe.Models.PseudoNotes;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;
import static fr.eseo.hervy.sopfe.Manager.RequestsClass.AVG_NOTE_ATTRIBUTE;
import static fr.eseo.hervy.sopfe.Manager.RequestsClass.FORENAME_ATTRIBUTE;
import static fr.eseo.hervy.sopfe.Manager.RequestsClass.MY_NOTE_ATTRIBUTE;
import static fr.eseo.hervy.sopfe.Manager.RequestsClass.SURNAME_ATTRIBUTE;
import static fr.eseo.hervy.sopfe.Manager.RequestsClass.USER_ID_ATTRIBUTE;

public class NoteFragment extends Fragment {

    private RecyclerView recyclerViewStudentsNotes;
    private TextView txt_error_access, my_annotation, txt_title, txt_pseudoNtTitle, txtView_notes;
    private List<Students> listeEtudiants;
    private EditText edt_annotation, edt_pseudoNt;
    private Button btn_validate_annotation, btn_validate_pseudoNt;
    private int projectId;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar progressBarNote;
    private Window window;
    private LinearLayout linearPseudoNt;
    private NoteViewModel noteViewModel;
    private Fragment frag = this;

    public NoteFragment(int projectId, Window window) {
        this.window = window;
        this.projectId = projectId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.fragment_notes, container, false);

        noteViewModel = ViewModelProviders.of(getActivity()).get(NoteViewModel.class);

        progressBarNote = (ProgressBar) root.findViewById(R.id.progressBarNotes);
        recyclerViewStudentsNotes = (RecyclerView) root.findViewById(R.id.recyclerView_students_notes);
        linearPseudoNt = (LinearLayout) root.findViewById(R.id.linearPseudoNt);
        txt_error_access = (TextView) root.findViewById(R.id.txt_error_access);
        txtView_notes = (TextView) root.findViewById(R.id.txtView_notes);
        //txt_pseudoNtTitle = (TextView) root.findViewById(R.id.txt_title_pseudo);
        edt_annotation = (EditText) root.findViewById(R.id.edt_annotation);
        edt_pseudoNt = (EditText) root.findViewById(R.id.edt_pseudoNt);
        btn_validate_annotation = (Button) root.findViewById(R.id.btn_validate_annotation);
        btn_validate_pseudoNt = (Button) root.findViewById(R.id.btn_validate_pseudoNt);



        my_annotation = (TextView) root.findViewById(R.id.my_annotation);
        txt_title = (TextView) root.findViewById(R.id.txt_title);


        gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerViewStudentsNotes.setLayoutManager(gridLayoutManager);

        SharedPreferences pref = getActivity().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");

        if (!username.equals("visiteur")) {
            listStudents();
        } else {
            progressBarNote.setVisibility(GONE);
            txtView_notes.setVisibility(GONE);
            btn_validate_pseudoNt.setVisibility(VISIBLE);
            edt_pseudoNt.setVisibility(VISIBLE);
            txt_title.setVisibility(VISIBLE);
        }
        final Annotation resultChecking = displayAnnotation();


        btn_validate_annotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = edt_annotation.getText().toString();
                if(!message.equals("")) {
                    Annotation annotation = new Annotation(username, projectId, message);
                    insertAnnotationToRoomDB(resultChecking, annotation, username);
                } else {
                    Snackbar.make(getView(), "Vous devez entrer une annotation !", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        if (username.equals("visiteur")) {
            linearPseudoNt.setVisibility(VISIBLE);
            my_annotation.setVisibility(GONE);

            noteViewModel.getPseudoNotesByIdProjectList(projectId).observe(this, new Observer<List<PseudoNotes>>() {
                @Override
                public void onChanged(List<PseudoNotes> pseudoNote) {
                    if (pseudoNote.size()>0) {
                        txt_title.setVisibility(VISIBLE);
                        btn_validate_pseudoNt.setVisibility(VISIBLE);
                        edt_pseudoNt.setVisibility(VISIBLE);
                    }
                }
            });

        }
        btn_validate_pseudoNt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String messagePseudoNote = edt_pseudoNt.getText().toString();
                if (!messagePseudoNote.equals("")) {
                    final PseudoNotes pseudoNotes = new PseudoNotes(username, projectId, messagePseudoNote);
                    Log.i("tag notefrag INT", messagePseudoNote);

                    noteViewModel.insertPseudoNotes(pseudoNotes);
                    //SoPFEDatabase.getDatabase(getContext()).PseudoNotesDao().insertPseudoNotes(pseudoNotes);
                    noteViewModel.getPseudoNotesByIdProjectList(projectId).observe(getViewLifecycleOwner(), new Observer<List<PseudoNotes>>() {
                        @Override
                        public void onChanged(List<PseudoNotes> listePseudoNotes) {
                            for (int i = 0; i < listePseudoNotes.size(); i++) {
                                Log.i("tag notefrag", listePseudoNotes.get(i).getMessage());
                            }
                        }
                    });

                } else {
                    Snackbar.make(getView(), "Vous devez entrer une note !", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    private Annotation displayAnnotation() {
        /* Récupérer le username et le token pour pouvoir lancer la requête
         * On récupère également le pwd car si le token a expiré on relance la requête pour connecter l'utilisateur
         * */
        final Annotation annotationToReturn = new Annotation();

        final SharedPreferences pref = getActivity().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");

        noteViewModel.getLiveDataAllAnnotationByUsernameAndProjectId(username, projectId).observe(this, new Observer<List<Annotation>>() {
            @Override
            public void onChanged(List<Annotation> annotationList) {
                Log.d("[Annotat°List LiveData]", annotationList.toString());

                /* Créer une listView pour afficher la liste des annotations s'il en a droit à plusieurs, ici juste une seule */
                if (!username.equals("visiteur")) {
                    //En tant que prof
                    if (!annotationList.isEmpty()) {
                        my_annotation.setText(annotationList.get(0).getUsername() + " - " + annotationList.get(0).getMessage());
                        annotationToReturn.setIdProject(annotationList.get(0).getIdProject());
                        annotationToReturn.setUsername(annotationList.get(0).getUsername());
                        annotationToReturn.setMessage(annotationList.get(0).getMessage());
                    } else {
                        my_annotation.setVisibility(GONE);
                    }
                } else {
                    //En tant que visiteur
                    if (!annotationList.isEmpty()) {
                        for (int i = 0; i < annotationList.size(); i++) {
                            my_annotation.setText(annotationList.get(i).getUsername() + " - " + annotationList.get(i).getMessage());
                            annotationToReturn.setIdProject(annotationList.get(i).getIdProject());
                            annotationToReturn.setUsername(annotationList.get(i).getUsername());
                            annotationToReturn.setMessage(annotationList.get(i).getMessage());
                        }

                    } else {
                        my_annotation.setVisibility(GONE);
                    }
                }
            }

        });
        return annotationToReturn;
    }



    /* Autoriser la notation pour les jpo via la base de données Room */
    private void insertAnnotationToRoomDB(final Annotation oldAnnotation, final Annotation newAnnotation, final String username) {
        Log.d("TAG DANS INSERT ", "Dans insert");

        if (!username.equals("visiteur")) {
            /* On vérifie si l'utilisateur a déjà mis une annotation à ce projet */
            if (oldAnnotation.getIdProject() != 0) { //Il y a déjà une annotation d'enregistrée
                /* On affiche une Snackbar demandant si l'annotation doit être remplacée par la nouvelle ou pas */
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false)
                        .setMessage("Vous avez déjà une annotation enregistrée, souhaitez-vous la remplacer ?")
                        .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.d("TAG ", "clic sur oui");
                                new ReplaceAnnotation(oldAnnotation, newAnnotation).onClick(getView());
                            }
                        })
                        .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

        } else {
            Log.d("TAG insertion", newAnnotation.getUsername());
            noteViewModel.insertAnnotation(newAnnotation);

                displayAnnotation();
                edt_annotation.setText("");
                final Snackbar mySnackbar = Snackbar.make(window.findViewById(R.id.containerProjectActivity), "Annotation enregistrée !", Snackbar.LENGTH_SHORT);
                mySnackbar.show();

            }
        } else {
            //On insère autant d'annotations qu'on veut si on est visiteur
            noteViewModel.insertAnnotation(newAnnotation);
            //SoPFEDatabase.getDatabase(getContext()).annotationDao().insertAnnotation(newAnnotation);
            displayAnnotation();
            edt_annotation.setText("");
            final Snackbar mySnackbar = Snackbar.make(window.findViewById(R.id.containerProjectActivity), R.string.txt_annotation_saved, Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }
    }

    private void listStudents() {
        /* Récupérer le username et le token pour pouvoir lancer la requête
         * On récupère également le pwd car si le token a expiré on relance la requête pour connecter l'utilisateur
         * */

        final SharedPreferences pref = getActivity().getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        final String token = pref.getString("token", "tokenDefault");
        final String password = pref.getString("password", "passwordDefault");

        listeEtudiants = new ArrayList<Students>();

        /* Récupérer le token dans les sharedPreference ainsi que le username */
        final String url = SERVER_IP + "NOTES&user=" + username + "&proj=" + projectId + "&token=" + token;
        Log.d("tag url", url);
        final RequestQueue queue = Volley.newRequestQueue(getContext());
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    /* Display the repsonse from the server */
                    Log.i("TAG RESPONSE ", "" + response.toString());
                    final String result = response.getString("result");

                            /* Créer la hashmap */
                            if ("OK".equals(result)) {
                                final JSONArray jsonArray = response.getJSONArray("notes");
                                if (jsonArray.length() != 0) {
                                    /* On parcourt le tableau juries de la réponse
                                     * Pour chaque étudiant, on récupère l'id, la note ...
                                     * */
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        final int userId = jsonArray.getJSONObject(i).getInt(USER_ID_ATTRIBUTE);
                                        final String forename = jsonArray.getJSONObject(i).getString(FORENAME_ATTRIBUTE);
                                        final String surname = jsonArray.getJSONObject(i).getString(SURNAME_ATTRIBUTE);
                                        int mynote = jsonArray.getJSONObject(i).optInt(MY_NOTE_ATTRIBUTE);
                                        int avgnote = jsonArray.getJSONObject(i).optInt(AVG_NOTE_ATTRIBUTE);
                                        Log.d("TAG", "tag");
                                        final Students student = new Students(userId, forename, surname, mynote, avgnote);
                                        listeEtudiants.add(student);
                                    }
                                    final ListStudentsAdapter myAdapter = new ListStudentsAdapter(getContext(), listeEtudiants, projectId, token, username, frag);
                                    recyclerViewStudentsNotes.setLayoutManager(gridLayoutManager);
                                    //myAdapter.setClickListener(this);
                                    recyclerViewStudentsNotes.setAdapter(myAdapter);
                                    progressBarNote.setVisibility(GONE);
                                } else {
                                    /* Pas de données à afficher */
                                    txt_error_access.setVisibility(VISIBLE);
                                    txt_error_access.setText(R.string.txt_no_data);
                                }

                    } else {
                        /* Gérer erreur no read access si il n'a pas accès aux notes*/
                        if (response.getString("error").equals("No Read Access")) {
                            txt_error_access.setVisibility(VISIBLE);
                            progressBarNote.setVisibility(GONE);
                        } else if (response.getString("error").equals("Invalid Credentials")) {
                            /* Reconnecter l'utilisateur pour délivrer un token valide */
                            ConnexionManager.reconnectUser(username, password, getContext());
                            /* Relancer listStudents()*/
                            listStudents();
                        }
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG ERROR lister etu", error.toString());

            }
        });

        queue.add(jsonObjectRequest);
    }

    /* Cette classe permet de supprimer une annotation déjà enregistrée et insérer la nouvelle */
    private class ReplaceAnnotation implements View.OnClickListener {

        final Annotation oldAnnotation, newAnnotation;

        public ReplaceAnnotation(Annotation oldAnnotation, Annotation newAnnotation) {
            this.oldAnnotation = oldAnnotation;
            this.newAnnotation = newAnnotation;
        }

        @Override
        public void onClick(View v) {
            Log.d("[ReplaceAnnotation] ", "Avant suppression");
            /* On supprime l'annotation */
            noteViewModel.deleteAnnotation(oldAnnotation);
            //SoPFEDatabase.getDatabase(getContext()).annotationDao().deleteAnnotation(oldAnnotation);
            Log.d("[ReplaceAnnotation] ", "Après suppression");

            /* On insert la nouvelle annotation*/
            noteViewModel.insertAnnotation(newAnnotation);
            //final long insertion = SoPFEDatabase.getDatabase(getContext()).annotationDao().insertAnnotation(newAnnotation);

                displayAnnotation();
                edt_annotation.setText("");
                final Snackbar mySnackbar = Snackbar.make(window.findViewById(R.id.containerProjectActivity), "Annotation enregistrée !", Snackbar.LENGTH_SHORT);
                mySnackbar.show();


        }

    }
}