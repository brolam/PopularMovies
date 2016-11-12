package org.themoviedb.api.v3.schemes;

import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.base.ItemBase;

/**
 * Representa um objeto Video recuperado na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies/get-movie-videos.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieVideo extends ItemBase {
    private final String URL_PLAY_FORMAT = "https://www.youtube.com/watch?v=%s";

    //constantes para facilitar o acesso aos campos do objeto JSON.
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SITE = "site";
    private static final String TYPE = "type";
    private static final String KEY = "key";

    private String id;
    private String name;
    private String site;
    private String type;
    private String key;

    /**
     * Constrói um objeto Video conforme documentação em https://developers.themoviedb.org/3/movies/get-movie-videos.
     * @param jsonMovieVideo informar um objeto JSON conformme documentção da api do TheMovieDd.
     * @throws JSONException
     */
    public MovieVideo(JSONObject jsonMovieVideo) throws JSONException {
        super(jsonMovieVideo);
        this.id = jsonMovieVideo.getString(ID);
        this.name = jsonMovieVideo.getString(NAME);
        this.site = jsonMovieVideo.getString(SITE);
        this.type = jsonMovieVideo.getString(TYPE);
        this.key = jsonMovieVideo.getString(KEY);

    }

    /**
     * Transforma um objeto {@link MovieVideo} em um objeto JSON.
     * @return um objeto JSON válido.
     * @throws JSONException
     */
    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonMovieVideo = new JSONObject();
        jsonMovieVideo.put(ID,this.id);
        jsonMovieVideo.put(NAME, this.getName());
        jsonMovieVideo.put(SITE, this.getSite());
        jsonMovieVideo.put(TYPE, this.getType());
        jsonMovieVideo.put(KEY, this.getKey());
        return jsonMovieVideo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSite(){
        return this.site;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getUrlPlay(){
        return String.format(URL_PLAY_FORMAT, getKey());
    }

}
