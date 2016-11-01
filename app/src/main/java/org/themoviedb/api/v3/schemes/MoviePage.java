package org.themoviedb.api.v3.schemes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Representa um página recuperado na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MoviePage {
    private static final String PAGE = "page";
    private static final String RESULTS = "results";

    /**
     * informa a pádina atual.
     */
    int currentPage;
    /**
     * Informa todos os filmes retornado na página um até a
     * página atual.
     */
    ArrayList<Movie> movies;

    /**
     * Informa o erro ocorrido ao tentar recuperar uma página.
     */
    Exception exception;

    /**
     * Construtor padrão.
     */
    public MoviePage(){
        this.currentPage = 0;
        this.movies = new ArrayList<>();
        this.exception = null;
    }

    /**
     * Constrói uma MoviePage conforme texto no formato JSON retornado no {@see TheMovieDd}
     * @param jsonString texto no formato JSON
     * @throws JSONException
     */
    public MoviePage(String jsonString) throws JSONException {
        this();
        JSONObject jsonPage = new JSONObject(jsonString);
        JSONArray jsonArrayMovies = jsonPage.getJSONArray(RESULTS);
        this.currentPage = jsonPage.getInt(PAGE);
        for( int index = 0; index < jsonArrayMovies.length(); index++){
            JSONObject jsonMovie = jsonArrayMovies.getJSONObject(index);
            Movie movie = new Movie(jsonMovie);
            this.movies.add(movie);

        }
        //Se o MoviePage for instaciado com sucesso!
        this.exception = null;

    }

    /**
     * Constrói um MoviePage informando um exception, {@see TheMovieDb.getMovies}
     * @param exception
     */
    public MoviePage(Exception exception)  {
        this();
        this.exception = exception;
    }

    /**
     * Converte um MoviePage em um objeto JSON.
     * @return um objeto JSON com os campos de um MoviePage.
     * @throws JSONException
     */
    public JSONObject getJsonObject() throws JSONException {
        JSONObject jsonPage = new JSONObject();
        JSONArray jsonArrayMovies = new JSONArray();
        for (Movie movie : getMovies()) {
            jsonArrayMovies.put(new JSONObject(movie.getJsonString()));
        }
        jsonPage.put(PAGE, getCurrentPage());
        jsonPage.put(RESULTS, jsonArrayMovies);
        return jsonPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public ArrayList<Movie> getMovies() {
        return movies;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isException(){
        return this.exception != null;
    }

    public void addMovies(int currentPag, ArrayList<Movie> movies){
        this.currentPage = currentPag;
        this.movies.addAll(movies);
    }
}
