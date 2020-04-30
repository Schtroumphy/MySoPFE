package fr.eseo.hervy.sopfe.Adapters;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.eseo.hervy.sopfe.Models.Members;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.ProjectActivity;
import fr.eseo.hervy.sopfe.R;

/**
 * Created on 30/09/2019 - 13:27.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : SubItemAdapter
 */


public class SubItemAdapter extends RecyclerView.Adapter<SubItemAdapter.SubItemViewHolder> {

    private List<Project> subItemList;
    private List<Members> membersList;
    private Context context;

    SubItemAdapter(List<Project> subItemList, List<Members> membersList, Context context) {
        this.subItemList = subItemList;
        this.membersList = membersList;
        this.context=context;
    }

    @NonNull
    @Override
    public SubItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_sub_item, viewGroup, false);
        return new SubItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubItemViewHolder subItemViewHolder, final int i) {
        final Project subItem = subItemList.get(i);
        subItemViewHolder.tvSubItemTitle.setText(subItem.getTitle());

        subItemViewHolder.cardView_subItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subItemViewHolder.progressBar.setVisibility(View.VISIBLE);
                final Intent intent = new Intent(v.getContext(), ProjectActivity.class);

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        /* Récupérer les informations du projet un à un car on ne peut pas transférer un projet ou une liste d eprojet */
                        /* On récupère le projet sur lequel on clique et on le transfère à l'activité ProjectActivity*/

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putParcelableArrayListExtra("MemberList" ,(ArrayList<? extends Parcelable>) membersList);
                        intent.putExtra("Project", subItemList.get(i));
                        Log.d("TAG projectID sub", subItem.getProjectId()+"");
                        intent.putExtra("projectId", subItem.getProjectId());
                        SharedPreferences pref = context.getSharedPreferences("UserPref", 0);
                        final String username = pref.getString("username", "userDefault");
                        if (!username.equals("jpo")) {
                            intent.putExtra("Supervisor_surname", subItemList.get(i).getSupervisor().getSurname());
                            intent.putExtra("Supervisor_forename", subItemList.get(i).getSupervisor().getForename());

                        }

                        context.startActivity(intent);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final Void result){
                        subItemViewHolder.progressBar.setVisibility(View.GONE);
                    }
                }.execute();

            }
        });
    }

    @Override
    public int getItemCount() {
        return subItemList.size();
    }

    class SubItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubItemTitle;
        CardView cardView_subItem;
        ProgressBar progressBar;

        SubItemViewHolder(View itemView) {
            super(itemView);
            tvSubItemTitle = itemView.findViewById(R.id.tv_sub_item_title);
            cardView_subItem = itemView.findViewById(R.id.cardView_subItem);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
