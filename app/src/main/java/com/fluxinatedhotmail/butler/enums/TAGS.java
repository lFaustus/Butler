package com.fluxinatedhotmail.butler.enums;

/**
 * Created by Fluxi on 11/9/2015.
 */
public class Tags
{

    /**
     * Created by Fluxi on 11/6/2015.
     */
    public enum MapTags
    {
        ORIGIN("ORIGIN"),
        DESTINATION("DESTINATION");
        private String TAG;
        MapTags(String mtag)
        {
            TAG = mtag;
        }

        public String getTag()
        {
            return TAG;
        }


    }

    public enum FragmentActivityTags {

        MAP_FRAGMENT("MapFragment"),
        HOME_FRAGMENT("HomeFragment"),
        NEWS_FRAGMENT("NewsFragment"),

        MAP_ACTIVITY("MapActivity");
        private String TAG;

        FragmentActivityTags(String t)
        {
            TAG = t;
        }
        public String getTAG()
        {
            return TAG;
        }
    }


}
