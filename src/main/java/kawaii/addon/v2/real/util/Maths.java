package kawaii.addon.v2.real.util;

public class Maths {
    public static float randomFloat(float min, float max) {
        return min + (float) Math.random() * (max - min);
    }
}
