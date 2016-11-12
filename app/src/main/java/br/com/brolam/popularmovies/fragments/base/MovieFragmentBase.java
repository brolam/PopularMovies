package br.com.brolam.popularmovies.fragments.base;

import android.support.v4.app.Fragment;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.Movie;


/**
 * A MovieFragmentBase declara funcionalidades basicas para um fragmento de tela relacionado
 * a um filme.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MovieFragmentBase extends Fragment {
    private static final String  DEBUG_TAG = "MovieFragmentBase: ";
    public static final String MOVIE_JSON_STRING = "movie_json_string";

    /**
     * Recupera um filme {@see Movie} com base no Bundle informado no fragmento de tela {@see Fragment.setArguments()}
     * @return
     */
    public Movie getMovie() {
        Movie movie = null;
        //Recupera um {@link Movie} através do paramentro {@link MOVIE_JSON_STRING}
        if (( getArguments() != null) && getArguments().containsKey(MOVIE_JSON_STRING)) {
            String jsonString = getArguments().getString(MOVIE_JSON_STRING);
            try {
                JSONObject jsonMovie = new JSONObject(jsonString);
                movie = new Movie(jsonMovie);
            } catch (JSONException e) {
                //O erro será tratado no método {@link onCreateView}
                Log.d(DEBUG_TAG, String.format("onCreate error: %s", jsonString));
            }

        }
        return movie;
    }
}
