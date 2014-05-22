package com.example.androidmaps;

import java.util.ArrayList;

import sofia.app.OptionsMenu;
import sofia.gps.LocationTracker;
import sofia.maps.MapItem;
import sofia.maps.MapScreen;
import sofia.maps.Route;
import android.location.Location;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.androidmaps.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Contains examples demonstrating the new Sofia functionality added for Google
 * Maps API v2
 * 
 * @author Cameron Wyatt
 * @author Tyler Lenig
 * @version 2014.5.7
 */
@OptionsMenu
public class DemoLauncher extends MapScreen {

	public GoogleMap myMap;
	private MapItem previouslyClicked = null;
	private ArrayList<Route> routes = null;

	public void initialize() {
		// Get the GoogleMap object
		myMap = getMap(R.id.map);

		// ArrayList used to hold the Route objects that are currently on the
		// map
		routes = new ArrayList<Route>();

		// Create 4 MapItem objects
		// Each marker takes a latitude and a longitude
		// Once created, there are optional attributes that can be set on each
		// MapItem such as the title, icon, visibility, etc.
		MapItem theWhiteHouse = new MapItem(38.897889, -77.036509);
		theWhiteHouse.setTitle("The White House");

		MapItem capitol = new MapItem(38.890135, -77.009061);
		capitol.setTitle("The Capitol Building");

		MapItem washingtonMonument = new MapItem(38.889617, -77.035237);
		washingtonMonument.setTitle("The Washington Monument");

		MapItem lincoln = new MapItem(38.889423, -77.050165);
		lincoln.setTitle("The Lincoln Memorial");

		// To actually display the MapItem on the map, you must call addMapItem
		// The first parameter is the MapItem object to add
		// The second parameter is whether the additional details of the MapItem
		// should be shown immediately, such as the snippet or icon
		// The details of only one MapItem may be displayed at a time, so this
		// should only be true once
		// The third parameter, which is optional, determines whether the map is
		// zoomed to center on this point
		// The fourth parameter, which is optional, determines the level of zoom
		// for the map
		addMapItem(theWhiteHouse, true, true, 15);
		addMapItem(capitol, false);
		addMapItem(washingtonMonument, false);
		addMapItem(lincoln, false);
	}

	private boolean drawRoute = false;

	/**
	 * Called when "Draw Driving Route between 2 points" menu option is clicked
	 * 
	 * @param menuItem
	 */
	public void drawRouteClicked(MenuItem menuItem) {
		// This boolean variable will be checked every time onMapItemClicked is
		// called to see if the user wants to draw a route between two MapItem
		// points
		this.drawRoute = true;
	}

	/**
	 * Called whenever a MapItem object is clicked
	 * 
	 * @param item
	 */
	public void mapItemWasClicked(MapItem item) {
		// If the user has chosen to draw a route between points
		if (this.drawRoute == true) {
			// Keep track of the first MapItem that was clicked
			if (previouslyClicked == null) {
				previouslyClicked = item;
			} else {
				// If this is the second MapItem that was clicked, create a
				// Route object containing the previouslyClicked MapItem and the
				// MapItem that was just clicked
				Route myRoute = new Route(previouslyClicked, item);

				// Tell drawRoute that we want the mode to be driving
				// The other option is MapScreen.MODE_WALKING for walking
				// directions
				String mode = MapScreen.MODE_DRIVING;
				try {
					// Try to draw the Route object on the map with the selected
					// mode
					drawRoute(myRoute, mode);
					routes.add(myRoute);
					this.drawRoute = false;
				} catch (Exception e) {
					Toast.makeText(this, "Unable to draw route between points",
							Toast.LENGTH_SHORT).show();
				}
				// Update previouslyClicked
				previouslyClicked = null;
			}
		} else {
			// If the user has not chosen to draw a route between two points
			// Place custom logic here
			// Currently, show a toast that displays the MapItem's title
			Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
		}
	}

	private boolean trackingUser = false;
	private Route trackingRoute = null;

	/**
	 * Called whenever the user long-presses the Map
	 * 
	 * @param point
	 */
	public void mapWasLongClicked(LatLng point) {
		// Create a MapItem at the point that the user long-pressed and add it
		// to the map
		MapItem newMapItem = new MapItem(point.latitude, point.longitude);
		addMapItem(newMapItem, false);

		// Get the user's my recent location
		Location lastLocation = getLastLocation();

		// Create a MapItem based on this location
		MapItem mapItemLastLocation = new MapItem(lastLocation.getLatitude(),
				lastLocation.getLongitude());

		// Create a route from the user's current location
		// (mapItemLastLocation) and the location they long-pressed
		// (newMapItem)
		Route myRoute = new Route(mapItemLastLocation, newMapItem);
		try {
			drawRoute(myRoute, MapScreen.MODE_WALKING);
			routes.add(myRoute);
			this.trackingRoute = myRoute;
			this.trackingUser = true;

			// Turn on location tracking so that locationDidChange will be
			// called
			// Request location updates every 30 seconds
			LocationTracker lt = new LocationTracker();
			lt.setPriority(LocationTracker.PRIORITY_HIGH_ACCURACY);
			lt.setInterval(30000);
			getLocationUpdates(lt);
		} catch (Exception e) {
			Toast.makeText(this, "Unable to draw route between points",
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Called when the LocationTracker updates with a new Location
	 * 
	 * @param location
	 */
	public void locationDidChange(Location location) {
		// If the user has chosen to draw Route objects between their current
		// location and the location on the map that was long-pressed
		if (this.trackingUser == true) {
			// Get the endLocation and the current location
			MapItem endLocation = this.trackingRoute.getDestination();
			MapItem newSourceLocation = new MapItem(location.getLatitude(),
					location.getLongitude());

			// If you are more than 5 meters away from the target location,
			// remove the old Route and draw a new one
			if (distanceBetween(newSourceLocation, endLocation) > 5.0) {
				removeRoute(this.trackingRoute);
				routes.remove(this.trackingUser);
				Route updatedRoute = new Route(newSourceLocation, endLocation);
				try {
					drawRoute(updatedRoute, MapScreen.MODE_WALKING);
					routes.add(updatedRoute);
					this.trackingRoute = updatedRoute;
					Toast.makeText(
							this,
							"Distance = "
									+ distanceBetween(newSourceLocation,
											endLocation)  + "m", Toast.LENGTH_SHORT)
							.show();
				} catch (Exception e) {
					Toast.makeText(this, "Unable to draw route between points",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				// If you are within 5 meters of the target location, you have
				// arrived and we can stop tracking the user
				Toast.makeText(this, "You have arrived at your destination",
						Toast.LENGTH_SHORT).show();
				this.trackingUser = false;
			}
		}
	}
}
