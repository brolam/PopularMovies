package br.com.brolam.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.themoviedb.api.v3.schemes.MovieReview;
import java.util.ArrayList;
import br.com.brolam.popularmovies.R;

/**
 * Adaptador da lista de revisões de um filme.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    /**
     * Interface para definir o evento on click de um item na lista de revisões e
     * quando o usuário acessa o final da lista de revisões.
     */
    public interface IMovieReviewsAdapter {
        /**
         * Quando uma revisões for selecionada.
         *
         * @param movieReview revisão selecionada.
         */
        void onMovieReviewClick(MovieReview movieReview);

        /**
         * Quando a ultima revisão for exibida.
         */
        void onEndOfMovies();
    }

    IMovieReviewsAdapter iMovieReviewsAdapter;
    private ArrayList<MovieReview> movieReviews;

    public MovieReviewsAdapter(IMovieReviewsAdapter iMovieReviewsAdapter) {
        this.iMovieReviewsAdapter = iMovieReviewsAdapter;
        this.movieReviews = new ArrayList<>();
    }

    public void update(ArrayList<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
        this.notifyDataSetChanged();
    }

    public ArrayList<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_review_list_content, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.setPosition(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iMovieReviewsAdapter.onMovieReviewClick(getMovieReview(holder.position));
            }
        });

        if (movieReviews.size() == (position + 1)) {
            this.iMovieReviewsAdapter.onEndOfMovies();
        }
    }

    @Override
    public int getItemCount() {
        if (movieReviews != null) {
            return movieReviews.size();
        }
        return 0;
    }

    /**
     * Recupera uma revisão da lista de revisões
     * @param position informar uma posição válida.
     * @return nulo se a posição for inválida.
     */
    public MovieReview getMovieReview(int position) {
        return ((movieReviews != null) && (movieReviews.size() > position)) ? movieReviews.get(position) : null;
    }

    /**
     * Suporte para construir item na lista de revisões.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textAuthor;
        public TextView textContent;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
            this.textAuthor = (TextView) this.itemView.findViewById(R.id.textAuthor);
            this.textContent = (TextView) this.itemView.findViewById(R.id.textContent);
        }

        public void setPosition(int position) {
            this.position = position;
            MovieReview movieReview = getMovieReview(position);
            if (movieReview != null) {
                this.textAuthor.setText(movieReview.getAuthor());
                this.textContent.setText(movieReview.getContent());
            }
        }
    }
}
