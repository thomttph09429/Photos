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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.model.Chat;
import com.poly.photos.view.activity.ListChatActivity;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView navigation;
    static Activity activity;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private boolean doubleBackToExitPressedOnce = false;
    private TextView tvChatBadge;

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
        final MenuItem menuItem = menu.findItem(R.id.chat);
        View actionView = menuItem.getActionView();
        tvChatBadge = (TextView) actionView.findViewById(R.id.tv_chat_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                navController.navigate(R.id.action_serach);
                return true;
            case R.id.chat:
                startActivity(new Intent(MainActivity.this, ListChatActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    private void setupBadge() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unRead = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getIsSeen().equals("false")) {
                        unRead++;
                    }
                }
                if (unRead == 0) {
                    if (tvChatBadge.getVisibility() != View.GONE) {
                        tvChatBadge.setVisibility(View.GONE);
                    }
                } else {
                    tvChatBadge.setText(unRead+"");
                    if (tvChatBadge.getVisibility() != View.VISIBLE) {
                        tvChatBadge.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
    protected void onDestroy() {
        super.onDestroy();
        status("offline");

    }
}