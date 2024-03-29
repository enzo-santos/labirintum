package com.example.labirintumapp

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView

class InfoArquivo(nome: String, modif: String, dir: String){
    val nomeArquivo = nome
    val dataModificado = modif
    val diretorioArquivo = dir
}

class ArquivoAdapter(arquivos: MutableList<InfoArquivo>,
    act: MenuRegistros, list: RecyclerViewClickListener
    ) : RecyclerView.Adapter<ArquivoAdapter.ArquivoViewHolder>(){

    inner class ArquivoViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val txtNomeArquivo: TextView = v.findViewById(R.id.txtNomeArquivo)
        val txtDataModificado: TextView = v.findViewById(R.id.txtDataModificado)
        private val btnMostraOpcoes: AppCompatImageButton = v.findViewById(R.id.btnMostraOpcoes)
        private val cardViewArquivo: LinearLayout = v.findViewById(R.id.cardViewLayout)

        init {
            btnMostraOpcoes.setOnClickListener {
                val popup = PopupMenu(activity, btnMostraOpcoes)
                popup.menuInflater.inflate(R.menu.popup_registros, popup.menu)

                popup.setOnMenuItemClickListener {
                    item: MenuItem ->
                        listener.onItemClicked(item.title.toString(), adapterPosition)
                        true
                }

                popup.show()
            }
            cardViewArquivo.setOnClickListener {
                listener.onCardClicked(adapterPosition)
            }
        }
    }

    private var listaArquivos = arquivos
    private val activity = act
    private val listener = list

    override fun getItemCount() = listaArquivos.size

    override fun onBindViewHolder(avh: ArquivoViewHolder, i: Int){
        val a = this.listaArquivos[i]
        val nomeArquivo = a.nomeArquivo
        val dataModificado = "Modificado em %s".format(a.dataModificado)

        avh.txtNomeArquivo.text = nomeArquivo
        avh.txtDataModificado.text = dataModificado
    }

    override fun onCreateViewHolder(vg: ViewGroup, i: Int): ArquivoViewHolder {
        val itemView = LayoutInflater.from(vg.context).inflate(
            R.layout.campo_registros_anteriores, vg, false)

        return ArquivoViewHolder(itemView)
    }

    fun atualizaLista(novaLista: MutableList<InfoArquivo>){
        this.listaArquivos = novaLista
        notifyDataSetChanged()
    }
}