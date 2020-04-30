package fr.eseo.hervy.sopfe.Adapters;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.hervy.sopfe.Models.Project;
import fr.eseo.hervy.sopfe.ProjectActivity;
import fr.eseo.hervy.sopfe.R;


/**
 * Created on 01/10/2019 - 09:46
 *
 * @author : HERVY Tiffaine
 * @filename : ListProjectsAdapter
 */
public class ListProjectsAdapter extends RecyclerView.Adapter<ListProjectsAdapter.ViewHolder> {
    private List<Project> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public ListProjectsAdapter(Context context, List<Project> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ListProjectsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_item, null);


        // create ViewHolder

        ListProjectsAdapter.ViewHolder viewHolder = new ListProjectsAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(final ListProjectsAdapter.ViewHolder holder, final int position) {
        final String title = mData.get(position).getTitle();
        final int projectId = mData.get(position).getProjectId();

        if(mData.get(position)!=null) {

            if (mData.get(position).getSupervisor() != null) {
                holder.txt_supervisor.setText(context.getString(R.string.Superviseur) + " : " + mData.get(position).getSupervisor().getSurname() + " " + mData.get(position).getSupervisor().getForename());
            }
            holder.txt_confid.setText(context.getString(R.string.Confidentialite) + " : " + mData.get(position).getConfid());
            holder.txt_title.setText(title);



            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.progressBar.setVisibility(View.VISIBLE);
                    final Intent intent = new Intent(view.getContext(), ProjectActivity.class);
                    new AsyncTask<Void, Void, Void>() {

                        @SuppressLint("WrongThread")
                        @Override
                        protected Void doInBackground(Void... voids) {

                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("Project", mData.get(position));
                                intent.putExtra("projectId", projectId);
                                if (mData.get(position).getSupervisor() != null) {
                                    intent.putExtra("Supervisor_surname", mData.get(position).getSupervisor().getSurname());
                                    intent.putExtra("Supervisor_forename", mData.get(position).getSupervisor().getForename());
                                }
                                context.startActivity(intent);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(final Void result){
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }.execute();
                }
            });
        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView  txt_title,txt_supervisor,txt_confid;
        CardView cardview;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            txt_supervisor = itemView.findViewById(R.id.txt_supervisor);
            txt_title = itemView.findViewById(R.id.txt_title);
            cardview = itemView.findViewById(R.id.cardView_itemProject);
            txt_confid = itemView.findViewById(R.id.txt_confid);
            progressBar= itemView.findViewById(R.id.progressBar);

        }
    }


}
