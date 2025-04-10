package de.rettedasplanet.minefight.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PointsManager {

    private static final Map<UUID, Integer> points = new HashMap<>();

    public static void addPoints(UUID uuid, int pts) {
        int currentPoints = points.getOrDefault(uuid, 0);
        points.put(uuid, currentPoints + pts);
    }

    public static void removePoints(UUID uuid, int pts) {
        int currentPoints = points.getOrDefault(uuid, 0);
        points.put(uuid, currentPoints - pts);
    }

    public static void setPoints(UUID uuid, int pts) {
        points.put(uuid, pts);
    }

    public static int getPoints(UUID uuid) {
        return points.getOrDefault(uuid, 0);
    }
}
