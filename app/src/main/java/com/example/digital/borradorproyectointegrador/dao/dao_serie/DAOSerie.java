package com.example.digital.borradorproyectointegrador.dao.dao_serie;

import android.util.Log;

import com.example.digital.borradorproyectointegrador.dao.DaoHelper;
import com.example.digital.borradorproyectointegrador.model.serie.Serie;
import com.example.digital.borradorproyectointegrador.model.serie.SerieConteiner;
import com.example.digital.borradorproyectointegrador.util.ResultListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DAOSerie extends DaoHelper {

    private ServiceSerie serviceSerie;

    public DAOSerie() {
        super("https://api.themoviedb.org/3/discover/");
        serviceSerie = retrofit.create(ServiceSerie.class);
    }

    public void buscarSeries(final ResultListener<List<Serie>> listResultListener){

        Call<SerieConteiner> call = serviceSerie.getSerieConteiner();

        call.enqueue(new Callback<SerieConteiner>() {
            @Override
            public void onResponse(Call<SerieConteiner> call, Response<SerieConteiner> response) {
                SerieConteiner serieConteiner = response.body();
                List<Serie> serieList = serieConteiner.getResults();
                listResultListener.finish(serieList);

            }

            @Override
            public void onFailure(Call<SerieConteiner> call, Throwable t) {
                Log.e("MIERRROR----------", t.toString());
            }
        });

    }

}