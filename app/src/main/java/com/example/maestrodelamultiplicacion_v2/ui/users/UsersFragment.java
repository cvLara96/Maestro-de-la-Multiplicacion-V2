package com.example.maestrodelamultiplicacion_v2.ui.users;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.maestrodelamultiplicacion_v2.DatabaseHelper;
import com.example.maestrodelamultiplicacion_v2.MainActivity;
import com.example.maestrodelamultiplicacion_v2.R;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentStadisticsBinding;
import com.example.maestrodelamultiplicacion_v2.databinding.FragmentUsersBinding;
import com.example.maestrodelamultiplicacion_v2.ui.stadistics.StadisticsViewModel;
import com.google.android.material.navigation.NavigationView;

public class UsersFragment extends Fragment {

    ImageButton imageadmin;
    ImageButton imageuser;
    ImageButton botonHabilitar;
    ImageButton botonDeshabilitar;
    boolean usuarioHabilitado;
    SQLiteDatabase db;
    DatabaseHelper dbHelper;

    private FragmentUsersBinding binding;
    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UsersViewModel usersViewModel =
                new ViewModelProvider(this).get(UsersViewModel.class);

        View root = inflater.inflate(R.layout.fragment_users, container, false);

        //Creamos la tabla de usuarios, automaticamente se generara el usuario administrador
        dbHelper = new DatabaseHelper(getActivity(),"MaestroMultiplicacion",null,1);
        db = dbHelper.getWritableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS usuarios(tipo VARCHAR);");
        /*FIN SQLite para la tabla de usuarios*/

        // Verificamos si el usuario administrador ya existe en la base de datos
        // Si no existe, lo insertamos
        if (!isAdminUserExists()) {
            insertAdminUser();
        }else{
            System.out.println("Usuario admin generado");
        }

        // Obtener la referencia al NavigationView usando root
         NavigationView navigationView = root.findViewById(R.id.mobile_navigation);

        imageuser = root.findViewById(R.id.imageButtonPlayer);
        imageadmin = root.findViewById(R.id.imageButtonAdmin);
        botonHabilitar = root.findViewById(R.id.imageButtonAdd);
        botonDeshabilitar = root.findViewById(R.id.imageButtonDelete);

        imageadmin.setOnClickListener(this::pulsadoAdmin);
        imageuser.setOnClickListener(this::pulsadoUser);

        if(!isUserExists()){
            imageuser.setVisibility(View.INVISIBLE);
        }else{
            imageuser.setVisibility(View.VISIBLE);
        }


        botonHabilitar.setOnClickListener(this::habilitarUser);
        botonDeshabilitar.setOnClickListener(this::deshabilitarUser);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        db.close();
        dbHelper.close();
    }

    private void mostrarDialogoLoginAdmin() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.login_admin, null);

        final EditText etNombreUsuario = dialogView.findViewById(R.id.editTextNombreUsuario);
        final EditText etContraseña = dialogView.findViewById(R.id.editTextContraseña);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle(getBoldText("LOGIN"))
                .setPositiveButton(getBoldText("INICIAR SESION"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Validar el nombre de usuario y la contraseña
                        String usuarioIngresado = etNombreUsuario.getText().toString();
                        String contraseñaIngresada = etContraseña.getText().toString();

                        if (verificarCredenciales(usuarioIngresado, contraseñaIngresada)) {
                            // Las credenciales son correctas, aquí puedes realizar acciones adicionales
                            // como navegar a otros fragmentos del Navigation Drawer.
                            Toast.makeText(requireContext(), "Acceso concedido", Toast.LENGTH_SHORT).show();
                            // Aquí puedes permitir el acceso a todas las opciones del menú
                            adminOptions();
                            openNav();//Abre el navigation drawer

                        } else {
                            // Credenciales incorrectas, muestra un mensaje de error o realiza otras acciones.
                            Toast.makeText(requireContext(), "Acceso denegado", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .setNegativeButton(getBoldText("CANCELAR"), null)
                .show();
    }

    private void mostrarDialogoLoginAdminHabilitar() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.login_admin, null);

        final EditText etNombreUsuario = dialogView.findViewById(R.id.editTextNombreUsuario);
        final EditText etContraseña = dialogView.findViewById(R.id.editTextContraseña);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle(getBoldText("PERMISO DE ADMINISTRADOR PARA HABILITAR"))
                .setPositiveButton(getBoldText("HABILITAR"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Validar el nombre de usuario y la contraseña
                        String usuarioIngresado = etNombreUsuario.getText().toString();
                        String contraseñaIngresada = etContraseña.getText().toString();

                        if (verificarCredenciales(usuarioIngresado, contraseñaIngresada)) {

                            if(!isUserExists()){
                                insertUser();//Inserta el usuario en la base de datos
                                // Las credenciales son correctas, aquí puedes realizar acciones adicionales
                                // como navegar a otros fragmentos del Navigation Drawer.
                                Toast.makeText(requireContext(), "Usuario habilitado", Toast.LENGTH_SHORT).show();
                                // Aquí puedes permitir el acceso a todas las opciones del menú
                                usuarioHabilitado = true;
                                imageuser.setVisibility(View.VISIBLE);
                            }else{
                                Toast.makeText(requireContext(), "Ya hay un usuario habilitado", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            // Credenciales incorrectas, muestra un mensaje de error o realiza otras acciones.
                            Toast.makeText(requireContext(), "Acceso denegado", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .setNegativeButton(getBoldText("CANCELAR"), null)
                .show();
    }

    private void mostrarDialogoLoginAdminDeshabilitar() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.login_admin, null);

        final EditText etNombreUsuario = dialogView.findViewById(R.id.editTextNombreUsuario);
        final EditText etContraseña = dialogView.findViewById(R.id.editTextContraseña);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView)
                .setTitle(getBoldText("PERMISO DE ADMINISTRADOR PARA DESHABILITAR"))
                .setPositiveButton(getBoldText("DESHABILITAR"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Validar el nombre de usuario y la contraseña
                        String usuarioIngresado = etNombreUsuario.getText().toString();
                        String contraseñaIngresada = etContraseña.getText().toString();

                        if (verificarCredenciales(usuarioIngresado, contraseñaIngresada)) {
                            // Las credenciales son correctas, aquí puedes realizar acciones adicionales
                            // como navegar a otros fragmentos del Navigation Drawer.
                            Toast.makeText(requireContext(), "Usuario deshabilitado", Toast.LENGTH_SHORT).show();
                            // Aquí puedes permitir el acceso a todas las opciones del menú
                            usuarioHabilitado = false;
                            imageuser.setVisibility(View.INVISIBLE);
                            deleteUser();
                            deshabilitarAllOptions();

                        } else {
                            // Credenciales incorrectas, muestra un mensaje de error o realiza otras acciones.
                            Toast.makeText(requireContext(), "Acceso denegado", Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .setNegativeButton(getBoldText("CANCELAR"), null)
                .show();
    }

    public void habilitarUser(View v){

        mostrarDialogoLoginAdminHabilitar();

    }

    public void deshabilitarUser(View v){

        mostrarDialogoLoginAdminDeshabilitar();

    }


    //Metodo para el boton usuario admin
    public void pulsadoAdmin(View v){

        mostrarDialogoLoginAdmin();

    }

    //Habilitar opciones de administrador
    public void adminOptions(){

        // Obtener la referencia al NavigationView
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);

        // Obtener los IDs de los elementos que deseas deshabilitar
        int idItemContacts = R.id.nav_contacts;
        int idItemGallery = R.id.nav_gallery;
        int idItemHome = R.id.nav_home;
        int idItemSlideshow = R.id.nav_slideshow;
        int idItemStadistics = R.id.nav_stadistics;

        // Deshabilitar el acceso a los elementos del menú
        MenuItem itemContacts = navigationView.getMenu().findItem(idItemContacts);
        MenuItem itemGallery = navigationView.getMenu().findItem(idItemGallery);
        MenuItem itemHome = navigationView.getMenu().findItem(idItemHome);
        MenuItem itemSlideshow = navigationView.getMenu().findItem(idItemSlideshow);
        MenuItem itemStadistics = navigationView.getMenu().findItem(idItemStadistics);

        if (itemContacts != null) {
            itemContacts.setVisible(true);
        }

        if (itemGallery != null) {
            itemGallery.setVisible(false);
        }

        if (itemHome != null) {
            itemHome.setVisible(false);
        }

        if (itemSlideshow != null) {
            itemSlideshow.setVisible(true);
        }

        if (itemStadistics != null) {
            itemStadistics.setVisible(true);
        }

    }

    //Metodo para el boton usuario normal
    public void pulsadoUser(View v){

            openNav();

            // Obtener la referencia al NavigationView
            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);

            // Obtener los IDs de los elementos que deseas deshabilitar
            int idItemContacts = R.id.nav_contacts;
            int idItemGallery = R.id.nav_gallery;
            int idItemHome = R.id.nav_home;
            int idItemSlideshow = R.id.nav_slideshow;
            int idItemStadistics = R.id.nav_stadistics;

            // Deshabilitar el acceso a los elementos del menú
            MenuItem itemContacts = navigationView.getMenu().findItem(idItemContacts);
            MenuItem itemGallery = navigationView.getMenu().findItem(idItemGallery);
            MenuItem itemHome = navigationView.getMenu().findItem(idItemHome);
            MenuItem itemSlideshow = navigationView.getMenu().findItem(idItemSlideshow);
            MenuItem itemStadistics = navigationView.getMenu().findItem(idItemStadistics);

            if (itemContacts != null) {
                itemContacts.setVisible(false);
            }

            if (itemGallery != null) {
                itemGallery.setVisible(true);
            }

            if (itemHome != null) {
                itemHome.setVisible(true);
            }

            if (itemSlideshow != null) {
                itemSlideshow.setVisible(false);
            }

            if (itemStadistics != null) {
                itemStadistics.setVisible(false);
            }


    }

    @Override
    public void onResume() {
        super.onResume();
        // Obtener la referencia al NavigationView
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);

        // Obtener los IDs de los elementos que deseas deshabilitar
        int idItemContacts = R.id.nav_contacts;
        int idItemGallery = R.id.nav_gallery;
        int idItemHome = R.id.nav_home;
        int idItemSlideshow = R.id.nav_slideshow;
        int idItemStadistics = R.id.nav_stadistics;

        // Deshabilitar el acceso a los elementos del menú
        MenuItem itemContacts = navigationView.getMenu().findItem(idItemContacts);
        MenuItem itemGallery = navigationView.getMenu().findItem(idItemGallery);
        MenuItem itemHome = navigationView.getMenu().findItem(idItemHome);
        MenuItem itemSlideshow = navigationView.getMenu().findItem(idItemSlideshow);
        MenuItem itemStadistics = navigationView.getMenu().findItem(idItemStadistics);

        if (itemContacts != null) {
            itemContacts.setVisible(false);
        }

        if (itemGallery != null) {
            itemGallery.setVisible(false);
        }

        if (itemHome != null) {
            itemHome.setVisible(false);
        }

        if (itemSlideshow != null) {
            itemSlideshow.setVisible(false);
        }

        if (itemStadistics != null) {
            itemStadistics.setVisible(false);
        }

    }

    public void deshabilitarAllOptions(){

        // Obtener la referencia al NavigationView
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);

        // Obtener los IDs de los elementos que deseas deshabilitar
        int idItemContacts = R.id.nav_contacts;
        int idItemGallery = R.id.nav_gallery;
        int idItemHome = R.id.nav_home;
        int idItemSlideshow = R.id.nav_slideshow;
        int idItemStadistics = R.id.nav_stadistics;

        // Deshabilitar el acceso a los elementos del menú
        MenuItem itemContacts = navigationView.getMenu().findItem(idItemContacts);
        MenuItem itemGallery = navigationView.getMenu().findItem(idItemGallery);
        MenuItem itemHome = navigationView.getMenu().findItem(idItemHome);
        MenuItem itemSlideshow = navigationView.getMenu().findItem(idItemSlideshow);
        MenuItem itemStadistics = navigationView.getMenu().findItem(idItemStadistics);

        if (itemContacts != null) {
            itemContacts.setVisible(false);
        }

        if (itemGallery != null) {
            itemGallery.setVisible(false);
        }

        if (itemHome != null) {
            itemHome.setVisible(false);
        }

        if (itemSlideshow != null) {
            itemSlideshow.setVisible(false);
        }

        if (itemStadistics != null) {
            itemStadistics.setVisible(false);
        }

    }

    private boolean verificarCredenciales(String nombreUsuario, String contraseña) {
        // Cambiado: Usa directamente los valores esperados
        return nombreUsuario.equals("Admin") && contraseña.equals("1234");
    }

    //Texto en negrita
    private SpannableString getBoldText(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    // Método para verificar si el usuario administrador ya existe en la base de datos
    private boolean isAdminUserExists() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE tipo = 'admin'", null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        return false;
    }

    // Método para verificar si el usuario user ya existe en la base de datos
    private boolean isUserExists() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios WHERE tipo = 'user'", null);
        if (cursor != null && cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        return false;
    }

    // Método para insertar el usuario administrador en la base de datos
    private void insertAdminUser() {
        db.execSQL("INSERT INTO usuarios VALUES ('admin')");
    }

    // Método para insertar el usuario normal en la base de datos
    private void insertUser() {
        db.execSQL("INSERT INTO usuarios VALUES ('user')");
    }

    // Método para eliminar el usuario normal en la base de datos
    private void deleteUser() {
        db.execSQL("DELETE FROM usuarios WHERE tipo = 'user'");
    }

    //Metodo que se llamara cuando se pulse sobre los botones admin o user para que automaticamente abra el navigation drawer
    public void openNav(){
        // Obtener la referencia al DrawerLayout que contiene el Navigation Drawer
        DrawerLayout drawerLayout = requireActivity().findViewById(R.id.drawer_layout);
        // Abrir el Navigation Drawer
        drawerLayout.openDrawer(GravityCompat.START);

    }


}
