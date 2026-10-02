package kawaii.addon.v2.real.modules;

//made by capman_505 on dc.(ai generated for sure)
//but we ball

import kawaii.addon.v2.real.KawaiiAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.CameraType;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class ChinaHat extends Module {
    public enum CosmeticType { Chinese_Hat, Halo, Cat_Ears }

    // How far below the top of the head the head "pivots" (used so ears follow head pitch)
    private static final double HEAD_PIVOT_DROP = 0.45;

    // ── Groups ──────────────────────────────────────────────────
    private final SettingGroup sgGeneral       = settings.getDefaultGroup();
    private final SettingGroup sgHat           = settings.createGroup("Chinese Hat");
    private final SettingGroup sgAfterimage    = settings.createGroup("Afterimage");
    private final SettingGroup sgParticles     = settings.createGroup("Particles");
    private final SettingGroup sgParticleMove  = settings.createGroup("Particle Movement");
    private final SettingGroup sgParticleColor = settings.createGroup("Particle Color");
    private final SettingGroup sgHalo          = settings.createGroup("Halo");
    private final SettingGroup sgCatEars       = settings.createGroup("Cat Ears");

    // ── General ──────────────────────────────────────────────────
    private final Setting<CosmeticType> cosmeticType = sgGeneral.add(new EnumSetting.Builder<CosmeticType>()
        .name("cosmetic-type")
        .description("Which cosmetic to show. Switch between Chinese Hat, Halo and Cat Ears.")
        .defaultValue(CosmeticType.Chinese_Hat)
        .build()
    );

    // ── Chinese Hat ──────────────────────────────────────────────
    private final Setting<Boolean> hatEnabled = sgHat.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Show the Chinese hat on your head.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat)
        .build()
    );
    private final Setting<Boolean> rainbow = sgHat.add(new BoolSetting.Builder()
        .name("rainbow")
        .description("Makes the hat cycle through all colors of the rainbow.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get())
        .build()
    );
    private final Setting<Double> rainbowSpeed = sgHat.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("How fast the hat changes color.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get() && rainbow.get())
        .build()
    );
    private final Setting<SettingColor> coneColorSetting = sgHat.add(new ColorSetting.Builder()
        .name("color")
        .description("The color of the hat. Only visible when rainbow is off.")
        .defaultValue(new SettingColor(200, 160, 60, 220))
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get() && !rainbow.get())
        .build()
    );
    private final Setting<Double> coneRadiusSetting = sgHat.add(new DoubleSetting.Builder()
        .name("radius")
        .description("How wide the bottom of the hat is.")
        .defaultValue(0.5).min(0.01).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get())
        .build()
    );
    private final Setting<Double> coneHeightSetting = sgHat.add(new DoubleSetting.Builder()
        .name("height")
        .description("How tall the hat is from bottom to tip.")
        .defaultValue(0.35).min(0.01).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get())
        .build()
    );
    private final Setting<Double> yOffsetSetting = sgHat.add(new DoubleSetting.Builder()
        .name("y-offset")
        .description("Moves the hat up or down on your head.")
        .defaultValue(-0.025).min(-2.0).max(2.0).sliderMin(-2.0).sliderMax(2.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get())
        .build()
    );
    private final Setting<Boolean> rimOutline = sgHat.add(new BoolSetting.Builder()
        .name("rim-outline")
        .description("Draws an outline around the bottom edge of the hat.")
        .defaultValue(false)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get())
        .build()
    );
    private final Setting<Double> rimThickness = sgHat.add(new DoubleSetting.Builder()
        .name("rim-thickness")
        .description("How thick the rim outline is.")
        .defaultValue(0.025).min(0.005).max(0.15).sliderMax(0.15)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get() && rimOutline.get())
        .build()
    );
    private final Setting<Boolean> rimRainbow = sgHat.add(new BoolSetting.Builder()
        .name("rim-rainbow")
        .description("Rim outline cycles through rainbow colors.")
        .defaultValue(false)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get() && rimOutline.get())
        .build()
    );
    private final Setting<Double> rimRainbowSpeed = sgHat.add(new DoubleSetting.Builder()
        .name("rim-rainbow-speed")
        .description("How fast the rim color cycles.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get() && rimOutline.get() && rimRainbow.get())
        .build()
    );
    private final Setting<SettingColor> rimColorSetting = sgHat.add(new ColorSetting.Builder()
        .name("rim-color")
        .description("Color of the rim outline when rainbow is off.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get() && rimOutline.get() && !rimRainbow.get())
        .build()
    );

    // ── Afterimage ───────────────────────────────────────────────
    private final Setting<Boolean> afterimage = sgAfterimage.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Leaves fading ghost copies behind you as you move.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> afterimageCount = sgAfterimage.add(new IntSetting.Builder()
        .name("count")
        .description("How many ghost copies trail behind you.")
        .defaultValue(8).min(1).max(20).sliderMax(20)
        .visible(afterimage::get)
        .build()
    );
    private final Setting<Double> afterimageInterval = sgAfterimage.add(new DoubleSetting.Builder()
        .name("interval")
        .description("How often a new ghost is recorded. Lower = denser trail.")
        .defaultValue(0.05).min(0.01).max(0.3).sliderMax(0.3)
        .visible(afterimage::get)
        .build()
    );
    private final Setting<Double> afterimageFadeSpeed = sgAfterimage.add(new DoubleSetting.Builder()
        .name("fade-speed")
        .description("How quickly ghost copies fade out.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(afterimage::get)
        .build()
    );
    private final Setting<Boolean> afterimageRainbow = sgAfterimage.add(new BoolSetting.Builder()
        .name("rainbow")
        .description("Each ghost copy cycles through rainbow colors.")
        .defaultValue(false)
        .visible(afterimage::get)
        .build()
    );
    private final Setting<Boolean> afterimageRainbowSync = sgAfterimage.add(new BoolSetting.Builder()
        .name("sync-with-cosmetic")
        .description("Afterimage rainbow follows the same color as the active cosmetic.")
        .defaultValue(false)
        .visible(() -> afterimage.get() && afterimageRainbow.get())
        .build()
    );
    private final Setting<SettingColor> afterimageColorSetting = sgAfterimage.add(new ColorSetting.Builder()
        .name("color")
        .description("Color of ghost copies when rainbow is off.")
        .defaultValue(new SettingColor(200, 160, 60, 100))
        .visible(() -> afterimage.get() && !afterimageRainbow.get())
        .build()
    );

    // ── Particles (Chinese Hat only) ─────────────────────────────
    private final Setting<Boolean> particles = sgParticles.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Spawns star shaped particles that float around the hat.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat)
        .build()
    );
    private final Setting<Integer> particleCount = sgParticles.add(new IntSetting.Builder()
        .name("count")
        .description("How many star particles exist at once.")
        .defaultValue(30).min(1).max(30).sliderMax(30)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Double> particleSize = sgParticles.add(new DoubleSetting.Builder()
        .name("size")
        .description("How big each star particle is.")
        .defaultValue(0.03).min(0.01).max(0.2).sliderMax(0.2)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Boolean> particleFade = sgParticles.add(new BoolSetting.Builder()
        .name("fade-out")
        .description("Stars slowly become transparent before disappearing.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Boolean> particleShrink = sgParticles.add(new BoolSetting.Builder()
        .name("shrink")
        .description("Stars slowly shrink in size before disappearing.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );

    // ── Particle Movement ────────────────────────────────────────
    private final Setting<Double> particleLifetime = sgParticleMove.add(new DoubleSetting.Builder()
        .name("lifetime")
        .description("How many seconds each star lives.")
        .defaultValue(1.5).min(0.2).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Double> particleRiseSpeed = sgParticleMove.add(new DoubleSetting.Builder()
        .name("rise-speed")
        .description("How fast stars float upward.")
        .defaultValue(0.006).min(0.001).max(0.05).sliderMax(0.05)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );

    @SuppressWarnings("unused")
    private final Setting<Double> particleRise = sgParticleMove.add(new DoubleSetting.Builder()
        .name("rise-height")
        .description("How high stars float up before fading.")
        .defaultValue(0.3).min(0.05).max(2.0).sliderMax(2.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Double> particleDriftSpeed = sgParticleMove.add(new DoubleSetting.Builder()
        .name("drift-speed")
        .description("How fast stars drift outward.")
        .defaultValue(0.003).min(0.0).max(0.02).sliderMax(0.02)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Double> particleRotationSpeed = sgParticleMove.add(new DoubleSetting.Builder()
        .name("spin-speed")
        .description("How fast each star spins.")
        .defaultValue(0.05).min(0.0).max(0.3).sliderMax(0.3)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Double> particleSpawnRadius = sgParticleMove.add(new DoubleSetting.Builder()
        .name("spawn-radius")
        .description("How far from center stars can spawn.")
        .defaultValue(0.8).min(0.0).max(2.0).sliderMax(2.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Double> particleSpawnHeight = sgParticleMove.add(new DoubleSetting.Builder()
        .name("spawn-height")
        .description("Where on the hat stars spawn. 0 = bottom, 1 = tip.")
        .defaultValue(0.5).min(0.0).max(1.0).sliderMax(1.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );

    // ── Particle Color ───────────────────────────────────────────
    private final Setting<Boolean> particleRainbow = sgParticleColor.add(new BoolSetting.Builder()
        .name("rainbow")
        .description("Stars cycle through rainbow colors.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get())
        .build()
    );
    private final Setting<Boolean> particleRainbowSync = sgParticleColor.add(new BoolSetting.Builder()
        .name("sync-with-hat")
        .description("Particle rainbow follows the same color as the hat.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get() && particleRainbow.get())
        .build()
    );
    private final Setting<SettingColor> particleColorSetting = sgParticleColor.add(new ColorSetting.Builder()
        .name("color")
        .description("Color of stars when rainbow is off.")
        .defaultValue(new SettingColor(255, 255, 255, 220))
        .visible(() -> cosmeticType.get() == CosmeticType.Chinese_Hat && particles.get() && !particleRainbow.get())
        .build()
    );

    // ── Halo ─────────────────────────────────────────────────────
    private final Setting<Boolean> halo = sgHalo.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Renders a glowing ring floating above your head.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo)
        .build()
    );
    private final Setting<Double> haloRadius = sgHalo.add(new DoubleSetting.Builder()
        .name("radius")
        .description("How wide the halo ring is.")
        .defaultValue(0.4).min(0.1).max(2.0).sliderMax(2.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Double> haloThickness = sgHalo.add(new DoubleSetting.Builder()
        .name("thickness")
        .description("How thick the halo ring is.")
        .defaultValue(0.04).min(0.01).max(0.2).sliderMax(0.2)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Double> haloYOffset = sgHalo.add(new DoubleSetting.Builder()
        .name("y-offset")
        .description("How high above your head the halo floats.")
        .defaultValue(0.07).min(-0.5).max(1.5).sliderMax(1.5)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Double> haloTilt = sgHalo.add(new DoubleSetting.Builder()
        .name("tilt")
        .description("How much the halo tilts backward. 0 = flat, 90 = vertical.")
        .defaultValue(15.0).min(0.0).max(90.0).sliderMax(90.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Double> haloSpinSpeed = sgHalo.add(new DoubleSetting.Builder()
        .name("spin-speed")
        .description("How fast the halo spins. 0 = no spin.")
        .defaultValue(0.5).min(0.0).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Boolean> haloRainbow = sgHalo.add(new BoolSetting.Builder()
        .name("rainbow")
        .description("Halo cycles through rainbow colors.")
        .defaultValue(false)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Double> haloRainbowSpeed = sgHalo.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("How fast the halo color cycles.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get() && haloRainbow.get())
        .build()
    );
    private final Setting<SettingColor> haloColorSetting = sgHalo.add(new ColorSetting.Builder()
        .name("color")
        .description("Color of the halo when rainbow is off.")
        .defaultValue(new SettingColor(255, 0, 0, 255))
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get() && !haloRainbow.get())
        .build()
    );
    private final Setting<Boolean> haloPulse = sgHalo.add(new BoolSetting.Builder()
        .name("pulse")
        .description("Halo slowly pulses in and out.")
        .defaultValue(false)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get())
        .build()
    );
    private final Setting<Double> haloPulseSpeed = sgHalo.add(new DoubleSetting.Builder()
        .name("pulse-speed")
        .description("How fast the halo pulses.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get() && haloPulse.get())
        .build()
    );
    private final Setting<Double> haloPulseAmount = sgHalo.add(new DoubleSetting.Builder()
        .name("pulse-amount")
        .description("How much the halo grows and shrinks when pulsing.")
        .defaultValue(0.05).min(0.01).max(0.3).sliderMax(0.3)
        .visible(() -> cosmeticType.get() == CosmeticType.Halo && halo.get() && haloPulse.get())
        .build()
    );

    // ── Cat Ears ─────────────────────────────────────────────────
    private final Setting<Boolean> earsEnabled = sgCatEars.add(new BoolSetting.Builder()
        .name("enabled")
        .description("Show cat ears on your head.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears)
        .build()
    );
    private final Setting<Boolean> earRainbow = sgCatEars.add(new BoolSetting.Builder()
        .name("rainbow")
        .description("Ears cycle through rainbow colors.")
        .defaultValue(false)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earRainbowSpeed = sgCatEars.add(new DoubleSetting.Builder()
        .name("rainbow-speed")
        .description("How fast the ears change color.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get() && earRainbow.get())
        .build()
    );
    private final Setting<SettingColor> earColorSetting = sgCatEars.add(new ColorSetting.Builder()
        .name("color")
        .description("Color of the ears when rainbow is off.")
        .defaultValue(new SettingColor(55, 55, 60, 240))
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get() && !earRainbow.get())
        .build()
    );
    private final Setting<Boolean> earInner = sgCatEars.add(new BoolSetting.Builder()
        .name("inner-ear")
        .description("Draws a colored patch inside the front of each ear.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<SettingColor> earInnerColorSetting = sgCatEars.add(new ColorSetting.Builder()
        .name("inner-color")
        .description("Color of the inner ear patch.")
        .defaultValue(new SettingColor(255, 140, 170, 240))
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get() && earInner.get())
        .build()
    );
    private final Setting<Double> earHeight = sgCatEars.add(new DoubleSetting.Builder()
        .name("height")
        .description("How tall each ear is.")
        .defaultValue(0.22).min(0.05).max(1.0).sliderMax(1.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earWidth = sgCatEars.add(new DoubleSetting.Builder()
        .name("width")
        .description("How wide the base of each ear is.")
        .defaultValue(0.18).min(0.05).max(0.6).sliderMax(0.6)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earThickness = sgCatEars.add(new DoubleSetting.Builder()
        .name("thickness")
        .description("How thick each ear is from front to back.")
        .defaultValue(0.07).min(0.02).max(0.3).sliderMax(0.3)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earSpacing = sgCatEars.add(new DoubleSetting.Builder()
        .name("spacing")
        .description("How far each ear sits from the center of your head.")
        .defaultValue(0.15).min(0.0).max(0.5).sliderMax(0.5)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earTilt = sgCatEars.add(new DoubleSetting.Builder()
        .name("tilt")
        .description("How far the ear tips lean outward in degrees. Negative leans inward.")
        .defaultValue(12.0).min(-45.0).max(45.0).sliderMin(-45.0).sliderMax(45.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earYOffset = sgCatEars.add(new DoubleSetting.Builder()
        .name("y-offset")
        .description("Moves the ears up or down on your head.")
        .defaultValue(-0.02).min(-0.5).max(0.5).sliderMin(-0.5).sliderMax(0.5)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earForwardOffset = sgCatEars.add(new DoubleSetting.Builder()
        .name("forward-offset")
        .description("Moves the ears toward the front (+) or back (-) of your head.")
        .defaultValue(0.0).min(-0.3).max(0.3).sliderMin(-0.3).sliderMax(0.3)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Boolean> earFollowPitch = sgCatEars.add(new BoolSetting.Builder()
        .name("follow-pitch")
        .description("Ears tilt with your head when you look up and down.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Boolean> earTwitch = sgCatEars.add(new BoolSetting.Builder()
        .name("twitch")
        .description("Ears randomly twitch every few seconds.")
        .defaultValue(true)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get())
        .build()
    );
    private final Setting<Double> earTwitchSpeed = sgCatEars.add(new DoubleSetting.Builder()
        .name("twitch-speed")
        .description("How often the ears twitch.")
        .defaultValue(1.0).min(0.1).max(5.0).sliderMax(5.0)
        .visible(() -> cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get() && earTwitch.get())
        .build()
    );

    // ── Internal state ───────────────────────────────────────────
    private final List<StarParticle> activeParticles = new ArrayList<>();
    private final Deque<AfterimageFrame> afterimageFrames = new ArrayDeque<>();
    private final Random random = new Random();
    private long startTime = -1;
    private double lastFrameTime = 0;
    private double haloAngle = 0;

    public ChinaHat() {
        super(KawaiiAddon.CATEGORY, "cosmetic-hat", "Renders cosmetics on your player. Switch between Chinese Hat, Halo and Cat Ears.");
    }

    @Override
    public void onActivate() {
        startTime = System.currentTimeMillis();
        activeParticles.clear();
        afterimageFrames.clear();
        lastFrameTime = 0;
        haloAngle = 0;

        for (int i = 0; i < particleCount.get(); i++) {
            float lifetime = (float)(particleLifetime.get() * (0.7 + random.nextDouble() * 0.6));
            float startLife = random.nextFloat() * lifetime;
            activeParticles.add(new StarParticle(
                0, 0, 0, 0, 0, 0,
                startLife, lifetime,
                (float)(random.nextDouble() * Math.PI * 2),
                random.nextFloat()
            ));
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.player == null) return;
        if (mc.options.getCameraType() == CameraType.FIRST_PERSON) return;

        long now = System.currentTimeMillis();
        if (startTime < 0) startTime = now;
        double elapsed = (now - startTime) / 1000.0;

        double x = mc.player.xOld + (mc.player.getX() - mc.player.xOld) * event.tickDelta;
        double y = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * event.tickDelta + mc.player.getBbHeight() + yOffsetSetting.get();
        double z = mc.player.zOld + (mc.player.getZ() - mc.player.zOld) * event.tickDelta;
        double bodyY = mc.player.yOld + (mc.player.getY() - mc.player.yOld) * event.tickDelta;
        double headY = bodyY + mc.player.getBbHeight();
        double headYaw = Math.toRadians(mc.player.getYHeadRot());

        float coneRadius = coneRadiusSetting.get().floatValue();
        float coneHeight = coneHeightSetting.get().floatValue();

        // ── Chinese Hat ───────────────────────────────────────────
        if (cosmeticType.get() == CosmeticType.Chinese_Hat && hatEnabled.get()) {
            Color coneColor;
            if (rainbow.get()) {
                float hue = (float)((elapsed * rainbowSpeed.get() * 0.1) % 1.0);
                int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                java.awt.Color c = new java.awt.Color(rgb);
                coneColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 220);
            } else {
                coneColor = coneColorSetting.get();
            }

            Color rimColor = null;
            if (rimOutline.get()) {
                if (rimRainbow.get()) {
                    float hue = (float)((elapsed * rimRainbowSpeed.get() * 0.1) % 1.0);
                    int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                    java.awt.Color c = new java.awt.Color(rgb);
                    rimColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
                } else {
                    rimColor = rimColorSetting.get();
                }
            }

            // Afterimage
            if (afterimage.get()) {
                if (elapsed - lastFrameTime >= afterimageInterval.get()) {
                    lastFrameTime = elapsed;
                    afterimageFrames.addLast(new AfterimageFrame(x, y, z, coneColor, 1.0f));
                    while (afterimageFrames.size() > afterimageCount.get()) afterimageFrames.pollFirst();
                }
                int totalFrames = afterimageFrames.size();
                int frameIndex = 0;
                Iterator<AfterimageFrame> fit = afterimageFrames.iterator();
                while (fit.hasNext()) {
                    AfterimageFrame frame = fit.next();
                    frame.alpha -= (float)(event.tickDelta * 0.016f * afterimageFadeSpeed.get() * 3.0f);
                    if (frame.alpha <= 0) { fit.remove(); continue; }
                    float ageFraction = (float) frameIndex / Math.max(1, totalFrames - 1);
                    int alpha = (int)(frame.alpha * (1.0f - ageFraction * 0.7f) * 180);
                    alpha = Math.clamp(alpha, 0, 255);
                    Color ghostColor;
                    if (afterimageRainbow.get()) {
                        float hue = afterimageRainbowSync.get()
                            ? (float)((elapsed * rainbowSpeed.get() * 0.1 + ageFraction * 0.1) % 1.0)
                            : (float)((elapsed * rainbowSpeed.get() * 0.1 + ageFraction * 0.3) % 1.0);
                        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                        java.awt.Color c = new java.awt.Color(rgb);
                        ghostColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    } else {
                        SettingColor base = afterimageColorSetting.get();
                        ghostColor = new Color(base.r, base.g, base.b, alpha);
                    }
                    drawCone(event, frame.x, frame.y, frame.z, coneRadius, coneHeight, ghostColor, null, 0);
                    frameIndex++;
                }
            }

            drawCone(event, x, y, z, coneRadius, coneHeight, coneColor, rimColor, rimThickness.get());

            // Particles
            if (particles.get()) {
                while (activeParticles.size() < particleCount.get()) {
                    double spawnAngle = random.nextDouble() * Math.PI * 2;
                    double spawnRadius = random.nextDouble() * coneRadius * particleSpawnRadius.get();
                    double spawnY = y + coneHeight * particleSpawnHeight.get();
                    double spawnX = x + Math.cos(spawnAngle) * spawnRadius;
                    double spawnZ = z + Math.sin(spawnAngle) * spawnRadius;
                    double driftAngle = random.nextDouble() * Math.PI * 2;
                    double driftSpeed = particleDriftSpeed.get() * (0.5 + random.nextDouble());
                    double riseSpeed = particleRiseSpeed.get() * (0.5 + random.nextDouble());
                    float lifetime = (float)(particleLifetime.get() * (0.7 + random.nextDouble() * 0.6));
                    activeParticles.add(new StarParticle(
                        spawnX, spawnY, spawnZ,
                        Math.cos(driftAngle) * driftSpeed, riseSpeed, Math.sin(driftAngle) * driftSpeed,
                        lifetime, lifetime,
                        (float)(random.nextDouble() * Math.PI * 2), random.nextFloat()
                    ));
                }

                float ps = particleSize.get().floatValue();
                double rotSpeed = particleRotationSpeed.get();
                Iterator<StarParticle> it = activeParticles.iterator();
                while (it.hasNext()) {
                    StarParticle p = it.next();
                    p.life -= event.tickDelta * 0.016f;
                    if (p.life <= 0) { it.remove(); continue; }
                    p.px += p.vx; p.py += p.vy; p.pz += p.vz;
                    p.rotation += (float) rotSpeed;

                    if (p.vx == 0 && p.vy == 0 && p.vz == 0) {
                        double spawnAngle = random.nextDouble() * Math.PI * 2;
                        double spawnRadius = random.nextDouble() * coneRadius * particleSpawnRadius.get();
                        p.px = x + Math.cos(spawnAngle) * spawnRadius;
                        p.py = y + coneHeight * particleSpawnHeight.get();
                        p.pz = z + Math.sin(spawnAngle) * spawnRadius;
                        double driftAngle = random.nextDouble() * Math.PI * 2;
                        p.vx = Math.cos(driftAngle) * particleDriftSpeed.get() * (0.5 + random.nextDouble());
                        p.vy = particleRiseSpeed.get() * (0.5 + random.nextDouble());
                        p.vz = Math.sin(driftAngle) * particleDriftSpeed.get() * (0.5 + random.nextDouble());
                    }

                    float lifeFraction = p.life / p.maxLife;
                    int alpha = particleFade.get() ? (int)(lifeFraction * 220) : 220;
                    alpha = Math.clamp(alpha, 0, 255);
                    float size = particleShrink.get() ? ps * lifeFraction : ps;

                    Color pc;
                    if (particleRainbow.get()) {
                        float hue = particleRainbowSync.get()
                            ? (float)(((elapsed * rainbowSpeed.get() * 0.1) + p.hueOffset * 0.1) % 1.0)
                            : (float)(((elapsed * rainbowSpeed.get() * 0.1) + p.hueOffset) % 1.0);
                        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                        java.awt.Color c = new java.awt.Color(rgb);
                        pc = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    } else {
                        SettingColor base = particleColorSetting.get();
                        pc = new Color(base.r, base.g, base.b, alpha);
                    }

                    double cos = Math.cos(p.rotation);
                    double sin = Math.sin(p.rotation);
                    double ax1 = p.px + cos * size * 1.8, az1 = p.pz + sin * size * 1.8;
                    double ax2 = p.px - cos * size * 1.8, az2 = p.pz - sin * size * 1.8;
                    double bx1 = p.px - sin * size * 1.8, bz1 = p.pz + cos * size * 1.8;
                    double bx2 = p.px + sin * size * 1.8, bz2 = p.pz - cos * size * 1.8;

                    event.depthRenderer.quad(ax1, p.py-size*0.4, az1, ax1, p.py+size*0.4, az1, ax2, p.py+size*0.4, az2, ax2, p.py-size*0.4, az2, pc);
                    event.depthRenderer.quad(bx1, p.py-size*0.4, bz1, bx1, p.py+size*0.4, bz1, bx2, p.py+size*0.4, bz2, bx2, p.py-size*0.4, bz2, pc);
                    event.depthRenderer.quad(p.px-size*0.4, p.py-size*0.4, p.pz, p.px+size*0.4, p.py-size*0.4, p.pz, p.px+size*0.4, p.py+size*0.4, p.pz, p.px-size*0.4, p.py+size*0.4, p.pz, pc);
                }
            }

            // ── Halo ─────────────────────────────────────────────────
        } else if (cosmeticType.get() == CosmeticType.Halo && halo.get()) {
            haloAngle += event.tickDelta * 0.016 * haloSpinSpeed.get() * 2.0;
            double hr = haloRadius.get();
            if (haloPulse.get()) {
                hr += Math.sin(elapsed * haloPulseSpeed.get() * Math.PI) * haloPulseAmount.get();
            }
            double ht = haloThickness.get();
            double baseTilt = Math.toRadians(haloTilt.get());
            double haloY = headY + haloYOffset.get();
            int haloSegs = 32;

            Color haloColor;
            if (haloRainbow.get()) {
                float hue = (float)((elapsed * haloRainbowSpeed.get() * 0.1) % 1.0);
                int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                java.awt.Color c = new java.awt.Color(rgb);
                haloColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 220);
            } else {
                haloColor = haloColorSetting.get();
            }

            Color haloGlow = new Color(
                Math.min(255, haloColor.r + 80),
                Math.min(255, haloColor.g + 80),
                Math.min(255, haloColor.b + 80),
                120
            );

            // Afterimage
            if (afterimage.get()) {
                if (elapsed - lastFrameTime >= afterimageInterval.get()) {
                    lastFrameTime = elapsed;
                    afterimageFrames.addLast(new AfterimageFrame(x, haloY, z, haloColor, 1.0f));
                    while (afterimageFrames.size() > afterimageCount.get()) afterimageFrames.pollFirst();
                }
                int totalFrames = afterimageFrames.size();
                int frameIndex = 0;
                Iterator<AfterimageFrame> fit = afterimageFrames.iterator();
                while (fit.hasNext()) {
                    AfterimageFrame frame = fit.next();
                    frame.alpha -= (float)(event.tickDelta * 0.016f * afterimageFadeSpeed.get() * 3.0f);
                    if (frame.alpha <= 0) { fit.remove(); continue; }
                    float ageFraction = (float) frameIndex / Math.max(1, totalFrames - 1);
                    int alpha = (int)(frame.alpha * (1.0f - ageFraction * 0.7f) * 180);
                    alpha = Math.clamp(alpha, 0, 255);
                    Color ghostColor;
                    if (afterimageRainbow.get()) {
                        float hue = afterimageRainbowSync.get()
                            ? (float)((elapsed * haloRainbowSpeed.get() * 0.1 + ageFraction * 0.1) % 1.0)
                            : (float)((elapsed * haloRainbowSpeed.get() * 0.1 + ageFraction * 0.3) % 1.0);
                        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                        java.awt.Color c = new java.awt.Color(rgb);
                        ghostColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    } else {
                        SettingColor base = afterimageColorSetting.get();
                        ghostColor = new Color(base.r, base.g, base.b, alpha);
                    }
                    drawHaloRing(event, frame.x, frame.y, frame.z, hr, ht, baseTilt, haloAngle, haloSegs, ghostColor, null, headYaw);
                    frameIndex++;
                }
            }

            drawHaloRing(event, x, haloY, z, hr, ht, baseTilt, haloAngle, haloSegs, haloColor, haloGlow, headYaw);

            // ── Cat Ears ─────────────────────────────────────────────
        } else if (cosmeticType.get() == CosmeticType.Cat_Ears && earsEnabled.get()) {
            // Smoothly interpolated head rotation so the ears don't lag behind the head model
            double earYaw = lerpAngle(mc.player.yHeadRotO, mc.player.getYHeadRot(), event.tickDelta);
            double earPitch = mc.player.xRotO + (mc.player.getXRot() - mc.player.xRotO) * event.tickDelta;
            double earY = headY + earYOffset.get();

            Color earColor;
            if (earRainbow.get()) {
                float hue = (float)((elapsed * earRainbowSpeed.get() * 0.1) % 1.0);
                int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                java.awt.Color c = new java.awt.Color(rgb);
                earColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 240);
            } else {
                earColor = earColorSetting.get();
            }
            Color innerColor = earInner.get() ? earInnerColorSetting.get() : null;

            // Afterimage
            if (afterimage.get()) {
                if (elapsed - lastFrameTime >= afterimageInterval.get()) {
                    lastFrameTime = elapsed;
                    afterimageFrames.addLast(new AfterimageFrame(x, earY, z, earColor, 1.0f, earYaw, earPitch));
                    while (afterimageFrames.size() > afterimageCount.get()) afterimageFrames.pollFirst();
                }
                int totalFrames = afterimageFrames.size();
                int frameIndex = 0;
                Iterator<AfterimageFrame> fit = afterimageFrames.iterator();
                while (fit.hasNext()) {
                    AfterimageFrame frame = fit.next();
                    frame.alpha -= (float)(event.tickDelta * 0.016f * afterimageFadeSpeed.get() * 3.0f);
                    if (frame.alpha <= 0) { fit.remove(); continue; }
                    float ageFraction = (float) frameIndex / Math.max(1, totalFrames - 1);
                    int alpha = (int)(frame.alpha * (1.0f - ageFraction * 0.7f) * 180);
                    alpha = Math.clamp(alpha, 0, 255);
                    Color ghostColor;
                    if (afterimageRainbow.get()) {
                        float hue = afterimageRainbowSync.get()
                            ? (float)((elapsed * earRainbowSpeed.get() * 0.1 + ageFraction * 0.1) % 1.0)
                            : (float)((elapsed * earRainbowSpeed.get() * 0.1 + ageFraction * 0.3) % 1.0);
                        int rgb = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
                        java.awt.Color c = new java.awt.Color(rgb);
                        ghostColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
                    } else {
                        SettingColor base = afterimageColorSetting.get();
                        ghostColor = new Color(base.r, base.g, base.b, alpha);
                    }
                    drawCatEars(event, frame.x, frame.y, frame.z, frame.yaw, frame.pitch, elapsed, ghostColor, null);
                    frameIndex++;
                }
            }

            drawCatEars(event, x, earY, z, earYaw, earPitch, elapsed, earColor, innerColor);
        }
    }

    // ── Cat ear rendering ────────────────────────────────────────
    // Each ear is a wedge: a rectangular base sitting on the head that narrows to a short ridge at the tip.
    // Ear-local axes: ls = outward from the head, lf = toward the front of the face, lu = up.
    private void drawCatEars(Render3DEvent event, double x, double y, double z,
                             double yawDeg, double pitchDeg, double elapsed,
                             Color color, Color innerColor) {
        double yaw = Math.toRadians(yawDeg);
        double pitch = earFollowPitch.get() ? Math.toRadians(pitchDeg) : 0;
        double cosY = Math.cos(yaw), sinY = Math.sin(yaw);
        double cosP = Math.cos(pitch), sinP = Math.sin(pitch);

        double w = earWidth.get();
        double d = earThickness.get();
        double h = earHeight.get();
        double spacing = earSpacing.get();
        double fwd = earForwardOffset.get();
        double baseTip = h * Math.tan(Math.toRadians(earTilt.get()));

        for (int side = -1; side <= 1; side += 2) {
            double tip = baseTip + earTwitchOffset(elapsed, side, h);

            // Base corners (on the head) and the two ridge points (at the tip)
            double[] b1 = { -w / 2, -d / 2, 0 };
            double[] b2 = {  w / 2, -d / 2, 0 };
            double[] b3 = {  w / 2,  d / 2, 0 };
            double[] b4 = { -w / 2,  d / 2, 0 };
            double[] t1 = { tip, -d * 0.15, h };
            double[] t2 = { tip,  d * 0.15, h };

            double[] B1 = earPoint(b1, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);
            double[] B2 = earPoint(b2, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);
            double[] B3 = earPoint(b3, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);
            double[] B4 = earPoint(b4, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);
            double[] T1 = earPoint(t1, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);
            double[] T2 = earPoint(t2, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);

            earQuad(event, B2, B3, T2, T1, color); // outer side
            earQuad(event, B1, B4, T2, T1, color); // inner side
            earQuad(event, B3, B4, T2, T2, color); // front
            earQuad(event, B1, B2, T1, T1, color); // back

            // Inner ear patch: a shrunken copy of the front face, nudged forward to avoid z-fighting
            if (innerColor != null) {
                double[][] tri = { b3, b4, t2 };
                double cs = (tri[0][0] + tri[1][0] + tri[2][0]) / 3.0;
                double cf = (tri[0][1] + tri[1][1] + tri[2][1]) / 3.0;
                double cu = (tri[0][2] + tri[1][2] + tri[2][2]) / 3.0;
                double[][] out = new double[3][];
                for (int i = 0; i < 3; i++) {
                    double[] local = {
                        cs + (tri[i][0] - cs) * 0.55,
                        cf + (tri[i][1] - cf) * 0.55 + 0.006,
                        cu + (tri[i][2] - cu) * 0.55
                    };
                    out[i] = earPoint(local, side, spacing, fwd, x, y, z, cosY, sinY, cosP, sinP);
                }
                earQuad(event, out[0], out[1], out[2], out[2], innerColor);
            }
        }
    }

    /** Converts an ear-local point into a world position, applying head yaw and (optionally) pitch. */
    private double[] earPoint(double[] p, int side, double spacing, double fwd,
                              double x, double y, double z,
                              double cosY, double sinY, double cosP, double sinP) {
        // Head-local coordinates: hs = sideways (mirrored per ear), hf = forward, hu = up from head top
        double hs = side * (spacing + p[0]);
        double hf = fwd + p[1];
        double relU = p[2] + HEAD_PIVOT_DROP;

        // Pitch around the neck (positive pitch = looking down)
        double nf = hf * cosP + relU * sinP;
        double nu = relU * cosP - hf * sinP;

        // Minecraft yaw: forward = (-sin, cos), right = (-cos, -sin)
        double wx = x + hs * (-cosY) + nf * (-sinY);
        double wz = z + hs * (-sinY) + nf * cosY;
        double wy = y + nu - HEAD_PIVOT_DROP;

        return new double[]{ wx, wy, wz };
    }

    private void earQuad(Render3DEvent event, double[] a, double[] b, double[] c, double[] d, Color color) {
        event.depthRenderer.quad(
            a[0], a[1], a[2],
            b[0], b[1], b[2],
            c[0], c[1], c[2],
            d[0], d[1], d[2],
            color
        );
    }

    /** Short damped wiggle of the ear tip once per twitch period. Left and right ears are slightly offset. */
    private double earTwitchOffset(double elapsed, int side, double height) {
        if (!earTwitch.get()) return 0;
        double period = 4.0 / earTwitchSpeed.get();
        double t = (((elapsed + (side > 0 ? 0.0 : 0.3)) % period) / period) / 0.1; // burst lasts 10% of the period
        if (t >= 1.0) return 0;
        return Math.sin(t * Math.PI * 4.0) * Math.sin(t * Math.PI) * height * 0.35;
    }

    private static double lerpAngle(double from, double to, double t) {
        double diff = (to - from) % 360.0;
        if (diff >= 180.0) diff -= 360.0;
        else if (diff < -180.0) diff += 360.0;
        return from + diff * t;
    }

    private void drawHaloRing(Render3DEvent event, double x, double y, double z,
                              double hr, double ht, double baseTilt, double angleOffset,
                              int segments, Color color, Color glowColor, double headYaw) {
        double innerR = hr - ht;
        double outerR = hr + ht;
        if (innerR < 0) innerR = 0;

        for (int i = 0; i < segments; i++) {
            double a1 = (2 * Math.PI * i) / segments + angleOffset;
            double a2 = (2 * Math.PI * (i + 1)) / segments + angleOffset;

            double[] p1 = transformHaloPoint(a1, innerR, baseTilt, headYaw);
            double[] p2 = transformHaloPoint(a1, outerR, baseTilt, headYaw);
            double[] p3 = transformHaloPoint(a2, outerR, baseTilt, headYaw);
            double[] p4 = transformHaloPoint(a2, innerR, baseTilt, headYaw);

            event.depthRenderer.quad(
                x+p1[0], y+p1[1], z+p1[2],
                x+p2[0], y+p2[1], z+p2[2],
                x+p3[0], y+p3[1], z+p3[2],
                x+p4[0], y+p4[1], z+p4[2],
                color
            );

            if (glowColor != null) {
                double glowT = ht * 0.8;
                double[] g1 = transformHaloPoint(a1, outerR + glowT, baseTilt, headYaw);
                double[] g2 = transformHaloPoint(a2, outerR + glowT, baseTilt, headYaw);
                event.depthRenderer.quad(
                    x+p2[0], y+p2[1], z+p2[2],
                    x+g1[0], y+g1[1], z+g1[2],
                    x+g2[0], y+g2[1], z+g2[2],
                    x+p3[0], y+p3[1], z+p3[2],
                    glowColor
                );
            }
        }
    }

    private double[] transformHaloPoint(double angle, double radius, double baseTilt, double headYaw) {
        double lx = Math.cos(angle) * radius;
        double ly = 0;
        double lz = Math.sin(angle) * radius;

        double tiltedY = ly * Math.cos(-baseTilt) - lz * Math.sin(-baseTilt);
        double tiltedZ = ly * Math.sin(-baseTilt) + lz * Math.cos(-baseTilt);
        ly = tiltedY;
        lz = tiltedZ;

        double finalX = lx * Math.cos(headYaw) - lz * Math.sin(headYaw);
        double finalZ = lx * Math.sin(headYaw) + lz * Math.cos(headYaw);

        return new double[]{ finalX, ly, finalZ };
    }

    private void drawCone(Render3DEvent event, double x, double y, double z,
                          float coneRadius, float coneHeight, Color color,
                          Color rimColor, double rimThickness) {
        int segments = 32;
        double[] coneX = new double[segments];
        double[] coneZ = new double[segments];
        for (int i = 0; i < segments; i++) {
            double angle = (2 * Math.PI * i) / segments;
            coneX[i] = x + coneRadius * Math.cos(angle);
            coneZ[i] = z + coneRadius * Math.sin(angle);
        }
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            event.depthRenderer.quad(
                coneX[i], y, coneZ[i],
                coneX[next], y, coneZ[next],
                x, y + coneHeight, z,
                x, y + coneHeight, z,
                color
            );
        }
        if (rimColor != null && rimThickness > 0) {
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                double outerX1 = x + (coneRadius + rimThickness) * Math.cos(2 * Math.PI * i / segments);
                double outerZ1 = z + (coneRadius + rimThickness) * Math.sin(2 * Math.PI * i / segments);
                double outerX2 = x + (coneRadius + rimThickness) * Math.cos(2 * Math.PI * next / segments);
                double outerZ2 = z + (coneRadius + rimThickness) * Math.sin(2 * Math.PI * next / segments);
                event.depthRenderer.quad(
                    coneX[i], y, coneZ[i],
                    coneX[next], y, coneZ[next],
                    outerX2, y, outerZ2,
                    outerX1, y, outerZ1,
                    rimColor
                );
            }
        }
    }

    private static class AfterimageFrame {
        double x, y, z; Color color; float alpha;
        double yaw, pitch; // only used by Cat Ears
        AfterimageFrame(double x, double y, double z, Color color, float alpha) {
            this.x=x; this.y=y; this.z=z; this.color=color; this.alpha=alpha;
        }
        AfterimageFrame(double x, double y, double z, Color color, float alpha, double yaw, double pitch) {
            this(x, y, z, color, alpha);
            this.yaw=yaw; this.pitch=pitch;
        }
    }

    private static class StarParticle {
        double px, py, pz, vx, vy, vz;
        float life, maxLife, rotation, hueOffset;
        StarParticle(double px, double py, double pz, double vx, double vy, double vz,
                     float life, float maxLife, float rotation, float hueOffset) {
            this.px=px; this.py=py; this.pz=pz;
            this.vx=vx; this.vy=vy; this.vz=vz;
            this.life=life; this.maxLife=maxLife;
            this.rotation=rotation; this.hueOffset=hueOffset;
        }
    }
}
