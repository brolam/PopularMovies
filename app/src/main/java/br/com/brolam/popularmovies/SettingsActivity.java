package br.com.brolam.popularmovies;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import org.themoviedb.api.v3.TheMovieDb;

import java.util.List;


/**
 * A SettingsActivity permite definir as preferências do usuário conforme guia de configurações em
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 * @see PreferenceActivity
 * @see AppCompatPreferenceActivity
 * @author Breno Marques
 * @version 1.00
 * @since Release 01
 */

public class SettingsActivity extends AppCompatPreferenceActivity {
    public static final String KEY_THEMOVIEDB_API_ORDER  = "key_themoviedb_api_order";


    /**
     * Recuperar a ordem de exibição dos filmes conforme a preferência definida pelo usuário.
     * @param context informar um contexto válido
     * @return um TheMovieDb.Order válido ou um TheMovieDb.DEFAULT_ORDER se o usuário ainda não definiu uma preferência.
     */
    public static TheMovieDb.Order getTheMovieDbApiOrderValue(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String stringValue = preferences.getString(KEY_THEMOVIEDB_API_ORDER, null);
        TheMovieDb.Order order = stringValue == null?  TheMovieDb.DEFAULT_ORDER : TheMovieDb.Order.valueOf(stringValue);
        return order;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue =  value.toString();
            String title = stringValue;
            if ( preference.getKey().equals(KEY_THEMOVIEDB_API_ORDER) ){
                TheMovieDb.Order order = stringValue.isEmpty()?  TheMovieDb.DEFAULT_ORDER : TheMovieDb.Order.valueOf(stringValue);
                title = order.getTitle(preference.getContext());
            }
            preference.setSummary(title);
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //Definir RESULT_OK para atualizar a lista de filmes no onActivityResult de MovieListActivity
            setResult(RESULT_OK);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //Definir RESULT_OK para atualizar a lista de filmes no onActivityResult de MovieListActivity
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);
            //Os valores e títulos serão definidas com base em {@see TheMovieDb.Order}
            ListPreference listPreferenceTheMovieApiOrder = (ListPreference)findPreference(KEY_THEMOVIEDB_API_ORDER);
            TheMovieDb.Order[] orders = TheMovieDb.Order.values();
            String[] values = new String[orders.length];
            String[] titles = new String[orders.length];
            for(int index = 0; index < orders.length; index++ ){
                values[index] = orders[index].toString();
                titles[index] = orders[index].getTitle(listPreferenceTheMovieApiOrder.getContext());
            }
            listPreferenceTheMovieApiOrder.setEntries(titles);
            listPreferenceTheMovieApiOrder.setEntryValues(values);
            listPreferenceTheMovieApiOrder.setDefaultValue(TheMovieDb.DEFAULT_ORDER);
            bindPreferenceSummaryToValue(listPreferenceTheMovieApiOrder);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }

}
