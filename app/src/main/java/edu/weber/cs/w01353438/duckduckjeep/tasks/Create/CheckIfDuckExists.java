package edu.weber.cs.w01353438.duckduckjeep.tasks.Create;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetSpecificDuck;

public class CheckIfDuckExists extends AsyncTask<Void, Void, List<cloudDuck>> {

    FirebaseFirestore db;
    public OnTaskCompleteListener listener;
    String duckId;

    public interface OnTaskCompleteListener{
        void onTaskComplete(List<cloudDuck> cloudDucks);
    }

    public CheckIfDuckExists(FirebaseFirestore db, String duckId, OnTaskCompleteListener listener){
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
        this.duckId = duckId;
    }

    @Override
    protected List<cloudDuck> doInBackground(Void... voids) {
        List<cloudDuck> cloudDucks = new ArrayList<>();
        db.collection("duck")
                .whereEqualTo("duckId", duckId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                cloudDuck cloudDuck = new cloudDuck();

                                cloudDuck.setDuckName(document.getString("duckName"));
                                cloudDuck.setDuckId(document.getId());

                                cloudDucks.add(cloudDuck);
                            }
                            listener.onTaskComplete(cloudDucks);
                        }
                    }
                });
        return cloudDucks;
    }

//    @Override
//    protected Void doInBackground(String... strings) {
//
//        final boolean[] Flag = new boolean[1];
//
//        db.collection("duck")
//                .whereEqualTo("duckId", duckId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            QuerySnapshot result = task.getResult();
//                            Log.d("TAG", "onComplete: " + result.getDocuments());
//                            if(result.isEmpty()){
//                                Flag[0] = true;
//                                listener.onTaskComplete(true);
//                            }
//                            else{
//                                Flag[0] = false;
//                                listener.onTaskComplete(false);
//                            }
//                        }
//                        else {
//                            Flag[0] = false;
//                            listener.onTaskComplete(false);
//                        }
//                    }
//                });
//
//
//        return null;
//    }
}
