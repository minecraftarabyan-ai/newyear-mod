package com.newyear;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModParticles {
    public static final SimpleParticleType SNOWFLAKE = FabricParticleTypes.simple();
    public static final SimpleParticleType FLUFF = FabricParticleTypes.simple();
    public static final SimpleParticleType SPARKLE = FabricParticleTypes.simple();

    public static void register() {
        reg("snowflake", SNOWFLAKE);
        reg("fluff", FLUFF);
        reg("sparkle", SPARKLE);
    }

    private static void reg(String name, SimpleParticleType type) {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(NewYear.MOD_ID, name), type);
    }
}
