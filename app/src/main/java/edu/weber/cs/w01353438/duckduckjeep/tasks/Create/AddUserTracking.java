package edu.weber.cs.w01353438.duckduckjeep.tasks.Create;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.weber.cs.w01353438.duckduckjeep.db.Models.UserDuckTracking;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;

public class AddUserTracking extends AsyncTask<Void, Void, UserDuckTracking> {

    FirebaseFirestore db;
    public OnTaskCompleteListener listener;
    UserDuckTracking userDuckTracking;

    public AddUserTracking(FirebaseFirestore db, UserDuckTracking userDuckTracking, OnTaskCompleteListener listener){
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
        this.userDuckTracking = userDuckTracking;
    }


    @Override
    protected UserDuckTracking doInBackground(Void... voids) {
        db.collection("userDuckTracking")
                .add(userDuckTracking)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        listener.onTaskComplete(userDuckTracking);
                    }
                });
        return null;
    }

    public interface OnTaskCompleteListener{
        void onTaskComplete(UserDuckTracking userDuckTracking);
    }

}
