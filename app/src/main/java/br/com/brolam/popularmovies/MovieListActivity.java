package br.com.brolam.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.TheMovieDb;
import org.themoviedb.api.v3.schemes.Movie;
import org.themoviedb.api.v3.schemes.MoviePage;

import br.com.brolam.popularmovies.adapters.MoviesAdapter;

/**
 * A MovieListActivity é a atividade principal do aplicativo, onde será exibida uma lista e detalhes dos filmes.
 * Essa atividade tem diferentes apresentações conforme o tamanho da tela do dispositivo( tablet ou smartphone).
 * Para dispositivos inferior a w820dp o detalhe do filme será exibido no {@link MovieDetailActivity} ou em um painel ao
 * lado da lista de filmes se a tela for maior igual a w820dp.
 * @see MovieDetailFragment
 * @see org.themoviedb.api.v3
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.IMoviesAdapter, SwipeRefreshLayout.OnRefreshListener {
    private static final String DEBUG_TAG = "MovieHelper:";

    //Paramentro para armazenar a lista de filmes quando for necessário salvar a situação
    //da atividade {@see onSaveInstanceState} e {@see loadSavedInstanceState}
    private static final String SAVE_STATE_PAGE_JSON = "save_state_page_json";

    //Quando for necessário salvar a situação da atividade, também será armazenado o último filme
    //selecionado para que ele seja exibido quando a tela for reconstruida.
    //{@see onMovieClick}
    //{@see savedInstanceState
    //{@see loadSavedInstanceState}
    private Movie lastMovieSelected = null;


    //Armazena a página recuperada na API do {@see TheMovieDb } com a lista de filmes.
    private MoviePage moviePage;


    private MoviesAdapter moviesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    AsyncTaskMovies asyncTaskMovies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        this.moviePage = new MoviePage();
        this.swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.movie_list);


        this.moviesAdapter = new MoviesAdapter(this);
        //Gerar um grid com duas colunas ou três conforme o tamanho da tela.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, MovieHelper.isTwoPane(this)?3:2);
        recyclerView.setAdapter(moviesAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        loadSavedInstanceState(savedInstanceState);

        //A lista de filmes somente será atulizada com informações online do {@see TheMovieDb }
        //se for a primeira construção da tela! Na próxíma atualização da tela, os filmes serão recuperados
        // através do {@link loadSavedInstanceState } ou se o usuário solicitar a atualização online através
        // do {@link swipeRefreshLayout}
        this.swipeRefreshLayout.setOnRefreshListener(this);
        if ( this.moviePage.getPage() == 0){
            onRefresh();
        }
    }


    /**
     * Quando um filme for selecionado {@see MoviesAdapter.IMoviesAdapter}
     * @param movie
     */
    @Override
    public void onMovieClick(Movie movie) {
        try {
            if (MovieHelper.isTwoPane(this)) {
                Bundle arguments = new Bundle();
                arguments.putString(MovieDetailFragment.MOVIE_JSON_STRING, movie.getJsonString());
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.MOVIE_JSON_STRING, movie.getJsonString());
                this.startActivity(intent);
            }
            this.lastMovieSelected = movie;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, String.format("onMovieClick error: %s", e.getMessage()) );
            Toast.makeText(this, getText(R.string.error_could_not_display_movie_detail), Toast.LENGTH_LONG).show();
        }
    }


    private void showSwipeRefreshLayout(boolean show) {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(show);
        }
    }

    @Override
    public void onRefresh() {
        if ( MovieHelper.checkConnection(this) == false ) {
            showSwipeRefreshLayout(false);
            Toast.makeText(this, getText(R.string.error_connection_to_internet), Toast.LENGTH_LONG).show();
            return;
        }

        //Somente executa a atualização online dos filmes se o asyncTaskMovies não estiver
        //em execução.
        if ( ( asyncTaskMovies == null ) || ( asyncTaskMovies.getStatus() != AsyncTask.Status.RUNNING)) {
            AsyncTaskMovies asyncTaskMovies = new AsyncTaskMovies();
            asyncTaskMovies.execute();
        }
    }

    /**
     * Constrói uma nova linha de execução para acionar o {@see TheMovieDb.getMovies} e recuperar
     * uma pagina com os filmes
     */
    public class AsyncTaskMovies extends AsyncTask<Void,Void,MoviePage>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout(true);
        }

        @Override
        protected MoviePage doInBackground(Void... voids) {
            int numPage = moviePage.getPage() == 0 ? 1 : moviePage.getPage();
            return TheMovieDb.getMovies(MovieListActivity.this, TheMovieDb.Order.POPULAR, numPage);
        }

        @Override
        protected void onPostExecute(MoviePage moviePage) {
            super.onPostExecute(moviePage);
            showSwipeRefreshLayout(false);

            if ( moviePage.isException() == false ) {
                MovieListActivity.this.moviePage = moviePage;
                Movie[] movies = MovieListActivity.this.moviePage.getMovies();
                MovieListActivity.this.moviesAdapter.update(movies);
                if ( MovieHelper.isTwoPane(MovieListActivity.this) && (movies.length > 0)) {
                    onMovieClick(movies[0]);
                }
            } else {
                Toast.makeText(MovieListActivity.this, moviePage.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            showSwipeRefreshLayout(false);
        }

    }


    /**
     * Atualiza a situação da tela conforme a última situação salva.
     * @param savedInstanceState
     */
    private void loadSavedInstanceState(Bundle savedInstanceState) {
        if ( (savedInstanceState != null) &&  savedInstanceState.containsKey(SAVE_STATE_PAGE_JSON) ){
            try {
                Movie movie = null;
                this.moviePage = new MoviePage(savedInstanceState.getString(SAVE_STATE_PAGE_JSON));
                this.moviesAdapter.update(this.moviePage.getMovies());

                if ( savedInstanceState.containsKey(MovieDetailFragment.MOVIE_JSON_STRING) ){
                    String jsonMovieString = savedInstanceState.getString(MovieDetailFragment.MOVIE_JSON_STRING);
                    movie = new Movie(new JSONObject(jsonMovieString));
                } else if ( this.moviePage.getMovies().length > 0 ){
                    movie = this.moviePage.getMovies()[0];
                }

                //O codigo abaixo vai atualizar o segundo painel com o detalhe do filme
                //se o segundo painel estiver disponível.
                if (( movie != null ) && MovieHelper.isTwoPane(this)){
                    onMovieClick(movie);
                }
            } catch (JSONException e) {
                //Se não for possível recuperar o objeto {@link MoviePage} será gerada uma instancia
                //com o construtor padrão para foçar uma atualização online, {@see onCreate }
                this.moviePage = new MoviePage();
                Log.d(DEBUG_TAG, String.format("Load savedInstanceState error: %s", e.getMessage()) );
            }
        }
    }


    /**
     * Salva a última situação da lista de filmes, evitando que a cada atualização da tela seja realizada
     * uma atualização online dos filmes.
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (moviePage.getPage() > 0) {
                outState.putString(SAVE_STATE_PAGE_JSON, moviePage.getJsonObject().toString());
                if (lastMovieSelected != null) {
                    outState.putString(MovieDetailFragment.MOVIE_JSON_STRING, lastMovieSelected.getJsonString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
