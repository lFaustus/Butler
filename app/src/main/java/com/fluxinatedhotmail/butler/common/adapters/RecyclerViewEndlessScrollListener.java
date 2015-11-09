package com.fluxinatedhotmail.butler.common.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by flux on 6/1/15.
 */
public class RecyclerViewEndlessScrollListener extends RecyclerView.OnScrollListener
{
    private final int visibleThreshold = 5;
    private StaggeredGridLayoutManager mStaggeredgrid;
    private int firstVisibleItem, VisibleItemCount, totalItemCount, current_page = 1;
    private static int previousTotal;
    public static final int PREVIOUS_SCROLL_STATE_DEFAULT = -1;
    private int previous_scroll_state = PREVIOUS_SCROLL_STATE_DEFAULT;
    private boolean isLoading = false;
    private ScrollCallback mScrollCallback;
    private LoadCallback mLoadCallback;



    public RecyclerViewEndlessScrollListener(StaggeredGridLayoutManager staggeredgrid)
    {
        mStaggeredgrid = staggeredgrid;
        previousTotal = mStaggeredgrid.getItemCount();
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);
        firstVisibleItem = mStaggeredgrid.findFirstVisibleItemPositions(null)[0];
        VisibleItemCount = recyclerView.getChildCount();
        totalItemCount = mStaggeredgrid.getItemCount();
        current_page = totalItemCount;
        if(mScrollCallback != null)
           mScrollCallback.OnScrolled(firstVisibleItem,VisibleItemCount,totalItemCount,previousTotal);

        /*Log.i("ItemCount", "ItemCount " + totalItemCount);
        Log.i("Childcount", "childcount: " + VisibleItemCount);
        Log.i("FirstPosition", "FirstVisibleItemPosition: " + firstVisibleItem);*/


        /*if ((totalItemCount - VisibleItemCount) <= (firstVisibleItem + visibleThreshold))
        {
            current_page++;
            OnLoadMore(current_page);
        }*/
        if(isLoading)
        {

            if(totalItemCount > previousTotal)
            {
                previousTotal = totalItemCount + 1;
                isLoading = false;
            }

        }

        if(!isLoading &&(firstVisibleItem + VisibleItemCount) >= totalItemCount)
        {
            current_page++;
            Log.e("Current Page",current_page+"");
            isLoading = true;

            if(mLoadCallback != null)
               mLoadCallback.OnLoadMore(current_page);
            /*else
                Log.e("endlessloadcallback","is null");*/
        }
        /*if((VisibleItemCount + firstVisibleItem) >= totalItemCount)
        {
            OnLoadMore(current_page);
            current_page ++;
        }*/


    }
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(mScrollCallback != null)
            mScrollCallback.OnScrollStateChanged(previous_scroll_state,newState);
        previous_scroll_state = newState;
    }

    public void setScrollCallback(ScrollCallback mScrollCallback)
    {
        this.mScrollCallback = mScrollCallback;
    }

    public void setLoadCallback(LoadCallback mLoadCallback)
    {
        this.mLoadCallback = mLoadCallback;
    }

    public interface ScrollCallback
    {
        void OnScrolled(int FirstVisibleItem, int VisibleItemCount, int TotalItemCount, int LastTotalItemCount);
        void OnScrollStateChanged(int previous_state,int newState);
    }

    public interface LoadCallback
    {
        void OnLoadMore(int page);
    }

   // public abstract void OnLoadMore(int page);

    //public abstract void OnScrolled(int FirstVisibleItem, int VisibleItemCount, int TotalItemCount, int LastTotalItemCount);

    //public abstract void OnScrollStateChanged(int previous_state,int newState);

}