package com.example.cameraxdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.ArrayList;
import java.util.List;

//Bottom navigation bar reference: https://github.com/oneHamidreza/MeowBottomNavigation
public class MainActivity extends AppCompatActivity {
    private int REQUEST_CODE_PERMISSIONS = 101;
    //ask for permission to open camera and load image from gallery
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"
    };
    MeowBottomNavigation bottomNavigation;

    //get returned plants chosen in camera activity
    private List<String> addedClasses;
    private List<String> addedPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigation = (MeowBottomNavigation) findViewById(R.id.bottom_navigation);

        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_reminder));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_camera));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_garden));

        //change text in action bar and load fragment when switching
        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                Fragment fragment = null;
                switch (item.getId()){
                    case 1:
                        getSupportActionBar().setTitle("Nhắc nhở hằng ngày");
                        fragment = new ReminderFragment();
                        loadFragment(fragment);
                        break;
                    case 2:
                        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Vườn nhà tui");
                        fragment = new GardenFragment();
                        loadFragment(fragment);
                        break;
                }
            }
        });

        //load daily reminder fragment first
        bottomNavigation.show(1, true);

        //get added plants chosen by camera activity
        if(getIntent() != null){
            addedClasses = new ArrayList<>();
            addedPaths = new ArrayList<>();
            this.addedClasses = (ArrayList<String>) getIntent().getSerializableExtra("addedClasses");
            this.addedPaths = (ArrayList<String>) getIntent().getSerializableExtra("addedPaths");
            if(addedClasses != null) {
                //if any new plants, load my garden fragment
                bottomNavigation.show(3, true);
            }
        }

        //camera starts a new activity not a fragment, so set onclicklistener for camera nav button
        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                if (item.getId() == 2){
                    Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intent);
                }
            }
        });


        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

        //request permission if haven't been accepted yet
        if(allPermissionsGranted()){
        } else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public List<String> getAddedClasses(){
        return addedClasses;
    }

    public List<String> getAddedPaths(){
        return addedPaths;
    }
}