package com.casii.droid.booklistingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "&key=AIzaSyCA43vMPQRih3-0_Dwpn9p2P2Ab02QZIZ0";
    private static final String QUERY = "QUERY";
    private static final String LIST_STATE = "LIST_STATE";
    private String query = "android";
    private String URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private ListView listView;
    private Adapter adapter;
    private ProgressDialog progress;
    private Parcelable state;
    private boolean refresh;
    private TextView textView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listview);
        if (savedInstanceState != null) {
            query = savedInstanceState.getString(QUERY);
        }

        refresh = true;
        List<Book> books = new ArrayList<>();
        adapter = new Adapter(this, books);
        textView = (TextView) findViewById(R.id.message);
        if (isConnectedToInternet()) {
            showProgressBar();
            AsyncTask task = new AsyncTask();
            task.execute();
            showMessage("");
            listView.setVisibility(View.VISIBLE);
        } else {
            showMessage(getString(R.string.no_network));
        }
    }

    private void showMessage(String message) {
        textView.setText(message);
        listView.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(QUERY, query);
        state = listView.onSaveInstanceState();
        savedInstanceState.putParcelable(LIST_STATE, state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        state = savedInstanceState.getParcelable(LIST_STATE);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isConnectedToInternet()) {
                    setQuery(query);
                    showProgressBar();
                    AsyncTask task = new AsyncTask();
                    task.execute();
                } else {
                    showMessage(getString(R.string.no_network));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void updateUI(List<Book> books) {
        adapter = new Adapter(this, books);
        listView.setAdapter(adapter);
        setScrollPosition();
        adapter.setNotifyOnChange(true);
        adapter.notifyDataSetChanged();
    }

    private void setScrollPosition() {
        if (state != null & refresh) {
            listView.onRestoreInstanceState(state);
            refresh = false;
        }
    }

    private void setQuery(String query) {
        this.query = query;
    }

    private void showProgressBar() {
        progress = new ProgressDialog(MainActivity.this, R.style.DialogStyle);
        progress.setMessage(getString(R.string.loading_message));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo nInfo = connectivity.getActiveNetworkInfo();
            if (nInfo != null && nInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    private class AsyncTask extends android.os.AsyncTask<String, List<Book>, List<Book>> {

        @Override
        protected List<Book> doInBackground(String... strings) {
            // Create URL object
            URL url = createUrl(URL + query + API_KEY);
            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse;
            jsonResponse = makeHttpRequest(url);
            List<Book> books = extractFeatureFromJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null) {
                return;
            }
            if (books.size() == 0) {
                showMessage(getString(R.string.no_books));
                return;
            }
            showMessage("");
            listView.setVisibility(View.VISIBLE);
            updateUI(books);
            try {
                progress.dismiss();
            } catch (IllegalArgumentException e) {
            }
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
            }
            return url;
        }

        private String makeHttpRequest(URL url) {
            String jsonResponse = "";
            HttpURLConnection urlConnection;
            InputStream inputStream;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            String out = "";
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    out = out + line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return out;
        }

        private List<Book> extractFeatureFromJson(String bookJSON) {
            List<Book> books = new ArrayList<>();
            try {
                JSONObject root = new JSONObject(bookJSON);
                JSONArray items = root.getJSONArray("items");
                if (items.length() > 0) {
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        JSONObject book = item.getJSONObject("volumeInfo");
                        String title = book.getString("title");
                        String author = "";
                        String description = "-";
                        try {
                            author = book.getString("authors");
                        } catch (JSONException e) {
                        }
                        try {
                            author = book.getString("publisher");
                        } catch (Exception e) {
                        }
                        description = book.getString("description");
                        books.add(new Book(title, author, description));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progress.dismiss();
            }
            return books;
        }
    }
}
