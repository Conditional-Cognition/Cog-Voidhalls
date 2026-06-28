package com.cogworks.voidhalls.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty; // Added for property tracking
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Layer0ChunkGenerator extends ChunkGenerator {

    public static final MapCodec<Layer0ChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, Layer0ChunkGenerator::new)
    );

    private static final int CELL_SIZE = 7;
    private static final int CORRIDOR_WIDTH = 3;
    private static final float WALL_OPEN_CHANCE = 0.65f;

    private static final int CEILING_Y = 13; // Shifted +1 for the taller room body
    private static final int DIM_HEIGHT = 64;

    private static final int LIGHT_SPACING_X = 7;
    private static final int LIGHT_SPACING_Z = 3;
    private static final int LIGHT_PHASE_X = 3;
    private static final int LIGHT_PHASE_Z = 1;

    private static final float CARPET_DECAY_CHANCE = 0.08f;

    private static final ResourceLocation MAZE_KEY =
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_0_maze");
    private static final ResourceLocation CARPET_DECAY_KEY =
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_0_carpet_decay");

    private final BlockState fuckinfAir;
    private final BlockState scaffoldState;
    private final BlockState ceilingTileState;
    private final BlockState wallState;
    private final BlockState lightFixtureState;

    private static final int START_Y = 6; // Moved up 2 blocks from the original 4
    private static final int FLOOR_HEIGHT = 8; // Distance between floor levels (room body now 4 blocks tall)
    private static final int MAX_FLOORS = 3; // Only generate this many floors, then stop

    public Layer0ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        this.fuckinfAir = Blocks.AIR.defaultBlockState();
        this.scaffoldState = BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_zero_scaffold"))
                .defaultBlockState();
        this.ceilingTileState = BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_zero_ceiling_tile"))
                .defaultBlockState();
        this.wallState = BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_zero_wall"))
                .defaultBlockState();
        this.lightFixtureState = Blocks.OCHRE_FROGLIGHT.defaultBlockState();
    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Blender blender, RandomState randomState, @NotNull StructureManager structureManager, ChunkAccess chunk) {
        PositionalRandomFactory mazeRandom = randomState.getOrCreateRandomFactory(MAZE_KEY);
        PositionalRandomFactory decayRandom = randomState.getOrCreateRandomFactory(CARPET_DECAY_KEY);
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int corridorMin = (CELL_SIZE - CORRIDOR_WIDTH + 1) / 2;
        int corridorMax = corridorMin + CORRIDOR_WIDTH - 1;

        BooleanProperty groundedProperty = null;
        if (wallState.getProperties().stream().anyMatch(p -> p.getName().equals("grounded"))) {
            groundedProperty = (BooleanProperty) wallState.getBlock().getStateDefinition().getProperty("grounded");
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = minX + x;
                int worldZ = minZ + z;
                boolean open = isOpenColumn(worldX, worldZ, mazeRandom, corridorMin, corridorMax);
                boolean isLightFixture = open && isLightFixtureColumn(worldX, worldZ, mazeRandom, corridorMin, corridorMax);
                boolean carpetDecayed = isCarpetDecayed(worldX, worldZ, decayRandom);

                for (int y = 0; y < DIM_HEIGHT; y++) {
                    pos.set(worldX, y, worldZ);

                    BlockState state;

                    if (y == 0) {
                        // Absolute world bottom
                        state = Blocks.BEDROCK.defaultBlockState();
                    } else if (y <= START_Y - 2) {
                        // Caps off the void under floor 0 with a ceiling-like underside —
                        // fills everything between bedrock and the foundation, however tall that is
                        state = ceilingTileState;
                    } else {
                        int relativeY = y - (START_Y - 1);
                        int localY = Math.floorMod(relativeY, FLOOR_HEIGHT);
                        int floorIndex = Math.floorDiv(relativeY, FLOOR_HEIGHT);

                        if (floorIndex < 0) {
                            // Dead zone below floor 0 (shouldn't be reachable given the cap above, but just in case)
                            state = fuckinfAir;
                        } else if (floorIndex >= MAX_FLOORS) {
                            // Above the last floor: solid scaffold fill, bedrock capping the world top
                            state = (y == DIM_HEIGHT - 1) ? Blocks.BEDROCK.defaultBlockState() : scaffoldState;
                        } else if (localY == 0) {
                            state = scaffoldState; // Foundation
                        } else if (localY == 1) {
                            // Carpet where open, wall where closed — this is the wall's
                            // ground-contact row now, so it gets `grounded` instead of localY 2
                            if (open) {
                                state = carpetDecayed ? fuckinfAir : Blocks.BROWN_CARPET.defaultBlockState();
                            } else {
                                state = (groundedProperty != null)
                                        ? wallState.setValue(groundedProperty, true)
                                        : wallState;
                            }
                        } else if (localY == 2 || localY == 3 || localY == 4 || localY == 5) {
                            // Room body: air where the column is open, wall otherwise
                            state = open ? fuckinfAir : wallState;
                        } else if (localY == 6) {
                            // BOTTOM CEILING LAYER (The lights)
                            state = isLightFixture ? lightFixtureState : ceilingTileState;
                        } else {
                            // TOP CEILING LAYER (Solid thickness)
                            state = ceilingTileState;
                        }
                    }

                    chunk.setBlockState(pos, state, false);
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    private boolean isOpenColumn(int worldX, int worldZ, PositionalRandomFactory mazeRandom, int corridorMin, int corridorMax) {
        int cellX = Math.floorDiv(worldX, CELL_SIZE);
        int cellZ = Math.floorDiv(worldZ, CELL_SIZE);
        int localX = Math.floorMod(worldX, CELL_SIZE);
        int localZ = Math.floorMod(worldZ, CELL_SIZE);

        boolean onXBoundary = localX == 0;
        boolean onZBoundary = localZ == 0;

        if (onXBoundary && onZBoundary) return false;

        if (onXBoundary) {
            RandomSource rand = mazeRandom.at(cellX, 0, cellZ);
            return rand.nextFloat() < WALL_OPEN_CHANCE && localZ >= corridorMin && localZ <= corridorMax;
        }

        if (onZBoundary) {
            RandomSource rand = mazeRandom.at(cellX, 1, cellZ);
            return rand.nextFloat() < WALL_OPEN_CHANCE && localX >= corridorMin && localX <= corridorMax;
        }

        return true;
    }

    private boolean isLightFixtureColumn(int worldX, int worldZ, PositionalRandomFactory mazeRandom, int corridorMin, int corridorMax) {
        if (Math.floorMod(worldX, LIGHT_SPACING_X) != LIGHT_PHASE_X) return false;

        int zMod = Math.floorMod(worldZ, LIGHT_SPACING_Z);
        int partnerZ;
        if (zMod == LIGHT_PHASE_Z) {
            partnerZ = worldZ + 1;
        } else if (zMod == LIGHT_PHASE_Z + 1) {
            partnerZ = worldZ - 1;
        } else {
            return false;
        }

        return isOpenColumn(worldX, partnerZ, mazeRandom, corridorMin, corridorMax);
    }

    private boolean isCarpetDecayed(int worldX, int worldZ, PositionalRandomFactory decayRandom) {
        RandomSource rand = decayRandom.at(worldX, 0, worldZ);
        return rand.nextFloat() < CARPET_DECAY_CHANCE;
    }

    @Override
    public void applyCarvers(@NotNull WorldGenRegion region, long seed, @NotNull RandomState randomState, @NotNull BiomeManager biomeManager, @NotNull StructureManager structureManager, @NotNull ChunkAccess chunk, GenerationStep.@NotNull Carving step) {}

    @Override
    public void buildSurface(@NotNull WorldGenRegion region, @NotNull StructureManager structureManager, @NotNull RandomState randomState, @NotNull ChunkAccess chunk) {}

    @Override
    public void spawnOriginalMobs(@NotNull WorldGenRegion region) {}

    @Override
    public int getGenDepth() { return DIM_HEIGHT; }

    @Override
    public int getSeaLevel() { return 0; }

    @Override
    public int getMinY() { return 0; }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.@NotNull Types type, @NotNull LevelHeightAccessor level, @NotNull RandomState randomState) {
        return CEILING_Y;
    }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int x, int z, @NotNull LevelHeightAccessor level, @NotNull RandomState randomState) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(@NotNull List<String> info, @NotNull RandomState randomState, @NotNull BlockPos pos) {}
}