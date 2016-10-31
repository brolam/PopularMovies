package org.themoviedb.api.v3;

import android.content.Context;
import android.util.Log;
import org.themoviedb.api.v3.schemes.MoviePage;
import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;

/**
 * A TheMovieDb implementa as regras para acessar a api do TheMovieDb
 * conforme documentação em https://developers.themoviedb.org/3/movies
 */
public class TheMovieDb {
    private static final String  DEBUG_TAG = "TheMovieDb:";
    public static final Order DEFAULT_ORDER = Order.popular;

    //Texto com parâmentros para construir a URL dos web métodos:
    //Parâmetro 1 - informar a ordem dos filmes {@see Order}
    //Parâmetro 2 - informar a api_key {@see R.string.api_themoviedb}
    //Parâmetro 3 - informar o número da página.
    private static final String URL_FORMAT = "https://api.themoviedb.org/3/movie/%s?api_key=%s&page=%s";

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
     * Recupera uma página com filmes conforme os parâmetros abaixo:
     * @param context informar um contexto válido.
     * @param order informar uma ordem válida {@see Order}
     * @param page informar uma página maior que zero.
     * @return retorna a um {@see MoviePage}
     */
    public static MoviePage getMovies(Context context, Order order, int page )  {



        String key = context.getString(R.string.api_themoviedb);
        String jsonString = null;
        try {
             if ( key.isEmpty()){
                 throw new Exception(context.getString(R.string.error_api_themoviedb_not_define));
             }
            jsonString = MovieHelper.requestHttp("GET", String.format(URL_FORMAT, order.toString(), key, page));
            return new MoviePage(jsonString);
        } catch (Exception e) {
            Log.e (DEBUG_TAG, e.getMessage());
            //retorna com um MoviePage informando o erro.
            return new MoviePage(e);
        }

    }


}
