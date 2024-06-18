package edu.weber.cs.w01353438.duckduckjeep.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.R;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.Duck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.UserDuckTracking;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;
import edu.weber.cs.w01353438.duckduckjeep.db.UserDuckTrackingAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link yourDucks#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class yourDucks extends Fragment implements UserDuckTrackingAdapter.OnButtonClickListener {

    private View root;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SwipeRefreshLayout refresh;
    private UserDuckTrackingAdapter.OnButtonClickListener listener;
    List<Duck> duckList;

    // Define an interface for the callback
    interface OnLocationReceivedListener {
        void onLocationReceived(duckLocation duckLocation);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment yourDucks.
     */
    // TODO: Rename and change types and number of parameters
    public static yourDucks newInstance(String param1, String param2) {
        yourDucks fragment = new yourDucks();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public yourDucks() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_your_ducks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.yourDucksToolbar);
        toolbar.inflateMenu(R.menu.yourduckstoobar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.profileBtn) {
                    //Open Profile Page
                    ProfileFragment profileFragment = new ProfileFragment();
                    profileFragment.show(getParentFragmentManager(), "ProfileFragment");
                    return false;
                }
            return false;
            }


        });

        toolbar.setTitle("Your Ducklings");
    }

    @Override
    public void onResume() {
        db = FirebaseFirestore.getInstance();
        super.onResume();

        refresh = root.findViewById(R.id.swiperefresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    Log.d("TAG", "onRefresh: Refreshing");
                    getAllDucks(refresh);
            }
        });

        getAllDucks(refresh);

        FloatingActionButton fab = root.findViewById(R.id.addDuckFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewDuck newDuck = new NewDuck();
                newDuck.show(getParentFragmentManager(), "New Duck Fragment");
            }
        });

    }

    public void getAllDucks(SwipeRefreshLayout refresh){

        refresh.setRefreshing(true);

        List<UserDuckTracking> userDuckTrackingList = new ArrayList<>();

        db.collection("userDuckTracking")
                .whereEqualTo("userId", mAuth.getUid())
                //.orderBy("CreatedAt")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String duckId = documentSnapshot.getId();
                            String duckName = documentSnapshot.getString("duckName");
                            String duckLocation = documentSnapshot.getString("duckLastLocation");
                            String duckDocumentId = documentSnapshot.getString("duckId");
                            Timestamp Timestamp = (Timestamp) documentSnapshot.get("timestamp");
                            UserDuckTracking userDuckTracking = new UserDuckTracking(duckId, duckName, duckLocation, duckDocumentId, Timestamp);
                            userDuckTrackingList.add(userDuckTracking);
                        }
                        RecyclerView recyclerView = root.findViewById(R.id.yourDucksRecyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(new UserDuckTrackingAdapter(userDuckTrackingList, yourDucks.this));

                        displayNoDucksFound(userDuckTrackingList.isEmpty());

                        refresh.setRefreshing(false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error getting documents: ", e);
                    }
                });

    }



    private void getLastDuckLocation(String duckId, OnLocationReceivedListener listener) {
        db.collection("duckTracking")
                .whereEqualTo("duckId", duckId)
                .orderBy("timestamp", Query.Direction.DESCENDING) // Assuming "timestamp" is the field name for the timestamp
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Assuming duckLocation is a POJO representing the location data
                        duckLocation duckLocation = documentSnapshot.toObject(duckLocation.class);
                        if (duckLocation != null) {
                            listener.onLocationReceived(duckLocation);
                            return; // Exit loop after processing the first document
                        }
                    }
                    // If no documents were found
                    listener.onLocationReceived(null);
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "onFailure: Failed to get duck location", e);
                    // Call the listener with null when there's a failure
                    listener.onLocationReceived(null);
                });
    }

    @Override
    public void onButtonClick(String duckId, String documentId) {
        Log.d("TAG", "onButtonClick: " + duckId);

        trackingFragment trackingFragment = new trackingFragment();
        trackingFragment.setDuckId(duckId, documentId);
        trackingFragment.show(getParentFragmentManager(), "TrackingFragment");
    }

    public void deleteRefresh() {
        getAllDucks(refresh);
    }

public void displayNoDucksFound(boolean display){
    ImageView imageView = root.findViewById(R.id.imageView);

    imageView.setImageResource(R.drawable.duck);
    TextView textView = root.findViewById(R.id.noDucksFound);

    if(display) {
        imageView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }else{
        imageView.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
    }
}


    public duckLocation returnLastDuckLocation(String duckId){
        duckLocation location = new duckLocation();
        getLastDuckLocation(duckId, new OnLocationReceivedListener() {
            @Override
            public void onLocationReceived(duckLocation duckLocation) {
                location.setDuckId(duckLocation.getDuckId());
                location.setLocation(duckLocation.getLocation());
                location.setCity(duckLocation.getCity());
                location.setState(duckLocation.getState());
                location.setTimestamp(duckLocation.getTimestamp());
            }
        });

        return location;
    }




}