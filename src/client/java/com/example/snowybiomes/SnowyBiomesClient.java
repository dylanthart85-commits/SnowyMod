package com.example.snowybiomes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;

import java.util.Random;

/**
 * Kleine client-side mod: spawnt continu sneeuw-particles rondom de speler,
 * maar uitsluitend wanneer de kolom waarin de particle valt een biome is
 * waar het (volgens de vanilla temperatuur/precipitatie-logica) zou sneeuwen.
 *
 * Dit is puur visueel (particles), er wordt niets aan het echte
 * weersysteem of aan serverdata veranderd.
 */
public class SnowyBiomesClient implements ClientModInitializer {

    private static final Random RANDOM = new Random();

    // Hoeveel particles er per tick geprobeerd worden te spawnen.
    private static final int PARTICLES_PER_TICK = 8;

    // Straal (in blokken) rondom de speler waarbinnen gezocht wordt.
    private static final int RADIUS = 16;

    // Hoogte boven het hoogste blok waarop de sneeuw begint te vallen.
    private static final int SPAWN_HEIGHT_ABOVE = 10;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        ClientWorld world = client.world;
        PlayerEntity player = client.player;

        if (world == null || player == null) {
            return;
        }

        BlockPos playerPos = player.getBlockPos();

        for (int i = 0; i < PARTICLES_PER_TICK; i++) {
            int x = playerPos.getX() + RANDOM.nextInt(RADIUS * 2) - RADIUS;
            int z = playerPos.getZ() + RANDOM.nextInt(RADIUS * 2) - RADIUS;

            // Alleen doorgaan als de chunk al geladen is (voorkomt onnodige loads).
            if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                continue;
            }

            BlockPos columnTopPos = world.getTopPosition(
                    Heightmap.Type.MOTION_BLOCKING,
                    new BlockPos(x, 0, z)
            );

            if (!isSnowyAt(world, columnTopPos)) {
                continue;
            }

            double spawnX = x + RANDOM.nextDouble();
            double spawnZ = z + RANDOM.nextDouble();
            double spawnY = columnTopPos.getY() + SPAWN_HEIGHT_ABOVE + RANDOM.nextDouble() * 6.0;

            world.addParticle(
                    ParticleTypes.SNOWFLAKE,
                    spawnX, spawnY, spawnZ,
                    0.0, -0.12, 0.0
            );
        }
    }

    /**
     * Bepaalt of het op deze positie een "sneeuw-biome" is, op dezelfde manier
     * als vanilla bepaalt of neerslag als sneeuw of regen valt (op basis van
     * de temperatuur op die hoogte). Dit dekt automatisch alle ijs- en
     * sneeuwbiomen (besneeuwde vlaktes, ijspieken, bevroren oceaan,
     * besneeuwde taiga, grove, etc.) zonder dat we losse biome-tags
     * hoeven te hardcoden.
     *
     * LET OP: de methodenaam Biome#getPrecipitation(BlockPos) en de enum
     * Biome.Precipitation kunnen tussen mapping-versies licht verschillen
     * (bijv. getPrecipitationAt). Controleer dit in je IDE ("Go to
     * declaration") tegen de Yarn mappings die je in gradle.properties
     * gebruikt, en pas zo nodig aan.
     */
    private boolean isSnowyAt(ClientWorld world, BlockPos pos) {
        Biome biome = world.getBiome(pos).value();
        Biome.Precipitation precipitation = biome.getPrecipitation(pos);
        return precipitation == Biome.Precipitation.SNOW;
    }
}
