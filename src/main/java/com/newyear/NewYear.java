package com.newyear;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewYear implements ModInitializer {
    public static final String MOD_ID = "newyear";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModParticles.register();
        LOGGER.info("С Новым годом! Мод загружен.");
    }
}
