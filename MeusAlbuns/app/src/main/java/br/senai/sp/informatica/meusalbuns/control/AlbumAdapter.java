package br.senai.sp.informatica.meusalbuns.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.senai.sp.informatica.meusalbuns.R;
import br.senai.sp.informatica.meusalbuns.lib.Util;
import br.senai.sp.informatica.meusalbuns.model.Album;
import br.senai.sp.informatica.meusalbuns.model.AlbumDao;

/**
 * Created by 34023325821 on 16/11/2017.
 */

public class AlbumAdapter extends BaseAdapter implements View.OnClickListener{
    private AlbumDao dao = AlbumDao.manager;
    private Map<Integer, Long> mapa;

    private boolean alteraLayout = false;
    private boolean editar = false;

    private static DateFormat fmt = DateFormat.getDateInstance(DateFormat.LONG);

    private List<Long> listaSelecionados = new ArrayList<>();

    private int posicao = -1;

    public void criaMapa(){
        mapa = new HashMap<>();
        List<Album> listaAlbuns =dao.getLista();
        for (int i = 0; i < listaAlbuns.size(); i++) {
            if (listaAlbuns.get(i).isAtivo()){
                mapa.put(i,listaAlbuns.get(i).getId());
            }
        }
    }
    public AlbumAdapter(){
        criaMapa();
    }

    @Override
    public int getCount() {
        return mapa.size();
    }

    @Override
    public Object getItem(int position) {
        return dao.getAlbum(mapa.get(position));
    }

    @Override
    public long getItemId(int position) {
        return mapa.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConstraintLayout layout;
        if (convertView == null || alteraLayout == true){
            Context ctx = parent.getContext();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = new ConstraintLayout(ctx);
            if (!editar){
                inflater.inflate(R.layout.layout_lista_detalhe, layout);
            } else {
                inflater.inflate(R.layout.layout_lista_detalhe_edit, layout);
            }
        } else {
            layout = (ConstraintLayout)convertView;
        }

        TextView tvArtista = layout.findViewById(R.id.etArtista);
        TextView tvNome = layout.findViewById(R.id.tvNome);
        TextView tvGenero = layout.findViewById(R.id.tvGenero);
        TextView tvDtLancamento = layout.findViewById(R.id.tvDtLancamento);
        ImageView ivCapa = layout.findViewById(R.id.ivCapa);

        Album album = dao.getAlbum(mapa.get(position));

        tvArtista.setText(album.getArtista());
        tvNome.setText(album.getAlbum());
        tvGenero.setText(album.getGenero());
        Date data = album.getDtLancamento();
        if(data != null) {
            tvDtLancamento.setText(fmt.format(data));
        }


        try {
            ivCapa.setImageBitmap(Util.bitmapFromBase64(album.getCapa()));
        } catch (Exception e) {
            String nomeAlbum = tvNome.getText().toString();
            if (!nomeAlbum.isEmpty()) {
                Bitmap bitmap = Util.circularBitmapAndText(
                        ContextCompat.getColor(layout.getContext(), R.color.colorPrimary)
                        , 200
                        , 600
                        , nomeAlbum.substring(0, 1).toUpperCase());
                ivCapa.setImageBitmap(bitmap);
            }
        }
        //TODO: Ajustar apresentação de capa

        CheckBox cbApagar = (CheckBox)layout.findViewById(R.id.cbApagar);

        if (listaSelecionados.contains((long)position)){
            cbApagar.setChecked(true);
            //Toast.makeText(convertView.getContext(), ">> "+listaSelecionados, Toast.LENGTH_LONG).show();
            posicao = -1;

        }

        if (editar) {
            cbApagar.setTag(album.getId());
            cbApagar.setOnClickListener(this);

        }
        return layout;
    }

    @Override
    public void notifyDataSetChanged() {
        criaMapa();
        super.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        Long id = (Long)v.getTag();
        if (!listaSelecionados.contains(id)) {
            listaSelecionados.add(id);
        }
        //Toast.makeText(v.getContext(), ">>2"+listaSelecionados, Toast.LENGTH_LONG).show();
        //Album album =  dao.getAlbum(id);
        //album.setAtivo(!album.isAtivo());
    }

    enum TipoLista {
        EDITAR,
        APAGAR;
    }

    public void setLayout(TipoLista tipo){
        if (tipo == TipoLista.EDITAR){
            editar = true;
        } else {
            editar = false;
        }
        alteraLayout = true;
        listaSelecionados = new ArrayList<>();
        //dao.retiraSelecionados(listaSelecionados);
        notifyDataSetChanged();
    }


    public List<Long> getListaSelecionados(){
        return listaSelecionados;
    }

    public void setItemSelecionado(int pos){
        listaSelecionados.add(mapa.get(pos));
        posicao = pos;
        notifyDataSetChanged();
    }
    public void limpaListaSelecionada(){
        listaSelecionados = new ArrayList<>();
    }

}
