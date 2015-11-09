package com.fluxinatedhotmail.butler.common.adapters.recyclerview.viewholders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fluxinatedhotmail.butler.R;

/**
 * Created by Fluxi on 11/9/2015.
 */
public class TransportationViewHolder extends BaseViewHolder implements View.OnClickListener
{

    public TextView mVehicleName;
    public Button mGetVehicle;
    public TransportationViewHolder(View itemView)
    {
        super(itemView);
        mVehicleName = (TextView)itemView.findViewById(R.id.card_vehicle_name);
        mGetVehicle = (Button)itemView.findViewById(R.id.card_getVehicle);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.card_getVehicle:

                break;
        }
    }
}
