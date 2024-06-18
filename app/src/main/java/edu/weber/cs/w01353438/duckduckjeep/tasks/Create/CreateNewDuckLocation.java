package edu.weber.cs.w01353438.duckduckjeep.tasks.Create;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;

public class CreateNewDuckLocation extends AsyncTask<Void,Void, duckLocation> {

    FirebaseFirestore db;

    public OnTaskCompleteListener listener;
    duckLocation duckLocation;

    public interface OnTaskCompleteListener {
        void onTaskComplete(boolean success, String duckId);
    }

    public CreateNewDuckLocation(FirebaseFirestore db, duckLocation duckLocation, OnTaskCompleteListener listener){
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
        this.duckLocation = duckLocation;
    }


    @Override
    protected duckLocation doInBackground(Void... voids) {
        db.collection("duckTracking")
                .add(duckLocation)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        DocumentReference document = task.getResult();

                        //What to do when duck location is created
                        listener.onTaskComplete(true, document.getId());
                    }
                });
        return null;
    }
}
