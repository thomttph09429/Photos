package com.poly.photos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.poly.photos.view.activity.ListChatActivity;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView navigation;
    static Activity activity;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity = this;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        navigation = findViewById(R.id.navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navigation, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.action_serach) {
                    toolbar.setVisibility(View.GONE);
                } else if (destination.getId() == R.id.action_account) {
                    toolbar.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = getSharedPreferences("name", MODE_PRIVATE).edit();
                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();

                } else if (destination.getId() == R.id.action_profile) {

                    toolbar.setVisibility(View.GONE);

                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    public static MainActivity getInstance() {
        return (MainActivity) activity;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;

    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.action_create_post) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_account) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_profile) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_setting) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_detail_post) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_notification) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_setting) {
            navController.navigate(R.id.action_account);
            return;
        }
        if (navController.getCurrentDestination().getId() == R.id.action_serach) {
            navController.navigate(R.id.action_home);
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                navController.navigate(R.id.action_serach);
                return true;
            case R.id.setting:
                startActivity(new Intent(MainActivity.this, ListChatActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();

    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}