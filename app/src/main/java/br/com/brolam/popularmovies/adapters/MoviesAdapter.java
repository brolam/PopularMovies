package br.com.brolam.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import org.themoviedb.api.v3.schemes.Movie;

import java.util.ArrayList;

import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;

/**
 * Adaptador da lista de filmes.
 * @author Breno Marques
 * @version 1.00
 * @since Release 02
 * Udacity Review
 * SUGGESTION
 * Não é uma boa prática "armazenar" um objeto/entidade no ViewHolder. A proposta do view holder é
 *justamente manter apenas os dados/informações que são relevantes aos componentes de tela, ok?

 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    /**
     * Interface para definir o evento on click de um item na lista de filmes e
     * quando o usuário acessa o final da lista de filmes.
     */
    public interface  IMoviesAdapter{
        /**
         * Quando um filme for selecionado.
         * @param movie filme selecionado.
         */
        void onMovieClick(Movie movie);

        /**
         * Quando o ultimo filme for exibido.
         */
        void onEndOfMovies();
    }

    IMoviesAdapter iMoviesAdapter;
    private ArrayList<Movie> movies;

    public MoviesAdapter(IMoviesAdapter iMoviesAdapter) {
        this.iMoviesAdapter = iMoviesAdapter;
        this.movies = new ArrayList<>();
    }

    public void update(ArrayList<Movie> movies){
        this.movies = movies;
        this.notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovies() {
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

        holder.setPosition(position);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMoviesAdapter.onMovieClick(getMovie(holder.position));
            }
        });

        if (movies.size() == (position + 1)) {
            this.iMoviesAdapter.onEndOfMovies();
        }
    }

    @Override
    public int getItemCount() {
        /**
         * Udacity Review
         * SUGGESTION
         * Você consegue garantir que movies nunca será null?
         * Se não consegue garantir, te sugiro a seguinte implementação para o método em questão:
         */
        if (movies != null) {
            return movies.size();
        }
        return 0;


    }

    /**
     * Recupera um filme da lista de filmes
     * @param position informar uma posição válida.
     * @return nulo se a posição for inválida.
     */
    public Movie getMovie(int position ){
        return  (( movies !=null ) && ( movies.size() > position))? movies.get(position) : null;
    }

    /**
     * Suporte para construir item na lista de filmes.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView  imageView;
        public int position;

        public ViewHolder(ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }

        public void setPosition(int position) {
            this.position = position;
            Movie movie = getMovie(position);
            if(movie != null ) {
                MovieHelper.requestImage(movie.getPosterPath(), imageView);
            } else {
                imageView.setImageDrawable(null);
            }
        }
    }
}
