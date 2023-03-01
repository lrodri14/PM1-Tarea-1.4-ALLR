package com.example.proyecto14;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyecto14.conexion.SQLiteConexion;
import com.example.proyecto14.transacciones.Perfil;
import com.example.proyecto14.transacciones.Transacciones;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgregarPerfil extends AppCompatActivity {

    Button tomarFoto, guardar, regresar;

    EditText nombre, descripcion;

    ImageView foto;

    Perfil perfil;
    Boolean actualizacionActiva;
    String ubicacion;
    static final  int captura = 1;
    static final  int acceso_camara = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_perfil);

        actualizacionActiva = false;

        tomarFoto = ( Button ) findViewById(R.id.agregarfoto);
        guardar =  ( Button ) findViewById(R.id.guardar);
        regresar = ( Button ) findViewById(R.id.regresar);

        nombre = ( EditText ) findViewById(R.id.nombre);
        descripcion = ( EditText ) findViewById(R.id.descripcion);
        foto = ( ImageView ) findViewById(R.id.foto);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if (id != -1){
            actualizacionActiva = true;
            getSupportActionBar().setTitle("Actualizar Perfil");
            extraerContacto(String.valueOf(id));
        }else{
            foto.setImageResource(R.drawable.user);
            getSupportActionBar().setTitle("Agregar Perfil");
        }

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgregarPerfil.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nombre.getText().toString().equals("")){
                    Toast.makeText(AgregarPerfil.this, "Agregue un nombre valido", Toast.LENGTH_LONG).show();
                    return;
                }

                if (descripcion.getText().toString().equals("")){
                    Toast.makeText(AgregarPerfil.this, "Agregue una descripcion valida", Toast.LENGTH_LONG).show();
                    return;
                }

                if (id == -1){
                    agregarContacto(nombre.getText().toString(),
                            descripcion.getText().toString(),
                            ubicacion.toString());
                }else{
                    actualizarContacto(id,
                            nombre.getText().toString(),
                            descripcion.getText().toString(),
                            ubicacion.toString());
                }
            }
        });

    }

    public void agregarContacto(String nombre, String direccion, String imagen){
        try{

            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
            SQLiteDatabase bd = conexion.getWritableDatabase();
            Perfil perfil = new Perfil(nombre, direccion, imagen);
            ContentValues valores = new ContentValues();
            valores.put("nombre", perfil.getNombre());
            valores.put("direccion", perfil.getDescripcion());
            valores.put("imagen", perfil.getImagen());
            bd.insert(Transacciones.tablacontactos, "id", valores);
            Toast.makeText(this, "Se ha agregado " + nombre + " a tus perfiles", Toast.LENGTH_SHORT).show();
            limpiar();

        }catch(Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void actualizarContacto(int id, String nombre, String descripcion, String imagen){
        try{

            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
            SQLiteDatabase bd = conexion.getWritableDatabase();
            Perfil perfil = new Perfil(nombre, descripcion, imagen);
            ContentValues valores = new ContentValues();
            valores.put("nombre", perfil.getNombre());
            valores.put("direccion", perfil.getDescripcion());
            valores.put("imagen", perfil.getImagen());
            String[] idActualizacion = new String[] {String.valueOf(id)};
            bd.update(Transacciones.tablacontactos, valores,"id=?", idActualizacion);
            Toast.makeText(this, "Se ha actualizado " + nombre + " de tus perfiles", Toast.LENGTH_SHORT).show();
            limpiar();

            Intent intent = new Intent(AgregarPerfil.this, VerPerfiles.class);
            startActivity(intent);

        }catch(Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void extraerContacto(String id){
        try{
            SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
            SQLiteDatabase bd = conexion.getWritableDatabase();
            String[] busqueda = {String.valueOf(id)};
            String[] campos = {"id", "nombre", "direccion", "imagen"};
            Cursor cursor =  bd.query(Transacciones.tablacontactos, campos, "id=?", busqueda, null, null, null);
            cursor.moveToFirst();
            perfil = new Perfil(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3));
            nombre.setText(perfil.getNombre());
            descripcion.setText(perfil.getDescripcion());
            ubicacion = perfil.getImagen();
            Bitmap b = BitmapFactory.decodeFile(perfil.getImagen());
            foto.setImageBitmap(b);
        }catch(Exception ex){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void permisos()
    {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},acceso_camara);
        }
        else
        {
            fotoDispatch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int codigo, @NonNull String[] permisos, @NonNull int[] resultados) {
        super.onRequestPermissionsResult(codigo, permisos, resultados);

        if(codigo == acceso_camara)
        {
            if(resultados.length > 0 && resultados[0] == PackageManager.PERMISSION_GRANTED)
            {
                fotoDispatch();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Acceso de Camara Denegado",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int codigo, int resultado, @Nullable Intent data) {
        super.onActivityResult(codigo, resultado, data);

        if(codigo == captura && resultado == RESULT_OK)
        {
            try {
                File archivoFoto = new File(ubicacion);
                foto.setImageURI(Uri.fromFile(archivoFoto));
            }
            catch (Exception ex)
            {
                ex.toString();
            }
        }
    }

    private File crearImagen() throws IOException {
        String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File archivos = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = File.createTempFile(
                fecha,
                ".jpg",
                archivos
        );

        ubicacion = imagen.getAbsolutePath();
        return imagen;
    }
    private void fotoDispatch() {
        Intent fotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (fotoIntent.resolveActivity(getPackageManager()) != null) {
            File archivo = null;
            try {
                archivo = crearImagen();
            } catch (IOException ex) {
                ex.toString();
            }
            if (archivo != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.proyecto14.fileprovider",
                        archivo);
                fotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(fotoIntent, captura);
            }
        }
    }

    public void limpiar() {
        nombre.setText(Transacciones.Empty);
        descripcion.setText(Transacciones.Empty);
        foto.setImageResource(android.R.color.transparent);
    }
}