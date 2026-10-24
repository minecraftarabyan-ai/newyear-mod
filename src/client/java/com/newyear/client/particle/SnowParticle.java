package com.newyear.client.particle;

import com.newyear.client.NewYearConfig;
import com.newyear.client.SnowSpawner;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SnowParticle extends SpriteBillboardParticle {

    public enum Kind { FLAKE, FLUFF, SPARKLE }

    private final Kind kind;
    private float fall, swayAmp, swayFreq, phase, spin, windResponse, baseAlpha, baseScale;
    private int landed = -1;

    protected SnowParticle(ClientWorld world, double x, double y, double z, SpriteProvider sprites, Kind kind) {
        super(world, x, y, z);
        this.kind = kind;
        this.setSprite(sprites);
        this.gravityStrength = 0f;
        this.velocityMultiplier = 1f;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;
        this.phase = random.nextFloat() * 100f;

        switch (kind) {
            case FLAKE -> {
                baseScale = 0.07f + random.nextFloat() * 0.06f;
                fall = 0.035f + random.nextFloat() * 0.025f;
                swayAmp = 0.012f + random.nextFloat() * 0.01f;
                swayFreq = 0.04f + random.nextFloat() * 0.04f;
                spin = (random.nextFloat() - 0.5f) * 0.08f;
                windResponse = 1f;
                baseAlpha = 0.95f;
                setMaxAge(300 + random.nextInt(200));
                setColor(0.93f, 0.97f, 1f);
            }
            case FLUFF -> {
                baseScale = 0.12f + random.nextFloat() * 0.08f;
                fall = 0.02f + random.nextFloat() * 0.015f;
                swayAmp = 0.02f + random.nextFloat() * 0.015f;
                swayFreq = 0.025f + random.nextFloat() * 0.025f;
                spin = 0f;
                windResponse = 1.4f;
                baseAlpha = 0.55f;
                setMaxAge(350 + random.nextInt(250));
                setColor(1f, 1f, 1f);
            }
            case SPARKLE -> {
                baseScale = 0.04f + random.nextFloat() * 0.03f;
                fall = 0.015f + random.nextFloat() * 0.015f;
                swayAmp = 0.008f;
                swayFreq = 0.05f;
                spin = (random.nextFloat() - 0.5f) * 0.03f;
                windResponse = 0.8f;
                baseAlpha = 1f;
                setMaxAge(120 + random.nextInt(100));
                if (random.nextFloat() < 0.4f) setColor(1f, 0.85f, 0.55f); // тёплое золото
                else setColor(0.7f, 0.88f, 1f);                            // ледяной голубой
            }
        }
        this.scale = baseScale * NewYearConfig.get().size;
        this.alpha = 0f;
    }

    @Override
    public void tick() {
        NewYearConfig cfg = NewYearConfig.get();
        float spd = cfg.speed;

        if (landed >= 0) {
            velocityX = 0;
            velocityY = 0;
            velocityZ = 0;
            prevAngle = angle;
            landed++;
            if (landed > 30) {
                markDead();
                return;
            }
        } else {
            float t = age + phase;
            velocityX = MathHelper.sin(t * swayFreq) * swayAmp * spd + SnowSpawner.windX * windResponse;
            velocityZ = MathHelper.cos(t * swayFreq * 0.8f) * swayAmp * spd + SnowSpawner.windZ * windResponse;
            velocityY = -fall * spd;
            prevAngle = angle;
            angle += spin * spd;
        }

        super.tick();

        if (landed < 0 && (onGround || !world.getFluidState(BlockPos.ofFloored(x, y, z)).isEmpty())) {
            landed = 0;
        }

        float a = baseAlpha * Math.min(1f, age / 15f);
        int left = maxAge - age;
        if (left < 40) a *= left / 40f;
        if (landed >= 0) a *= 1f - landed / 30f;
        if (kind == Kind.SPARKLE) a *= 0.55f + 0.45f * MathHelper.sin((age + phase) * 0.35f);
        this.alpha = MathHelper.clamp(a, 0f, 1f);

        float shrink = landed >= 0 ? 1f - landed / 60f : 1f;
        this.scale = baseScale * cfg.size * shrink;
    }

    // Свечение: ночью частица светится сама, даже в темноте
    @Override
    public int getBrightness(float tint) {
        int base = super.getBrightness(tint);
        float glow = NewYearConfig.get().glow * SnowSpawner.night;
        if (kind == Kind.SPARKLE) glow = Math.min(1f, glow * 1.3f + 0.2f * SnowSpawner.night);
        int block = LightmapTextureManager.getBlockLightCoordinates(base);
        int sky = LightmapTextureManager.getSkyLightCoordinates(base);
        return LightmapTextureManager.pack(Math.max(block, Math.round(15 * glow)), sky);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    public record Factory(SpriteProvider sprites, Kind kind) implements ParticleFactory<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientWorld world,
                                       double x, double y, double z, double vx, double vy, double vz) {
            return new SnowParticle(world, x, y, z, sprites, kind);
        }
    }
}
