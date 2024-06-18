package edu.weber.cs.w01353438.duckduckjeep.tasks;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OpenForTesting;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;

public class GetSpecificDuck extends AsyncTask<Void, Void, List<cloudDuck>> {

    //USAGE
    //THIS GETS A SPECIFIC DUCK BY ITS DUCK/DOCUMENT ID
        //REQUIREMENTS
            //database
            //duckId
            //listener

    private FirebaseFirestore db;
    private String duckId;
    public OnTaskCompleteListener listener;
    public GetSpecificDuck(FirebaseFirestore db, String duckId, OnTaskCompleteListener listener){
        this.db = FirebaseFirestore.getInstance();
        this.duckId = duckId;
        this.listener = listener;
    }


    @Override
    protected List<cloudDuck> doInBackground(Void... voids) {
        List<cloudDuck> results = new ArrayList<>();

        db.collection("duck")
                .document(duckId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        cloudDuck cloudDuck = new cloudDuck();

                        cloudDuck.setDuckName((String) task.getResult().get("duckName"));
                        cloudDuck.setDuckId(duckId);

//                        Timestamp timestamp = task.getResult().getTimestamp("CreatedAt");
//                        cloudDuck.setCreatedTime(timestamp.toDate());

                        cloudDuck.setCreatedBy((String) task.getResult().get("CreatedBy"));


                        results.add(cloudDuck);
                        listener.onTaskComplete(results);

                    }
                });

        return results;
    }

    public interface OnTaskCompleteListener {
        void onTaskComplete(List<cloudDuck> results);
    }
}
