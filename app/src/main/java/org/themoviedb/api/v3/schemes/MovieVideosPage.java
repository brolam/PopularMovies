package org.themoviedb.api.v3.schemes;

import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.base.PageBase;


/**
 * Representa uma ou mais páginas com Vídeos recuperadas na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies/get-movie-videos.
 * @see PageBase
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieVideosPage extends PageBase<MovieVideo> {

    public MovieVideosPage(String jsonString) throws JSONException {
        super(jsonString);
    }

    public MovieVideosPage(Exception e) {
        super(e);
    }

    @Override
    public MovieVideo getNewItem(JSONObject jsonObject) throws JSONException {
        return new MovieVideo(jsonObject);
    }
}
