package de.rettedasplanet.minefight.manager;

import de.rettedasplanet.minefight.utils.CubeSelection;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class CubeSelectionManager {

    private static final HashMap<UUID, CubeSelection> selections = new HashMap<>();

    public static void setPointOne(UUID uuid, Location loc) {
        CubeSelection selection = selections.getOrDefault(uuid, new CubeSelection());
        selection.setPointOne(loc);
        selections.put(uuid, selection);
    }

    public static void setPointTwo(UUID uuid, Location loc) {
        CubeSelection selection = selections.getOrDefault(uuid, new CubeSelection());
        selection.setPointTwo(loc);
        selections.put(uuid, selection);
    }

    public static CubeSelection getSelection(UUID uuid) {
        return selections.get(uuid);
    }

    public static void clearSelection(UUID uuid) {
        selections.remove(uuid);
    }
}
