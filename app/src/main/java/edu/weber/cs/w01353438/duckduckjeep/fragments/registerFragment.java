package edu.weber.cs.w01353438.duckduckjeep.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import edu.weber.cs.w01353438.duckduckjeep.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link loginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registerFragment extends Fragment {

    private TextView loginEmail;
    private TextView loginPassword;
    private Button loginBTN;
    private TextView registerBtn;
    FirebaseAuth mAuth;

    public mainTasks mainTasks;
    public interface mainTasks{
        void returnToLogin();
    }

    private Toolbar toolbar;

    final private String TAG = "loginFragment";

    private View root;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public registerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment loginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static loginFragment newInstance(String param1, String param2) {
        loginFragment fragment = new loginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FullScreenDialogDemo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return root = inflater.inflate(R.layout.activity_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //requireDialog().getWindow().setWindowAnimations(R.style.Base_Theme_DuckDuckJeep_DialogAnimation);

    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        try{
            mainTasks = (mainTasks) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement loginTasks in loginFragment");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        toolbar = root.findViewById(R.id.registerToolbar);
        toolbar.setTitle("Register");

        loginEmail = root.findViewById(R.id.regEmail);
        loginPassword = root.findViewById(R.id.regPassword);
        loginBTN = root.findViewById(R.id.regBTN);
        registerBtn = root.findViewById(R.id.registerLoginButton);

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySignup();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tell Main Activity to Show registration page.
                mainTasks.returnToLogin();
            }
        });
    }

    public void trySignup(){

        mAuth = FirebaseAuth.getInstance();
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        if(TextUtils.isEmpty(loginEmail.getText())){
            loginEmail.setError("Email cannot be empty");
            loginEmail.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password cannot be empty");
            loginPassword.requestFocus();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show();
                        mainTasks.returnToLogin();
                    }else {
                        Toast.makeText(getContext(), "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: " + task.getException().getMessage());
                    }
                }
            });
        }
    }
}