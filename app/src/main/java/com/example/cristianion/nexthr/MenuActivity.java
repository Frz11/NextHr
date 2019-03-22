package com.example.cristianion.nexthr;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cristianion.nexthr.Models.Image;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.Models.UserRole;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import static com.example.cristianion.nexthr.Utils.Global.adminRole;
import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("last_fragment");
        if (fragment != null) {
            getSupportFragmentManager().putFragment(outState, "last_fragment", fragment);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment fragment = new ProfileFragment();
        if(savedInstanceState != null) {
           // fragment = getSupportFragmentManager().getFragment(savedInstanceState,"last_fragment");
        }

        ft.replace(R.id.Frame,fragment);

        ft.commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        ((TextView) findViewById(R.id.Company)).setText(currentCompany.name);
        ((TextView) findViewById(R.id.Name)).setText(currentEmployee.lastName + " " + currentEmployee.firstName);
        final ImageView profilePhoto = findViewById(R.id.profileImage);


        db.collection("companies").document(currentCompany.id)
                .collection("images").whereEqualTo("userId",currentEmployee.id)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot data : Objects.requireNonNull(task.getResult())){
                        Image image = data.toObject(Image.class);
                        StorageReference storage = FirebaseStorage.getInstance().getReference("images/").child(currentCompany.id).child(image.id);
                        storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Glide.with(getApplicationContext()).load(task.getResult()).into(profilePhoto);

                            }
                        });
                    }
                } else {

                }
            }
        });
        db.collection("companies").document(currentCompany.id).collection("roles").whereEqualTo("name","Admin")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot data : Objects.requireNonNull(task.getResult())){
                        adminRole = data.toObject(Role.class);
                        break;
                    }
                    //check if user has admin role!
                    db.collection("companies").document(currentCompany.id)
                            .collection("user_role").whereEqualTo("roleId",adminRole.id).whereEqualTo("userId",currentEmployee.id)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                if(task.getResult() != null){
                                    final NavigationView nav = findViewById(R.id.nav_view);
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            nav.getMenu().findItem(R.id.nav_administration).setVisible(true);
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {

                }
            }
        });


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu){

        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MenuActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.nav_profile) {
            // Handle the camera action
            fragment = new ProfileFragment();
        } else if (id == R.id.nav_attendance) {
            fragment = new AttendanceFragment();
        } else if (id == R.id.nav_salary) {

        } else if (id == R.id.nav_holidays) {

        } else if (id == R.id.nav_roles) {
            fragment = new RolesFragment();
        } else if (id == R.id.nav_employees) {
            fragment = new EmployeesFragment();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.Frame,fragment,"last_fragment");
            fragmentTransaction.commit();
        }

        return true;
    }
}
