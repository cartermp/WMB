package com.jmstudios.corvallistransit.routeTools;

import android.util.Xml;

import com.jmstudios.corvallistransit.models.BusRoute;
import com.jmstudios.corvallistransit.models.BusRouteStop;
import com.jmstudios.corvallistransit.models.BusStop;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phillip on 12/27/13.
 */
public class CtsXmlParser {
    // Don't use namespaces.
    private static final String ns = null;

    /**
     * Parses CTS Bus Route information from XML feeds.
     *
     * @param in Input stream of XML data.
     * @return A list of Bus Routes.
     * @throws org.xmlpull.v1.XmlPullParserException
     * @throws java.io.IOException
     */
    public List parseRouteInfo(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, "UTF-8");
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, "RoutePattern");

            // Totally hacked, but gets me where I need to go for now...
            // Ignoring the <Content> tag
            parser.nextTag();
            parser.nextTag();
            parser.nextTag();
            return readPatterns(parser);
        } finally {
            in.close();
        }
    }

    /**
     * Parses the ETA of a given Bus Route Stop from the CTS XML feed.
     *
     * @param in          Input stream of XML data.
     * @param stopClosure the Bus Route Stop whose eta will be updated.
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void parseStopEta(InputStream in, BusRouteStop stopClosure) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, "RoutePositionET");

            // Ignoring the <Content> tag, which apparently requires three calls to nextTag()
            parser.nextTag();
            parser.nextTag();
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, ns, "Platform");

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }

                String name = parser.getName();

                if (name.equals("Route")) {
                    /*
                     * Only grab the ETA for the route we care about; ETAs are updated
                     * on a per-route basis for now.
                     */
                    String routeName = parser.getAttributeValue(null, "RouteNo");
                    if (routeName != null && routeName.equals(stopClosure.routeNo)) {
                        stopClosure = parseRouteEta(parser, stopClosure);
                    } else {
                        skip(parser);
                    }
                } else {
                    skip(parser);
                }
            }
        } finally {
            in.close();
        }
    }

    /**
     * From within the CTS XML feed, parses the Eta tag out from the Route tag.
     *
     * @param parser      the XmlPullParser that parses the XML feed.
     * @param stopClosure the Bus Route Stop whose eta will be updated.
     * @return the updated Bus Route Stop.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BusRouteStop parseRouteEta(XmlPullParser parser, BusRouteStop stopClosure) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Route");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("Destination")) {
                stopClosure = getEta(parser, stopClosure);
            } else {
                skip(parser);
            }
        }

        return stopClosure;
    }

    /**
     * From the CTS XML feed, parses the ETA from the Destination tag.
     *
     * @param parser      the XmlPullParser that parses the XML feed.
     * @param stopClosure the Bus Route Stop whose eta will be updated.
     * @return the updated Bus Route Stop.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BusRouteStop getEta(XmlPullParser parser, BusRouteStop stopClosure) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Destination");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("Trip")) {
                stopClosure.eta = Integer.parseInt(parser.getAttributeValue(null, "ETA"));
            } else {
                skip(parser);
            }
        }

        return stopClosure;
    }

    /**
     * Reads the RoutePattern XML data for bus routes.
     *
     * @param parser the XmlPullParser that parses the XML feed.
     * @return A list of Bus Routes.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<BusRoute> readPatterns(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<BusRoute> busRoutes = new ArrayList<BusRoute>();

        parser.require(XmlPullParser.START_TAG, ns, "Project");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if (name.equals("Route")) {
                busRoutes.add(readRoute(parser));
            } else {
                skip(parser);
            }
        }

        return busRoutes;
    }

    /**
     * Skips any tags we don't "care" about.
     *
     * @param parser the XmlPullParser that parses the XML feed.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     * Parses a Bus Route from the XML feed.
     *
     * @param parser The XmlPullParser that reads the XML feed.
     * @return a new Bus Route.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BusRoute readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Route");

        BusRoute route = new BusRoute();
        route.routeNumber = parser.getAttributeValue(null, "RouteNo");
        route.name = parser.getAttributeValue(null, "Name");
        route.stopList = new ArrayList<BusRouteStop>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("Destination")) {
                route = readDestination(parser, route);
            } else {
                skip(parser);
            }
        }

        return route;
    }

    private BusRoute readDestination(XmlPullParser parser, BusRoute route) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Destination");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("Pattern")) {
                route = readRoutePattern(parser, route);
            } else {
                skip(parser);
            }
        }

        return route;
    }

    /**
     * Parses a Route Pattern for a given Bus Route.
     *
     * @param parser the XmlPullParser that parses the XML feed.
     * @param route  The Bus Route whose Route Pattern will be parsed.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BusRoute readRoutePattern(XmlPullParser parser, BusRoute route) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Pattern");
        route.routeTimeWarning = parser.getAttributeValue(null, "Schedule").equals("Active");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("Platform")) {
                parser.require(XmlPullParser.START_TAG, ns, "Platform");
                if (!containsStopWithTag(route.stopList, parser.getAttributeValue(null, "PlatformTag"))) {
                    BusRouteStop brs = readPlatform(parser);
                    brs.routeNo = route.routeNumber;
                    route.stopList.add(brs);
                } else {
                    parser.nextTag();
                }
            } else {
                skip(parser);
            }
        }

        return route;
    }

    /**
     * Parses a Bus Route Stop (platform in the feed).
     *
     * @param parser the XmlPullParser that parses the XML feed.
     * @return A new Bus Route Stop.
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BusRouteStop readPlatform(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Platform");
        BusRouteStop brs = new BusRouteStop();

        BusStop stop = new BusStop();
        stop.stopTag = parser.getAttributeValue(null, "PlatformTag");
        stop.stopNumber = parser.getAttributeValue(null, "PlatformNo");
        stop.address = parser.getAttributeValue(null, "Name");

        brs.stopModel = stop;
        brs.stopTag = stop.stopTag;

        // Need to move the parser to the next tag whenever we actually parse something out.
        parser.nextTag();
        parser.require(XmlPullParser.END_TAG, ns, "Platform");
        return brs;
    }

    /**
     * Helper method to determine if a route's list of stops
     * contains a stop with the specified tag.
     *
     * @param stops   the list of stops for a given route.
     * @param stopTag the stop to check
     * @return true if a stop with the specified tag is found; otherwise, false.
     */
    private static boolean containsStopWithTag(List<BusRouteStop> stops, String stopTag) {
        if (stopTag == null || stopTag.isEmpty()
                || stops == null || stops.isEmpty()) {
            return false;
        }

        for (BusRouteStop stop : stops) {
            if (stop != null && stop.stopTag != null && stop.stopTag.equals(stopTag)) {
                return true;
            }
        }

        return false;
    }
}
