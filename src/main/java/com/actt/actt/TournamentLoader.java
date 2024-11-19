package com.actt.actt;

import com.actt.actt.models.*;

import java.util.*;

public class TournamentLoader {
    protected List<Driver> drivers;
    private CarClassSettings[] classes;
    private TournamentSettings settings; // TODO is this actually needed?

    public TournamentLoader(TournamentSettings settings) {
        drivers = new ArrayList<>();
        this.settings = settings;
        classes = settings.getClasses();
    }

    // TODO method: score by class (pass SORTED lists of drivers, each list for one class, and apply the scores. First of each class gets full points, second gets p2's points etc.)
    public Object loadTournament(ResultJSONModel[] resultFiles) {
        for (var result : resultFiles) {
            processRace(result);
        }

        return null;
    }

    private void processRace(ResultJSONModel race) {
        List<Driver> raceDrivers = race.getDrivers();
        Map<CarClassSettings, List<Driver>> driversByClass = createDriversListByClass(raceDrivers);
        // TODO score each driver according to their position in the model
    }

    private Map<CarClassSettings, List<Driver>> createDriversListByClass(List<Driver> driversInRace) {
        Map<CarClassSettings, List<Driver>> driversByClass = new HashMap<>();
        for (var carClass : classes) {
            driversByClass.put(carClass, new ArrayList<>());
        }

        for (var raceDriver : driversInRace) {
            Driver tournamentDriver = getTournamentDriver(raceDriver);
            driversByClass.forEach((carClass, list) -> {
                if (carClass.isInClass(tournamentDriver.getCar())) {
                    list.add(tournamentDriver);
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
}
