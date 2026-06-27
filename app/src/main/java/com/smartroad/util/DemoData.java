package com.smartroad.util;

import com.smartroad.model.Hazard;

import java.util.ArrayList;
import java.util.List;

/**
 * Built-in sample data used when ApiClient.DEMO_MODE is true, so the app is
 * fully demonstrable without a live backend. Remove or ignore once your
 * server is connected.
 */
public class DemoData {

    public static List<Hazard> sampleHazards() {
        List<Hazard> list = new ArrayList<>();
        list.add(make("1", "Pothole", "Large pothole causing traffic congestion.",
                "3.1390", "101.6869", "New", "Ali Ahmad"));
        list.add(make("2", "Flood", "Road flooded after heavy rain.",
                "3.1421", "101.6890", "Under Investigation", "Siti Aminah"));
        list.add(make("3", "Traffic Accident", "Minor collision blocking left lane.",
                "3.1365", "101.6845", "New", "Ravi Kumar"));
        list.add(make("4", "Fallen Tree", "Tree down across the road after storm.",
                "3.1410", "101.6820", "Resolved", "Mei Ling"));
        list.add(make("5", "Damaged Road Sign", "Stop sign knocked over at junction.",
                "3.1378", "101.6905", "Under Investigation", "Daniel Tan"));
        list.add(make("6", "Broken Traffic Light", "Traffic light not working at crossroads.",
                "3.1402", "101.6862", "New", "Nurul Huda"));
        return list;
    }

    private static Hazard make(String id, String type, String desc,
                               String lat, String lng, String status, String reporter) {
        Hazard h = new Hazard();
        h.setId(id);
        h.setType(type);
        h.setDescription(desc);
        h.setLatitude(lat);
        h.setLongitude(lng);
        h.setStatus(status);
        h.setReporter(reporter);
        h.setDatetime("14/06/2026 10:30");
        return h;
    }
}
