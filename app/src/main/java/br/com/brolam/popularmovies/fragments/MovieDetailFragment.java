package br.com.brolam.popularmovies.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.themoviedb.api.v3.schemes.Movie;
import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;
import br.com.brolam.popularmovies.adapters.MovieSubDetailPagerAdapter;
import br.com.brolam.popularmovies.fragments.base.MovieFragmentBase;
import br.com.brolam.popularmovies.models.FavoriteModel;

/**
 * A MovieDetailFragment exibe o detalhe do filme.
 * Esse fragmento de tela é utilizado nas atividades {@see MovieListActivity} e {@see MovieDetailFragment}
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MovieDetailFragment extends MovieFragmentBase implements View.OnClickListener {
    private static final String  DEBUG_TAG = "MovieDetailFragment: ";

    private boolean isFavorite;

    public MovieDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ( getMovie() != null) {
            isFavorite = FavoriteModel.existsFavoriteMovie(this.getContext(), getMovie().getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Se não for possível recuperar um objeto movie com base na string Json informada no
        //Bundle e parametro MOVIE_JSON_STRING, será exibida uma mensagem no lugar do detalhe do filme.
        View rootView = null;
        Movie movie = super.getMovie();
        if (movie != null) {
            rootView = inflater.inflate(R.layout.movie_detail, container, false);
            ((TextView) rootView.findViewById(R.id.textViewOriginalTitle)).setText(movie.getOriginalTitle());
            ((TextView) rootView.findViewById(R.id.textViewReleaseDateYear)).setText(MovieHelper.getDateFormatted(movie.getReleaseDate(), "yyyy"));
            ((TextView) rootView.findViewById(R.id.textViewReleaseDateDayMonth)).setText(MovieHelper.getDateFormatted(movie.getReleaseDate(), "dd/MM"));
            ((RatingBar) rootView.findViewById(R.id.ratingBarVoteAverage)).setRating(movie.getVoteAverageFiveStars());
            MovieHelper.requestImage(movie.getPosterPath(), (ImageView)rootView.findViewById(R.id.imageView));
            ImageButton imageButtonFavorite = (ImageButton)rootView.findViewById(R.id.imageButtonFavorite);
            setImageButtonFavorite(imageButtonFavorite);
            imageButtonFavorite.setOnClickListener(this);
            ViewPager viewPagerSubDetail = (ViewPager)rootView.findViewById(R.id.viewPagerSubDetail);
            setSubDetail(viewPagerSubDetail, getArguments());
            TabLayout tabLayoutSubDetail = (TabLayout)rootView.findViewById(R.id.tabLayoutSubDetail);
            tabLayoutSubDetail.setupWithViewPager(viewPagerSubDetail);


        } else {
            rootView = inflater.inflate(R.layout.movie_detail_error_message, container, false);
            ((TextView) rootView.findViewById(R.id.textViewErrorMessage)).setText(getText(R.string.error_message_unable_detail_movie));
        }
        return rootView;
    }

    /**
     * Constroí o subdetalhe / páginas ( Overview, Videos e Reviews)  do filme.
     * @param viewPagerSubDetail informar o ViewPager onde as páginas serão adicionadas
     * @param bundle informar o bundle com infromações do filme {@see MovieFragmentBase.getMovie()}
     */
    private void setSubDetail(ViewPager viewPagerSubDetail, Bundle bundle) {
        MovieSubDetailPagerAdapter movieSubDetailPagerAdapter = new MovieSubDetailPagerAdapter(this.getFragmentManager());
        viewPagerSubDetail.setAdapter(movieSubDetailPagerAdapter);
        movieSubDetailPagerAdapter.addFrag(new MovieSubDetailOverviewFragment(), getString(R.string.movie_sub_detail_overview_title) , bundle);
        movieSubDetailPagerAdapter.addFrag(new MovieSubDetailVideosFragment(), getString(R.string.movie_sub_detail_videos_title), bundle);
        movieSubDetailPagerAdapter.addFrag(new MovieSubDetailReviewsFragment(), getString(R.string.movie_sub_detail_reviews_title), bundle);
        movieSubDetailPagerAdapter.notifyDataSetChanged();
    }

    private void setImageButtonFavorite(ImageButton imageButtonFavorite){
        imageButtonFavorite.setImageResource(isFavorite?R.drawable.ic_favorite_yes:R.drawable.ic_favorite_no);
    }

    @Override
    public void onClick(View view) {
        Movie movie = super.getMovie();
        if (movie != null) {
            if (view.getId() == R.id.imageButtonFavorite) {
                if (this.isFavorite) {
                    FavoriteModel.deleteFavoriteMovie(this.getContext(), movie.getId());
                    this.isFavorite = false;
                } else {
                    try {
                        FavoriteModel.setFavoriteMovie(this.getContext(), movie);
                        this.isFavorite = true;
                    } catch (JSONException e) {
                        Log.d(DEBUG_TAG, e.getMessage());
                        Toast.makeText(this.getContext(), R.string.error_can_not_setting_movie_favorite,Toast.LENGTH_LONG).show();
                    }
                }
                setImageButtonFavorite((ImageButton) view);
            }
        }
    }
}
