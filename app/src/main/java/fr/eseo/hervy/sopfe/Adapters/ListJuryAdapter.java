package fr.eseo.hervy.sopfe.Adapters;



import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.AuthProvider;
import java.util.List;

import fr.eseo.hervy.sopfe.Models.Jury;
import fr.eseo.hervy.sopfe.Models.Members;
import fr.eseo.hervy.sopfe.R;

import static android.view.View.GONE;

/**
 * Created on 25/09/2019 - 14:22.
 *
 * @author : JEAN-LOUIS Thessalène
 * @filename : ListJuryAdapter
 */
public class ListJuryAdapter extends RecyclerView.Adapter<ListJuryAdapter.ViewHolder> {

    private List<Jury> mData;
    private LayoutInflater mInflater;
    private Context context;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    // data is passed into the constructor
    public ListJuryAdapter(Context context, List<Jury> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.jury_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String dateJury = mData.get(position).getDate();
        final int idJury = mData.get(position).getId();
        final int nb_projects = mData.get(position).getProjectList().size();
        final List<Members> membersList = mData.get(position).getMemberList();

        //Log.d("TAG members ", ""+membersList.size());
        String text = "";

        for (int i = 0; i < membersList.size(); i++) {
            text += membersList.get(i).getForename() + " " + membersList.get(i).getSurname() + "\n";
        }

        holder.txt_members.setText(text);

        holder.txt_id.setText("ID du jury : " + idJury);
        holder.txt_nb_projects.setText("Nombre de projets : "+ nb_projects);
        holder.txt_date.setText("Date du jury : " + dateJury);

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.rvSubItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );

        GridLayoutManager gridLayoutManager = new GridLayoutManager(holder.rvSubItem.getContext(), 2);
        layoutManager.setInitialPrefetchItemCount(mData.get(position).getProjectList().size());

        // Create sub item view adapter
        SubItemAdapter subItemAdapter = new SubItemAdapter(mData.get(position).getProjectList(), mData.get(position).getMemberList(), context);

        holder.rvSubItem.setLayoutManager(layoutManager);
        holder.rvSubItem.setAdapter(subItemAdapter);
        holder.rvSubItem.setRecycledViewPool(viewPool);


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_date, txt_id, txt_nb_projects, txt_members;
        RecyclerView rvSubItem;

        ViewHolder(View itemView) {
            super(itemView);
            txt_id = itemView.findViewById(R.id.txt_id);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_nb_projects = itemView.findViewById(R.id.txt_nb_projects);
            txt_members = itemView.findViewById(R.id.txt_members);
            itemView.setOnClickListener(this);
            rvSubItem = itemView.findViewById(R.id.rv_sub_item);
        }

        @Override
        public void onClick(View view) {
            if (rvSubItem.getVisibility() == GONE) {
                rvSubItem.setVisibility(View.VISIBLE);
            } else {
                rvSubItem.setVisibility(View.GONE);
            }

        }
    }
}