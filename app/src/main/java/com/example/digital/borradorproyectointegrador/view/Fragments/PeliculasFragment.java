package com.example.digital.borradorproyectointegrador.view.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.digital.borradorproyectointegrador.controller.ControllerPelicula;
import com.example.digital.borradorproyectointegrador.model.pelicula.Peliculas;
import com.example.digital.borradorproyectointegrador.util.ResultListener;
import com.example.digital.borradorproyectointegrador.view.Adaptadores.AdaptadorRecyclerPeliculaSerie;
import com.example.digital.borradorproyectointegrador.model.PeliculaSerie;
import com.example.digital.borradorproyectointegrador.R;
import com.example.digital.borradorproyectointegrador.view.Adaptadores.PeliculaAdaptador;
import com.example.digital.borradorproyectointegrador.view.MainActivity;
import com.example.digital.borradorproyectointegrador.view.TrailerActivity;

import java.util.ArrayList;
import java.util.List;


public class PeliculasFragment extends Fragment implements AdaptadorRecyclerPeliculaSerie.AdapterInterface,PeliculaAdaptador.AdapterPeliInterface {

    private OnFragmentInteractionListener mListener;

    public PeliculasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_peliculas, container, false);

        //Recycler view
        //Lista
        final RecyclerView recyclerViewPrimero = view.findViewById(R.id.recylcerViewPrimero);
        final RecyclerView recyclerViewSegundo = view.findViewById(R.id.recylcerViewSegundo);
        final RecyclerView recyclerViewTercero = view.findViewById(R.id.recylcerViewTercero);
        final RecyclerView recyclerViewCuarto = view.findViewById(R.id.recylcerViewCuarto);
        final RecyclerView recyclerViewQuinto = view.findViewById(R.id.recylcerViewQuinto);

        ControllerPelicula controllerPelicula = new ControllerPelicula();
        controllerPelicula.entregarPeliculas(view.getContext(), new ResultListener<List<Peliculas>>() {
            @Override
            public void finish(List<Peliculas> Resultado) {
                cargarRecycler(view.getContext(),recyclerViewPrimero,Resultado, PeliculasFragment.this);
                cargarRecycler(view.getContext(),recyclerViewSegundo,Resultado,PeliculasFragment.this);
                cargarRecycler(view.getContext(),recyclerViewTercero,Resultado,PeliculasFragment.this);
                cargarRecycler(view.getContext(),recyclerViewCuarto,Resultado,PeliculasFragment.this);
                cargarRecycler(view.getContext(),recyclerViewQuinto,Resultado,PeliculasFragment.this);
            }
        });



        return view;


    }

    public void cargarRecycler(Context context, RecyclerView recyclerView,List<Peliculas> peliculas, PeliculaAdaptador.AdapterPeliInterface escuchador){
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(llm);

        PeliculaAdaptador peliculaAdaptador = new PeliculaAdaptador(context,peliculas,escuchador);
        recyclerView.setAdapter(peliculaAdaptador);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void irTrailer(PeliculaSerie peliculaSerie) {
        Intent intent = new Intent(getActivity(), TrailerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(TrailerActivity.VIDEO_ID, peliculaSerie.getVideoId());
        bundle.putString(TrailerActivity.KEY_NOMBRE, peliculaSerie.getNombre());
        bundle.putString(TrailerActivity.KEY_IMAGE, peliculaSerie.getImagen().toString());
        bundle.putInt(TrailerActivity.KEY_CANT_ESTRELLAS, peliculaSerie.getCantEstrellas());
        bundle.putInt(TrailerActivity.KEY_RESUMEN, peliculaSerie.getResumen());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void irTrailer(Peliculas peliculas) {
        Intent intent = new Intent(getActivity(), TrailerActivity.class);
        Bundle bundle = new Bundle();
        //bundle.putString(TrailerActivity.VIDEO_ID, peliculas.getVideoId());
        bundle.putString(TrailerActivity.KEY_NOMBRE, peliculas.getTitle());
        //bundle.putString(TrailerActivity.KEY_IMAGE, peliculas.getPoster_path());
        //bundle.putInt(TrailerActivity.KEY_CANT_ESTRELLAS, peliculas.getVote_average());
        //bundle.putInt(TrailerActivity.KEY_RESUMEN, peliculas.getOverview());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }


}
