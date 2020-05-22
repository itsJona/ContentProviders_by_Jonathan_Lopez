package com.example.contentproviders

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CallLog
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    val siLlamada = 1
    val siCalendario = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnListarLlamadas.setOnClickListener {
            listarLlamadasPerdidas()
        }
        btnListarCalendarios.setOnClickListener {
            listarCalendarios()
        }

    }

    private fun listarCalendarios() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_CALENDAR),siCalendario)
        }else{

            var calendarios = ArrayList<String>()
            var projection = arrayOf(CalendarContract.Calendars.NAME)
            var cursosCalendario : Cursor ?= null

            try {
                cursosCalendario = contentResolver.query(CalendarContract.Calendars.CONTENT_URI,projection,null,null,null)

                while(cursosCalendario?.moveToNext()!!){
                    calendarios.add(cursosCalendario.getString(0))
                }

            }catch (ex:Exception){
                Toast.makeText(this,"Error: "+ex,Toast.LENGTH_LONG).show()
            }
            finally {
                listaCal.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,calendarios)
                cursosCalendario?.close()
            }

        }

    }

    private fun listarLlamadasPerdidas() {

        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_CALL_LOG),siLlamada)
        }else{
            var llamadas = ArrayList<String>()
            val selection:String = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE
            var cursor:Cursor?=null

            try{
                cursor = contentResolver.query(Uri.parse("content://call_log/calls"),null,selection,null,null)

                var registros=""

                while(cursor?.moveToNext()!!){
                    val nombre:String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
                    val numero:String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                    val tipo:String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))

                    registros="\n Nombre: "+nombre+"\nNumero: "+numero+"\nTipo: "+tipo+"\n"

                    llamadas.add(registros)

                }

            }catch (ex:Exception){
                Toast.makeText(this,"Error: "+ex,Toast.LENGTH_LONG).show()
            }finally {
                listaLlamadas.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,llamadas)
                cursor?.close()
            }
        }
    }
}
