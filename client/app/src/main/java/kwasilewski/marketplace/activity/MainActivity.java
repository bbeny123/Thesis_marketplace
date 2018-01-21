package kwasilewski.marketplace.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final int LOGIN_CODE = 1;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu menu;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startAdListFragment(AdFragment.ListModes.NORMAL_MODE);
    }



    @Override
    protected void onResume() {
        prepareActivity();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            startAdListFragment(AdFragment.ListModes.NORMAL_MODE);
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
            startAdListFragment(AdFragment.ListModes.NORMAL_MODE);
        } else if (id == R.id.nav_favourites) {
            startAdListFragment(AdFragment.ListModes.FAVOURITE_MODE);
        } else if (id == R.id.nav_new) {
            startActivity(new Intent(this, NewAddActivity.class));
        } else if (id == R.id.nav_active) {
            startAdListFragment(AdFragment.ListModes.ACTIVE_MODE);
        } else if (id == R.id.nav_inactive) {
            startAdListFragment(AdFragment.ListModes.INACTIVE_MODE);
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPrefUtil.getInstance(this).removeUserData();
            startAdListFragment(AdFragment.ListModes.NORMAL_MODE);
            prepareActivity();
            MRKUtil.toast(this, getString(R.string.toast_logout_successful));
        } else if (id == R.id.nav_login) {
            startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_CODE);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startAdListFragment(int listMode) {
        Fragment fragment = AdFragment.newInstance(listMode);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.dupa, fragment)
                .commit();
    }

    private void prepareActivity() {
        String token = SharedPrefUtil.getInstance(this).getToken();
        navigationView.getMenu().setGroupVisible(R.id.nav_logged, token != null);
        navigationView.getMenu().setGroupVisible(R.id.nav_not_logged, token == null);
        setUserData(token != null);
    }

    private void setUserData(boolean logged) {
        View headerView = navigationView.getHeaderView(0);
        TextView nameView = headerView.findViewById(R.id.nav_user);
        TextView emailView = headerView.findViewById(R.id.nav_email);
        String name = null;
        String email = null;
        if (logged) {
            UserData user = SharedPrefUtil.getInstance(this).getUserData();
            if (user != null) {
                name = user.getName();
                email = user.getEmail();
            }
        }
        nameView.setText(name);
        emailView.setText(email);
    }


}
