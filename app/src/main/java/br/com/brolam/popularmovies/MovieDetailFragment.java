package br.com.brolam.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.Movie;

import br.com.brolam.popularmovies.models.FavoriteModel;


/**
 * A MovieDetailFragment exibe o detalhe do filme.
 * Esse fragmento de tela é utilizado nas atividades {@see MovieListActivity} e {@see MovieDetailFragment}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener {
    private static final String  DEBUG_TAG = "MovieDetailFragment: ";

    /**
     * Parâmentro para recuperar uma texto Json de um {@link Movie}, enviado via Bundle.
     */
    public static final String MOVIE_JSON_STRING = "movie_json_string";
    private Movie movie;
    private boolean isFavorite;

    public MovieDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recupera um {@link Movie} através do paramentro {@link MOVIE_JSON_STRING}
        if (getArguments().containsKey(MOVIE_JSON_STRING)) {
            String jsonString = getArguments().getString(MOVIE_JSON_STRING);
            try {

                JSONObject jsonMovie = new JSONObject(jsonString);
                this.movie = new Movie(jsonMovie);
                isFavorite = FavoriteModel.existsFavoriteMovie(this.getContext(),  this.movie.getId());

            } catch (JSONException e) {
                //O erro será tratado no método {@link onCreateView}
                Log.d(DEBUG_TAG, String.format("onCreate error: %s", jsonString));
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Se não for possível recuperar um objeto movie com base na string Json informada no
        //Bundle e parametro MOVIE_JSON_STRING, será exibida uma mensagem no lugar do detalhe do filme.
        View rootView = null;
        if (movie != null) {
            rootView = inflater.inflate(R.layout.movie_detail, container, false);
            ((TextView) rootView.findViewById(R.id.textViewOriginalTitle)).setText(movie.getOriginalTitle());
            ((TextView) rootView.findViewById(R.id.textViewReleaseDateYear)).setText(MovieHelper.getDateFormatted(movie.getReleaseDate(), "yyyy"));
            ((TextView) rootView.findViewById(R.id.textViewReleaseDateDayMonth)).setText(MovieHelper.getDateFormatted(movie.getReleaseDate(), "dd/MM"));
            ((RatingBar) rootView.findViewById(R.id.ratingBarVoteAverage)).setRating(movie.getVoteAverageFiveStars());
            ((TextView) rootView.findViewById(R.id.textViewOverview)).setText(movie.getOverview());
            MovieHelper.requestImage(this.movie.getPosterPath(), (ImageView)rootView.findViewById(R.id.imageView));
            ImageButton imageButtonFavorite = (ImageButton)rootView.findViewById(R.id.imageButtonFavorite);
            setImageButtonFavorite(imageButtonFavorite);
            imageButtonFavorite.setOnClickListener(this);
        } else {
            rootView = inflater.inflate(R.layout.movie_detail_error_message, container, false);
            ((TextView) rootView.findViewById(R.id.textViewErrorMessage)).setText(getText(R.string.error_message_unable_detail_movie));
        }
        return rootView;
    }

    private void setImageButtonFavorite(ImageButton imageButtonFavorite){
        imageButtonFavorite.setImageResource(isFavorite?R.drawable.ic_favorite_yes:R.drawable.ic_favorite_no);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.imageButtonFavorite ){
            if ( this.isFavorite){
                FavoriteModel.deleteFavoriteMovie(this.getContext(), this.movie.getId());
                this.isFavorite = false;
            } else {
                try {
                    FavoriteModel.setFavoriteMovie(this.getContext(), this.movie);
                    this.isFavorite = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setImageButtonFavorite((ImageButton)view);
        }
    }
}
