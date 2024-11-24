package com.actt.actt;

import com.actt.actt.models.*;
import com.actt.actt.utils.AppData;

import java.util.*;

public class TournamentLoader {
    protected List<Driver> drivers;
    private final CarClassSettings[] classes;
    private final TournamentSettings settings;

    public TournamentLoader(TournamentSettings settings) {
        drivers = new ArrayList<>();
        this.settings = settings;
        classes = settings.getClasses();
    }

    public List<Driver> loadTournament(ResultJSONModel[] resultFiles) {
        for (var result : resultFiles) {
            processRace(result);
        }

        return drivers;
    }

    private void processRace(ResultJSONModel race) {
        ResultsJSONSessions[] sessions = race.getSessions();
        List<Driver> raceDrivers = race.getDrivers();
        List<Driver> sortedDrivers = sortDrivers(raceDrivers, sessions);
        Map<CarClassSettings, List<Driver>> driversByClass = createDriversListByClass(sortedDrivers);
        scoreDrivers(driversByClass, race.getTrack(), race.getTrackId());
    }

    private List<Driver> sortDrivers(List<Driver> jsonOrderedDrivers, ResultsJSONSessions[] sessions) {
        List<Driver> sortedDrivers = new ArrayList<>();
        int[] results = getRaceSessionResults(sessions);

        for (int driverIndex : results) {
            Driver driver = getTournamentDriver(jsonOrderedDrivers.get(driverIndex));

            if (driver != null) {
                sortedDrivers.add(driver);
            }
        }

        return sortedDrivers;
    }

    private Map<CarClassSettings, List<Driver>> createDriversListByClass(List<Driver> driversInRace) {
        Map<CarClassSettings, List<Driver>> driversByClass = new HashMap<>();
        for (var carClass : classes) {
            driversByClass.put(carClass, new ArrayList<>());
        }

        for (var raceDriver : driversInRace) {
            driversByClass.forEach((carClass, list) -> {
                if (carClass.isInClass(raceDriver.getCar())) {
                    raceDriver.setCarClass(carClass.getName());
                    list.add(raceDriver);
                }
            });
        }

        return driversByClass;
    }

    private Driver getTournamentDriver(Driver raceDriver) {
        if (!drivers.contains(raceDriver)) {
            drivers.add(raceDriver);
        }

        return Driver.find(drivers, raceDriver.getName());
    }

    private void scoreDrivers(Map<CarClassSettings, List<Driver>> driversByClass, String trackFolderName, String raceId) {
        int[] pointsSystem = settings.getPointsSystem().getPoints();

        driversByClass.forEach((_, list) -> {
            for (int i = 0; i < list.size(); i++) {
                Driver driver = list.get(i);
                DriverResults newResults = new DriverResults();

                int points = 0;
                if (i < pointsSystem.length) {
                    points = pointsSystem[i];
                }

                newResults.setRaceId(raceId);
                newResults.setTrackName(AppData.getTrackName(trackFolderName));
                newResults.setPoints(points);
                newResults.setPosition(i+1);
                driver.addPoints(points);

                driver.addDriverResults(newResults);
            }
        });
    }

    private int[] getRaceSessionResults(ResultsJSONSessions[] sessions) {
        return Arrays.stream(sessions)
                .filter(s -> s.getName().equals("Quick Race") || s.getName().equals("Race"))
                .toList()
                .getFirst()
                .getRaceResult();
    }
}
