package br.com.brolam.popularmovies;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.themoviedb.api.v3.TheMovieDb;
import org.themoviedb.api.v3.schemes.Movie;
import org.themoviedb.api.v3.schemes.MoviePage;
import org.themoviedb.api.v3.schemes.MovieReviewsPage;
import org.themoviedb.api.v3.schemes.MovieVideosPage;
import br.com.brolam.popularmovies.models.FavoriteModel;
import static org.junit.Assert.*;

/**
 * Executa os casos de testes em um Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AndroidCasesTest {
    /**
     * Caso de teste TheMovieDb: Teste de todos os web métodos utilizados na API {@see TheMovieDb}
     * @throws Exception
     */
    @Test
    public void  case1TheMovieDb() throws Exception {
        // Recupera o contexto da aplicação.
        Context appContext = InstrumentationRegistry.getTargetContext();

        //Testa o web método Get movies
        MoviePage moviePage = getMoviesTest(appContext);

        //Recupera o primeiro filme para ser utilizadas no teste dos web metodos abaixo:
        Movie firstMovie = moviePage.getItems().get(0);
        assertNotNull("The first movie is invalid", firstMovie);

        //Testa o web método Get movie videos.
        MovieVideosPage movieVideosPage = TheMovieDb.getMovieVideos(appContext, firstMovie.getId());
        assertFalse(String.format("Get movie videos exception: %s", movieVideosPage.getException()), movieVideosPage.isException());
        assertNotEquals(String.format("The video list of the movie (%s)  is empty!", firstMovie.getOriginalTitle()), movieVideosPage.getItems().size(),0);

        //Testa o web método Get movie reviews
        MovieReviewsPage movieReviewsPage = TheMovieDb.getMovieReviews(appContext, firstMovie.getId(), 1);
        assertFalse(String.format("Get movie reviews exception: %s", movieReviewsPage.getException()), movieReviewsPage.isException());
        assertNotEquals(String.format("The reviews list of the movie (%s)  is empty!", firstMovie.getOriginalTitle()), movieReviewsPage.getItems().size(),0);

    }


    /**
     * Caso de teste filmes Favoritos : Teste da inclusão, Alteração e exclusão na lista de filmes favoritos.
     * @throws Exception
     */
    @Test
    public void case2FavoriteMovies() throws Exception {
        // Recupera o contexto da aplicação.
        Context appContext = InstrumentationRegistry.getTargetContext();
        //Testa o web método Get movies
        MoviePage moviePage = getMoviesTest(appContext);

        //Todos os filmes serão adicionados a lista de filmes favoritos.
        for(Movie movie: moviePage.getItems()){
            FavoriteModel.setFavoriteMovie(appContext, movie);
        }

        //A quantidade de filmes favorite não pode ser diferente da quantidade de filmes.
        MoviePage favoriteMoviePage = FavoriteModel.getMoviePage(appContext);
        assertFalse(String.format("Get favorites movies exception: %s", favoriteMoviePage.getException()), favoriteMoviePage.isException());
        int amountMovies = moviePage.getItems().size();
        int amountFavoriteMovies = favoriteMoviePage.getItems().size();
        assertNotEquals("The amount of favorite movies is different from the amount of movies!", amountFavoriteMovies, amountMovies);

        //Todos os filmes serão alterados na lista de filmes favoritos.
        for(Movie movie: moviePage.getItems()){
            FavoriteModel.setFavoriteMovie(appContext, movie);
        }

        //Atualiza a lista de filmes favoritos após as alterações.
        favoriteMoviePage = FavoriteModel.getMoviePage(appContext);
        amountFavoriteMovies = favoriteMoviePage.getItems().size();
        assertNotEquals("After update the amount of favorite movies is different from the amount of movies!", amountFavoriteMovies, amountMovies);

        //Todos os filmes serão removidos da lista de filmes favoritos.
        for(Movie movie: moviePage.getItems()){
            FavoriteModel.deleteFavoriteMovie(appContext, movie.getId());
        }
        assertNotEquals("All favorite movies were not removed!", amountFavoriteMovies, amountMovies);


    }

    /**
     * Testa o web método Get movies
     * @param appContext
     * @return a primeira página da lista de filmes ou uma exceção.
     */
    public MoviePage getMoviesTest(Context appContext){
        MoviePage moviePage = TheMovieDb.getMovies(appContext, TheMovieDb.DEFAULT_ORDER, 1);
        assertFalse(String.format("Get movies exception: %s", moviePage.getException()), moviePage.isException());
        assertNotEquals("The movie list is empty!", moviePage.getItems().size(),0);
        return moviePage;

    }

}
