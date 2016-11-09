package org.themoviedb.api.v3.schemes;

import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.base.ItemBase;

/**
 * Representa um objeto Review de um filme recuperado na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies/get-movie-reviews.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MovieReview extends ItemBase {

    //constantes para facilitar o acesso aos campos do objeto JSON.
    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";


    private String id;
    private String author;
    private String content;

    /**
     * Constrói um objeto Review conforme documentação em https://developers.themoviedb.org/3/movies/get-movie-reviews.
     * @param jsonMovieReview informar um objeto JSON conformme documentção da api do TheMovieDd.
     * @throws JSONException
     */
    public MovieReview(JSONObject jsonMovieReview) throws JSONException {
        super(jsonMovieReview);
        this.id = jsonMovieReview.getString(ID);
        this.author = jsonMovieReview.getString(AUTHOR);
        this.content = jsonMovieReview.getString(CONTENT);

    }


    /**
     * Transforma um objeto {@link MovieReview} em um objeto JSON.
     * @return um objeto JSON válido.
     * @throws JSONException
     */
    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonMovieReview = new JSONObject();
        jsonMovieReview.put(ID,this.id);
        jsonMovieReview.put(AUTHOR, this.getAuthor());
        jsonMovieReview.put(CONTENT, this.getContent());
        return jsonMovieReview;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
