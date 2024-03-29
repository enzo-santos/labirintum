package com.example.labirintumapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MenuOpcoes : AppCompatActivity(){
    private lateinit var spnIntGrav: Spinner
    private lateinit var rgpExtArq: RadioGroup
    private lateinit var rgpModoCalc: RadioGroup
    private lateinit var edtMaxLin: EditText
    private lateinit var swtMaxLin: Switch
    private lateinit var txtMaxLin: TextView
    private lateinit var chkGrafAcc: CheckBox
    private lateinit var chkGrafGir: CheckBox
    private lateinit var btnSalvar: TextView
    private lateinit var btnCancelar: TextView

    private var intervaloGravacao: Int
        get() = when (spnIntGrav.selectedItem.toString()){
            "Delay normal (200 milissegundos)" -> 200
            "Delay acima do normal (100 milissegundos)" -> 100
            "Delay rápido (60 milissegundos)" -> 60
            "Delay ultrarrápido (20 milissegundos)" -> 20
            else -> 0
        }

        set(valor){
            when (valor){
                200 -> spnIntGrav.setSelection(0)
                100 -> spnIntGrav.setSelection(1)
                60 -> spnIntGrav.setSelection(2)
                20 -> spnIntGrav.setSelection(3)
                else -> throw IllegalArgumentException()
            }  
        }

    private var extensaoArquivo: String
        get() = when(findViewById<RadioButton>(
        rgpExtArq.checkedRadioButtonId
        ).text.toString()){
            "Formato .csv" -> "csv"
            "Formato .txt" -> "txt"
            else -> ""
        }

        set(valor){
            when (valor){
                "csv" -> rgpExtArq.check(R.id.chkCSV)
                "txt" -> rgpExtArq.check(R.id.chkTXT)
                else -> throw IllegalArgumentException()
            }
        }

    private var isNumMaxLinhas: Boolean
        get() = swtMaxLin.isChecked

        set(valor){            
            if (valor){
                txtMaxLin.text = "Recurso ativado"
                edtMaxLin.isEnabled = true

            } else {
                txtMaxLin.text = "Recurso desativado"
                edtMaxLin.isEnabled = false
            }

            swtMaxLin.isChecked = valor
        }

    private var numMaxLinhas: Int
        get() = edtMaxLin.text.toString().toInt()

        set(valor){
            edtMaxLin.setText(valor.toString())
        }

    private var graficosVisiveis: Int
        get() = (if (chkGrafAcc.isChecked) 1 else 0) + (if (chkGrafGir.isChecked) 2 else 0)

        set(valor){
            when (valor){
                0 -> {
                    chkGrafAcc.isChecked = false
                    chkGrafGir.isChecked = false
                }

                1 -> {
                    chkGrafAcc.isChecked = true
                    chkGrafGir.isChecked = false
                }

                2 -> {
                    chkGrafAcc.isChecked = false
                    chkGrafGir.isChecked = true
                }

                3 -> {
                    chkGrafAcc.isChecked = true
                    chkGrafGir.isChecked = true
                }

                else -> throw IllegalArgumentException()
            }
        }

    private var modoCalculo: String
        get() = when(findViewById<RadioButton>(
            rgpModoCalc.checkedRadioButtonId
        ).text.toString()){
            "Valores brutos" -> "brt"
            "Variação dos valores" -> "var"
            else -> ""
        }

        set(valor){
            when (valor){
                "var" -> rgpModoCalc.check(R.id.rdbVariacao)
                "brt" -> rgpModoCalc.check(R.id.rdbBruto)
                else -> throw IllegalArgumentException()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_opcoes)

        spnIntGrav  = findViewById(R.id.delaySpinner)
        rgpExtArq   = findViewById(R.id.cgpFormatArq)
        rgpModoCalc = findViewById(R.id.rgpModoCalc)
        edtMaxLin   = findViewById(R.id.edtNumMaxLinhas)
        swtMaxLin   = findViewById(R.id.switchStopRecording)
        txtMaxLin   = findViewById(R.id.txtSwitchStopRecording)
        chkGrafAcc  = findViewById(R.id.checkboxAcc)
        chkGrafGir  = findViewById(R.id.checkboxGir)
        btnSalvar   = findViewById(R.id.btnSalvar)
        btnCancelar = findViewById(R.id.btnCancelar)

        val arrayDelays = arrayOf(
            "Delay normal (200 milissegundos)",
            "Delay acima do normal (100 milissegundos)",
            "Delay rápido (60 milissegundos)",
            "Delay ultrarrápido (20 milissegundos)")

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayDelays)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnIntGrav.adapter = aa

        val pref = applicationContext.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val prefEditor = pref.edit()

        val ig1 = pref.getInt("KEY_DELAY_GRAVACAO", 200)
        val ea1 = pref.getString("KEY_EXTENSAO_ARQUIVO", "csv") ?: "csv"
        val ml1 = pref.getInt("KEY_NUM_MAX_LINHAS", 120)
        val im1 = pref.getBoolean("KEY_IS_NUM_MAX_LINHAS", true)
        val gv1 = pref.getInt("KEY_GRAFICOS_VISIVEIS", 3)
        val mc1 = pref.getString("KEY_MODO_CALCULO", "var") ?: "var"

        intervaloGravacao = ig1
        extensaoArquivo = ea1
        numMaxLinhas = ml1
        isNumMaxLinhas = im1
        graficosVisiveis = gv1
        modoCalculo = mc1

        swtMaxLin.setOnCheckedChangeListener {
            _: CompoundButton, isChecked: Boolean ->
                isNumMaxLinhas = isChecked
        }

        btnSalvar.setOnClickListener {
            if (intervaloGravacao != ig1 || extensaoArquivo != ea1 ||
                numMaxLinhas != ml1 || isNumMaxLinhas != im1 ||
                graficosVisiveis != gv1 || modoCalculo != mc1){
                    prefEditor.putInt("KEY_NUM_MAX_LINHAS", numMaxLinhas)
                    prefEditor.putInt("KEY_DELAY_GRAVACAO", intervaloGravacao)
                    prefEditor.putString("KEY_EXTENSAO_ARQUIVO", extensaoArquivo)
                    prefEditor.putBoolean("KEY_IS_NUM_MAX_LINHAS", isNumMaxLinhas)
                    prefEditor.putInt("KEY_GRAFICOS_VISIVEIS", graficosVisiveis)
                    prefEditor.putString("KEY_MODO_CALCULO", modoCalculo)
                    prefEditor.apply()

                    Toast.makeText(this, "As alterações foram " +
                            "salvas com sucesso!", Toast.LENGTH_SHORT).show()
            }

            voltaMenuPrincipal()
        }

        btnCancelar.setOnClickListener {
            if (intervaloGravacao != ig1 || extensaoArquivo != ea1 ||
                numMaxLinhas != ml1 || isNumMaxLinhas != im1 ||
                graficosVisiveis != gv1 || modoCalculo != mc1){
                    val builderCancelar = AlertDialog.Builder(this)
                    builderCancelar.setTitle("Deseja cancelar?")
                    builderCancelar.setMessage("As alterações feitas não serão salvas.")

                    builderCancelar.setPositiveButton("Continuar"){
                        dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            voltaMenuPrincipal()
                    }

                    builderCancelar.setNegativeButton("Voltar"){
                        dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                    }

                    builderCancelar.create().show()

            } else voltaMenuPrincipal()           
        }
    }

    private fun voltaMenuPrincipal(){
        val intentMenuPrincipal = Intent(applicationContext, MenuPrincipal::class.java)
        startActivity(intentMenuPrincipal)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_configuracoes, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_mostrar_sensores -> {
                val builderSensores = AlertDialog.Builder(this)
                val appSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                val sensores = appSensorManager.getSensorList(Sensor.TYPE_ALL).map{it.name}.toTypedArray()
                
                builderSensores.setCancelable(false)
                builderSensores.setItems(sensores){_: DialogInterface, _: Int -> }
                
                builderSensores.setPositiveButton("OK"){
                    dialog: DialogInterface, _: Int ->
                        dialog.dismiss()
                }
                
                builderSensores.show()
            }
        }

        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus

            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }
}