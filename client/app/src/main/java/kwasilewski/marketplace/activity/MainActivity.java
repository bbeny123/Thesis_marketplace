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
import android.view.View;
import android.widget.TextView;

import kwasilewski.marketplace.R;
import kwasilewski.marketplace.dto.user.UserData;
import kwasilewski.marketplace.util.AppConstants;
import kwasilewski.marketplace.util.MRKUtil;
import kwasilewski.marketplace.util.SharedPrefUtil;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView nameField;
    private TextView emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.menu_open, R.string.menu_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        nameField = headerView.findViewById(R.id.nav_user);
        emailField = headerView.findViewById(R.id.nav_email);

        startAdListFragment(AppConstants.MODE_NORMAL, R.id.nav_ads, getString(R.string.title_app_name));
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareActivity();
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
        switch (item.getItemId()) {
            case R.id.nav_ads:
                startAdListFragment(AppConstants.MODE_NORMAL, R.id.nav_ads, getString(R.string.title_app_name));
                break;
            case R.id.nav_favourites:
                startAdListFragment(AppConstants.MODE_FAVOURITE, R.id.nav_favourites, getString(R.string.title_favourites));
                break;
            case R.id.nav_active:
                startAdListFragment(AppConstants.MODE_ACTIVE, R.id.nav_active, getString(R.string.title_active));
                break;
            case R.id.nav_inactive:
                startAdListFragment(AppConstants.MODE_INACTIVE, R.id.nav_inactive, getString(R.string.title_inactive));
                break;
            case R.id.nav_new:
                startActivity(new Intent(this, NewAddActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        SharedPrefUtil.getInstance(this).removeUserData();
        startAdListFragment(AppConstants.MODE_NORMAL, R.id.nav_ads, getString(R.string.title_app_name));
        prepareActivity();
        MRKUtil.toast(this, getString(R.string.toast_logout_successful));
    }

    private void startAdListFragment(int listMode, int id, String title) {
        navigationView.setCheckedItem(id);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, AdFragment.newInstance(listMode)).commit();
    }

    private void prepareActivity() {
        String token = SharedPrefUtil.getInstance(this).getToken();
        navigationView.getMenu().setGroupVisible(R.id.nav_logged, token != null);
        navigationView.getMenu().setGroupVisible(R.id.nav_not_logged, token == null);
        setUserData(token != null);
    }

    private void setUserData(boolean logged) {
        String name = null;
        String email = null;
        if (logged) {
            UserData user = SharedPrefUtil.getInstance(this).getUserData();
            if (user != null) {
                name = user.getName();
                email = user.getEmail();
            }
        }
        nameField.setText(name);
        emailField.setText(email);
    }

}
