package fr.eseo.hervy.sopfe.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import fr.eseo.hervy.sopfe.Models.PseudoProject;
import fr.eseo.hervy.sopfe.ProjectActivity;
import fr.eseo.hervy.sopfe.R;

/**
 * Created on 15/10/2019 - 09:13
 *
 * @author : HERVY Tiffaine
 * @filename : ListPseudoProjectAdapter
 */
public class ListPseudoProjectAdapter extends RecyclerView.Adapter<ListPseudoProjectAdapter.ViewHolder>{
    private List<PseudoProject> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public ListPseudoProjectAdapter(Context context, List<PseudoProject> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ListPseudoProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.project_item, null);


        // create ViewHolder

        ListPseudoProjectAdapter.ViewHolder viewHolder = new ListPseudoProjectAdapter.ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final String title = mData.get(position).getTitle();


        if(mData.get(position)!=null) {


            holder.txt_title.setText(title);
            holder.txt_supervisor.setVisibility(View.GONE);
            holder.txt_confid.setVisibility(View.GONE);
            holder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ProjectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Project", mData.get(position));
                    intent.putExtra("projectId", mData.get(position).getProjectId());

                    context.startActivity(intent);
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
        TextView txt_title,txt_supervisor,txt_confid;
        CardView cardview;
        ViewHolder(View itemView) {
            super(itemView);
            txt_supervisor = itemView.findViewById(R.id.txt_supervisor);
            txt_title = itemView.findViewById(R.id.txt_title);
            cardview = itemView.findViewById(R.id.cardView_itemProject);
            txt_confid = itemView.findViewById(R.id.txt_confid);

        }
    }
}
