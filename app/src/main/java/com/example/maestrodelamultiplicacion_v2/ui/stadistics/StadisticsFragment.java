package com.example.maestrodelamultiplicacion_v2.ui.stadistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.maestrodelamultiplicacion_v2.DatabaseHelper;
import com.example.maestrodelamultiplicacion_v2.R;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentStadisticsBinding;
import com.example.maestrodelamultiplicacion_v2.ui.gallery.Estadisticas;
import com.example.maestrodelamultiplicacion_v2.ui.gallery.GalleryFragment;
import com.example.maestrodelamultiplicacion_v2.ui.gallery.Multiplicacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StadisticsFragment extends Fragment{


    Spinner spinnerFecha, spinnerTabla;
    ListView listViewEstadisticas;

    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    String fechaSeleccionada;
    Button reset;


    private FragmentStadisticsBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StadisticsViewModel stadisticsViewModel =
                new ViewModelProvider(this).get(StadisticsViewModel.class);

        binding = FragmentStadisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*INICIO registro spinners*/
        spinnerFecha = binding.spinnerFecha;
        spinnerTabla = binding.spinnerTabla;
        /*FIN registro spinners*/

        /*INICIO REGISTRO LIST VIEW*/
        listViewEstadisticas = binding.listViewEstadisticas;
        /*FIN REGISTRO LIST VIEW*/

        /*INICIO obtener info de la base de datos y gestion de spinners*/
        dbHelper = new DatabaseHelper(getActivity(),"MaestroMultiplicacion",null,1);
        try {
            db = dbHelper.getReadableDatabase();
            obtenerFechas();

            spinnerFecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    obtenerTablas(parent.getSelectedItem().toString());
                    fechaSeleccionada = parent.getSelectedItem().toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spinnerTabla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    obtenerEstadisticas(fechaSeleccionada, parent.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (SQLiteException e) {
            // La base de datos no está creada, maneja la situación aquí
            Toast.makeText(getContext(), "La base de datos no está creada", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        /*FIN obtener info de la base de datos y gestion de spinners*/

        //INICIO BUTTON RESET
        reset = binding.buttonReset;
        reset.setOnClickListener(this::reset);
        //FIN BUTTON RESET

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

        db.close();
        dbHelper.close();

    }


    //Metodo para obtener array de fechas de la base de datos
    public void obtenerFechas(){

        ArrayAdapter<String> adapterSpinnerFecha;
        ArrayList<String> fechas = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT Fecha FROM partidas",null);
        if(cursor.getCount() == 0){
            fechas.add("No hay registros");
        }else{
            while(cursor.moveToNext()){
                if(!fechas.contains(cursor.getString(0))){
                    fechas.add(cursor.getString(0));
                }

            }
        }
        adapterSpinnerFecha = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fechas);
        spinnerFecha.setAdapter(adapterSpinnerFecha);

        cursor.close();

    }

    //Metodo para obtener tablas
    public void obtenerTablas(String fecha){

        ArrayAdapter<String> adapterSpinnerTabla;
        ArrayList<String> tablas = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT TablaJugada FROM partidas WHERE Fecha = '" + fecha +"'",null);
        if(cursor.getCount() == 0){
            tablas.add("No hay registros");
        }else{
            while(cursor.moveToNext()){
                if(!tablas.contains(cursor.getString(0))){
                    tablas.add(cursor.getString(0));
                }

            }
        }
        adapterSpinnerTabla = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tablas);
        spinnerTabla.setAdapter(adapterSpinnerTabla);

        cursor.close();

    }

    //Metodo para obtener las estadisticas de las tablas jugadas
    public void obtenerEstadisticas(String fecha, String tabla){

        ArrayAdapter<String> adapter;
        ArrayList<String> listaEstadisticas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT InfoPartida FROM partidas WHERE Fecha = '" + fecha +"' AND TablaJugada = '" + tabla +"'",null);

        if(cursor.getCount()==0){
            listaEstadisticas.add("No hay registros");
        }else{
            while(cursor.moveToNext()){
                listaEstadisticas.add(cursor.getString(0));
            }
        }

        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaEstadisticas);
        listViewEstadisticas.setAdapter(adapter);

        cursor.close();

    }

    //Metodo para eliminar las estadisticas de la base de datos
    public void reset(View v){

        try {
            db.execSQL("DELETE FROM partidas");
            spinnerFecha.setAdapter(null);
            spinnerTabla.setAdapter(null);
            listViewEstadisticas.setAdapter(null);

            Toast.makeText(getContext(), "Estadisticas eliminadas ", Toast.LENGTH_SHORT).show();
        }catch (SQLException s){
            Toast.makeText(getContext(), "Error al borrar", Toast.LENGTH_SHORT).show();
        }

    }

}
