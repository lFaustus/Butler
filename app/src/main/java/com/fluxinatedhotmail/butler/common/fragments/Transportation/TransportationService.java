package com.fluxinatedhotmail.butler.common.fragments.Transportation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fluxinatedhotmail.butler.R;
import com.fluxinatedhotmail.butler.common.adapters.recyclerview.RecyclerViewEndlessScrollListener;
import com.fluxinatedhotmail.butler.common.adapters.recyclerview.TransportationAdapter;
import com.fluxinatedhotmail.butler.common.fragments.BaseFragment;
import com.fluxinatedhotmail.butler.common.models.Car;
import com.fluxinatedhotmail.butler.common.models.Card;

import java.util.ArrayList;

/**
 * Created by Fluxi on 11/9/2015.
 */
public class TransportationService extends BaseFragment implements RecyclerViewEndlessScrollListener.LoadCallback
                                                                    ,RecyclerViewEndlessScrollListener.ScrollCallback
{
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private RecyclerViewEndlessScrollListener mRecyclerViewEndlessScrollListener;
    private TransportationAdapter mTransportationAdapter;
    private ArrayList<Card> mCarList;



    public static TransportationService newInstance(@Nullable String params)
    {

        Bundle args = new Bundle();
        TransportationService fragment = new TransportationService();
        args.putString(FRAGMENT_KEY,fragment.getClass().getName());
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
       View v = inflater.inflate(R.layout.staggeredgridview,container,false);
       this.mRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerView);
       return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        try
        {
            mCarList = savedInstanceState.getParcelableArrayList(this.getClass().getName());
        }catch (NullPointerException exp)
        {
            mCarList = new ArrayList<>();
            int i = 0;
            while(i!=5)
            {
                Car mCar = new Car();
                mCar.setName(""+i);
                Card mCard = new Card(mCar);
                mCarList.add(mCard);
                i++;
            }
        }

        this.mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        this.mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        this.mRecyclerViewEndlessScrollListener = new RecyclerViewEndlessScrollListener(this.mStaggeredGridLayoutManager);
        this.mRecyclerViewEndlessScrollListener.setLoadCallback(this);
        this.mRecyclerViewEndlessScrollListener.setScrollCallback(this);

        this.mTransportationAdapter = new TransportationAdapter(mCarList);
        this.mRecyclerView.setAdapter(this.mTransportationAdapter);
        this.mRecyclerView.setLayoutManager(this.mStaggeredGridLayoutManager);
    }


    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void initializeViews(ViewGroup vg)
    {

    }




    @Override
    public void OnLoadMore(int page)
    {

    }

    @Override
    public void OnScrolled(int FirstVisibleItem, int VisibleItemCount, int TotalItemCount, int LastTotalItemCount)
    {

    }

    @Override
    public void OnScrollStateChanged(int previous_state, int newState)
    {

    }
}
