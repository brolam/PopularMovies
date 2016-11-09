package org.themoviedb.api.v3.schemes.base;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A ItemBase declara o construtor e método obrigatório de um item recuperado na api do TheMovieDd, veja https://developers.themoviedb.org/3.
 * @author Breno Marques
 * @version 1.00
 * @since Release 03
 */
public abstract class ItemBase {

    /**
     * Constrói um objeto com base em um objeto JSON.
     * Favor redefinir esse construtor na nova classe
     * @param jsonObject informar um objeto JSON válido.
     * @throws JSONException
     */
    public ItemBase(JSONObject jsonObject) throws JSONException {};

    /**
     * Converte um item em um objeto JSON.
     * Favor redefinir esse método na nova classe.
     * @return retornar a um objeto JSON válido.
     * @throws JSONException
     */
    public JSONObject getJSONObject() throws JSONException {
        throw  new UnsupportedOperationException("Not implemented");
    }
}
