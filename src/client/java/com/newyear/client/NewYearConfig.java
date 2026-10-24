package com.newyear.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class NewYearConfig {
    // --- Снег ---
    public boolean snowEnabled = true;
    public float density = 4f;         // сколько снежинок появляется за тик (для телефона уменьшено)
    public float speed = 1f;           // скорость падения
    public float size = 1f;            // размер
    public float glow = 0.8f;          // свечение ночью: 0 = нет, 1 = максимум
    public float wind = 1f;            // сила ветра
    public float radius = 20f;         // радиус вокруг игрока
    public float sparkleShare = 0.12f; // доля искорок
    public float fluffShare = 0.25f;   // доля пушистых хлопьев

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static NewYearConfig instance;

    public static NewYearConfig get() {
        if (instance == null) load();
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("newyear.json");
    }

    public static void load() {
        try {
            if (Files.exists(path())) {
                try (Reader r = Files.newBufferedReader(path())) {
                    instance = GSON.fromJson(r, NewYearConfig.class);
                }
            }
        } catch (Exception e) {
            instance = null;
        }
        if (instance == null) instance = new NewYearConfig();
        save();
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(path())) {
            GSON.toJson(instance, w);
        } catch (Exception ignored) {
        }
    }
}
