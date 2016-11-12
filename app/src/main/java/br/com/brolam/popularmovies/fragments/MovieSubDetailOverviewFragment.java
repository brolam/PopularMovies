package br.com.brolam.popularmovies.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.brolam.popularmovies.R;
import br.com.brolam.popularmovies.fragments.base.MovieFragmentBase;


/**
 * Página do subdetalhe para exibi a Visão geral de um filme
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieSubDetailOverviewFragment extends MovieFragmentBase {

    public MovieSubDetailOverviewFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        if (getMovie() != null) {
            rootView = inflater.inflate(R.layout.movie_sub_detail_overview, container, false);
            ((TextView) rootView.findViewById(R.id.textViewOverview)).setText(getMovie().getOverview());
        } else {
            rootView = inflater.inflate(R.layout.movie_detail_error_message, container, false);
            ((TextView) rootView.findViewById(R.id.textViewErrorMessage)).setText(getText(R.string.error_message_unable_detail_movie));
        }
        return rootView;
    }

}
