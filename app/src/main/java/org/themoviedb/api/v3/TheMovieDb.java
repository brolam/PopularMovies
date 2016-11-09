package org.themoviedb.api.v3;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.MoviePage;
import org.themoviedb.api.v3.schemes.MovieReviewsPage;
import org.themoviedb.api.v3.schemes.MovieVideo;
import org.themoviedb.api.v3.schemes.MovieVideosPage;

import java.util.ArrayList;
import java.util.List;

import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;

/**
 * A TheMovieDb declara as regras para acessar a api do TheMovieDb
 * conforme documentação em https://developers.themoviedb.org/3
 */
public class TheMovieDb {
    private static final String  DEBUG_TAG = "TheMovieDb:";
    public static final Order DEFAULT_ORDER = Order.popular;

    //Texto com parâmentros para construir a URL dos web método getMovies:
    //Parâmetro 1 - informar a ordem dos filmes {@see Order}
    //Parâmetro 2 - informar a api_key {@see R.string.api_themoviedb}
    //Parâmetro 3 - informar o número da página.
    private static final String URL_FORMAT = "https://api.themoviedb.org/3/movie/%s?api_key=%s&page=%s";

    //Texto com parâmentros para construir a URL dos web método getVideos:
    //Parâmetro 1 - informar o id do filme {@see Movie}
    //Parâmetro 2 - informar a api_key {@see R.string.api_themoviedb}
    private static final String URL_MOVIE_VIDEOS_FORMAT = "https://api.themoviedb.org/3/movie/%s/videos?api_key=%s";

    //Texto com parâmentros para construir a URL dos web método getReviews:
    //Parâmetro 1 - informar o id do filme {@see Movie}
    //Parâmetro 2 - informar a api_key {@see R.string.api_themoviedb}
    //Parâmetro 3 - informar o número da página.
    private static final String URL_MOVIE_REVIEWS_FORMAT = "https://api.themoviedb.org/3/movie/%s/reviews?api_key=%s&page=%s";

    /**
     * informa as possíveis ordem dos filmes definidas na api do TheMovieDb.
     */
    public enum Order {
        popular,
        top_rated;

        public String getTitle(Context context){
            if ( this.equals(top_rated) ) {
                return context.getString(R.string.pref_list_themoviedb_api_order_top_rated_title);
            } else {
                return context.getString(R.string.pref_list_themoviedb_api_order_popular_title);
            }
        }
    }

    /**
     * Recupera a chave da API do https://api.themoviedb.org
     * @param context informar um contexto válido
     * @return a chave ou uma exceção se a chavé não foi definida.
     * @throws Exception
     */
    private static String getApiKey(Context context) throws Exception {
        String key = context.getString(R.string.api_themoviedb);
        if ( key.isEmpty()){
            throw new Exception(context.getString(R.string.error_api_themoviedb_not_define));
        }
        return key;
    }

    /**
     * Recupera uma ou mais páginas com filmes conforme os parâmetros abaixo:
     * @param context informar um contexto válido.
     * @param order informar uma ordem válida {@see Order}
     * @param page informar uma página maior que zero.
     * @return retorna com {@see MoviePage}
     */
    public static MoviePage getMovies(Context context, Order order, int page ) {
        try {
            String key = getApiKey(context);
            String jsonString = MovieHelper.requestHttp("GET", String.format(URL_FORMAT, order.toString(), key, page));
            return new MoviePage(jsonString);
        } catch (Exception e) {
            Log.e (DEBUG_TAG, e.getMessage());
            //retorna com um MoviePage informando o erro.
            return new MoviePage(e);
        }

    }


    /**
     * Recupera os vídeos relacionados a um filme.
     * @param context informar um contéxto válido.
     * @param movieId informar o id do filme.
     * @return um MovieVideosPage ou uma exceção, {@see MovieVideosPage }
     */
    public static MovieVideosPage getMovieVideos(Context context, long movieId ) {
        try {
            String key = getApiKey(context);
            String jsonString = MovieHelper.requestHttp("GET", String.format(URL_MOVIE_VIDEOS_FORMAT, movieId, key));
            return new MovieVideosPage(jsonString);
        } catch (Exception e) {
            Log.e (DEBUG_TAG, e.getMessage());
            //retorna com um MovieVideosPage informando o erro.
            return new MovieVideosPage(e);
        }

    }


    /**
     * Recupera uma ou mais páginas com as revisões relacionadas a um filme.
     * @param context informar um contexto válido.
     * @param movieId informar o id do filme.
     * @param page informar o número da página que deve ser recuperada.
     * @return um MovieReviewsPage ou uma exceção {@see MovieReviewsPage}
     */
    public static MovieReviewsPage getMovieReviews(Context context, long movieId, int page ) {
        try {
            String key = getApiKey(context);
            String jsonString = MovieHelper.requestHttp("GET", String.format(URL_MOVIE_REVIEWS_FORMAT, movieId, key, page));
            return new MovieReviewsPage(jsonString);
        } catch (Exception e) {
            Log.e (DEBUG_TAG, e.getMessage());
            //retorna com um getMovieReviews informando o erro.
            return new MovieReviewsPage(e);
        }

    }


}
