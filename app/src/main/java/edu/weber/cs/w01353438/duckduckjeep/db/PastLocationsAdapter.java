package edu.weber.cs.w01353438.duckduckjeep.db;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.type.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.R;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;

public class PastLocationsAdapter extends RecyclerView.Adapter<PastLocationsAdapter.ViewHolder> {

    private List<duckLocation> data;

    public onClickListener listener;
    public interface onClickListener{
        void onClickable(GeoPoint geoPoint);
    }

    //Constructor
    public PastLocationsAdapter(List<duckLocation> data, onClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_locations_recyclerview, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        duckLocation item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView City;
        TextView Region;
        TextView Coords;
        TextView Time;

        Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            City = itemView.findViewById(R.id.City);
            Region = itemView.findViewById(R.id.Region);
            Coords = itemView.findViewById(R.id.coords);
            Time = itemView.findViewById(R.id.createdat);
            btn = itemView.findViewById(R.id.buttonClickArea);

        }

        public void bind(duckLocation item) {
            //GeoPoint geoPoint = new GeoPoint(item.getLocation().split(","));
            City.setText(item.getCity());
            Region.setText(item.getState());
            Coords.setText(item.getLocation().getLongitude() + ", " + item.getLocation().getLatitude());

            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(Time.getContext());
            Time.setText(dateFormat.format(item.getTimestamp().toDate()).toString());

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onClickable(item.getLocation());
                    }
                }
            });

            //Time.setText(item.getTimestamp().toDate().toString());
        }
    }
}
