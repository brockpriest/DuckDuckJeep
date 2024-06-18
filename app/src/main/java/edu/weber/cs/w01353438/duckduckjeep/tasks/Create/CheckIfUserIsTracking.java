package edu.weber.cs.w01353438.duckduckjeep.tasks.Create;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.Models.UserDuckTracking;

public class CheckIfUserIsTracking extends AsyncTask<Void, Void, List<UserDuckTracking>> {

    FirebaseFirestore db;
    public OnTaskCompleteListener listener;
    String userId;
    String duckDocumentId;

    public interface OnTaskCompleteListener{
        void onTaskComplete(List<UserDuckTracking> userDuckTrackingList);
    }

    public CheckIfUserIsTracking(FirebaseFirestore db, String userId, String duckDocumentId, OnTaskCompleteListener listener){
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
        this.userId = userId;
        this.duckDocumentId = duckDocumentId;
    }

    @Override
    protected List<UserDuckTracking> doInBackground(Void... voids) {

        //Needs to see if a duck is being tracked by that user.

        List<UserDuckTracking> userDuckTrackingList = new ArrayList<>();
        db.collection("userDuckTracking")
                .whereEqualTo("userId", userId)
                .whereEqualTo("duckId", duckDocumentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                UserDuckTracking userDuckTracking = new UserDuckTracking();

                                userDuckTracking.setDuckId(duckDocumentId);
                                userDuckTracking.setUserId(userId);

                                userDuckTrackingList.add(userDuckTracking);
                            }
                            listener.onTaskComplete(userDuckTrackingList);
                        }
                    }
                });
        return userDuckTrackingList;
    }
}
