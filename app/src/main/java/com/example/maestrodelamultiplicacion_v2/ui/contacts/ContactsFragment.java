package com.example.maestrodelamultiplicacion_v2.ui.contacts;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maestrodelamultiplicacion_v2.DatabaseHelper;
import com.example.maestrodelamultiplicacion_v2.MainActivity;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentContactsBinding;
import com.example.maestrodelamultiplicacion_v2.ui.gallery.Estadisticas;

import java.util.ArrayList;

public class ContactsFragment extends Fragment {

    EditText editText;
    RecyclerView recyclerView;

    //Creamos el adaptador en la calse MyAdapter
    MyAdapter adaptador;


    //CREAMOS EL SIGUIENTE BOOLEAN PARA CUANDO SE SOLICITEN PERMISOS:
    private boolean tengo_permisos;

    //Creamos el objeto SQLite
    SQLiteDatabase db;
    DatabaseHelper dbHelper;

    private FragmentContactsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactsViewModel contactsViewModel =
                new ViewModelProvider(this).get(ContactsViewModel.class);

        binding = FragmentContactsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tengo_permisos = recuperaPermisos();

        editText = binding.editTextBuscar;


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if(tengo_permisos){
                        ArrayList<Contacto>lista_contactos = buscar(editText.getText().toString());

                        //mandamos el adapatador a la lista de contactos
                        recyclerView = binding.recycler;
                        adaptador = new MyAdapter(lista_contactos, requireContext(), ContactsFragment.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        recyclerView.setAdapter(adaptador);

                    }
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        // Ocultar el teclado virtual
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        return true;
                    }



                }catch(SecurityException e){
                    Toast.makeText(getContext(), "La aplicación necesita permisos de contacto", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        //Creamos la tabla para los contactos favoritos
        dbHelper = new DatabaseHelper(getActivity(),"MaestroMultiplicacion",null,1);
        db = dbHelper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS favoritos(nombre VARCHAR, numTelefono VARCHAR);");
        /*FIN SQLite para guardar el contactos favoritos*/


        return root;
    }

    //CREACION DEL METODO BUSCAR: Obtener una referencia al ContentResolver para que a
    //través del método Query, poder hacer una consulta sobre la Uri
    //ContactsContract.Contacts.CONTENT_URI. Esta Uri nos da acceso a la tabla de contactos, de
    //la que consultaremos los campos: ID, DISPLAY_NAME, HAS_PHONE_NUMBER, PHOTO_ID
    @SuppressLint("Range")
    public ArrayList<Contacto> buscar (String nombreContacto){

        //Datos que consultaremos en la tabla de contactos:
        String proyeccion[] = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_ID};

        //Filtro:
        String filtro = ContactsContract.Contacts.DISPLAY_NAME + " like?";
        String args_filtro[] = {"%" + nombreContacto + "%"};


        ArrayList<Contacto> lista_contactos = new ArrayList<>();

        //Con el content resolver, ejecutar el método query con el filtro obtenido de la caja de texto que
        //el usuario ha rellenado:
        ContentResolver cr = requireContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, proyeccion, filtro, args_filtro, null);


        //Con el cursor, recorrer la lista de contactos extraída, agregando los elementos a un
        //ArrayList. Con ese ArrayList construir un adaptador para el listView.
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                Contacto contacto = new Contacto();
                //Obtenemos el nombre del contacto
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                contacto.setNombre(name);

                // Ahora, con el ID del contacto, podemos consultar los números de teléfono asociados
                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                if (phones != null) {
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacto.setTelefono(phoneNumber);
                        lista_contactos.add(contacto);
                    }

                /*Si tiene telefono, lo agregamos a la lista de contactos:
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                    lista_contactos.add(contacto);

                }*/
                }
                phones.close();

            }
        }
        //Cerramos el cursor
        cursor.close();
        //Devolvemos la lista de contactos
        return lista_contactos;
    }

    //Metodo para recuperar permisos instantaneamente
    private boolean recuperaPermisos(){
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // El segundo parámetro es el valor predeterminado en caso de que no se encuentre la clave
        return preferences.getBoolean("permisos", true);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        db.close();
        dbHelper.close();
    }

    public void finalizar(String nombre, String numTelefono) {

        Contacto contacto = new Contacto(nombre, numTelefono);
        añadir(contacto);

    }

    //Metodo para añadir los contactos favoritos
    public void añadir (Contacto contacto){

        if(contacto!=null){
            db.execSQL("INSERT INTO favoritos VALUES ('" + contacto.getNombre() + "','" +
                    contacto.getTelefono() + "')");
            Toast.makeText(getContext(), "Se añadio el contacto a favoritos", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "No hay contactos en favoritos", Toast.LENGTH_SHORT).show();
        }
    }

}