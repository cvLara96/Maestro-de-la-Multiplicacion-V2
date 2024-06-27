package com.example.maestrodelamultiplicacion_v2.ui.gallery;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.maestrodelamultiplicacion_v2.DatabaseHelper;
import com.example.maestrodelamultiplicacion_v2.R;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentGalleryBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.google.gson.Gson;

public class GalleryFragment extends Fragment {

    int posibleSolucion = 0;

    //Aciertos y fallos
    int aciertos = 0;
    int fallos = 0;
    //Creamos un ArrayList para almacenar los errores
    ArrayList<Multiplicacion> errores = new ArrayList<>();

    //Barra de progreso
    int progreso = 0;
    ProgressBar progressBar;

    //Creamos un ArrayList donde meteremos las multiplicaciones de las tablas
    ArrayList<Multiplicacion> tabla = new ArrayList<>();

    //Indices
    int indice = 0;
    int indiceAvatar = 0;

    //Boton verificar
    Button verificar;

    //Boton borrar
    Button borrar;

    //Boton pocion y texto pocion
    ImageButton poti;
    TextView textoPocion;

    //Grid con los botones
    GridLayout grid;
    //Boolean para comprobar resultado
    boolean correcto;

    //Arrays de imagenes de avatares
    int spiderman [] = {R.drawable.spider11, R.drawable.spider10, R.drawable.spider9,
            R.drawable.spider8, R.drawable.spider7, R.drawable.spider6, R.drawable.spider5,
            R.drawable.spider4,R.drawable.spider3, R.drawable.spider2, R.drawable.spider1};
    int hulk [] = {R.drawable.hulk11, R.drawable.hulk10, R.drawable.hulk9,
            R.drawable.hulk8, R.drawable.hulk7, R.drawable.hulk6, R.drawable.hulk5,
            R.drawable.hulk4,R.drawable.hulk3, R.drawable.hulk2, R.drawable.hulk1};
    int blackpanther [] = {R.drawable.black11, R.drawable.black10, R.drawable.black9,
            R.drawable.black8, R.drawable.black7, R.drawable.black6, R.drawable.black5,
            R.drawable.black4,R.drawable.black3, R.drawable.black2, R.drawable.black1};
    int capi [] = {R.drawable.capi11, R.drawable.capi10, R.drawable.capi9,
            R.drawable.capi8, R.drawable.capi7, R.drawable.capi6, R.drawable.capi5,
            R.drawable.capi4,R.drawable.capi3, R.drawable.capi2, R.drawable.capi1};
    int ironman [] = {R.drawable.iron11, R.drawable.iron10, R.drawable.iron9,
            R.drawable.iron8, R.drawable.iron7, R.drawable.iron6, R.drawable.iron5,
            R.drawable.iron4,R.drawable.iron3, R.drawable.iron2, R.drawable.iron1};

    int avatarElegido[] = new int[10];

    public static Estadisticas informe;

    //Creamos el objeto SQLite
    SQLiteDatabase db;
    DatabaseHelper dbHelper;

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        generarTablaPrincipal(recuperarNumSeleccionado(), recuperarDificultad());
        cargarAvatar(recuperarAvatar());

        /*--INICIO asignar onClick*/
        grid = binding.gridPosibleSolucion;
        asignarOnClick(grid);
        /*--FIN asignar onClick*/

        //Asignar onClick al boton verificar
        verificar = (Button) binding.verificar;
        verificar.setOnClickListener(this::onClickVerificar);

        //Asignar onClick al boton Borrar
        borrar = (Button) binding.borrar;
        borrar.setOnClickListener(this::onClickBorrar);

        //Barra de progreso
        int colorGris = getResources().getColor(R.color.gris);
        progressBar = binding.progressBar;
        progressBar.setProgress(progreso);
        //Ponemos la barra de progreso gris siempre que se abra esta actividad
        progressBar.setProgressTintList(ColorStateList.valueOf(colorGris));

        /*--INICIO asignar onClick a la pocion y registro del texto de la pocion*/
        textoPocion = (TextView) binding.textoPocion;
        poti = (ImageButton) binding.imagePoti;
        if(recuperarAciertos()==11){
            poti.setVisibility(View.VISIBLE);
            textoPocion.setVisibility(View.VISIBLE);
        }else{
            poti.setVisibility(View.INVISIBLE);
            textoPocion.setVisibility(View.INVISIBLE);
        }
        poti.setOnClickListener(this::onClickPoti);
        /*--FIN asignar onClick a la pocion*/

        /*INICIO SQLite para guardar el informe de partidas*/

        //Creamos la tabla para los informes de las partidas
        dbHelper = new DatabaseHelper(getActivity(),"MaestroMultiplicacion",null,1);
        db = dbHelper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS partidas(Fecha VARCHAR, TablaJugada VARCHAR, InfoPartida VARCHAR);");
        /*FIN SQLite para guardar el informe de partidas*/

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        db.close();
        dbHelper.close();
    }

    //Metodo que recibe como parametro un numero, y genera la tabla de multiplicar
    //y la mete en el arrayList tabla
    public void generarTablaPrincipal (int numero, String dificultad){

        //Creamos los textview
        TextView text = (TextView) binding.operacion;
        //Creamos un objeto Random para los aleatorios
        Random random = new Random();


        switch(dificultad){

            case "Fácil":
                //En el caso de que el numero sea 10, siginfica que el usuario ha elegido '?',
                //por lo que generaremos una tabla de multiplicar aleatoria
                if(numero == 10){
                    int multiplicador = random.nextInt(11); //Numero aleatorio entre 0 y 10
                    for(int i = 0; i <= 10; i++){
                        //Creamos un objeto Multiplicacion
                        Multiplicacion multiplicacion = new Multiplicacion();
                        multiplicacion.setFactor1(multiplicador);
                        multiplicacion.setFactor2(i);
                        tabla.add(multiplicacion);
                    }
                    continuarTabla();
                }else {
                    for(int i = 0; i <= 10; i++) {
                        //Creamos un objeto Multiplicacion
                        Multiplicacion multiplicacion = new Multiplicacion();
                        multiplicacion.setFactor1(numero);
                        multiplicacion.setFactor2(i);
                        tabla.add(multiplicacion);
                    }
                    continuarTabla();
                }

                break;

            case "Medio":
                //En el caso de que el numero sea 10, siginfica que el usuario ha elegido '?',
                //por lo que generaremos una tabla de multiplicar aleatoria
                if(numero == 10){
                    int multiplicador = random.nextInt(11); //Numero aleatorio entre 0 y 10
                    for(int i = 10; i >= 0; i--){
                        //Creamos un objeto Multiplicacion
                        Multiplicacion multiplicacion = new Multiplicacion();
                        multiplicacion.setFactor1(multiplicador);
                        multiplicacion.setFactor2(i);
                        tabla.add(multiplicacion);
                    }
                    continuarTabla();
                }else {
                    for(int i = 10; i >= 0; i--) {
                        //Creamos un objeto Multiplicacion
                        Multiplicacion multiplicacion = new Multiplicacion();
                        multiplicacion.setFactor1(numero);
                        multiplicacion.setFactor2(i);
                        tabla.add(multiplicacion);
                    }
                    continuarTabla();
                }

                break;

            case "Difícil":

                if(numero == 10){
                    int multiplicador = random.nextInt(11); //Numero aleatorio entre 0 y 10
                    for(int i = 0; i <= 10; i++){
                        //Creamos un objeto Multiplicacion
                        Multiplicacion multiplicacion = new Multiplicacion();
                        multiplicacion.setFactor1(multiplicador);
                        multiplicacion.setFactor2(i);
                        tabla.add(multiplicacion);
                    }
                    //Con Collections.shuffle barajamos la lista
                    Collections.shuffle(tabla);
                    continuarTabla();

                }else {
                    for(int i = 0; i <= 10; i++) {
                        //Creamos un objeto Multiplicacion
                        Multiplicacion multiplicacion = new Multiplicacion();
                        multiplicacion.setFactor1(numero);
                        multiplicacion.setFactor2(i);
                        tabla.add(multiplicacion);
                    }
                    //Con Collections.shuffle barajamos la lista
                    Collections.shuffle(tabla);
                    continuarTabla();
                }
                break;
        }

    }

    //Metodo que cuando se llame mostrara la siguiente operacion a realizar
    public void continuarTabla(){

        int factor1;
        int factor2;
        TextView text = (TextView)binding.operacion;
        TextView textResultado = (TextView)binding.resultado;

        factor1 = tabla.get(indice).getFactor1();
        factor2 = tabla.get(indice).getFactor2();
        text.setText(factor1 + "x" + factor2 + " = ?");
        textResultado.setText("");

    }

    //Metodo que sera encargado de comprobar que el resultado introducido
    //por el usuario es correcto
    public void comprobarResultado(int posibleSolucion){

        //Creamos los textview donde mostraremos la correccion
        TextView revResultado1 = (TextView) binding.revResultado;
        TextView revResultado2 = (TextView) binding.revResultado2;

        int factor1 = tabla.get(indice).getFactor1();
        int factor2 = tabla.get(indice).getFactor2();
        int resultado = factor1 * factor2;

        if(posibleSolucion == resultado){
            revResultado2.setText("");
            revResultado1.setText(factor1 + " x " + factor2 +" = " + posibleSolucion + " V");
            revResultado1.setTextColor(getResources().getColor(R.color.verde));
            aciertos++;
            actualizarAvatar();
        }else{
            revResultado1.setText(factor1 + " x " + factor2 +" = " + posibleSolucion + " F");
            Multiplicacion multiplicacionFallada = new Multiplicacion(factor1, factor2, posibleSolucion);
            errores.add(multiplicacionFallada);
            revResultado2.setText(factor1 + " x " + factor2 +" = " + resultado + " V");
            revResultado1.setTextColor(getResources().getColor(R.color.rojo));
            revResultado2.setTextColor(getResources().getColor(R.color.verde));
            fallos++;
        }
    }

    //Metodo que actualiza el avatar
    public void actualizarAvatar(){

        ImageView imagen = (ImageView) binding.avatarSelected;
        imagen.setImageResource(avatarElegido[indiceAvatar]);
        indiceAvatar++;

    }

    //Actualizar barra de progreso
    public void actualizarBarra(){

        progreso = progreso + 10;
        int colorRojo = getResources().getColor(R.color.rojo);
        int colorAmarillo = getResources().getColor(R.color.amarillo);
        int colorVerde = getResources().getColor(R.color.verde);

        if(progreso>=0 && progreso <30){
            progressBar.setProgress(progreso);
            progressBar.setProgressTintList(ColorStateList.valueOf(colorRojo));
        }else if (progreso >= 30 && progreso<60){
            progressBar.setProgress(progreso);
            progressBar.setProgressTintList(ColorStateList.valueOf(colorAmarillo));
        }else if(progreso >= 60 && progreso <= 100) {
            progressBar.setProgress(progreso);
            progressBar.setProgressTintList(ColorStateList.valueOf(colorVerde));
        }
    }

    //Metodo que devuelve true si la tabla esta finalizada
    public boolean tablaCompleta(int indice){
        if(indice == tabla.size()){
            return true;
        }else{
            return false;
        }
    }

    //Metodo que carga el primer frame del avatar
    public void cargarAvatar(String avatar){
        ImageView imagenAvatar = (ImageView) binding.avatarSelected;

        switch(avatar){
            case "Black Panther":
                imagenAvatar.setImageResource(R.drawable.black12);
                avatarElegido = blackpanther;
                break;
            case "Capitan America":
                imagenAvatar.setImageResource(R.drawable.capi12);
                avatarElegido = capi;
                break;
            case "Hulk":
                imagenAvatar.setImageResource(R.drawable.hulk12);
                avatarElegido = hulk;
                break;
            case "Iron-Man":
                imagenAvatar.setImageResource(R.drawable.iron12);
                avatarElegido = ironman;
                break;
            case "Spiderman":
                imagenAvatar.setImageResource(R.drawable.spider12);
                avatarElegido = spiderman;
                break;
        }

    }

    //Metodo para la funcion de los botones
    private void onClickSolucion(View v){

        TextView text = (TextView) binding.resultado;

        if (text.length() == 0) {
            text.setText(((Button) v).getText().toString());
        } else if (text.length() < 3) {
            text.setText(text.getText().toString() + (((Button) v).getText().toString()));
        }

    }

    //Metodo para el boton verificar
    private void onClickVerificar(View v){

        int colorAzul = getResources().getColor(R.color.teal_200);
        if(indice < tabla.size()) {
            TextView text = (TextView) binding.resultado;
            TextView finTabla = (TextView) binding.finTabla;
            if (text.getText().toString().isEmpty()) {
                //Si se pulsa verificar sin introducir numeros se muestra un Toast
                Toast.makeText(getActivity(), "Debes añadir un resultado", Toast.LENGTH_SHORT).show();
            }
            try {
                posibleSolucion = Integer.parseInt(text.getText().toString());
                comprobarResultado(posibleSolucion);
                indice++;
                if(tablaCompleta(indice)){
                    System.out.println("Tabla completada");
                    finTabla.setText("¡Tabla del " + tabla.get(0).factor1 + " completada!");
                    guardarFactor1(tabla.get(0).factor1);
                    //Cuando se complete la tabla se pondra azul la barra de progreso
                    progressBar.setProgressTintList(ColorStateList.valueOf(colorAzul));
                    //Se guardaran los aciertos y fallos en sharedpreferences
                    guardarAciertos(aciertos);
                    guardarFallos(fallos);
                    guardarFalladas(errores);
                    informe = new Estadisticas(recuperarFecha(),String.valueOf(tabla.get(0).factor1),generarInfo(tabla.get(0).factor1));
                    if(aciertos==11){
                        poti.setVisibility(View.VISIBLE);
                        textoPocion.setVisibility(View.VISIBLE);
                    }

                }else {
                    guardarFactor1(tabla.get(0).factor1);
                    guardarAciertos(aciertos);
                    guardarFallos(fallos);
                    guardarFalladas(errores);
                    informe = new Estadisticas(recuperarFecha(),String.valueOf(tabla.get(0).factor1),generarInfo(tabla.get(0).factor1));
                    continuarTabla();
                    actualizarBarra();
                }
            } catch (NumberFormatException e) {
                System.out.println(e.toString());
            }
        }

    }

    //onClick del boton borrar
    private void onClickBorrar(View v){

        TextView textoSolucion = (TextView) binding.resultado;
        String resultado = textoSolucion.getText().toString();
        if(resultado.length()>0){
            String nuevoResultado = resultado.substring(0, resultado.length()-1);//Lo que hace es borrar caracter a caracter
            textoSolucion.setText(nuevoResultado);
        }
    }

    //Metodo onClick para el imagebutton para que al pulsar la pocion nos de la solucion
    private void onClickPoti(View v){

        if(tablaCompleta(indice)){
            //Si la tabla esta completa y se pulsa sobre la gema, lanzara una tostada
            Toast.makeText(getActivity(), "La tabla ya está completa", Toast.LENGTH_SHORT).show();
        }else {

            int factor1 = tabla.get(indice).getFactor1();
            int factor2 = tabla.get(indice).getFactor2();
            int resultado = factor1 * factor2;
            TextView textoSolucion = (TextView) binding.resultado;
            textoSolucion.setText(String.valueOf(resultado));

            poti.setVisibility(View.INVISIBLE);
            textoPocion.setVisibility(View.INVISIBLE);
        }
    }

    //Metodo que recorre el grid para asignar el onClick a cada boton
    private void asignarOnClick(GridLayout grid){

        for(int i = 0; i <=grid.getChildCount(); i++){
            View hijo = grid.getChildAt(i);
            if(hijo instanceof Button){
                Button b = (Button) hijo;
                b.setOnClickListener(this::onClickSolucion);
            }
        }
    }

    //Metodo para recuperar el numero seleccionado en el fragment principal
    private int recuperarNumSeleccionado(){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // El segundo parámetro es el valor predeterminado en caso de que no se encuentre la clave
        return preferences.getInt("numero_seleccionado", 0);
    }

    //Metodo para recuperar el avatar seleccionado en el fragment principal
    private String recuperarAvatar(){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // El segundo parámetro es el valor predeterminado en caso de que no se encuentre la clave
        return preferences.getString("avatar_seleccionado", "sin_seleccion");
    }

    //Metodo para recuperar el avatar seleccionado en el fragment principal
    private String recuperarDificultad(){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // El segundo parámetro es el valor predeterminado en caso de que no se encuentre la clave
        return preferences.getString("dificultad_seleccionada", "");
    }

    //Metodo para guardar los aciertos:
    private void guardarAciertos (int aciertos){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos el numero de aciertos
        editor.putInt("num_aciertos", aciertos);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }

    //Metodo para guardar los fallos:
    private void guardarFallos (int fallos){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos el numero de aciertos
        editor.putInt("num_fallos", fallos);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }

    //Metodo para guardar el factor:
    private void guardarFactor1 (int factor1){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos el factor1
        editor.putInt("factor1", factor1);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }


    //Metodos para guardar las multiplicaciones falladas:
    private void guardarFalladas (ArrayList<Multiplicacion> m){
        //Obtenemos una referencia a SharedPreferences asociado con la actividad actual
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        //Convertimos la lista en una cadena Json
        //implementamos implementation ("com.google.code.gson:gson:2.8.9") en build.gradle.kts
        Gson gson = new Gson();
        String mJSON = gson.toJson(m);
        //Creamos un editor para modificar los valores en SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        //Almacenamos la cadena JSON en SharedPreferences
        editor.putString("multiplicaciones_fallidas", mJSON);
        //Aplicar los cambios al editor (guardar los datos en SharedPreferences)
        editor.apply();
    }

    //Metodo para recuperar el numero de aciertos:
    private int recuperarAciertos(){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // El segundo parámetro es el valor predeterminado en caso de que no se encuentre la clave
        return preferences.getInt("num_aciertos", 0);
    }

    //Metodo para crear el String de infor partida
    public String generarInfo(int factor1){

        String infoPartida = "ESTADISTICAS:\n\n" +
                "- Tabla del " + factor1 +
                "\n- Dificultad seleccionada: " + recuperarDificultad() + "\n"+
                "- Nº de aciertos: " + aciertos + "\n" +
                "- Nº de fallos: " + fallos + "\n" +
                getOperaciones(errores) +
                "\n\nPorcentaje de aciertos del " + promedio(aciertos) + "%";

        return infoPartida;

    }

    //Metodo que devuelve las operaciones
    public String getOperaciones (ArrayList<Multiplicacion> m ){
        String lista = "";
        if(!m.isEmpty()){
            for (Multiplicacion multiplicacion : m){
                lista = lista +"\n"+ multiplicacion.getFactor1() + " x " + multiplicacion.getFactor2() + " = " + multiplicacion.getResultado();
            }
        }else{
            lista = "-- No hay multiplicaciones erróneas --";
        }
        return lista;
    }
    //Metodo para calcular el promedio de aciertos
    private double promedio (int aciertos){

        double promedio = 0;

        promedio = (aciertos * 100)/11;

        return promedio;
    }


    //Metodo para recuperar la fecha seleccionada en el fragment principal
    private String recuperarFecha(){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // El segundo parámetro es el valor predeterminado en caso de que no se encuentre la clave
        return preferences.getString("fecha_seleccionada", "sin_seleccion");
    }

    //Metodo para añadir la info de la partida a la base de datos:
    public void añadir (Estadisticas informe){

        if(informe!=null){
            db.execSQL("INSERT INTO partidas VALUES ('" + informe.getFechaPartida() + "','" +
                    informe.getTablaJugada() + "','" + informe.getInfoPartida() + "')");
            Toast.makeText(getContext(), "Se añadio el registro de la partida", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Aun no hay informes", Toast.LENGTH_SHORT).show();
        }
    }

    //Metodo para guardar la informacion siempre que se abandone la vista
    @Override
    public void onPause() {
        añadir(informe);
        super.onPause();
    }
}