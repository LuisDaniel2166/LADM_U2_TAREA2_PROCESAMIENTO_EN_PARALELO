package com.example.ladm_u2_tarea2_procesamiento_en_paralelo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.ladm_u2_tarea2_procesamiento_en_paralelo.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {
    lateinit var b : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        val hilo = hilo(this,b)

        b.button.setOnClickListener {
            try {
                hilo.start()
                hilo.reiniciar()
                b.button.text="Reiniciar Semaforo"
            }
            catch (e : IllegalThreadStateException){
                hilo.reiniciar()
            }

        }

        b.button2.setOnClickListener { hilo.pausar() }

        b.button3.setOnClickListener { hilo.terminar() }
    }

}

class hilo (p:MainActivity,b: ActivityMainBinding): Thread(){
    val p = p
    val b = b
    var pausado = false
    var detenido = false
    var semTimer = 4
    var semState = 0
    var yelState = true
    var stateColor = "Verde, Tiempo restante: "

    fun reiniciar(){
        semState = 0
        semTimer = 4
        stateColor="Verde, Tiempo restante: "
        detenido = false
    }

    fun pausar(){
        p.runOnUiThread{
            if(pausado) {
                b.button2.text = "Quitar Pausa del Semaforo"
            }
            else{
                b.button2.text = "Pausar Semaforo"
            }
        }
        pausado = !pausado
    }

    fun terminar(){
        detenido = true
        b.textView.setBackgroundColor(Color.BLACK)
        b.textView.text = "Semaforo"
    }

    override fun run() {
        super.run()
        while(true) {
            if (!detenido) {
                if (!pausado) {
                    p.runOnUiThread {
                        if (semState == 0) {
                            b.textView.setBackgroundColor(Color.parseColor("#0B6708"))
                        }
                        if (semState == 1) {
                            if (yelState) {
                                b.textView.setBackgroundColor(Color.parseColor("#EDD946"))
                                yelState = false
                            } else {
                                b.textView.setBackgroundColor(Color.BLACK)
                                yelState = true
                            }
                        }
                        if (semState == 2) {
                            b.textView.setBackgroundColor(Color.parseColor("#B20606"))
                        }
                        b.textView.text = stateColor + semTimer
                    }
                    semTimer--
                    sleep(1000)
                }
                if (semTimer == 0) {
                    semTimer = 4
                    if (semState == 0) {
                        semState = 1
                        stateColor = "Amarillo, Tiempo restante: "
                    } else if (semState == 1) {
                        semState = 2
                        stateColor = "Rojo, Tiempo restante: "
                    } else if (semState == 2) {
                        semState = 0
                        stateColor = "Verde, Tiempo restante: "
                    }
                }
            }
        }
    }
}