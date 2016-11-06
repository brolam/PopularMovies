package br.com.brolam.popularmovies.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.themoviedb.api.v3.schemes.Movie;
import org.themoviedb.api.v3.schemes.MoviePage;

import java.util.ArrayList;
import java.util.Date;


/**
 * A FavoriteModel é responsável pelas operações CRUD da tabela favorite_movies do banco de dados
 * SQLite master.db
 * @author Breno Marques
 * @version 1.00
 * @since Release 02
 * OBSERVAÇÃO: O objetivo é somente armazenar os filmes favoritos, sendo importante destacar, que
 * as informações do filmes são fornecidas pela API do www.themoviedb.org, sendo assim, somente será
 * armazenado o ID do filme é a string JSON com as informações do filme retornada pela API.
 */
public class FavoriteModel {
    private static final String  DEBUG_TAG = "FavoriteModel:";

    public static abstract class Fields implements BaseColumns {
        public static final String TABLE_NAME = "favorite_movies";
        public static final String MOVIE_JSON_STRING = "movieJsonString";
        public static final String CREATE_DATE = "createDate";

        public static String getSQLCreate() {
            return String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY , %s TEXT, %s LONG)",
                    TABLE_NAME,
                    _ID,
                    MOVIE_JSON_STRING,
                    CREATE_DATE
            );
        }

        public static String[] getFields() {
            return new String[]{_ID, MOVIE_JSON_STRING, CREATE_DATE};
        }

    }

    /**
     * A FavoriteMovie é uma extensão da {@see Movie} e contém as informações de um filme
     * favorito.
     */
    public static class FavoriteMovie extends Movie {
        /**
         * Informa a data em que o filme foi definido como favorito.
         */
        private long createDate;

        public FavoriteMovie(String movieJsonString, long createDate) throws JSONException {
            super(new JSONObject(movieJsonString));
            this.createDate = createDate;
        }
    }

    /**
     * Método base para retornar com os filmes favoritos por ordem decrescente da data em que
     * o filme foi definido com favorito.
     * @param context informar um contexto válido.
     * @param where informar null para todos.
     * @param params informar os values para o parâmentro where ou informar null para todos.
     * @param limit informar o limite da quantidade de linhas ou null para todos.
     * @return lista de filmes favoritos.
     */
    private static ArrayList<Movie>  getMovies(Context context, String where, String[] params, String limit) {
        SQLiteDatabase sqLiteDatabase = new DatabaseHelper(context).getReadableDatabase();
        try {
            ArrayList<Movie> favoriteMovies = new ArrayList<>();
            String order = String.format("%s DESC", Fields.CREATE_DATE);
            Cursor cursor = sqLiteDatabase.query(Fields.TABLE_NAME, Fields.getFields(), where, params, null, null, order, limit);
            try {
                if (cursor.moveToFirst()) {
                    int IX_MOVIE_JSON_STRING = cursor.getColumnIndex(Fields.MOVIE_JSON_STRING);
                    int IX_CREATE_DATE = cursor.getColumnIndex(Fields.CREATE_DATE);
                    do {
                        String movieJonString = cursor.getString(IX_MOVIE_JSON_STRING);

                        try {
                            FavoriteMovie favoriteMovie = new FavoriteMovie(movieJonString, cursor.getLong(IX_CREATE_DATE));
                            favoriteMovies.add(favoriteMovie);
                        } catch (JSONException e) {
                            Log.e (DEBUG_TAG, e.getMessage());
                        }

                    } while (cursor.moveToNext());

                }

            } finally {
                cursor.close();
            }
            return favoriteMovies;

        } finally {
            sqLiteDatabase.close();
        }
    }

    /**
     * Retorna com todos os filmes favoritos por ordem decrescente da data em que
     * o filme foi definido com favorito.
     * @param context informar um contexto válido.
     * @return uma {@see MoviePage} com uma lista de filmes favoritos.
     */
    public static MoviePage getMoviePage(Context context){
        MoviePage moviePage = new MoviePage();
        SQLiteDatabase sqLiteDatabase = new DatabaseHelper(context).getReadableDatabase();
        try {
            ArrayList<Movie> movies = getMovies(context,null,null,null);
            moviePage.addMovies(1, movies);
        } finally {
            sqLiteDatabase.close();
        }
        return moviePage;
    }


    /**
     * Informa se o filme está na lista de filmes favoritos.
     * @param context informar um contexto válido.
     * @param movieId informar o ID do filme.
     * @return verdadeiro se o filme estiver na lista de filmes favoritos.
     */
    public static boolean existsFavoriteMovie(Context context, long movieId) {
        SQLiteDatabase sqLiteDatabase = new DatabaseHelper(context).getReadableDatabase();
        try {
            return existsFavoriteMovie(sqLiteDatabase, movieId);
        } finally {
            sqLiteDatabase.close();
        }
    }

    /**
     * Método base para informar se o filme está na lista de filmes favoritos.
     * OBSERVAÇÃO: esse método não fecha a conexão com a banco de dados, sendo assim,
     * o processo principal de deve ser responsável por fechar a conexão com o banco de dados
     * informada no parâmento sqLiteDatabase.
     * @param sqLiteDatabase informar uma conexão com o banco de dados válidas
     * @param movieId informar o ID do filme.
     * @return verdadeiro se o filme estiver na lista de filmes favoritos.
     */
    private static boolean existsFavoriteMovie(SQLiteDatabase sqLiteDatabase, long movieId) {
        Cursor cursor = sqLiteDatabase.query(
                Fields.TABLE_NAME,
                new String[]{Fields._ID},
                String.format("%s=?", Fields._ID),
                new String[]{String.valueOf(movieId)},
                null,
                null,
                null);
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }


    /**
     * Salva ou atualiza um filme na lista de filmes favoritos.
     * @param context informar um contexto válido.
     * @param movie informar um filme válido.
     * @throws JSONException
     */
    public static void setFavoriteMovie(Context context, Movie movie) throws JSONException {
        SQLiteDatabase sqLiteDatabase = new DatabaseHelper(context).getReadableDatabase();
        try {

            ContentValues favoriteMovieValues = new ContentValues();
            favoriteMovieValues.put(Fields.MOVIE_JSON_STRING, movie.getJsonString());
            favoriteMovieValues.put(Fields.CREATE_DATE, new Date().getTime());

            if (existsFavoriteMovie(sqLiteDatabase, movie.getId())) {
                sqLiteDatabase.update(Fields.TABLE_NAME, favoriteMovieValues, String.format("%s=?", Fields._ID),
                        new String[]{String.valueOf(movie.getId())});
            } else {
                favoriteMovieValues.put(Fields._ID, movie.getId());
                sqLiteDatabase.insertOrThrow(Fields.TABLE_NAME, null, favoriteMovieValues);
            }
        } finally {
            sqLiteDatabase.close();
        }

    }

    /**
     * Remove um filme da lista de filmes favoritos.
     * @param context informar um contexto válido.
     * @param movieId informar o ID do filme.
     */
    public static void deleteFavoriteMovie(Context context, long movieId) {
        SQLiteDatabase sqLiteDatabase = new DatabaseHelper(context).getReadableDatabase();
        try {
            sqLiteDatabase.delete(Fields.TABLE_NAME,
                    String.format("%s=?", Fields._ID),
                    new String[]{String.valueOf(movieId)});

        } finally {
            sqLiteDatabase.close();
        }
    }
}
