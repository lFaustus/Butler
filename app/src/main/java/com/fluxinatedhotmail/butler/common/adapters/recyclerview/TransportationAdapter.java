package com.fluxinatedhotmail.butler.common.adapters.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fluxinatedhotmail.butler.R;
import com.fluxinatedhotmail.butler.common.adapters.recyclerview.viewholders.BaseViewHolder;
import com.fluxinatedhotmail.butler.common.adapters.recyclerview.viewholders.TransportationViewHolder;
import com.fluxinatedhotmail.butler.common.models.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fluxi on 11/9/2015.
 */
public class TransportationAdapter extends RecyclerView.Adapter<BaseViewHolder>
{

    private ArrayList<Card> Cards;

    public TransportationAdapter(){}

    public TransportationAdapter(List<Card> array)
    {
        this.Cards = (ArrayList<Card>) array;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cards,parent,false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position)
    {
        if(holder instanceof TransportationViewHolder)
        {
            TransportationViewHolder mHolder = (TransportationViewHolder) holder;
            mHolder.mVehicleName.setText("Vehicle " + position);
            Cards.get(position).setPosition(position);
        }
    }

    @Override
    public int getItemCount()
    {
        return this.Cards.size();
    }
}
