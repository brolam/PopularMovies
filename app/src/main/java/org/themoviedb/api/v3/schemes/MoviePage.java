package org.themoviedb.api.v3.schemes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.base.PageBase;

import java.util.ArrayList;


/**
 * Representa uma ou mais páginas com filmes recuperadas na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies.
 * @see PageBase
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MoviePage extends PageBase<Movie> {

    public MoviePage(){
        super();
    }

    public MoviePage(String jsonString) throws JSONException {
        super(jsonString);
    }

    public MoviePage(Exception e) {
        super(e);
    }

    @Override
    public Movie getNewItem(JSONObject jsonObject) throws JSONException {
        return new Movie(jsonObject);
    }
}
