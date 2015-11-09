package com.fluxinatedhotmail.butler.enums;

/**
 * Created by User on 08/10/2015.
 */
public enum FragmentTags {

    MAP("MapFragment"),
    HOME("HomeFragment"),
    NEWS("NewsFragment");
    private String TAG;

    FragmentTags(String t)
    {
        TAG = t;
    }
    public String getTAG()
    {
        return TAG;
    }
}
