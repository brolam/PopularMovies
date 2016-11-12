package br.com.brolam.popularmovies.fragments;

import android.content.Context;
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
import org.themoviedb.api.v3.schemes.MovieReview;
import org.themoviedb.api.v3.schemes.MovieReviewsPage;

import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;
import br.com.brolam.popularmovies.adapters.MovieReviewsAdapter;
import br.com.brolam.popularmovies.fragments.base.MovieFragmentBase;


/**
 * Página do subdetalhe para exibi a lista de revisões de um filme
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieSubDetailReviewsFragment extends MovieFragmentBase implements SwipeRefreshLayout.OnRefreshListener, MovieReviewsAdapter.IMovieReviewsAdapter {

    SwipeRefreshLayout swipeRefreshLayout;
    MovieReviewsAdapter movieReviewsAdapter;
    AsyncTaskMovieReviews asyncTaskMovieReviews;

    public MovieSubDetailReviewsFragment() {
        this.movieReviewsAdapter = new MovieReviewsAdapter(this);
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
            rootView = inflater.inflate(R.layout.movie_sub_detail_reviews, container, false);
            this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
            RecyclerView recyclerViewMovieReviews = (RecyclerView)rootView.findViewById(R.id.recyclerViewMovieReviews);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
            recyclerViewMovieReviews.setAdapter(this.movieReviewsAdapter);
            recyclerViewMovieReviews.setLayoutManager(linearLayoutManager);
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

        //Somente executa a atualização online da lista de revisões se o asyncTaskMovieReviews não estiver
        //em execução.
        if ( ( asyncTaskMovieReviews == null ) || ( asyncTaskMovieReviews.getStatus() != AsyncTask.Status.RUNNING)) {
            if ( getMovie() != null) {
                this.asyncTaskMovieReviews = new AsyncTaskMovieReviews(getMovie().getId());
                this.asyncTaskMovieReviews.execute();
            }
        }
    }

    @Override
    public void onRefresh() {
        this.update();
    }

    @Override
    public void onMovieReviewClick(MovieReview movieReview) {

    }

    @Override
    public void onEndOfMovies() {

    }

    /**
     * Constrói uma nova linha de execução para acionar o {@see TheMovieDb.getMovieReviews} e recuperar
     * a lista de revisões.
     */
    public class AsyncTaskMovieReviews  extends AsyncTask<Void,Void,MovieReviewsPage> {
        long movieId = -1;

        public AsyncTaskMovieReviews(long movieId){
            this.movieId = movieId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout(true);
        }

        @Override
        protected MovieReviewsPage doInBackground(Void... voids) {
            Context context = MovieSubDetailReviewsFragment.this.getContext();
            return TheMovieDb.getMovieReviews(context, movieId, 1);
        }

        @Override
        protected void onPostExecute(MovieReviewsPage movieReviewsPage) {
            super.onPostExecute(movieReviewsPage);
            showSwipeRefreshLayout(false);

            if ( movieReviewsPage.isException() == false ) {
                movieReviewsAdapter.update(movieReviewsPage.getItems());
            } else {
                if (getContext() != null) {
                    Toast.makeText(getContext(), movieReviewsPage.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        protected void onCancelled() {
            showSwipeRefreshLayout(false);
        }

    }

}
