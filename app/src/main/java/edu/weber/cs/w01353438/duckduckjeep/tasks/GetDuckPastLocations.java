package edu.weber.cs.w01353438.duckduckjeep.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;

public class GetDuckPastLocations extends AsyncTask<Void, Void, List<duckLocation>> {

    private FirebaseFirestore db;
    private OnTaskCompleteListener listener;
    private String duckId;

    public interface OnTaskCompleteListener {
        void onTaskComplete(List<duckLocation> results);
    }

    public GetDuckPastLocations(FirebaseFirestore db, String duckId, OnTaskCompleteListener listener) {
        this.db = db;
        this.listener = listener;
        this.duckId = duckId;
    }

    @Override
    protected List<duckLocation> doInBackground(Void... voids) {
        List<duckLocation> duckLocations = new ArrayList<>();
        db.collection("duckTracking")
                .whereEqualTo("duckId", duckId)
                .limit(12)
                //.orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                duckLocation duckLocation = new duckLocation();

                                duckLocation.setLocation((GeoPoint) document.get("location"));
                                duckLocation.setState(document.getString("state"));
                                duckLocation.setCity(document.getString("city"));
                                duckLocation.setTimestamp((Timestamp) document.get("timestamp"));

                                duckLocations.add(duckLocation);

                            }
                            listener.onTaskComplete(duckLocations);
                    }
                }});

        return null;
    }
}
