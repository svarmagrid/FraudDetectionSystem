package org.example.service;

import org.example.model.Location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class LocationService {

    public static Location getLocation() {
        try {
            URL url = new URL("http://ip-api.com/line/?fields=city,regionName,country");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String city = reader.readLine();
            String state = reader.readLine();
            String country = reader.readLine();

            reader.close();

            return new Location(0, city, state, country);

        } catch (Exception e) {
            return new Location(0, "Unknown", "Unknown", "Unknown");
        }
    }
}