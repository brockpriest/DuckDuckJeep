package edu.weber.cs.w01353438.duckduckjeep.db.CloudDB;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import edu.weber.cs.w01353438.duckduckjeep.MainActivity;
import edu.weber.cs.w01353438.duckduckjeep.fragments.NewDuck;

public class cloudFunctions extends NewDuck {


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void CreateDuck(Map<String, Object> objectToAdd, String collectionToAddto, Context context) {
        if (!SearchForDuck(objectToAdd.get("duckId").toString())) {
            db.collection(collectionToAddto)
                    .add(objectToAdd)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            SearchForDuck("The First Duck");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error Adding Document: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public boolean SearchForDuck(String DuckId){
        //Usage: This check to see if duck already exists before.
        CollectionReference duckRef = db.collection("duck");
        Query query = duckRef.whereEqualTo("duckId",DuckId);

        final boolean[] flag = new boolean[1];
        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("TAG", "onSuccess: " + queryDocumentSnapshots.getDocuments().toString());
                        flag[0] = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        flag[0] = false;
                    }
                });

        return flag[0];
    }


}
