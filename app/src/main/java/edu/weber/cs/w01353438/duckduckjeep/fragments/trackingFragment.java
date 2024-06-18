package edu.weber.cs.w01353438.duckduckjeep.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.Date;

import java.sql.Time;
import java.util.List;
import java.util.Random;

import edu.weber.cs.w01353438.duckduckjeep.MainActivity;
import edu.weber.cs.w01353438.duckduckjeep.R;
import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.Duck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;
import edu.weber.cs.w01353438.duckduckjeep.db.PastLocationsAdapter;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetAllDucks;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetDuckPastLocations;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetSpecificDuck;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link trackingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class trackingFragment extends DialogFragment implements OnMapReadyCallback, PastLocationsAdapter.onClickListener {

    private View root;

    private Toolbar toolbar;

    private cloudDuck duck;

    private String duckId;
    private String documnetId;

    MapView mapView;
    GoogleMap map;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth mAuth;
    private List<cloudDuck> currentDuck;
    private List<duckLocation> duckLocations;
    FirebaseFirestore db;

    public deleteRefreshAction mCallback;

    @Override
    public void onClickable(GeoPoint geoPoint) {
        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng);
        map.addMarker(markerOptions);
    }

    public interface deleteRefreshAction {
        void deleteRefresh();
    }

    public trackingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment tracking.
     */
    // TODO: Rename and change types and number of parameters
    public static trackingFragment newInstance(String param1, String param2) {
        trackingFragment fragment = new trackingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FullScreenDialogDemo);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        duck = new cloudDuck();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_tracking, container, false);

        mapView = (MapView) root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);


        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setWindowAnimations(R.style.Base_Theme_DuckDuckJeep_DialogAnimation);


        toolbar = view.findViewById(R.id.trackingToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.inflateMenu(R.menu.trackingtoolbar);
        toolbar.setTitle("Track Duckling");
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (deleteRefreshAction) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement deleteRefresh in trackingFragment");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);

       /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(43.1, -87.9)));
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();


        //getDuck(duckId);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.deleteTracking){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Delete Confirmation");
                    builder.setMessage("Are you sure you want to delete?");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeleteDuckTracking();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                }
                return false;
            }
        });

        //Get a singular duck
        GetSpecificDuck getSpecificDuck = new GetSpecificDuck(db, duckId, new GetSpecificDuck.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(List<cloudDuck> results) {
                //Log.d("TAG", "onTaskComplete: " + results.get(0));
                currentDuck = results;

                //Run to Display Duck Info
                DisplayDuckInfo();
            }
        });
        getSpecificDuck.execute();

        GetDuckPastLocations getDuckPastLocations = new GetDuckPastLocations(db, duckId, new GetDuckPastLocations.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(List<duckLocation> results) {
                duckLocations = (results);
                Log.d("TAG", "onTaskComplete: " + results.get(0).getCity());

                LatLng latLng = new LatLng(results.get(0).getLocation().getLatitude(), results.get(0).getLocation().getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f));

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng);

                map.addMarker(markerOptions);


                //Run to display the recycler view for the locations.
                DisplayPastLocations(results);

            }
        });
        getDuckPastLocations.execute();

    }

    private void DisplayDuckInfo(){
        toolbar.setTitle(currentDuck.get(0).getDuckName());

        TextView duckName = root.findViewById(R.id.duckName);
        TextView duckStory = root.findViewById(R.id.duckStory);

        duckStory.setText(randomStory(currentDuck.get(0).getDuckName()));
        duckName.setText(currentDuck.get(0).getDuckName());

    }

    public String randomStory(String duckName){
        final String[] storyTemplates = {
                "%s is a brave duck who saved the pond from a ferocious cat!",
                "Once upon a time, %s went on a thrilling adventure to find the legendary golden snail.",
                "%s discovered a hidden treasure chest at the bottom of the pond and became the richest duck in the land!",
                "In a magical forest, %s met a wise old owl who taught them the secret of flying upside down.",
                "%s and their duck friends organized a grand feast to celebrate the annual migration of the swans.",
                "Legend has it that %s can quack in seven different languages!",
                "%s found a lost duckling and helped reunite it with its family, earning the title of 'Duck Hero'.",
                "During a stormy night, %s guided lost ducklings to safety using their keen sense of direction.",
                "%s once crossed a vast ocean, braving storms and high waves, to visit distant relatives.",
                "While exploring a sunflower field, %s stumbled upon a hidden duck paradise, where every wish comes true.",
                "%s befriended a wise old turtle who shared ancient wisdom about the secrets of the pond.",
                "Once in a moonlit night, %s led a magical duck dance, enchanting all who witnessed it.",
                "In the Heart of the Storm: Amidst a tempest, %s led a daring rescue mission, ensuring the safety of every duck in the pond.",
                "The Enchanted Lily Pond: %s discovered a mystical lily pond where each bloom held a secret wish, forever changing the fate of those who dared to dream.",
                "Guardian of the Marsh: %s stood firm against an encroaching threat, rallying the creatures of the marshland to defend their home.",
                "The Quack of Destiny: Legends whispered of %s, whose quack had the power to heal wounded hearts and bring joy to the darkest of days.",
                "Journey to Avalake: %s embarked on an epic quest to find Avalake, a legendary sanctuary rumored to hold the key to everlasting tranquility.",
                "The Feathered Diplomat: %s was hailed as a peacemaker, forging alliances between rival flocks and bringing harmony to the avian kingdom.",
                "Echoes of the Wind: Across the vast expanse of the sky, %s soared, chasing the whispers of the wind and discovering the melody of freedom.",
                "The Celestial Navigator: Guided by the stars, %s embarked on a celestial voyage, charting a course through the cosmos to find their place among the constellations.",
                "The Wings of Hope: In the face of adversity, %s unfurled their wings and lifted the spirits of those around them, proving that courage knows no bounds.",
                "A Splash of Magic: %s stumbled upon a hidden waterfall, its cascading waters imbued with ancient magic that bestowed blessings upon all who bathed in its shimmering spray."
        };

        Random random = new Random();
        int index = random.nextInt(storyTemplates.length);
        return String.format(storyTemplates[index], duckName);

    }

    void DisplayPastLocations(List<duckLocation> locations){
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.HORIZONTAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(new PastLocationsAdapter(locations, trackingFragment.this));
    }

    void DeleteDuckTracking(){

        // Delete the document
        db.collection("userDuckTracking")
                .document(documnetId)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("TAG", "Error deleting document", e);

                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mCallback.deleteRefresh();
                        Toast.makeText(getContext(),"Tracking Deleted", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });

    }

    public void getDuck(String DuckId){
        db.collection("duck")
                .document(DuckId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        duck.setDuckId(documentSnapshot.getId());
                        duck.setDuckName(String.valueOf(documentSnapshot.get("duckName")));
                        //duck.setCreatedTime(Time.valueOf(String.valueOf(documentSnapshot.get("CreatedAt"))));

                        //duck.notifyAll();
                    }
                });
    }

    public void getDuckRecords(String DuckId){
        db.collection("duckTracking")
                .whereEqualTo("duckId", DuckId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    }
                });
    }

    public void setDuckId(String duckId, String documnetId){
        this.duckId = duckId;
        this.documnetId = documnetId;
    }
}