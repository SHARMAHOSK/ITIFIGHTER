package com.example.itifighter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainDashboard extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView UserName,UserEmail;

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashbord);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,R.id.nav_profile,R.id.nav_chat,R.id.nav_groups,R.id.nav_change,R.id.nav_rating)
                .setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        final ImageButton fab = findViewById(R.id.fab);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getLabel() != null && destination.getLabel().toString().equals("Home")){
                    fab.setEnabled(true);
                    fab.setVisibility(View.VISIBLE);
                }
                else{
                    fab.setEnabled(false);
                    fab.setVisibility(View.GONE);
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainDashboard.this,"success",Toast.LENGTH_LONG)
                        .show();
                //openWhatsApp(view);
                startPayment();
            }
        });
        View view = navigationView.inflateHeaderView(R.layout.nav_header_main);
        UserName = view.findViewById(R.id.MenuName);
        UserEmail = view.findViewById(R.id.MenuEmail);
        UserName.setText("shubham");
        UserEmail.setText("sk@gmail.com");
    }

    private void startPayment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure to start payment ?")
                .setPositiveButton("RazorPay", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainDashboard.this,Payment.class));
                        finish();
                    }
                }).setPositiveButton("Paytm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //startActivity(new Intent(MainDashboard.this,PaytmPayment.class));
                startActivity(new Intent(MainDashboard.this,Counter.class));
                finish();
            }
        })
                .setNegativeButton("No", null);
        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_dashbord, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(MainDashboard.this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            showPopup();
            return true;
        }
        if(id==R.id.nav_share){
            shareApp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: lms-jim.xyz" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void logout() {
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signOut();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure?")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {
                        logout(); // Last step. Logout function
                    }
                }).setNegativeButton("Cancel", null);
        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    public void openWhatsApp(View view) {
        try {
            String text = "This is a test";
            String toNumber = "918840699736";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + toNumber + "&text=" + text));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setHeaderDetails(){

    }

    @Override
    protected void onStart() {
        super.onStart();
        setHeaderDetails();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}