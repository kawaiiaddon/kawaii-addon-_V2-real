package kawaii.addon.v2.real.util;

//very secrete!!!!!!!!!!!!!!!!!!!!!!!!
public class MathSecret {

    public static float transform(float seed, float multiplier) {
        return seed * multiplier;
    }

    public static float RandomTransform(float seed) {
        return (float) (seed * 77769420 * Math.random());
    }
}
