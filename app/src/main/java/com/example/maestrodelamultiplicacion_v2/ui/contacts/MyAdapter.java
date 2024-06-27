package com.example.maestrodelamultiplicacion_v2.ui.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maestrodelamultiplicacion_v2.R;

import java.util.ArrayList;

//(*)ESTA CLASE HEREDA DE RecyclerView.Adapter
//ESTO NOS SACARA VARIOS ERRORES
// - EN PRIMER LUGAR NOS HARA IMPLEMENTAR LA CLASE ViewHolderDatos
// - LUEGO NOS HARA IMPLEMENTAR LOS METODOS DE LA CLASE PADRE
// - LUEGO NOS HARA HACER QUE INDIQUEMOS QUE LA CLASE ViewHolderDatos
//EXTIENDE DE RecyclerView.ViewHolder
// - POR ULTIMO NOS HARA IMPLEMENTAR EL CONSTRUCTOR DE ViewHolderDatos
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolderDatos> {

    //ESTE ADAPTADOR RECIBIRA UNA LISTA DE DATOS, DE MANERA QUE CREAMOS UN ARRAYLIST
    ArrayList<Contacto> listaContactos;

    //CREAMOS UN LAYOUTINFLATER QUE INFLARA LA VISTA QUE TENDRA QUE MOSTRAR (la definida en list_pokemon.xml):
    private LayoutInflater inflater;

    //CREAMOS UN CONTEXT PARA INDICAR DE QUE CLASE ESTAMOS LLAMANDO ESTE ADAPTADOR
    private Context context;

    //CREAMOS UNA INSTANCIA DE ContactsFragment
    ContactsFragment contactsFragment;

    //CREAMOS LO NECESARIO PARA SABER QUE POKEMON SE HA ELEGIDO
    RelativeLayout relativeLayout;
    TextView contactoElegido;
    TextView numTelefonoElegido;
    ImageView fav;
    String nombreContacto;
    String numTelefono;


    //CREAREMOS EL CONSTRUCTOR
    public MyAdapter(ArrayList<Contacto> listaContactos, Context context,ContactsFragment contactsFragment) {

        this.listaContactos = listaContactos;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.contactsFragment = contactsFragment;
    }

    @NonNull
    @Override
    //ESTE METODO ENLAZA EL ADAPTADOR CON EL FICHERO itemsrecycler.xml
    public ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //DE MANERA QUE AQUI GENERAREMOS UN VIEW INFLADO CON ESE LAYOUT:
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_contactos, null, false);

        return new ViewHolderDatos(view);
    }

    //ESTE METODO SE ENCARGARA DE ESTABLECER LA COMUNICACION ENTRE NUESTRO ADAPTADOR Y
    //LA CLASE ViewHolderDatos
    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        //UTILIZAREMOS EL holder Y CREAREMOS UN METODO LLAMADO asignarDatos QUE
        //RECIBIRA COMO PARAMETRO LA INFORMACION QUE QUEREMOS QUE MUESTRE:
        //EL METODO asignarDatos debera estar creado en la clase ViewHolderDatos
        //DE MANERA QUE PULSAREMOS SOBRE EL PARA QUE LO GENERE EN ESTA CLASE:
        holder.asignarDatos(listaContactos.get(position));

        //ESTE METODO SE LLAMARA CUANDO SE PULSE SOBRE LOS RELATIVE LAYOUT (es decir, sobre los pokemon listados)
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LO QUE HARA SERA DEVOLVER EL CONTACTO ELEGIDO AL FRAGMENTO CONTACTSFRAGMENT:
                //PARA ELLO SE CREARA UN INTENT:
                relativeLayout = v.findViewById(R.id.relativePulsable);
                contactoElegido = relativeLayout.findViewById(R.id.textNombre);
                nombreContacto = contactoElegido.getText().toString();
                numTelefonoElegido = relativeLayout.findViewById(R.id.textTelefono);
                numTelefono = numTelefonoElegido.getText().toString();
                fav = relativeLayout.findViewById(R.id.imageCard);
                fav.setImageResource(R.drawable.star);
                contactsFragment.finalizar(nombreContacto,numTelefono);

            }
        });

    }

    //ESTE METODO RETORNARA EL TAMAÑO DE LA LISTA
    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {

        //AQUI REFERENCIAMOS LOS ELEMENTOS QUE TENDRA EL RECYCLERVIEW
        TextView textoNombre;
        TextView textoTelefono;
        View view;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.relativePulsable);
            //PARA REFERENCIARLO USAMOS EL itemView:
            textoNombre = itemView.findViewById(R.id.textNombre);
            textoTelefono = itemView.findViewById(R.id.textTelefono);
        }

        public void asignarDatos(Contacto datosContacto) {
            //UNA VEZ AQUI LE INDICAREMOS QUE ESTABLEZCA EN EL TEXTO EL STRING QUE RECIBE
            textoNombre.setText(datosContacto.getNombre());
            textoTelefono.setText(datosContacto.getTelefono());
        }
    }

//--> UNA VEZ CREADO EL ADAPTADOR PASAREMOS AL PASO 13 EN MAINACTIVITY2
}