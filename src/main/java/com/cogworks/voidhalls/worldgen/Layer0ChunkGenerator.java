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
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Layer0ChunkGenerator extends ChunkGenerator {

    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

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

    private static final ResourceLocation MAZE_KEY =
            ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_0_maze");

    private final BlockState scaffoldState;
    private final BlockState ceilingTileState;
    private final BlockState wallState;

    public Layer0ChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
        this.scaffoldState = BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_zero_scaffold"))
                .defaultBlockState();
        this.ceilingTileState = BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_zero_ceiling_tile"))
                .defaultBlockState();
        this.wallState = BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath("voidhalls", "layer_zero_wall"))
                .defaultBlockState();
    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public @NotNull CompletableFuture<ChunkAccess> fillFromNoise(@NotNull Blender blender, RandomState randomState, @NotNull StructureManager structureManager, ChunkAccess chunk) {
        long startNanos = System.nanoTime();

        PositionalRandomFactory mazeRandom = randomState.getOrCreateRandomFactory(MAZE_KEY);
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        int corridorMin = (CELL_SIZE - CORRIDOR_WIDTH + 1) / 2;
        int corridorMax = corridorMin + CORRIDOR_WIDTH - 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = minX + x;
                int worldZ = minZ + z;
                boolean open = isOpenColumn(worldX, worldZ, mazeRandom, corridorMin, corridorMax);

                for (int y = 0; y < DIM_HEIGHT; y++) {
                    pos.set(worldX, y, worldZ);
                    BlockState state;

                    if (y < FLOOR_Y) {
                        state = scaffoldState;
                    } else if (!open) {
                        state = (y <= CEILING_Y) ? wallState : scaffoldState;
                    } else if (y == FLOOR_Y) {
                        state = Blocks.BROWN_CARPET.defaultBlockState();
                    } else if (y <= ROOM_TOP) {
                        state = Blocks.AIR.defaultBlockState();
                    } else if (y == CEILING_Y) {
                        state = ceilingTileState;
                    } else {
                        state = scaffoldState;
                    }

                    chunk.setBlockState(pos, state, false);
                }
            }
        }

        double elapsedMs = (System.nanoTime() - startNanos) / 1_000_000.0;
        LOGGER.debug("[voidhalls] fillFromNoise {} took {} ms", chunk.getPos(), elapsedMs);

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
            return rand.nextFloat() < WALL_OPEN_CHANCE
                    && localZ >= corridorMin && localZ <= corridorMax;
        }

        if (onZBoundary) {
            RandomSource rand = mazeRandom.at(cellX, 1, cellZ);
            return rand.nextFloat() < WALL_OPEN_CHANCE
                    && localX >= corridorMin && localX <= corridorMax;
        }

        return true;
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