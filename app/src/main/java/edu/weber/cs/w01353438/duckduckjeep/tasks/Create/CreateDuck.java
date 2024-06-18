package edu.weber.cs.w01353438.duckduckjeep.tasks.Create;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetSpecificDuck;

public class CreateDuck extends AsyncTask<cloudDuck, Void, Void> {

    FirebaseFirestore db;
    public OnTaskCompleteListener listener;
    cloudDuck duck;

    public CreateDuck(FirebaseFirestore db, cloudDuck duck, OnTaskCompleteListener listener){
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
        this.duck = duck;
    }

    @Override
    protected Void doInBackground(cloudDuck... cloudDucks) {
        db.collection("duck")
                .add(duck)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        DocumentReference document = task.getResult();

                        //What to do when duck is created
                        listener.onTaskComplete(true, document.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onTaskComplete(false, "ERROR");
                    }
                });
        return null;
    }

    public interface OnTaskCompleteListener {
        void onTaskComplete(boolean success, String duckId);
    }
}
