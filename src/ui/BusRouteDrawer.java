package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.*;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A bus route drawer
public class BusRouteDrawer extends MapViewOverlay {
    /**
     * overlay used to display bus route legend text on a layer above the map
     */
    private BusRouteLegendOverlay busRouteLegendOverlay;
    /**
     * overlays used to plot bus routes
     */
    private List<Polyline> busRouteOverlays;

    /**
     * Constructor
     *
     * @param context the application context
     * @param mapView the map view
     */
    public BusRouteDrawer(Context context, MapView mapView) {
        super(context, mapView);
        busRouteLegendOverlay = createBusRouteLegendOverlay();
        busRouteOverlays = new ArrayList<>();
    }

    /**
     * Plot each visible segment of each route pattern of each route going through the selected stop.
     */
    public void plotRoutes(int zoomLevel) {
        // in the method that returns all the routes one for loop
        // then for every route you get there is a pattern one for loop
        // every pattern has a path --> which means it has geopoints
        // for loop for every points
        // for every two point (in List<lit<Geopoints>) you get you check if its in the rectangle
        Boolean continuousline = false;
        LatLon lastLocation = new LatLon(99.9,99.9);
        Stop stop = StopManager.getInstance().getSelected();
        updateVisibleArea();
        busRouteOverlays.clear();
        busRouteLegendOverlay.clear();
        if (!(stop == null)) {
            for (Route route: stop.getRoutes()) {
                List<GeoPoint> geoPoints = new ArrayList<>();
                busRouteLegendOverlay.add(route.getNumber());
                for (RoutePattern routePattern: route.getPatterns()) {
                    if (!(geoPoints.size() == 0)) {
                        polylinehelper(geoPoints,busRouteLegendOverlay.getColor(route.getNumber()), zoomLevel);
                        geoPoints.clear();
                    }
                    for (LatLon location: routePattern.getPath()) {
                        if (routePattern.getPath().get(0) == location)
                            lastLocation = location;
                        else if (Geometry.rectangleIntersectsLine(northWest,southEast,lastLocation,location)) {
                            GeoPoint currentGeoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                            GeoPoint previousGeoPoint = new GeoPoint(lastLocation.getLatitude(),lastLocation.getLongitude());
                            if (!geoPoints.contains(previousGeoPoint))
                                geoPoints.add(currentGeoPoint);
                                geoPoints.add(previousGeoPoint);
                                lastLocation = location;
                            if (!(geoPoints.size() == 0)) {
                                if (continuousline) {
                                    polylinehelper(geoPoints,busRouteLegendOverlay.getColor(route.getNumber()), zoomLevel);
                                    geoPoints.clear();
                                    continuousline = false;
                                }
                            }
                        } else {
                            continuousline = true;
                            lastLocation = location;
                        }
                    }

                }
            }
        }
    }
    private void polylinehelper(List<GeoPoint> points, int colorNumber, int zoomLevel) {
        Polyline polyline = new Polyline(new DefaultResourceProxyImpl(mapView.getContext()));
        polyline.setPoints(points);
        polyline.setColor(colorNumber);
        polyline.setWidth(getLineWidth(zoomLevel));
        polyline.isVisible();
        busRouteOverlays.add(polyline);
    }
//        for (Route r: StopManager.getInstance().getSelected().getRoutes()) {
//            busRouteLegendOverlay.add(r.getNumber());
//
//            for (RoutePattern rp: r.getPatterns()) {
//                for (LatLon latLon: rp.getPath()) {
//                    if (rp.getPath().get(0) == latLon)
//                        LatLon prevlocation = new Lat
//                }
//
//            }


 //           Polyline p = new Polyline(context);
 //           p.setColor(busRouteLegendOverlay.getColor(r.getNumber()));

        //TODO: complete the implementation of this method (Task 7)


    public List<Polyline> getBusRouteOverlays() {
        return Collections.unmodifiableList(busRouteOverlays);
    }

    public BusRouteLegendOverlay getBusRouteLegendOverlay() {
        return busRouteLegendOverlay;
    }


    /**
     * Create text overlay to display bus route colours
     */
    private BusRouteLegendOverlay createBusRouteLegendOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);
        return new BusRouteLegendOverlay(rp, BusesAreUs.dpiFactor());
    }

    /**
     * Get width of line used to plot bus route based on zoom level
     *
     * @param zoomLevel the zoom level of the map
     * @return width of line used to plot bus route
     */
    private float getLineWidth(int zoomLevel) {
        if (zoomLevel > 14) {
            return 7.0f * BusesAreUs.dpiFactor();
        } else if (zoomLevel > 10) {
            return 5.0f * BusesAreUs.dpiFactor();
        } else {
            return 2.0f * BusesAreUs.dpiFactor();
        }
    }
}
