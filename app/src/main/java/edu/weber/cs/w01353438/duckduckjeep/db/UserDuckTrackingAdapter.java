package edu.weber.cs.w01353438.duckduckjeep.db;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DateFormat;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.R;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.UserDuckTracking;

public class UserDuckTrackingAdapter extends RecyclerView.Adapter<UserDuckTrackingAdapter.ViewHolder> {
    private List<UserDuckTracking> data;
    private OnButtonClickListener listener;

    // Interface for click listener
    public interface OnButtonClickListener {
        void onButtonClick(String duckId, String documentId);
    }

    public UserDuckTrackingAdapter(List<UserDuckTracking> data, OnButtonClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.your_ducklings_recycler_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserDuckTracking item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView duckNameTextView;
        private TextView duckLocationTextView;
        private Button btn;

        public ViewHolder(View itemView) {
            super(itemView);
            duckNameTextView = itemView.findViewById(R.id.DuckName);
            duckLocationTextView = itemView.findViewById(R.id.duckSpotted);
            btn = itemView.findViewById(R.id.recyclerClickArea);
        }

        public void bind(UserDuckTracking item) {
            duckNameTextView.setText(item.getDuckName());

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(duckLocationTextView.getContext());
            duckLocationTextView.setText(dateFormat.format(item.getTimestamp().toDate()));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onButtonClick(item.getDuckDocumentId(), item.getDuckId());

                        }
                    }
                }
            });
        }
    }
}

