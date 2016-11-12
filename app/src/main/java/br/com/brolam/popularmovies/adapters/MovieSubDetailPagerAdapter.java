package br.com.brolam.popularmovies.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.themoviedb.api.v3.schemes.Movie;

import java.util.ArrayList;
import java.util.List;

import br.com.brolam.popularmovies.fragments.base.MovieFragmentBase;

/**
 * Adaptador das páginas do subdetalhe de um filme.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieSubDetailPagerAdapter extends FragmentStatePagerAdapter {
    private final List<MovieFragmentBase> movieFragmentBases = new ArrayList<>();
    private final List<String> titles = new ArrayList<>();

    public MovieSubDetailPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public MovieFragmentBase getItem(int position) {
        return movieFragmentBases.get(position);
    }

    @Override
    public int getCount() {
        return movieFragmentBases.size();
    }

    /**
     * Adiciona uma página no subdetalhe de um filme.
     * @param movieFragmentBase informar um fragmento de tela com base na classe {@see MovieFragmentBase}
     * @param title informar o título da página.
     * @param bundle informar um bundle com informações do filme, {@see MovieFragmentBase.getMovie()}
     */
    public void addFrag(MovieFragmentBase movieFragmentBase, String title, Bundle bundle) {
        movieFragmentBase.setArguments(bundle);
        movieFragmentBases.add(movieFragmentBase);
        titles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}

