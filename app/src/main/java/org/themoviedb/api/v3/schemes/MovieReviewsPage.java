package org.themoviedb.api.v3.schemes;

import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.base.PageBase;

/**
 * Representa uma ou mais páginas com revisões recuperadas na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies/get-movie-reviews.
 * @see PageBase
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieReviewsPage extends PageBase<MovieReview> {

    public MovieReviewsPage(){
        super();
    }

    public MovieReviewsPage(String jsonString) throws JSONException {
        super(jsonString);
    }

    public MovieReviewsPage(Exception e) {
        super(e);
    }

    @Override
    public MovieReview getNewItem(JSONObject jsonObject) throws JSONException {
        return new MovieReview(jsonObject);
    }


}
