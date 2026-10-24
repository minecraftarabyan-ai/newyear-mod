package com.newyear.client;

import com.newyear.ModParticles;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

public final class SnowSpawner {
    public static double windX, windZ;
    public static float night; // 0 = день, 1 = ночь

    private static double targetWindX, targetWindZ;
    private static int windTimer;
    private static final Random RNG = Random.create();

    public static void tick(MinecraftClient client) {
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null || client.isPaused()) return;

        NewYearConfig cfg = NewYearConfig.get();
        updateNight(world);
        updateWind(cfg);

        if (!cfg.snowEnabled || !world.getDimension().hasSkyLight()) return;

        int count = (int) cfg.density;
        if (RNG.nextFloat() < cfg.density - count) count++;

        for (int i = 0; i < count; i++) {
            double angle = RNG.nextDouble() * Math.PI * 2;
            double dist = Math.sqrt(RNG.nextDouble()) * cfg.radius;
            // смещаем снег чуть вперёд по ходу движения, чтобы игрок не обгонял снегопад
            double x = player.getX() + Math.cos(angle) * dist + player.getVelocity().x * 20;
            double z = player.getZ() + Math.sin(angle) * dist + player.getVelocity().z * 20;
            double y = player.getY() + 4 + RNG.nextDouble() * 16;

            int top = world.getTopY(Heightmap.Type.MOTION_BLOCKING, MathHelper.floor(x), MathHelper.floor(z));
            if (top > player.getY() + 24) continue; // глубоко под землёй снега нет
            if (y < top) y = top + RNG.nextDouble() * 4;

            world.addParticle(pickType(cfg), x, y, z, 0, 0, 0);
        }
    }

    private static ParticleEffect pickType(NewYearConfig cfg) {
        float r = RNG.nextFloat();
        float sparkle = cfg.sparkleShare * (0.3f + 0.7f * night); // ночью искорок больше
        if (r < sparkle) return ModParticles.SPARKLE;
        if (r < sparkle + cfg.fluffShare) return ModParticles.FLUFF;
        return ModParticles.SNOWFLAKE;
    }

    private static void updateNight(ClientWorld world) {
        long t = world.getTimeOfDay() % 24000L;
        float n;
        if (t < 12000) n = 0f;
        else if (t < 13500) n = (t - 12000) / 1500f;
        else if (t < 22500) n = 1f;
        else n = 1f - (t - 22500) / 1500f;
        night = Math.max(n, world.getRainGradient(1f) * 0.5f);
    }

    private static void updateWind(NewYearConfig cfg) {
        if (--windTimer <= 0) {
            windTimer = 200 + RNG.nextInt(400);
            targetWindX = (RNG.nextDouble() - 0.5) * 0.06 * cfg.wind;
            targetWindZ = (RNG.nextDouble() - 0.5) * 0.06 * cfg.wind;
        }
        windX += (targetWindX - windX) * 0.01;
        windZ += (targetWindZ - windZ) * 0.01;
    }
}
