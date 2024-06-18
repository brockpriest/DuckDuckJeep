package edu.weber.cs.w01353438.duckduckjeep.tasks;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;

public class GetAllDucks extends AsyncTask<Void, Void, List<cloudDuck>> {
    private FirebaseFirestore db;
    private OnTaskCompleteListener listener;


    public GetAllDucks(FirebaseFirestore db, OnTaskCompleteListener listener){
        this.db = db;
        this.listener = listener;
    }

    @Override
    protected List<cloudDuck> doInBackground(Void... voids) {
        List<cloudDuck> results = new ArrayList<>();
        db.collection("ducks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                cloudDuck duck = new cloudDuck();

                                duck.setDuckId(document.getId());
                                duck.setDuckName((String) document.get("name"));
                                Timestamp timestamp = document.getTimestamp("CreatedAt");
                                duck.setCreatedAt(timestamp.toDate());

                                results.add(duck);
                            }
                        }
                    }
                });


        return null;
    }

    @Override
    protected void onPostExecute(List<cloudDuck> cloudDucks) {
        if (listener != null) {
            listener.onTaskComplete(cloudDucks);
        }
    }

    public interface OnTaskCompleteListener {
        void onTaskComplete(List<cloudDuck> results);
    }



}
