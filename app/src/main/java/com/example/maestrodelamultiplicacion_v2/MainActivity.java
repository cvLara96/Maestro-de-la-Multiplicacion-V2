package com.example.maestrodelamultiplicacion_v2;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maestrodelamultiplicacion_v2.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    //CREAMOS LA SIGUIENTE CONSTANTE PARA GESTIONAR LOS PERMISOS:
    private final int PETICION_PERMISOS=1;
    //CREAMOS EL SIGUIENTE BOOLEAN PARA CUANDO SE SOLICITEN PERMISOS:
    public static boolean tengo_permisos = false;

    DatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Aplicacion no se gire automaticamente
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Solicitud de permisos
        if(checkSelfPermission("android.permission.READ_CONTACTS")!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{
                    "android.permission.READ_CONTACTS"
            }, PETICION_PERMISOS);
        }else{
            tengo_permisos = true;
            guardarPermisos(tengo_permisos);
        }

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_users,
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_stadistics, R.id.nav_contacts)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



    }

    //Metodo para guardar permisos de forma instantanea
    private void guardarPermisos (boolean permisos){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos el numero de aciertos
        editor.putBoolean("permisos", permisos);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}