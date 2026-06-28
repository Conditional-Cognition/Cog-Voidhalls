package com.cogworks.voidhalls.worldgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Layer1ChunkGenerator extends ChunkGenerator {

    public static final MapCodec<Layer1ChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
            ).apply(instance, Layer1ChunkGenerator::new)
    );
    @SuppressWarnings("unused")
    public static final ResourceKey<Biome> WIDE_BIOME = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_1_wide"));
    @SuppressWarnings("unused")
    public static final ResourceKey<Biome> NARROW_BIOME = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_1_narrow"));

    // Wide corridor: 5 blocks wide, 4 blocks tall
    private static final int CELL_SIZE_WIDE = 9;
    private static final int CORRIDOR_WIDTH_WIDE = 5;
    private static final int ROOM_HEIGHT_WIDE = 4;

    // Narrow corridor: 3 blocks wide, 3 blocks tall
    private static final int CELL_SIZE_NARROW = 7;
    private static final int CORRIDOR_WIDTH_NARROW = 3;
    private static final int ROOM_HEIGHT_NARROW = 3;

    // Chance that any given corridor segment (between two intersections) is open/passable
    private static final float SEGMENT_OPEN_CHANCE = 0.65f;

    private static final int CEILING_THICKNESS = 2;
    private static final int FLOOR_Y = 5;
    private static final int DIM_HEIGHT = 32;
    private static final int BASE_HEIGHT_HINT = FLOOR_Y + ROOM_HEIGHT_WIDE + CEILING_THICKNESS + 1;

    private static final ResourceLocation MAZE_KEY_WIDE =
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_1_maze_wide");
    private static final ResourceLocation MAZE_KEY_NARROW =
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_1_maze_narrow");

    private final BlockState air;
    private final BlockState fillState;          // black concrete — structural fill, never directly seen
    private final BlockState floorCeilingState;  // gray concrete — corridor floor & ceiling
    private final BlockState wallState;          // light gray concrete — corridor walls

    public Layer1ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        this.air = Blocks.AIR.defaultBlockState();
        this.fillState = Blocks.BLACK_CONCRETE.defaultBlockState();
        this.floorCeilingState = Blocks.GRAY_CONCRETE.defaultBlockState();
        this.wallState = Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState();
    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private enum Band { NONE, OPEN_CORE, WALL_RING }

    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Blender blender, RandomState randomState, @NotNull StructureManager structureManager, ChunkAccess chunk) {
        PositionalRandomFactory mazeRandomWide = randomState.getOrCreateRandomFactory(MAZE_KEY_WIDE);
        PositionalRandomFactory mazeRandomNarrow = randomState.getOrCreateRandomFactory(MAZE_KEY_NARROW);
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        Climate.Sampler sampler = randomState.sampler();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = minX + x;
                int worldZ = minZ + z;

                boolean isWide = isWideColumn(worldX, worldZ, sampler);
                int cellSize = isWide ? CELL_SIZE_WIDE : CELL_SIZE_NARROW;
                int corridorWidth = isWide ? CORRIDOR_WIDTH_WIDE : CORRIDOR_WIDTH_NARROW;
                int roomHeight = isWide ? ROOM_HEIGHT_WIDE : ROOM_HEIGHT_NARROW;
                PositionalRandomFactory mazeRandom = isWide ? mazeRandomWide : mazeRandomNarrow;

                int openHalf = corridorWidth / 2;
                int wallHalf = openHalf + 1;

                Band xBand = classifyAxis(worldX, worldZ, cellSize, openHalf, wallHalf, mazeRandom, 0);
                Band zBand = classifyAxis(worldZ, worldX, cellSize, openHalf, wallHalf, mazeRandom, 1);

                boolean open = xBand == Band.OPEN_CORE || zBand == Band.OPEN_CORE;
                boolean wall = !open && (xBand == Band.WALL_RING || zBand == Band.WALL_RING);

                int ceilingTop = FLOOR_Y + roomHeight + CEILING_THICKNESS;

                for (int y = 0; y < DIM_HEIGHT; y++) {
                    pos.set(worldX, y, worldZ);
                    BlockState state;

                    if (y < FLOOR_Y - 1 || y > ceilingTop) {
                        // Above the ceiling or below the foundation — solid fill
                        state = fillState;
                    } else if (y == FLOOR_Y - 1) {
                        // Foundation
                        state = fillState;
                    } else if (y == FLOOR_Y) {
                        // Floor row: corridor surface, wall base, or fill
                        state = open ? floorCeilingState : (wall ? wallState : fillState);
                    } else if (y <= FLOOR_Y + roomHeight) {
                        // Room body: open air, wall, or fill
                        state = open ? air : (wall ? wallState : fillState);
                    } else {
                        // Ceiling (both layers, for now plain — light fixtures can slot in later)
                        state = floorCeilingState;
                    }

                    chunk.setBlockState(pos, state, false);
                }
            }
        }

        return CompletableFuture.completedFuture(chunk);
    }

    private boolean isWideColumn(int worldX, int worldZ, Climate.Sampler sampler) {
        Holder<Biome> biome = getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(worldX), QuartPos.fromBlock(FLOOR_Y), QuartPos.fromBlock(worldZ), sampler);
        return biome.is(WIDE_BIOME);
    }

    /**
     * Classifies how close this column is to the nearest grid-aligned corridor line that
     * runs perpendicular to {@code coord} (e.g. for the X axis, the line is a constant-X
     * line running along Z). {@code otherCoord} identifies which segment of that line
     * (between two intersections) this column falls on, so a whole segment opens or
     * closes together rather than block-by-block.
     */
    private Band classifyAxis(int coord, int otherCoord, int cellSize, int openHalf, int wallHalf,
                               PositionalRandomFactory mazeRandom, int axisId) {
        int cellIndex = Math.floorDiv(coord, cellSize);
        int local = coord - cellIndex * cellSize;

        int gridlineIndex;
        int distance;
        if (local <= wallHalf) {
            gridlineIndex = cellIndex;
            distance = local;
        } else if (local >= cellSize - wallHalf) {
            gridlineIndex = cellIndex + 1;
            distance = cellSize - local;
        } else {
            return Band.NONE;
        }

        int segmentIndex = Math.floorDiv(otherCoord, cellSize);
        RandomSource rand = mazeRandom.at(gridlineIndex, axisId, segmentIndex);
        boolean segmentOpen = rand.nextFloat() < SEGMENT_OPEN_CHANCE;

        if (segmentOpen && distance <= openHalf) {
            return Band.OPEN_CORE;
        }
        return Band.WALL_RING;
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
        return BASE_HEIGHT_HINT;
    }

    @Override
    public @NotNull NoiseColumn getBaseColumn(int x, int z, @NotNull LevelHeightAccessor level, @NotNull RandomState randomState) {
        return new NoiseColumn(0, new BlockState[0]);
    }

    @Override
    public void addDebugScreenInfo(@NotNull List<String> info, @NotNull RandomState randomState, @NotNull BlockPos pos) {}
}
