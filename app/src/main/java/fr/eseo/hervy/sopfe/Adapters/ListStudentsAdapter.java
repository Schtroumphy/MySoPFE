package fr.eseo.hervy.sopfe.Adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.eseo.hervy.sopfe.BottmNavigationFragments.dashboard.NoteViewModel;
import fr.eseo.hervy.sopfe.MainActivity;
import fr.eseo.hervy.sopfe.Models.Annotation;
import fr.eseo.hervy.sopfe.Models.Mark;
import fr.eseo.hervy.sopfe.Models.Students;
import fr.eseo.hervy.sopfe.Models.database.SoPFEDatabase;
import fr.eseo.hervy.sopfe.Pop;
import fr.eseo.hervy.sopfe.R;

import static android.view.View.GONE;
import static fr.eseo.hervy.sopfe.Manager.ConnexionManager.SERVER_IP;

/**
 * Created on 30/09/2019 - 22:42.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : ListStudentsAdapter
 */

public class ListStudentsAdapter extends RecyclerView.Adapter<ListStudentsAdapter.ViewHolder> {

    private List<Students> mData;
    private LayoutInflater mInflater;
    private Context context;
    private Boolean is_btn_noter;
    private int idProject;
    private String token, username;
    private Fragment fragment;
    private NoteViewModel noteViewModel;


    // data is passed into the constructor
    public ListStudentsAdapter(Context context, List<Students> data, Boolean is_btn_noter) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.is_btn_noter=is_btn_noter;

        noteViewModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
    }

    // data is passed into the constructor
    public ListStudentsAdapter(Context context, List<Students> data, int idProject, String token, String username, Fragment fragment) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.idProject=idProject;
        this.token = token;
        this.username = username;
        this.fragment = fragment;
        noteViewModel = ViewModelProviders.of((FragmentActivity) context).get(NoteViewModel.class);
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_students, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String identite = mData.get(position).getSurname().toUpperCase() + " " + mData.get(position).getForename();
        int avg_note = mData.get(position).getAvgNote();
        int my_note = mData.get(position).getMyNote();

        /* Récupère les informations contenues dans le SharedPreferences*/
        SharedPreferences pref = context.getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");
        final String token = pref.getString("token", "tokenDefault");

        holder.txt_identite.setText(identite);
        holder.txt_my_note.setText("Ma note : "+ my_note);
        holder.txt_avg_note.setText("Moyenne : " + avg_note);

        holder.cv_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Pop.class);
                intent.putExtra("Student", mData.get(position));
                intent.putExtra("projectId", idProject);
                intent.putExtra("token", token);
                intent.putExtra("username", username);
                context.startActivity(intent);
            }
        });

        /* Récupérer le commentaire de la note dans Room */
        holder.fab_info.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setTitle("Commentaire");
                builder1.setMessage(displayComment(position));
                builder1.setCancelable(true);
                builder1.setPositiveButton("Fermer",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }

        });

    }

    private String displayComment(final int i) {
        /* Récupérer le username et le token pour pouvoir lancer la requête
         * On récupère également le pwd car si le token a expiré on relance la requête pour connecter l'utilisateur
         * */
        final String[] comment = {"Null"};

        SharedPreferences pref = context.getSharedPreferences("UserPref", 0);
        final String username = pref.getString("username", "userDefault");

        /* On va chercher la liste des notes dans NoteViewModel */
        noteViewModel.getAllMarkByUsernameAndIdProject(username, idProject).observe(fragment.getViewLifecycleOwner(), new Observer<List<Mark>>() {
            @Override
            public void onChanged(List<Mark> markList) {

                /* Créer une lmistView pour afficher la liste des annotations s'il en a droit à plusieurs, ici juste une seule */
                if (!markList.isEmpty()) {
                    comment[0] =  markList.get(i).getComment();
                } else {
                    comment[0] =  "Pas de commentaire enregistré pour le moment";
                }
            }
        });

        return comment[0];




    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_my_note;
        private TextView txt_identite;
        private TextView txt_avg_note;
        private EditText edit_noter;
        private Button btn_valider;
        private  CardView cv_student;
        private FloatingActionButton fab_info;

        ViewHolder(View itemView) {
            super(itemView);
            txt_identite = itemView.findViewById(R.id.txt_identite);
            txt_my_note = itemView.findViewById(R.id.txt_my_note);
            txt_avg_note = itemView.findViewById(R.id.txt_avg_note);
            btn_valider = itemView.findViewById(R.id.btn_valider);
            edit_noter = itemView.findViewById(R.id.edit_noter);
            cv_student = itemView.findViewById(R.id.cv_student);
            fab_info= itemView.findViewById(R.id.fab_info);
        }
    }
    private void noter(String username, int idProject, int id, final int note, String token, final ViewHolder holder) {
        String url = SERVER_IP+ "NEWNT&user="+ username + "&proj="+ idProject+"&student="+id+"&note="+note+"&token="+token;
        Log.d("tag url",url);
        final RequestQueue queue = Volley.newRequestQueue(context);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            /* Display the repsonse from the server */
                            String result = response.getString("result");
                            Log.d("tag reponse",result);
                            if(result.equals("OK")){
                                holder.txt_my_note.setText("Ma note : "+Integer.toString(note));


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

}
