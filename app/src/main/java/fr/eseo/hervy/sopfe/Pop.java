package fr.eseo.hervy.sopfe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteViewModel;
import fr.eseo.hervy.sopfe.Manager.InputFilterMinMax;
import fr.eseo.hervy.sopfe.Models.Annotation;
import fr.eseo.hervy.sopfe.Models.Mark;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;

import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

/**
 * Created on 11/10/2019 - 15:52.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : Pop
 *
 * C'est la classe permettant d'avoir le popup quand on clique sur une cardview avec l'étudiant, sa note et sa moyenne.
 */
public class Pop extends AppCompatActivity {

    private TextView txt_identite, txt_my_note, txt_avg_note;
    private Students student;
    private Button btn_valider;
    private EditText edt_new_note, edt_comment;
    private int projectId;
    private String token, username;
    private PopViewModel popViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupwindow);

        popViewModel = ViewModelProviders.of(this).get(PopViewModel.class);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int heigth = dm.heightPixels;
        getWindow().setLayout((int) (width*.8), (int) (heigth*.5));

        /* Récupérer la note de l'élève sur lequel on a cliqué */
        Intent intent = getIntent();
        if (intent != null) {
            student = intent.getParcelableExtra("Student");
            projectId = intent.getIntExtra("projectId", 0);
            token = intent.getStringExtra("token");
            username = intent.getStringExtra("username");
        }

        txt_identite = (TextView) findViewById(R.id.txt_identite);
        txt_avg_note = (TextView) findViewById(R.id.txt_avg_note);
        txt_my_note = (TextView) findViewById(R.id.txt_my_note);
        btn_valider = (Button) findViewById(R.id.btn_valider);
        edt_new_note = (EditText) findViewById(R.id.edt_new_note);
        edt_comment = (EditText) findViewById(R.id.edt_comment);

        final String identite = student.getSurname().toUpperCase() + " " + student.getForename();
        final int avgNote = student.getAvgNote();
        final int myNote = student.getMyNote();

        txt_identite.setText(identite);
        txt_my_note.setText("Ma note : " + myNote);
        txt_avg_note.setText("Moyenne : " + avgNote);
        btn_valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Valider la note entrée */
                if (!edt_new_note.getText().toString().equals("")) {
                    Log.d("TAG", "note : " + Integer.parseInt(edt_new_note.getText().toString()));
                    if (Integer.parseInt(edt_new_note.getText().toString()) > 20 || Integer.parseInt(edt_new_note.getText().toString()) < 0) {
                        Snackbar.make(v, R.string.txt_invalid_mark, Snackbar.LENGTH_SHORT).show();
                    } else {
                        /* Enregistrer la nouvelle note via le webservice*/
                        noter(username, projectId, Integer.parseInt(edt_new_note.getText().toString()), token,  edt_comment.getText().toString(), v);
                    }
                } else {
                    Snackbar.make(v, R.string.txt_error_empty_value, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void noter(final String username, int idProject, final int note, String token, final String comment, final View view) {
        final String url = SERVER_IP + "NEWNT&user=" + username + "&proj=" + idProject + "&student=" + student.getId() + "&note="
                + note + "&token=" + token;
        Log.d("tag url",url);
        final RequestQueue queue = Volley.newRequestQueue(this);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            /* Display the repsonse from the server */
                            String result = response.getString("result");
                            Log.d("tag reponse",response.toString());
                            if (result.equals("OK")) {
                                Snackbar.make(view, "Note enregistrée à la BDD ! ", Snackbar.LENGTH_SHORT).show();
                                /* A l'enregistrement de la note via le web service, on l'enregistre dans Room*/
                                insertMarkAndCommentToRoom(new Mark(username, projectId, student.getId(), note, comment), view);
                            } else {
                                Snackbar.make(view, response.getString("error"), Snackbar.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG : ", error.toString());

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void insertMarkAndCommentToRoom(Mark mark, View view) {
        Log.d("[INSERTION] : ", mark.getUsername());
        popViewModel.insertMark(mark);

        edt_comment.setText("");
        Snackbar.make(view, "Note et commentaires enregistrés ! ", Snackbar.LENGTH_SHORT).show();
    }
}