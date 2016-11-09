package org.themoviedb.api.v3.schemes.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A PageBase declara as principais funcionalidades para recuperar páginas na api do TheMovieDd, veja https://developers.themoviedb.org/3.
 * Sendo importante destacar, que a PageBase deve ser utilizada para recuperar
 * uma ou mais páginas para diferentes assuntos: Filmes, vídeos, revisões e etc.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public abstract class PageBase<T extends ItemBase> {

    private static final String PAGE = "page";
    private static final String RESULTS = "results";
    private static final String TOTAL_PAGES = "total_pages";

    /**
     * informa a pádina atual.
     */
    int currentPage;

    /**
     * Informa a quantidade total de páginas.
     */
    int totalPages;
    /**
     * Informa todos os filmes retornado na página um até a
     * página atual.
     */
    ArrayList<T> items;

    /**
     * Informa o erro ocorrido ao tentar recuperar uma página.
     */
    Exception exception;

    /**
     * Construtor padrão.
     */
    public PageBase(){
        this.currentPage = 0;
        this.totalPages = 0;
        this.items = new ArrayList<>();
        this.exception = null;
    }

    /**
     * Constrói uma MoviePage conforme texto no formato JSON retornado no {@see TheMovieDd}
     * @param jsonString texto no formato JSON
     * @throws JSONException
     */
    public PageBase(String jsonString) throws JSONException {
        this();
        JSONObject jsonPage = new JSONObject(jsonString);
        JSONArray jsonArrayMovies = jsonPage.getJSONArray(RESULTS);
        this.currentPage = jsonPage.has(PAGE)?jsonPage.getInt(PAGE):1;
        this.totalPages = jsonPage.has(TOTAL_PAGES)?jsonPage.getInt(TOTAL_PAGES):1;
        for( int index = 0; index < jsonArrayMovies.length(); index++){
            JSONObject jsonMovie = jsonArrayMovies.getJSONObject(index);
            this.items.add(getNewItem(jsonMovie));

        }
        //Se o MoviePage for instaciado com sucesso!
        this.exception = null;

    }

    public T getNewItem(JSONObject jsonObject) throws JSONException {
        return  null;
    }

    /**
     * Constrói um MoviePage informando um exception, {@see TheMovieDb.getMovies}
     * @param exception
     */
    public PageBase(Exception exception)  {
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
        for (T t : getItems()) {
            jsonArrayMovies.put(t.getJSONObject());
        }
        jsonPage.put(PAGE, getCurrentPage());
        jsonPage.put(TOTAL_PAGES, getTotalPages());
        jsonPage.put(RESULTS, jsonArrayMovies);
        return jsonPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public ArrayList<T> getItems() {
        return this.items;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isException(){
        return this.exception != null;
    }

    /**
     * Informa se a ultima página já foi recuperada.
     * @return verdadeira se a página atual for maior ou igual ao total de páginas.
     */
    public boolean isEndOfPage(){
        return getCurrentPage() >= getTotalPages();
    }

    public void addMovies(int currentPag, ArrayList<T> items){
        this.currentPage = currentPag;
        this.items.addAll(items);
    }
}
