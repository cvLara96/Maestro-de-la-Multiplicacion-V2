package com.example.maestrodelamultiplicacion_v2.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.maestrodelamultiplicacion_v2.R;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentHomeBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {

    GridLayout grid;

    //Creamos el array de dificultades
    String[] dificultad = {"Fácil", "Medio", "Difícil"};

    //Declaramos el adaptador
    ArrayAdapter<String> adaptador;

    //Creamos el array de avatares
    String [] avatares = {"Black Panther", "Capitan America", "Hulk", "Iron-Man", "Spiderman"};

    //Creamos el array de imagenes
    int imagenes [] = {R.drawable.black1, R.drawable.capi1, R.drawable.hulk1,R.drawable.iron1,R.drawable.spider1};

    //Creamos el textview para la fecha del dia
    TextView textoFecha;


    //Creamos el adaptador personalizado
    public class AdaptadorPersonalizado extends ArrayAdapter {

        public AdaptadorPersonalizado(Context context, int resource, Object[] objects) {
            super(context, resource, objects);
        }

        //Programamos los metodos getDropDownView y getView() de la clase padre que retornan
        //la vista personalizada de una fila determinada. Cuando el Spinner invoque a estos métodos
        //debemos pasarle la fila personalizada de la posición que nos indique el parámetro posicion
        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return crearFilaPersonalizada(position, convertView, parent);
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return crearFilaPersonalizada(position, convertView, parent);
        }

        //Los metodos reescritos invocan a una tercera función crearFilaPersonalizada() que
        //se encargara de "inflar" la vista del fichero xml filasspinneravatar.xml para crear
        //un objeto View con los datos de los arrays y lo retornará
        public View crearFilaPersonalizada(int position, View convertview, ViewGroup parent){

            LayoutInflater inflater = getLayoutInflater();
            View miFila = inflater.inflate(R.layout.filaspinneravatar, parent, false);

            TextView textSeleccion = (TextView) miFila.findViewById(R.id.textAvatarElegido);
            textSeleccion.setText(avatares[position]);

            ImageView imagenSeleccion = (ImageView) miFila.findViewById(R.id.imageAvatarElegido);
            imagenSeleccion.setImageResource(imagenes[position]);

            return miFila;
        }

    }

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*--INICIO Spinner seleccion avatar*/
        Spinner spSeleccion = (Spinner)binding.spinnerSeleccionAvatar;
        //Cuando creemos el spinner, le enviaremos el adaptador de esta subclase:
        AdaptadorPersonalizado a = new AdaptadorPersonalizado(getContext(), R.layout.filaspinneravatar, avatares);
        spSeleccion.setAdapter(a);
        spSeleccion.setOnItemSelectedListener(this);
        /*--FIN Spinner seleccion avatar*/

        /*--INICIO Spinner seleccion dificultad*/
        Spinner spSeleccionDificultad = (Spinner)binding.spinnerSeleccionDificultad;
        //Debemos crear el adaptador, pero para ello en primer lugar lo creamos
        //utilizando como parametro android.R.layout.simple_spinner_item y luego
        //al adaptador ya creado con el metodo .setDropViewResource() le pasamos como
        //parametro android.R.layout.simple_spinner_dropdown_item:
        adaptador = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dificultad);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /*De este modo:
            android.R.layout.simple_spinner_item sirve para mostrar el elemento seleccionado
            android.R.layout.simple_spinner_dropdown_item para mostrar los elementos desplegables
        * */
        spSeleccionDificultad.setAdapter(adaptador);
        spSeleccionDificultad.setOnItemSelectedListener(this);
        /*--FIN Spinner seleccion dificultad*/


        /*--INICIO asignacion metodo de los botones*/
        grid = (GridLayout) binding.gridSeleccionTabla;
        asignarOnCLick(grid);
        /*--FIN asignacion metodo de los botones*/

        //-- INICIO obtener fecha del sistema
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaActual = dateFormat.format(calendar.getTime());
            //Asignamos el valor al textview de la fecha
            textoFecha = binding.textViewFecha;
            textoFecha.setText(fechaActual);
            //Guardamos la fecha
            guardarFecha(fechaActual);
        //--FIN obtener fecha del sistema

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Metodo CALLBACK para cuando se selccione un elemento del spinner, aqui deberemos enviar la
    //eleccion al otro fragmento
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int spinnerId = parent.getId();
        String avatar;
        String dificultad;

        switch(spinnerId){

            case R.id.spinnerSeleccionAvatar:

                Spinner spinnerAvatar = (Spinner) binding.spinnerSeleccionAvatar;
                switch(spinnerAvatar.getSelectedItem().toString()){
                    case "Black Panther":
                        avatar = spinnerAvatar.getSelectedItem().toString();
                        guardarAvatar(avatar);
                        break;
                    case "Capitan America":
                        avatar = spinnerAvatar.getSelectedItem().toString();
                        guardarAvatar(avatar);
                        break;
                    case "Hulk":
                        avatar = spinnerAvatar.getSelectedItem().toString();
                        guardarAvatar(avatar);
                        break;
                    case "Iron-Man":
                        avatar = spinnerAvatar.getSelectedItem().toString();
                        guardarAvatar(avatar);
                        break;
                    case "Spiderman":
                        avatar = spinnerAvatar.getSelectedItem().toString();
                        guardarAvatar(avatar);
                        break;
                }
                break;

            case R.id.spinnerSeleccionDificultad:
                Spinner spinnerDificultad = (Spinner) binding.spinnerSeleccionDificultad;
                switch (spinnerDificultad.getSelectedItem().toString()){
                    case "Fácil":
                        dificultad = spinnerDificultad.getSelectedItem().toString();
                        guardarDificultad(dificultad);
                        break;
                    case "Medio":
                        dificultad = spinnerDificultad.getSelectedItem().toString();
                        guardarDificultad(dificultad);
                        break;
                    case "Difícil":
                        dificultad = spinnerDificultad.getSelectedItem().toString();
                        guardarDificultad(dificultad);
                        break;
                }
                break;
        }

    }



    //Metodo para cuando no haya nada seleccionado
    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    //Metodo para el pulsado de la botonera
    public void onCLickBotones (View v){

        TextView text = (TextView) binding.textTablaSeleccionada;
        int num_selected; //Aqui almacenaremos la seleccion del usuario

        switch(v.getId()){
            case R.id.button0:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button1:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button2:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button3:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button4:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button5:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button6:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button7:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button8:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.button9:
                text.setText("¡Tabla de multiplicar del " + ((Button)v).getText().toString() + " generada!");
                num_selected = Integer.parseInt(((Button)v).getText().toString());
                guardarNumSeleccionado(num_selected);
                break;
            case R.id.btnInterr:
                text.setText("¡Tabla de multiplicar aleatoria generada!");
                num_selected = 10;
                guardarNumSeleccionado(num_selected);
                break;
        }

    }

    //Metodo para asignar el metodo anterior
    public void asignarOnCLick(GridLayout grid){
        //Recorremos el grid y asignamos un .setOnClickListener(this) a cada boton
        for (int i = 0; i < grid.getChildCount(); i++){
            //Creamos una vista y la igualamos a elemento hijo que este en la posicion i
            View hijo = grid.getChildAt(i);

            //Si el elemento hijo es una instancia "instanceoff" de Button(...)
            if(hijo instanceof Button){
                //(...) Lo convertira en button y le asignara el .setOnClickListener
                Button b = (Button) hijo;
                b.setOnClickListener(this::onCLickBotones);
            }
        }

    }

    //Metodo para guardar el numero que seleccione el usuario
    private void guardarNumSeleccionado(int numero){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos el número seleccionado con la clave "numero_seleccionado"
        editor.putInt("numero_seleccionado", numero);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }

    //Metodo para guardar el avatar que seleccione el usuario
    private void guardarAvatar (String avatar){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos el avatar seleccionado con la clave "avatar_seleccionado"
        editor.putString("avatar_seleccionado", avatar);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();

    }

    //Metodo para guardar la dificultad que seleccione el usuario
    private void guardarDificultad (String dificultad){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos la dificultad seleccionado con la clave "dificultad_seleccionada"
        editor.putString("dificultad_seleccionada", dificultad);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();

    }

    //Metodo para guardar la fecha que seleccione el usuario
    private void guardarFecha (String fecha){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos la fecha seleccionado con la clave "fecha_seleccionada"
        editor.putString("fecha_seleccionada", fecha);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }

}