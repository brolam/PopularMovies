package br.com.brolam.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.TheMovieDb;
import org.themoviedb.api.v3.schemes.Movie;
import org.themoviedb.api.v3.schemes.MoviePage;

import java.util.ArrayList;

import br.com.brolam.popularmovies.adapters.MoviesAdapter;
import br.com.brolam.popularmovies.models.FavoriteModel;

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
public class MovieListActivity extends AppCompatActivity implements MoviesAdapter.IMoviesAdapter, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private static final String DEBUG_TAG = "MovieHelper:";

    //Paramentro para armazenar a lista de filmes quando for necessário salvar a situação
    //da atividade {@see onSaveInstanceState} e {@see loadSavedInstanceState}
    private static final String SAVE_STATE_PAGE_JSON = "save_state_page_json";
    private static final String SAVE_STATE_IS_SHOW_FAVORITE = "save_state_is_show_favorite";

    //Quando for necessário salvar a situação da atividade, também será armazenado o último filme
    //selecionado para que ele seja exibido quando a tela for reconstruida.
    //{@see onMovieClick}
    //{@see savedInstanceState
    //{@see loadSavedInstanceState}
    private Movie lastMovieSelected = null;

    //Informa se somente os filmes favoritos devem ser exibidos.
    private boolean isShowFavorite = false;


    //Armazena a página recuperada na API do {@see TheMovieDb } com a lista de filmes.
    private MoviePage moviePage;


    private MoviesAdapter moviesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton floatingActionButton;

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
        this.floatingActionButton = (FloatingActionButton)findViewById(R.id.floatingActionButton);
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
        if ( this.moviePage.getCurrentPage() == 0){
            onRefresh();
        }

        this.floatingActionButton.setOnClickListener(this);
        this.setFloatingActionButtonImage();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
                arguments.putString(MovieDetailFragment.MOVIE_JSON_STRING, movie.getJSONObject().toString());
                MovieDetailFragment fragment = new MovieDetailFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment)
                        .commit();
            } else {
                Intent intent = new Intent(this, MovieDetailActivity.class);
                intent.putExtra(MovieDetailFragment.MOVIE_JSON_STRING, movie.getJSONObject().toString());
                this.startActivity(intent);
            }
            this.lastMovieSelected = movie;
        } catch (JSONException e) {
            Log.d(DEBUG_TAG, String.format("onMovieClick error: %s", e.getMessage()) );
            Toast.makeText(this, getText(R.string.error_could_not_display_movie_detail), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onEndOfMovies() {
        /**
         * A verificação abaixo vai evitar que a ultima página seja recuperada mais de uma
         * vez.
         */
        if ( moviePage.isEndOfPage() == false) {
            newPage(moviePage.getCurrentPage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void showSwipeRefreshLayout(boolean show) {
        if (this.swipeRefreshLayout != null) {
            this.swipeRefreshLayout.setRefreshing(show);
        }
    }

    @Override
    /**
     * Atualiza somente a primeira página.
     */
    public void onRefresh() {
        newPage(0);
    }


    /**
     * Solicita uma nova página.
     * @param currentPage informar a página atual ou zero para recuperar a primeira página.
     */
    public void newPage(int currentPage) {
        if ( MovieHelper.checkConnection(this) == false ) {
            showSwipeRefreshLayout(false);
            Toast.makeText(this, getText(R.string.error_connection_to_internet), Toast.LENGTH_LONG).show();
            return;
        }

        //Somente executa a atualização online dos filmes se o asyncTaskMovies não estiver
        //em execução.
        if ( ( asyncTaskMovies == null ) || ( asyncTaskMovies.getStatus() != AsyncTask.Status.RUNNING)) {
            AsyncTaskMovies asyncTaskMovies = new AsyncTaskMovies(currentPage);
            asyncTaskMovies.execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK ){
            onRefresh();
        }
    }

    @Override
    public void onClick(View view) {
        if ( view.equals(this.floatingActionButton)){
            if ( this.isShowFavorite){
                this.showFavorite(false);
            } else {
                this.showFavorite(true);
            }
        }
    }


    private void showFavorite(boolean show) {
        this.isShowFavorite = show;
        this.setFloatingActionButtonImage();
        this.lastMovieSelected = null;
        newPage(0);

    }


    private void setFloatingActionButtonImage() {
        this.floatingActionButton.setImageResource(this.isShowFavorite? R.drawable.ic_favorite_yes:R.drawable.ic_favorite_no);
    }

    /**
     * Constrói uma nova linha de execução para acionar o {@see TheMovieDb.getMovies} e recuperar
     * uma pagina com os filmes
     */
    public class AsyncTaskMovies extends AsyncTask<Void,Void,MoviePage>{
        int currentPage;

        public AsyncTaskMovies(int currentPage){
            this.currentPage = currentPage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout(true);
        }

        @Override
        protected MoviePage doInBackground(Void... voids) {
            if ( MovieListActivity.this.isShowFavorite){
                return FavoriteModel.getMoviePage(MovieListActivity.this);
            } else {
                TheMovieDb.Order order = SettingsActivity.getTheMovieDbApiOrderValue(MovieListActivity.this);
                return TheMovieDb.getMovies(MovieListActivity.this, order, currentPage + 1);
            }
        }

        @Override
        protected void onPostExecute(MoviePage moviePage) {
            super.onPostExecute(moviePage);
            showSwipeRefreshLayout(false);

            if ( moviePage.isException() == false ) {

                //Depois da segunda página os filmes serão adicionados a lista de filmes.
                if ( moviePage.getCurrentPage() == 1 ) {
                    MovieListActivity.this.moviePage = moviePage;
                } else {
                    MovieListActivity.this.moviePage.addMovies(moviePage.getCurrentPage(), moviePage.getItems());
                }

                ArrayList<Movie> movies = MovieListActivity.this.moviePage.getItems();
                MovieListActivity.this.moviesAdapter.update(movies);
                //Se o painel do detalhes estiver disponível, o código abaixo
                //vai tentar exibir o detalhe do ultimo filme selecionado.
                if ( MovieHelper.isTwoPane(MovieListActivity.this) && (movies.size() > 0)) {
                    onMovieClick(movies.get(0));
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
        if (savedInstanceState != null) {
            try {
                this.isShowFavorite = savedInstanceState.getBoolean(SAVE_STATE_IS_SHOW_FAVORITE, this.isShowFavorite);
                if (savedInstanceState.containsKey(SAVE_STATE_PAGE_JSON)) {
                    Movie movie = null;
                    this.moviePage = new MoviePage(savedInstanceState.getString(SAVE_STATE_PAGE_JSON));
                    this.moviesAdapter.update(this.moviePage.getItems());

                    if (savedInstanceState.containsKey(MovieDetailFragment.MOVIE_JSON_STRING)) {
                        String jsonMovieString = savedInstanceState.getString(MovieDetailFragment.MOVIE_JSON_STRING);
                        movie = new Movie(new JSONObject(jsonMovieString));
                    } else if (this.moviePage.getItems().size() > 0) {
                        movie = this.moviePage.getItems().get(0);
                    }

                    //O codigo abaixo vai atualizar o segundo painel com o detalhe do filme
                    //se o segundo painel estiver disponível.
                    if ((movie != null) && MovieHelper.isTwoPane(this)) {
                        onMovieClick(movie);
                    }
                }
            } catch (JSONException e) {
                //Se não for possível recuperar o objeto {@link MoviePage} será gerada uma instancia
                //com o construtor padrão para foçar uma atualização online, {@see onCreate }
                this.moviePage = new MoviePage();
                Log.d(DEBUG_TAG, String.format("Load savedInstanceState error: %s", e.getMessage()));
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
            outState.putBoolean(SAVE_STATE_IS_SHOW_FAVORITE, this.isShowFavorite);
            if (moviePage.getCurrentPage() > 0) {
                outState.putString(SAVE_STATE_PAGE_JSON, moviePage.getJsonObject().toString());
                if (lastMovieSelected != null) {
                    outState.putString(MovieDetailFragment.MOVIE_JSON_STRING, lastMovieSelected.getJSONObject().toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
