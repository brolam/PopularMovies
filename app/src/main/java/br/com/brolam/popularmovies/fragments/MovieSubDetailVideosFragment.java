package br.com.brolam.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import org.themoviedb.api.v3.TheMovieDb;
import org.themoviedb.api.v3.schemes.MovieVideo;
import org.themoviedb.api.v3.schemes.MovieVideosPage;
import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;
import br.com.brolam.popularmovies.adapters.MovieVideosAdapter;
import br.com.brolam.popularmovies.fragments.base.MovieFragmentBase;


/**
 * Página do subdetalhe para exibi a lista de vídeos de um filme.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieSubDetailVideosFragment extends MovieFragmentBase implements SwipeRefreshLayout.OnRefreshListener, MovieVideosAdapter.IMovieVideosAdapter {

    SwipeRefreshLayout swipeRefreshLayout;
    MovieVideosAdapter movieVideosAdapter;
    AsyncTaskMovieVideos asyncTaskMovieVideos;

    public MovieSubDetailVideosFragment() {
        this.movieVideosAdapter = new MovieVideosAdapter(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        if (getMovie() != null ) {
            rootView = inflater.inflate(R.layout.movie_sub_detail_videos, container, false);
            this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            RecyclerView recyclerViewMovieVidoes = (RecyclerView)rootView.findViewById(R.id.recyclerViewMovieVidoes);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerViewMovieVidoes.setAdapter(this.movieVideosAdapter);
            recyclerViewMovieVidoes.setLayoutManager(linearLayoutManager);
            this.swipeRefreshLayout.setOnRefreshListener(this);

        } else {
            rootView = inflater.inflate(R.layout.movie_detail_error_message, container, false);
            ((TextView) rootView.findViewById(R.id.textViewErrorMessage)).setText(getText(R.string.error_message_unable_detail_movie));
        }
        update();
        return rootView;
    }


    private void showSwipeRefreshLayout(boolean show) {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(show);
        }
    }

    private void update(){
        if ( MovieHelper.checkConnection(this.getContext()) == false ) {
            showSwipeRefreshLayout(false);
            Toast.makeText(this.getContext(), getText(R.string.error_connection_to_internet), Toast.LENGTH_LONG).show();
            return;
        }

        //Somente executa a atualização online dos vídeos se o asyncTaskMovieVideos não estiver
        //em execução.
        if ( ( asyncTaskMovieVideos == null ) || ( asyncTaskMovieVideos.getStatus() != AsyncTask.Status.RUNNING)) {
            if ( getMovie() != null) {
                this.asyncTaskMovieVideos = new AsyncTaskMovieVideos(getMovie().getId());
                this.asyncTaskMovieVideos.execute();
            }
        }
    }

    @Override
    public void onRefresh() {
        this.update();
    }

    @Override
    public void onMovieVideoClick(MovieVideo movieVideo) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieVideo.getUrlPlay()));
        getContext().startActivity(intent);
    }

    /**
     * Constrói uma nova linha de execução para acionar o {@see TheMovieDb.getMovieVideos} e recuperar
     * a lista de vídoes.
     */
    public class AsyncTaskMovieVideos extends AsyncTask<Void,Void,MovieVideosPage> {
        long movieId = -1;

        public AsyncTaskMovieVideos(long movieId){
            this.movieId = movieId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout(true);
        }

        @Override
        protected MovieVideosPage doInBackground(Void... voids) {
            Context context = MovieSubDetailVideosFragment.this.getContext();
            return TheMovieDb.getMovieVideos(context, movieId);
        }

        @Override
        protected void onPostExecute(MovieVideosPage movieVideosPage) {
            super.onPostExecute(movieVideosPage);
            showSwipeRefreshLayout(false);

            if ( movieVideosPage.isException() == false ) {
                movieVideosAdapter.update(movieVideosPage.getItems());
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), movieVideosPage.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            showSwipeRefreshLayout(false);
        }

    }

}
