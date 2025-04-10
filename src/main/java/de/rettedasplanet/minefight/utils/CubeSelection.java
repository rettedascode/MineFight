package de.rettedasplanet.minefight.utils;

import org.bukkit.Location;

public class CubeSelection {

    private Location pointOne;
    private Location pointTwo;

    public Location getPointOne() {
        return pointOne;
    }

    public void setPointOne(Location pointOne) {
        this.pointOne = pointOne;
    }

    public Location getPointTwo() {
        return pointTwo;
    }

    public void setPointTwo(Location pointTwo) {
        this.pointTwo = pointTwo;
    }

    public boolean isComplete() {
        return pointOne != null && pointTwo != null;
    }
}
