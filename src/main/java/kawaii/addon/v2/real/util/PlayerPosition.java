package kawaii.addon.v2.real.util;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class PlayerPosition {

    private int floor(double value) {
        return (int) Math.floor(value);
    }

    public int getX() {
        assert mc.player != null;
        return floor(mc.player.getX());
    }

    public int getY() {
        assert mc.player != null;
        return floor(mc.player.getY());
    }

    public int getZ() {
        assert mc.player != null;
        return floor(mc.player.getZ());
    }

    public String getDimension() {
        assert mc.level != null;
        return mc.level.dimension().identifier().getPath();
    }
}
