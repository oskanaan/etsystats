package com.meccaartwork.etsystats;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.meccaartwork.etsystats.async.LoadRankChangesAsyncTask;
import com.meccaartwork.etsystats.helper.PreferenceNameHelper;

public class RankChangeFragment extends Fragment {

  private static final String ARG_COLUMN_COUNT = "column-count";
  private int mColumnCount = 1;
  private View root;
  private View loadingPanel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getArguments() != null) {
      mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    root = inflater.inflate(R.layout.fragment_rankchange_list, container, false);
    loadingPanel = root.findViewById(R.id.loadingPanel);

    new LoadRankChangesAsyncTask(getContext(), root, loadingPanel).execute();

    final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayout);
    refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refreshLayout.setRefreshing(false);
        new LoadRankChangesAsyncTask(getContext(), root, loadingPanel).execute();
      }
    });
    return root;
  }

  @Override
  public void onResume() {
    super.onResume();
    if(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(PreferenceNameHelper.getRankChangeIndicatorName(), false)){
      PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PreferenceNameHelper.getRankChangeIndicatorName(), false).commit();
      new LoadRankChangesAsyncTask(getContext(), root, loadingPanel).execute();
    }
  }
}
