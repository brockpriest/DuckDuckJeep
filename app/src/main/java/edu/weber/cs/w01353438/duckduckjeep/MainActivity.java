package edu.weber.cs.w01353438.duckduckjeep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.weber.cs.w01353438.duckduckjeep.fragments.NewDuck;
import edu.weber.cs.w01353438.duckduckjeep.fragments.ProfileFragment;
import edu.weber.cs.w01353438.duckduckjeep.fragments.loginFragment;
import edu.weber.cs.w01353438.duckduckjeep.fragments.registerFragment;
import edu.weber.cs.w01353438.duckduckjeep.fragments.trackingFragment;
import edu.weber.cs.w01353438.duckduckjeep.fragments.yourDucks;

public class MainActivity extends AppCompatActivity implements ProfileFragment.logoutClicked, trackingFragment.deleteRefreshAction, loginFragment.loginTasks, registerFragment.mainTasks, NewDuck.addRefreshAction {

    public FragmentManager fm;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new yourDucks(), "Your_Ducks")
                //.replace(R.id.mainActivityView, new OpenPages(), "TESTING")
                .commit();

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            fm.beginTransaction()
                    .replace(R.id.mainActivityView, new loginFragment(), "User_Login")
                    .commit();

        }
    }

    public void logoutClicked(){
        mAuth.signOut();
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new loginFragment(), "User_Login")
                .commit();

    }

    @Override
    public void deleteRefresh() {
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new yourDucks(), "Your_Ducks")
                .commit();
    }

    @Override
    public void loginSuccess() {
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new yourDucks(), "Your_Ducks")
                .commit();
    }

    @Override
    public void toRegisterPage() {
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new registerFragment(), "RegisterFrag")
                .commit();
    }

    @Override
    public void returnToLogin() {
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new loginFragment(), "LoginFrag")
                .commit();
    }

    @Override
    public void addRefresh() {
        fm.beginTransaction()
                .replace(R.id.mainActivityView, new yourDucks(), "Your_Ducks")
                .commit();
    }
}