package com.example.segev.traveler.Model;

import android.os.AsyncTask;

import com.example.segev.traveler.MyApplication;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class SearchQueryAsyncDao {

    public interface SearchQueryAsyncDaoListener<T>{
        void onComplete(T data);
    }

    public static void getTopThreeQueries(final SearchQueryAsyncDaoListener<List<SearchQuery>> listener) {
        class MyAsyncTask extends AsyncTask<String,String,List<SearchQuery>>{
            @Override
            protected List<SearchQuery> doInBackground(String... strings) {
                List<SearchQuery> queriesList = AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().getAllSearchQueries();

                Collections.sort(queriesList, new Comparator<SearchQuery>() {
                    @Override
                    public int compare(SearchQuery o1, SearchQuery o2) {
                        return o2.getSearchesAmount() - o1.getSearchesAmount();
                    }
                });

                if(queriesList.size() > 3)
                    return queriesList.subList(0,3);
                else{
                    return (queriesList);
                }
            }

            @Override
            protected void onPostExecute(List<SearchQuery> queries) {
                super.onPostExecute(queries);
                listener.onComplete(queries);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }


    public static void insertSearchQuery(final SearchQueryAsyncDaoListener<Boolean> listener,SearchQuery searchQuery){
        class MyAsyncTask extends AsyncTask<List<SearchQuery>,String,Boolean>{
            @Override
            protected Boolean doInBackground(List<SearchQuery>... queries) {
                for (SearchQuery query : queries[0]) {
                    AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().insertSearchQuery(query);
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                listener.onComplete(success);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        LinkedList<SearchQuery> temp = new LinkedList<>();
        temp.add(searchQuery);
        task.execute(temp);
    }

    static public void getSearchQueryByQuery(final SearchQueryAsyncDaoListener<SearchQuery> listener, final String query) {
        class MyAsyncTask extends AsyncTask<String,String,SearchQuery>{
            @Override
            protected SearchQuery doInBackground(String... strings) {
                return AppLocalDb.getsInstance(MyApplication.context).searchQueryDao().getSearchQueryByLocation(query);
            }

            @Override
            protected void onPostExecute(SearchQuery searchQuery) {
                super.onPostExecute(searchQuery);
                listener.onComplete(searchQuery);
            }
        }
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }
}
