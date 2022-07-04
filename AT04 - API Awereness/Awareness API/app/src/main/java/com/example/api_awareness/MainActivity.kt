package com.example.poc_awareness_api

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.fence.*
import com.google.android.gms.awareness.state.HeadphoneState
import com.google.android.gms.location.DetectedActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)


        // ---------------- VERIFICA SE HÁ UMA AÇÃO DE SAÍDA DO LOCAL ---------------- //

        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val local = LocationFence.exiting( 37.421686,-122.084277, 10.0)
            val myIntent = Intent(Intent.ACTION_SEND)
            myIntent.type = "text/plain"
            myIntent.putExtra(Intent.EXTRA_TEXT, "Estou saíndo!")

            val pendingIntent = PendingIntent.getActivity(this, 1, myIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val fenceRequest = FenceUpdateRequest.Builder()
                .addFence("a", local, pendingIntent)
                .build()

            Awareness.getFenceClient(this).updateFences(fenceRequest)
                .addOnSuccessListener {
                    Log.v("ATIVIDADE", "Cadastrado com sucesso")
                }
                .addOnFailureListener {
                    response -> response?.apply {
                        Log.v("ATIVIDADE",response.toString())
                    }
                }
        } else {
            solicitaLocalizacao()
        }


        // ---------------- FENCE PARA USUÁRIO CORRENDO COM FONE ---------------- //

        val fenceHeadphonesIn = HeadphoneFence.during(HeadphoneState.PLUGGED_IN)
        val fenceUserRunning = DetectedActivityFence.during(DetectedActivity.RUNNING)

        val userCorrendoOuComFone = AwarenessFence.and(fenceHeadphonesIn, fenceUserRunning)


        val correndoComFoneIntent = Intent(Intent.ACTION_SEND)
        correndoComFoneIntent.type = "text/plain"
        correndoComFoneIntent.putExtra(Intent.EXTRA_TEXT, "Usuário está correndo com fones de ouvido")

        val pendingIntent = PendingIntent.getActivity(this, 1, correndoComFoneIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val fenceRequest = FenceUpdateRequest.Builder()
            .addFence("B", userCorrendoOuComFone, pendingIntent)
            .build()


        Awareness.getFenceClient(this).updateFences(fenceRequest)
            .addOnSuccessListener {
                Log.v("ATIVIDADE", "Cadastrado com sucesso")
            }
            .addOnFailureListener {
                Log.v("ATIVIDADE", "Atividade Falhou")
            }

        // ---------------- FENCE PARA USUÁRIO PARADO SEM FONE ---------------- //

        val fenceUserStill = DetectedActivityFence.during(DetectedActivity.STILL)
        val fenceHeadphonesOut = HeadphoneFence.during(HeadphoneState.UNPLUGGED)

        val userParadoSemFone= AwarenessFence.and(fenceHeadphonesOut, fenceUserStill)

        val paradoOuSemFoneIntent = Intent(Intent.ACTION_SEND)
        paradoOuSemFoneIntent.type = "text/plain"
        paradoOuSemFoneIntent.putExtra(Intent.EXTRA_TEXT, "Usuário está parado e sem Fones de ouvido" )

        val pendingIntent2 = PendingIntent.getActivity(this, 1, paradoOuSemFoneIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val fenceRequest2 = FenceUpdateRequest.Builder()
            .addFence("C", userParadoSemFone, pendingIntent2)
            .build()


        Awareness.getFenceClient(this).updateFences(fenceRequest2)
            .addOnSuccessListener {
                Log.v("ATIVIDADE", "Cadastrado com sucesso")
            }
            .addOnFailureListener {
                Log.v("ATIVIDADE", "Atividade Falhou")
            }
    }


    // ---------------- SNAPSHOT PARA A COMPARTILHAR A LOCALIZAÇÃO DO USUÁRIO ---------------- //

    fun localizacaoAtual(v: View) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            Awareness.getSnapshotClient(this@MainActivity).getLocation().addOnSuccessListener { response -> response?.apply {

                val result = response.location

                result?.apply {

                    Awareness.getSnapshotClient(this@MainActivity).getHeadphoneState()
                        .addOnSuccessListener { response -> response?.apply {

                            var comFone = response.headphoneState.state == HeadphoneState.PLUGGED_IN
                            var text = "Não possui fones de ouvid conectado!\n"
                            if (comFone) text = "Os fones de ouvido está conectado!\n"

                            val myIntent = Intent(Intent.ACTION_SEND)
                            myIntent.type = "text/plain"
                            myIntent.putExtra(Intent.EXTRA_TEXT, text+"Latitude: "+result.latitude.toString() + "\nLongitude: " + result.longitude.toString())

                            startActivity(Intent.createChooser(myIntent, "Compartilhar Localização"))

                        } }
                }
            }}

        } else {
            solicitaLocalizacao()
        }
    }


    // ---------------- SOLICITA AS PERMISSÕES NECESSÁRIAS ---------------- //

    fun solicitaLocalizacao() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permissão Concedida", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Permissão Negada", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}