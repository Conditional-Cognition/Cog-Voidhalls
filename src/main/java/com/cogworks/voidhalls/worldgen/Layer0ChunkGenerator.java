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

    private static final int FLOOR_Y = 5;
    private static final int ROOM_TOP = 9;
    private static final int CEILING_Y = 10;
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

                    if (y < FLOOR_Y) {
                        state = scaffoldState;
                    } else if (!open) {
                        if (y <= CEILING_Y) {
                            // If we are placing a wall block at the bottom level (FLOOR_Y), apply grounded = true
                            if (y == FLOOR_Y && groundedProperty != null) {
                                state = wallState.setValue(groundedProperty, true);
                            } else if (groundedProperty != null) {
                                state = wallState.setValue(groundedProperty, false);
                            } else {
                                state = wallState;
                            }
                        } else {
                            state = scaffoldState;
                        }
                    } else if (y == FLOOR_Y) {
                        RandomSource rand = randomState.getOrCreateRandomFactory(MAZE_KEY).at(worldX, 0, worldZ);
                        if (rand.nextFloat() < 0.0003f) {
                            state = rand.nextBoolean() ?
                                    BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("voidhalls", "spruce_table")).defaultBlockState() :
                                    BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("voidhalls", "oak_table")).defaultBlockState();
                        } else {
                            state = carpetDecayed ? fuckinfAir : Blocks.BROWN_CARPET.defaultBlockState();
                        }
                    } else if (y <= ROOM_TOP) {
                        state = Blocks.AIR.defaultBlockState();
                    } else if (y == CEILING_Y) {
                        state = isLightFixture ? lightFixtureState : ceilingTileState;
                    } else {
                        state = scaffoldState;
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