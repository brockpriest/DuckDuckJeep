package edu.weber.cs.w01353438.duckduckjeep.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationProvider;
import android.location.LocationRequest;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.weber.cs.w01353438.duckduckjeep.MainActivity;
import edu.weber.cs.w01353438.duckduckjeep.Permissions.LocationManager;
import edu.weber.cs.w01353438.duckduckjeep.R;
import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudFunctions;
import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.Duck;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.UserDuckTracking;
import edu.weber.cs.w01353438.duckduckjeep.db.Models.duckLocation;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.AddUserTracking;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.CheckIfDuckExists;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.CheckIfUserIsTracking;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.CreateDuck;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.CreateNewDuckLocation;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetPublicIPAddressTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewDuck#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewDuck extends DialogFragment {

    private View root;
    private Toolbar toolbar;

    FirebaseAuth mAuth;

    FirebaseFirestore db;

    cloudFunctions cloudFunctions;

    GeoPoint userLocation;

    String City;
    String State;
    String Country;
    String randomName;
    public addRefreshAction mCallback;

    public interface addRefreshAction{
        void addRefresh();
    }

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewDuck() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewDuck.
     */
    // TODO: Rename and change types and number of parameters
    public static NewDuck newInstance(String param1, String param2) {
        NewDuck fragment = new NewDuck();
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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cloudFunctions = new cloudFunctions();

        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FullScreenDialogDemo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_new_duck, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireDialog().getWindow().setWindowAnimations(R.style.Base_Theme_DuckDuckJeep_DialogAnimation);

        toolbar = view.findViewById(R.id.newDuckToolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.inflateMenu(R.menu.newducktoolbar);
        toolbar.setTitle("New Duck");
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try{
            mCallback = (addRefreshAction) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement addRefresh in NewDuck Fragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Gets users location before anything else occurs.
        getUsersLocation();

        TextView DuckId = root.findViewById(R.id.newDuckId);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.saveNewDuckBtn) {

                    cloudDuck cloudDuck = new cloudDuck();
                    randomName = getRandomName();

                    cloudDuck.setDuckId(DuckId.getText().toString());
                    cloudDuck.setDuckLastLocation(userLocation);
                    cloudDuck.setCreatedBy(mAuth.getUid());
                    cloudDuck.setCreatedAt(Timestamp.now().toDate());

                    SearchForDuck(cloudDuck);

                }
                return false;
            }
        });

        Button btn = root.findViewById(R.id.scanQR);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initQRCodeScanner();
            }
        });
    }

    //Create something in cloud
    public void CreateDuck(cloudDuck cloudDuck) {

        CreateDuck createDuck = new CreateDuck(db, cloudDuck, new CreateDuck.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(boolean success, String duckId) {
                if (success) {
                    Toast.makeText(getContext(), "Duck  been Created", Toast.LENGTH_SHORT).show();
                    //Run add tracking
                    //AddTracking(mAuth.getUid(), cloudDuck);
                    checkTracking(mAuth.getUid(), duckId, cloudDuck);
                } else {
                    Toast.makeText(getContext(), "Duck Failed to Create", Toast.LENGTH_SHORT).show();
                }
            }
        });
        createDuck.execute();
    }

    //Search for Existing Duck
    public void SearchForDuck(cloudDuck cloudDuck) {

        final String[] DuckId = {cloudDuck.getDuckId()};

        CheckIfDuckExists checkIfDuckExists = new CheckIfDuckExists(db, DuckId[0], new CheckIfDuckExists.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(List<cloudDuck> cloudDucks) {
                if(cloudDucks.size() > 0) {
                    Toast.makeText(getContext(), "That duck Already Exists", Toast.LENGTH_SHORT).show();
                    //Create Tracking
                    //AddTracking(mAuth.getUid(),cloudDucks);
                    cloudDuck.setDuckName(cloudDucks.get(0).getDuckName());
                    DuckId[0] = cloudDucks.get(0).getDuckId().toString();
                    checkTracking(mAuth.getUid(), DuckId[0], cloudDuck);
                }
                else{
                    Toast.makeText(getContext(), "That duck does not exist", Toast.LENGTH_SHORT).show();
                    //Create the duck
                    cloudDuck.setDuckName(randomName);
                    CreateDuck(cloudDuck);
                }
            }
        });
        checkIfDuckExists.execute();

    }

    public void AddTracking(String UserId, String duckDocumentId, cloudDuck mainDuck) {

        UserDuckTracking userDuckTracking = new UserDuckTracking();
        userDuckTracking.setDuckName(mainDuck.getDuckName());
        userDuckTracking.setDuckId(duckDocumentId);
        userDuckTracking.setUserId(UserId);
        userDuckTracking.setTimestamp(Timestamp.now());



        AddUserTracking addUserTracking = new AddUserTracking(db, userDuckTracking, new AddUserTracking.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(UserDuckTracking userDuckTracking) {
                Toast.makeText(getContext(), "User is now tracking duck. " + userDuckTracking.getDuckId(), Toast.LENGTH_SHORT).show();
            }
        });
        addUserTracking.execute();

    }

    public String getRandomName(){
        final String[] FIRST_NAMES = {"Quacky", "Waddles", "Puddles", "Daffy", "Donald", "Daisy", "Howard", "Feathers", "Sunny", "Fluffy",
                "Splash", "Squeaky", "Nibbles", "Bubbles", "Lucky", "Pepper", "Scooter", "Ginger", "Marshmallow", "Freckles",
                "Biscuit", "Cinnamon", "Buttercup", "Pebbles", "Bluebell", "Whiskers", "Sugar", "Peaches", "Honey", "Tootsie",
                "Petal", "Twinkie", "Chickpea", "Lemon", "Pumpkin", "Doodle", "Dizzy", "Muffin", "Olive", "Pickles", "Poppy",
                "Rocky", "Snicker", "Sprinkles", "Truffle", "Velvet", "Wally", "Ziggy", "Alfalfa", "Banjo", "Bongo", "Coco", "IAMCAPTAINCODE"};

            Random random = new Random();
            return FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
    }

    //Check to see if user is already tracking
    public void checkTracking(String UserId, String duckDocumentId, cloudDuck mainDuck) {

        CheckIfUserIsTracking checkIfUserIsTracking = new CheckIfUserIsTracking(db, UserId, duckDocumentId, new CheckIfUserIsTracking.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(List<UserDuckTracking> userDuckTrackingList) {
                if(userDuckTrackingList.size() > 0){
                    Toast.makeText(getContext(), "User is already tracking duck", Toast.LENGTH_SHORT).show();
                    createDuckLocationRecord(duckDocumentId);


                }
                else {
                    Toast.makeText(getContext(), "Adding Tracking to user", Toast.LENGTH_SHORT).show();
                    AddTracking(UserId, duckDocumentId, mainDuck);
                    createDuckLocationRecord(duckDocumentId);

                }
            }
        });
        checkIfUserIsTracking.execute();

    }

    //A location will always be published to the DB
    public void createDuckLocationRecord(String duckDocumentId){
        duckLocation duckLocation = new duckLocation();

        duckLocation.setLocation(userLocation);
        duckLocation.setCity(City);
        duckLocation.setState(State);
        duckLocation.setDuckId(duckDocumentId);
        duckLocation.setTimestamp(Timestamp.now());

        CreateNewDuckLocation createNewDuckLocation = new CreateNewDuckLocation(db, duckLocation, new CreateNewDuckLocation.OnTaskCompleteListener() {
            @Override
            public void onTaskComplete(boolean success, String duckId) {
                Toast.makeText(getActivity(), "Duck Location Created", Toast.LENGTH_SHORT).show();
                mCallback.addRefresh();
                dismiss();
            }
        });
        createNewDuckLocation.execute();
    }

    public void addLocation(String duckId) {


        Map<String, Object> duckLocation = new HashMap<>();
        duckLocation.put("duckId", duckId);
        duckLocation.put("location", userLocation);
        duckLocation.put("City", City);
        duckLocation.put("State", State);
        duckLocation.put("CreatedAt", Timestamp.now().toDate());


        db.collection("duckTracking")
                .add(duckLocation);

        db.collection("duck")
                .document(duckId)
                .update("duckLastLocation", City + ", " + State);
    }

    private void getUsersLocation() {

        final String[] country = new String[1];
        final String[] city = new String[1];
        final String[] regionName = new String[1];
        final String[] latitude = new String[1];
        final String[] longitude = new String[1];

        // Create a new instance of GetPublicIPAddressTask
        GetPublicIPAddressTask task = new GetPublicIPAddressTask(new GetPublicIPAddressTask.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String[] locationInfo) {

                // Handle the completed task here
                if (locationInfo != null && locationInfo.length >= 5) {
                    country[0] = locationInfo[1];
                    city[0] = locationInfo[2];
                    regionName[0] = locationInfo[3];
                    latitude[0] = locationInfo[4];
                    longitude[0] = locationInfo[5];

                    // Do something with the location information
                    userLocation = new GeoPoint(Double.parseDouble(latitude[0]), Double.parseDouble(longitude[0]));
                    City = city[0];
                    State = regionName[0];
                    Country = country[0];

                }
            }

        });

        // Execute the task
        task.execute();

    }






    private void initQRCodeScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                TextView duckId = root.findViewById(R.id.newDuckId);
                duckId.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}