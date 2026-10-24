package com.newyear;

import com.newyear.client.NewYearConfig;
import com.newyear.client.SnowSpawner;
import com.newyear.client.particle.SnowParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class NewYearClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        NewYearConfig.load();

        ParticleFactoryRegistry reg = ParticleFactoryRegistry.getInstance();
        reg.register(ModParticles.SNOWFLAKE, s -> new SnowParticle.Factory(s, SnowParticle.Kind.FLAKE));
        reg.register(ModParticles.FLUFF, s -> new SnowParticle.Factory(s, SnowParticle.Kind.FLUFF));
        reg.register(ModParticles.SPARKLE, s -> new SnowParticle.Factory(s, SnowParticle.Kind.SPARKLE));

        ClientTickEvents.END_CLIENT_TICK.register(SnowSpawner::tick);
    }
}
