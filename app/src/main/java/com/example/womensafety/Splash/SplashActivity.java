package com.example.womensafety.Splash;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.womensafety.Activities.HomeActivity;
import com.example.womensafety.Auth.Login;
import com.example.womensafety.R;
import com.example.womensafety.databinding.ActivitySplashBinding;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.security.Permission;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {

                PermissionX.init(SplashActivity.this)
                        .permissions(Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.SEND_SMS)
                        .request(new RequestCallback() {
                            @Override
                            public void onResult(boolean b, @NonNull List<String> list, @NonNull List<String> list1) {
                                if (b)
                                {
                                    startActivity(new Intent(SplashActivity.this , Login.class));
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(SplashActivity.this, "These permissions are denied: "+list1, Toast.LENGTH_LONG).show();
                                }
                            }
                        });


            }
        },1000);
    }
}