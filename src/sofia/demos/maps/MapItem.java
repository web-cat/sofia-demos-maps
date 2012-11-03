package sofia.demos.maps;

import sofia.maps.ProvidesMarkerContent;
import sofia.maps.ProvidesMarkerGeoPoint;
import sofia.widget.ProvidesTitle;

import com.google.android.maps.GeoPoint;

public class MapItem
{
    private GeoPoint location;
    private String title;
    private String snippet;


    // ----------------------------------------------------------
    public MapItem(float latitude, float longitude, String title,
            String snippet)
    {
        this.location = new GeoPoint(
                (int) (latitude * 10e6), (int) (longitude * 10e6));
        this.title = title;
        this.snippet = snippet;
    }


    // ----------------------------------------------------------
    @ProvidesMarkerGeoPoint
    public GeoPoint getLocation()
    {
        return location;
    }


    // ----------------------------------------------------------
    @ProvidesTitle
    public String getTitle()
    {
        return title;
    }


    // ----------------------------------------------------------
    @ProvidesMarkerContent
    public String getSnippet()
    {
        return snippet;
    }
}
