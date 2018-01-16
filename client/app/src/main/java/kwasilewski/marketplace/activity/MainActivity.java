package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.util.SharedPref;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        hideSomeMenuOptions();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            //go to home
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_ads) {
            finish();
        } else if (id == R.id.nav_logout) {
            //go to home
            SharedPref.getInstance(this).removeUserData();
            hideSomeMenuOptions();
        } else if (id == R.id.nav_login) {
            startActivityForResult(new Intent(this, LoginActivity.class), 1);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hideSomeMenuOptions() {
        String token = SharedPref.getInstance(this).getToken();
        navigationView.getMenu().setGroupVisible(R.id.nav_logged, token != null);
        navigationView.getMenu().setGroupVisible(R.id.nav_not_logged, token == null);
    }

}
