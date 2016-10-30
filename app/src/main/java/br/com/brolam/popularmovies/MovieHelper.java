package br.com.brolam.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A MovieHelper implementa funcionalidades para faciltar o acesso a Web API, formatação, conversão e etc.
 * É uma class que formece recursos e facilidades gerais para o aplicativo.
 * @see com.squareup.picasso.Picasso
 * @see org.themoviedb.api.v3
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */
public class MovieHelper {

    private static final String  DEBUG_TAG = "MovieHelper:";

    /**
     * Verifica se existe uma conexão válida com a internet.
     * @param context informar um context válido.
     * @return verdadeiro se a conexão for válida.
     */
    public static boolean checkConnection(Context context){
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Informar se o tamanho da tela(w820dp) suporta dois painéis.
     * @param context informar um context válido.
     * @return verdadeiro se o tamanho da tela for maior ou igual a w820dp.
     */
    public static boolean isTwoPane(Context context){
        return context.getResources().getBoolean(R.bool.two_panel);
    }


    /**
     * Executa uma requisição HTTP conforme o Web method e url informado.
     * @param method informar GET, POST ou outros web método suportado no servidor HTTP.
     * @param strUrl informar uma URL válida.
     * @return texto com o retorno do servidor HTTP.
     * @throws Exception gerar um erro se o código de retorno HTTP não estiver entre 200 e 299
     * ou qualquer erro não previsto.
     */
    public static String requestHttp(String method, String strUrl) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(strUrl);
            httpURLConnection  = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(40000 /* milliseconds */);
            httpURLConnection.setConnectTimeout(40000 /* milliseconds */);
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoInput(true);
            Log.d(DEBUG_TAG, String.format("Method / URL : %s / %s ", method ,  strUrl));
            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();

            Log.d(DEBUG_TAG, "The response is: " + responseCode);

            if ( (responseCode >= 200) && ( (responseCode < 300))  ) {
                inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
            } else {
                throw  new Exception(String.format("Http code error : %s",responseCode));
            }


        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if ( httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            } catch (Exception e){
                //Não será considerado erro exceção gerada no fechamento da conexão com o
                //servidor HTTP.
                Log.e (DEBUG_TAG, e.getMessage());
            }
        }
        String stringReturn = stringBuilder.toString();
        Log.d(DEBUG_TAG, stringReturn );
        return stringReturn;
    }

    /**
     * Retonra com uma data válida conforme texto e texto de formatação informado {@see SimpleDateFormat}
     * @param strDate informar data conforme formato do paramentro dateFormat.
     * @param dateFormat texto conforme {@see SimpleDateFormat}
     * @return retorna com uma data válida ou null se a data não for válida.
     */
    public static Date getDate(String strDate, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            Log.d(DEBUG_TAG, String.format("getDate error: %s", e.getMessage()) );
        }
        return date;
    }

    /**
     * Retorna com uma data conforme formato informado no paramento dateFormat {@see SimpleDateFormat}
     * @param date informar uma data válida.
     * @param dateFormat texto conforme {@see SimpleDateFormat}
     * @return texto com a data formatada.
     */
    public static String getDateFormatted(Date date, String dateFormat ){
        return new SimpleDateFormat(dateFormat, Locale.ENGLISH).format(date);
    }

    /**
     * Facilita a recuperação de uma imagem no Picasso {@see com.squareup.picasso.Picasso} ou
     * http://square.github.io/picasso/
     * @param imageName nome da imagem.
     * @param imageView informar um ImageView dentro de um context.
     */
    public static void requestImage(String imageName, ImageView imageView){
        Context context = imageView.getContext();
        String imageUrl = String.format("http://image.tmdb.org/t/p/w185/%s", imageName);
        Picasso.with(context).load(imageUrl).into(imageView);
    }

}
