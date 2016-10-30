package br.com.brolam.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.themoviedb.api.v3.schemes.Movie;
import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;

/**
 * Adaptador da lista de filmes.
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {


    /**
     * Interface para definir o evento on click de um item na lista de filmes.
     */
    public interface  IMoviesAdapter{
        void onMovieClick(Movie movie);
    }

    IMoviesAdapter iMoviesAdapter;
    private Movie[] movies;

    public MoviesAdapter(IMoviesAdapter iMoviesAdapter) {
        this.iMoviesAdapter = iMoviesAdapter;
        this.movies = new Movie[]{};
    }

    public void update(Movie[] movies){
        this.movies = movies;
        this.notifyDataSetChanged();
    }

    public Movie[] getMovies() {
        return movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = (ImageView)LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setMovie(movies[position]);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               iMoviesAdapter.onMovieClick(holder.movie);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.length;
    }

    /**
     * Suporte para construir item na lista de filmes.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView  imageView;
        public Movie movie;

        public ViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }

        public void setMovie(Movie movie) {
            this.movie = movie;
            MovieHelper.requestImage(this.movie.getPosterPath(), imageView);
        }

        @Override
        public String toString() {
            return movie.getOriginalTitle();
        }
    }
}
