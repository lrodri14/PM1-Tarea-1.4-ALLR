package com.example.proyecto14;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.proyecto14.conexion.SQLiteConexion;
import com.example.proyecto14.transacciones.Perfil;
import com.example.proyecto14.transacciones.Transacciones;

import java.util.ArrayList;

public class VerPerfiles extends AppCompatActivity {

    SQLiteConexion conexion;
    ArrayList<Perfil> listaPerfiles;
    ArrayList<String> arregloPerfiles;
    ArrayAdapter adp;

    Button regresar, actualizar, eliminar, verfoto;

    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_perfiles2);
        getSupportActionBar().setTitle("Lista de Perfiles");
        conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);


        regresar = ( Button ) findViewById(R.id.regresar2);
        actualizar = ( Button ) findViewById(R.id.actualizar);
        eliminar = ( Button ) findViewById(R.id.eliminar);
        verfoto = ( Button ) findViewById(R.id.verfoto);

        lista = ( ListView ) findViewById(R.id.lista);

        ObtenerListaContactos();

        adp = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arregloPerfiles);
        lista.setAdapter(adp);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerPerfiles.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int color = Color.parseColor("#abaaad");
                view.setBackgroundColor(color);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seleccionado = lista.getCheckedItemPosition();
                if (seleccionado != ListView.INVALID_POSITION){
                    int id = listaPerfiles.get(seleccionado).getId();
                    Eliminar(id);
                    ObtenerListaContactos();
                    adp = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, arregloPerfiles);
                    lista.setAdapter(adp);
                }
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seleccionado = lista.getCheckedItemPosition();
                if (seleccionado != ListView.INVALID_POSITION){
                    int id = listaPerfiles.get(seleccionado).getId();
                    Actualizar(id);
                }
            }
        });

        verfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int seleccionado = lista.getCheckedItemPosition();
                if (seleccionado != ListView.INVALID_POSITION){
                    String nombre = listaPerfiles.get(seleccionado).getNombre();
                    String foto = listaPerfiles.get(seleccionado).getImagen();
                    Intent intent = new Intent(VerPerfiles.this, VerFoto.class);
                    intent.putExtra("foto", foto);
                    intent.putExtra("nombre", nombre);
                    startActivity(intent);
                }
            }
        });
    }

    public void ObtenerListaContactos()
    {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Perfil perfil = null;
        listaPerfiles = new ArrayList<Perfil>();

        // Cursor
        Cursor cursor = db.rawQuery("SELECT * FROM perfil", null );

        while(cursor.moveToNext())
        {
            perfil = new Perfil();

            perfil.setId(cursor.getInt(0));
            perfil.setNombre(cursor.getString(1));
            perfil.setDescripcion(cursor.getString(2));
            perfil.setImagen(cursor.getString(3));
            listaPerfiles.add(perfil);
        }

        Toast.makeText(this, "Contactos en Total: " + listaPerfiles.size(), Toast.LENGTH_SHORT).show();

        cursor.close();
        Filling();
    }

    public void Filling()
    {
        arregloPerfiles = new ArrayList<String>();
        for(int i = 0; i < listaPerfiles.size(); i++)
        {
            arregloPerfiles.add(listaPerfiles.get(i).getId() + " | "+
                    listaPerfiles.get(i).getNombre());
        }
    }

    public void Eliminar(int id){
        SQLiteDatabase db = conexion.getReadableDatabase();
        String[] argumentos = { String.valueOf(id) };
        String condicion = "id = ?";
        db.delete(Transacciones.tablacontactos, condicion, argumentos);
        Toast.makeText(this, "Perfil Eliminado Correctamente", Toast.LENGTH_SHORT).show();
        db.close();
    }

    public void Actualizar(int id){
        Intent intent = new Intent(VerPerfiles.this, AgregarPerfil.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }
}