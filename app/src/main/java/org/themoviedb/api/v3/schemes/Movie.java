package org.themoviedb.api.v3.schemes;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import br.com.brolam.popularmovies.MovieHelper;

/**
 * Representa um objeto Movie recuperado na api do TheMovieDd, veja https://developers.themoviedb.org/3/movies.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class Movie {

    //constantes para facilitar o acesso aos campos do objeto JSON com o detalhe do filme.
    private static final String ORIGINAL_TITLE = "original_title";
    private static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String RELEASE_DATE_FORMAT = "yyyy-MM-dd";


    private String originalTitle;
    private String posterPath;
    private String overview;
    private float voteAverage;
    private Date releaseDate;

    /**
     * Constrói um objeto movie conforme documentação em https://developers.themoviedb.org/3/movies.
     * @param jsonMovie informar um objeto JSON conformme documentção da api do TheMovieDd.
     * @throws JSONException
     */
    public Movie(JSONObject jsonMovie) throws JSONException {
        this.originalTitle = jsonMovie.getString(ORIGINAL_TITLE);
        this.posterPath = jsonMovie.getString(POSTER_PATH);
        this.overview = jsonMovie.getString(OVERVIEW);
        this.voteAverage = (float)jsonMovie.getDouble(VOTE_AVERAGE);
        Date date = MovieHelper.getDate(jsonMovie.getString(RELEASE_DATE), RELEASE_DATE_FORMAT);
        this.releaseDate = date == null? new Date(0):date;
    }

    /**
     * Transforma um objeto {@link Movie} em um texto no formato JSON.
     * @return texto no formato JSON.
     * @throws JSONException
     */
    public String getJsonString() throws JSONException {
        JSONObject jsonMovie = new JSONObject();
        jsonMovie.put(ORIGINAL_TITLE, this.getOriginalTitle());
        jsonMovie.put(POSTER_PATH, this.getPosterPath());
        jsonMovie.put(OVERVIEW, this.getOverview());
        jsonMovie.put(VOTE_AVERAGE, this.getVoteAverage());
        jsonMovie.put(RELEASE_DATE, MovieHelper.getDateFormatted(this.getReleaseDate(), RELEASE_DATE_FORMAT));
        return jsonMovie.toString();
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public String getOverview() {
        return this.overview;
    }

    public  Float getVoteAverage() {
        return this.voteAverage;
    }

    /**
     * Converte o percentual de base 10 para base 5.
     * @return valor entre 0.00 e 5.00.
     */
    public  float getVoteAverageFiveStars() {
        return (float) (this.voteAverage > 0? this.voteAverage / 2.00 : 0.00);
    }

    public  Date getReleaseDate() {
        return this.releaseDate;
    }
}
