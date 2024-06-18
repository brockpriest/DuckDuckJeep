package edu.weber.cs.w01353438.duckduckjeep;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import edu.weber.cs.w01353438.duckduckjeep.db.CloudDB.cloudDuck;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.CheckIfDuckExists;
import edu.weber.cs.w01353438.duckduckjeep.tasks.Create.CreateDuck;
import edu.weber.cs.w01353438.duckduckjeep.tasks.GetSpecificDuck;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OpenPages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenPages extends Fragment {

    private View root;

    public testPages mCallback;

    public interface testPages{
        void openPage(Fragment frag);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OpenPages() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OpenPages.
     */
    // TODO: Rename and change types and number of parameters
    public static OpenPages newInstance(String param1, String param2) {
        OpenPages fragment = new OpenPages();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.fragment_open_pages, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        cloudDuck duck = new cloudDuck();
        duck.setDuckName("Johnny");
        duck.setCreatedAt(Timestamp.now().toDate());
        duck.setDuckId("1234567890");
        duck.setCreatedBy(mAuth.getUid());

        Button createDuck = root.findViewById(R.id.TESTCreateDuck);
        createDuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateDuck createDuck = new CreateDuck(db, duck, new CreateDuck.OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(boolean success, String DuckId) {
                        if(success){
                            Toast.makeText(getContext(), "Success Duck Created", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                createDuck.execute();


            }

        });

        Button TestCheck = root.findViewById(R.id.TESTCheckDuck);
        TestCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckIfDuckExists checkIfDuckExists = new CheckIfDuckExists(db, "12345", new CheckIfDuckExists.OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(List<cloudDuck> cloudDucks) {
                        if(cloudDucks.size() > 0) {
                            Toast.makeText(getContext(), "That duck Already Exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getContext(), "That duck does not exist", Toast.LENGTH_SHORT).show();
                    }
                });
                checkIfDuckExists.execute();

//                  GetSpecificDuck getSpecificDuck = new GetSpecificDuck(db, "ABC008435", new GetSpecificDuck.OnTaskCompleteListener() {
//                        @Override
//                        public void onTaskComplete(List<cloudDuck> results) {
//                            if(results.isEmpty()) {
//                            Toast.makeText(getContext(), "That duck Already Exists", Toast.LENGTH_SHORT).show();
//                            }
//                            else{
//                                Toast.makeText(getContext(), "That duck does not exist", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                  getSpecificDuck.execute();

            }
        });
    }
}