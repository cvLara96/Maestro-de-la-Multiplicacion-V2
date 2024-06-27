package com.example.maestrodelamultiplicacion_v2.ui.slideshow;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maestrodelamultiplicacion_v2.DatabaseHelper;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentSlideshowBinding;
import com.example.maestrodelamultiplicacion_v2.ui.contacts.Contacto;
import com.example.maestrodelamultiplicacion_v2.ui.contacts.ContactsFragment;
import com.example.maestrodelamultiplicacion_v2.ui.contacts.MyAdapter;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    //FRAGMENTO PARA ENVIAR
    SQLiteDatabase db;
    DatabaseHelper dbHelper;
    Spinner spinnerFecha, spinnerTabla;
    String fechaSeleccionada;

    ArrayList<String> listaEstadisticas;
    ArrayList<Contacto> listaFavs;
    Button enviar;
    String mensaje;

    RecyclerView recyclerView;
    Contacto contactoFav;

    //Creamos el adaptador en la calse MyAdapter
    MyAdapterFavs adaptador;

    Button resetFavs;
    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listaEstadisticas = new ArrayList<>();
        listaFavs = new ArrayList<>();

        /*INICIO registro spinners*/
        spinnerFecha = binding.spinnerFechaSEND;
        spinnerTabla = binding.spinnerTablaSEND;
        /*FIN registro spinners*/

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

        enviar = binding.btnEnviar;
        enviar.setOnClickListener(this::enviarMail);

        //Obtener contactos favoritos:
        try {
            obtenerFavs();
        }catch(SQLiteException e) {
            // La base de datos no está creada, maneja la situación aquí
            Toast.makeText(getContext(), "La base de datos no está creada", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //mandamos el adapatador a la lista de contactos
        recyclerView = binding.recyclerFavs;
        adaptador = new MyAdapterFavs(listaFavs, requireContext(), SlideshowFragment.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adaptador);
        //Fin obtener favoritos

        //Inicio limpiar favoritos+
        resetFavs = binding.buttonDeleteFavs;
        resetFavs.setOnClickListener(this::resetFavs);

        //Fin limpiar favoritos

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

        if(!listaEstadisticas.isEmpty()){
            listaEstadisticas.clear();
        }else{
            System.out.println("Array vacio");
        }

        ArrayAdapter<String> adapter;
        Cursor cursor = db.rawQuery("SELECT InfoPartida FROM partidas WHERE Fecha = '" + fecha +"' AND TablaJugada = '" + tabla +"'",null);

        if(cursor.getCount()==0){
            listaEstadisticas.add("No hay registros");
        }else{
            while(cursor.moveToNext()){
                listaEstadisticas.add(cursor.getString(0));
            }
        }

        cursor.close();

    }

    //Metodo para obtener contactos favoritos:
    //Metodo para obtener las estadisticas de las tablas jugadas
    public void obtenerFavs(){



        if(!listaFavs.isEmpty()){
            listaFavs.clear();
        }else{
            System.out.println("Array vacio");
        }

        ArrayAdapter<String> adapter;
        Cursor cursor = db.rawQuery("SELECT * FROM favoritos",null);

        if(cursor.getCount()==0){
            Toast.makeText(getContext(), "No hay contactos en favoritos", Toast.LENGTH_SHORT).show();
        }else{
            while(cursor.moveToNext()){
                contactoFav = new Contacto();
                contactoFav.setNombre(cursor.getString(0));
                contactoFav.setTelefono(cursor.getString(1));
                listaFavs.add(contactoFav);
            }
        }

        cursor.close();

    }

    //Metodo para enviar el correo
    public void enviarMail (View v){


        //En primer lugar, se crea el objeto Intent:
        Intent i = new Intent();

        //Creamos un chooser y lo igualamos a null:
        //El chooser es el mecanismo por el cual Android permite al usuario elegir una aplicación de entre las
        //posibles candidatas a tratar la petición que envía el intent
        Intent chooser = null;

        //A continuacion creamos el editText donde escribimos la direccion de correo
        EditText mail = (EditText) binding.editTextEmail;
        //Asignamos la accion al intent implicito
        //ACTION_SEND -> (enviar)
        i.setAction(Intent.ACTION_SEND);
        //Le indicamos el valor de lo que debera buscar, al tratarse de un
        //email, indicaremos mailto: en el setData() y luego mediante .putExtra()
        //indicaremos el resto de campos:
        i.setData(Uri.parse("mailto:"));
        //Creamos el "para" del mail
        String para [] = {mail.getText().toString()};
        //Creamos el asunto
        i.putExtra(Intent.EXTRA_EMAIL, para);
        i.putExtra(Intent.EXTRA_SUBJECT, "Estadísticas maestro de la multiplicación"); //Indicamos un asunto por defecto
        if(!listaEstadisticas.isEmpty()){
            for(String s : listaEstadisticas){
                mensaje = mensaje + "\n" +
                        "------------------\n" + s.toString();
                i.putExtra(Intent.EXTRA_TEXT, mensaje); //Indicamos un mensaje por defecto
            }
        }else{
            System.out.println("Array vacio");
        }

        //Para enviar un email hay
        //que indicar que el tipo corresponde al MIME especificado en la RFC 822 --> ("message/rfc822")
        i.setType("message/rfc822");
        //Creamos el chooser
        chooser = Intent.createChooser(i, "Enviar email");
        //Iniciamos la actividad, al no esperar un resultado, sera con startActivity()
        startActivity(i);
        //Lanzamos una tostada a modo de informacion:
        Toast.makeText(getContext(),"Envia el email!", Toast.LENGTH_LONG).show();
    }

    public void finalizar(String nombreContacto) {

        EditText mail = (EditText) binding.editTextEmail;
        mail.setText(nombreContacto);
    }

    //Metodo para borrar favoritos
    public void resetFavs(View v){

        try {
            db.execSQL("DELETE FROM favoritos");
            recyclerView.setAdapter(null);

            Toast.makeText(getContext(), "Contactos favoritos eliminados ", Toast.LENGTH_SHORT).show();
        }catch (SQLException s){
            Toast.makeText(getContext(), "Error al borrar", Toast.LENGTH_SHORT).show();
        }

    }

}