package br.com.brolam.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.themoviedb.api.v3.schemes.Movie;
import org.themoviedb.api.v3.schemes.MovieVideo;

import java.util.ArrayList;

import br.com.brolam.popularmovies.MovieHelper;
import br.com.brolam.popularmovies.R;

/**
 * Adaptador da lista de vídeos de um filmes.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.ViewHolder> {

    /**
     * Interface para definir o evento on click de um item na lista de vídeos
     */
    public interface  IMovieVideosAdapter{
        /**
         * Quando um vídeo for selecionado.
         * @param movieVideo vídeo selecionado.
         */
        void onMovieVideoClick(MovieVideo movieVideo);
    }

    IMovieVideosAdapter iMovieVideosAdapter;
    private ArrayList<MovieVideo> movieVideos;

    public MovieVideosAdapter(IMovieVideosAdapter iMovieVideosAdapter) {
        this.iMovieVideosAdapter = iMovieVideosAdapter;
        this.movieVideos = new ArrayList<>();
    }

    public void update(ArrayList<MovieVideo> movieVideos){
        this.movieVideos = movieVideos;
        this.notifyDataSetChanged();
    }

    public ArrayList<MovieVideo> getMovieVideos() {
        return movieVideos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_video_list_content, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        if (movieVideos != null) {
            return movieVideos.size();
        }
        return 0;
    }

    /**
     * Recupera um vídeo da lista de vídeos
     * @param position informar uma posição válida.
     * @return nulo se a posição for inválida.
     */
    public MovieVideo getMovieVideo(int position ){
        return  (( movieVideos !=null ) && ( movieVideos.size() > position))? movieVideos.get(position) : null;
    }


    /**
     * Suporte para construir item na lista de vídeos.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imageButtonVideoPlay;
        public TextView textVideoName;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageButtonVideoPlay = (ImageButton)itemView.findViewById(R.id.imageButtonVideoPlay);
            this.textVideoName = (TextView) itemView.findViewById(R.id.textVideoName);
        }

        public void setPosition(int position) {
            this.position = position;
            final MovieVideo movieVideo = getMovieVideo(position);
            if(movieVideo != null ) {
                this.textVideoName.setText(movieVideo.getName());
            }

            this.imageButtonVideoPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iMovieVideosAdapter.onMovieVideoClick(getMovieVideo(ViewHolder.this.position));
                }
            });
        }
    }
}
