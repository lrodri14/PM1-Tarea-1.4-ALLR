package com.example.proyecto14.transacciones;

public class Transacciones
{
    // Nombre de la base de datos
    public static final String NameDatabase = "perfiles";
    // Tablas de la base de datos
    public static final String tablacontactos = "perfil";

    /* Transacciones de la base de datos PM1E11248 */
    public static final String CreateTBPerfiles =
            "CREATE TABLE perfil (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, direccion TEXT, imagen TEXT)";

    public static final String DropTablePerfiles = "DROP TABLE IF EXISTS perfil";

    // Helpers
    public static final String Empty = "";
}