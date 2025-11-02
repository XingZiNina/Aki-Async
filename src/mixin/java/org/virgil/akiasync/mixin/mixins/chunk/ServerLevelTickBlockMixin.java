package org.virgil.akiasync.mixin.mixins.chunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(value = ServerLevel.class)
public abstract class ServerLevelTickBlockMixin {

    @Unique
    private static final boolean ENABLED = true;

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("AkiAsync");

    @Unique
    private static final AtomicInteger pendingTasks = new AtomicInteger(0);

    @Unique
    private static final int MAX_PENDING_TASKS = 50;

    @Unique
    private static final int MAX_QUEUE_SIZE = 4096;

    @Unique
    private static long totalTasksSubmitted = 0;
    @Unique
    private static long totalTasksRejected = 0;

    @Unique
    private static volatile ExecutorService executorService;

    @Unique
    private static synchronized ExecutorService getExecutor() {
        if (executorService == null) {
            int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
            executorService = new ThreadPoolExecutor(
                    poolSize,
                    poolSize,
                    30L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(MAX_QUEUE_SIZE),
                    r -> {
                        Thread t = new Thread(r, "AkiAsync-Pool");
                        t.setDaemon(true);
                        return t;
                    },
                    (r, executor) -> {
                        totalTasksRejected++;
                        if (totalTasksRejected % 100 == 0) {
                            LOGGER.warn("线程池队列已满，已拒绝 {} 个任务，回退到主线程执行", totalTasksRejected);
                        }
                    }
            );
            LOGGER.info("[AkiAsync] 线程池已初始化: 线程数={}, 队列大小={}, 最大待处理任务={}",
                    poolSize, MAX_QUEUE_SIZE, MAX_PENDING_TASKS);
        }
        return executorService;
    }

    @Inject(method = "tickBlock", at = @At("HEAD"), cancellable = true)
    private void aki$controlledAsyncTickBlock(BlockPos pos, Block block, CallbackInfo ci) {
        if (!ENABLED) return;

        ServerLevel level = (ServerLevel) (Object) this;
        BlockState blockState = level.getBlockState(pos);

        if (!blockState.is(block)) {
            ci.cancel();
            return;
        }

        String blockName = block.getDescriptionId().toLowerCase();
        if (isUnsafeBlock(blockName)) {
            return;
        }

        int currentPending = pendingTasks.get();
        if (currentPending >= MAX_PENDING_TASKS) {
            totalTasksRejected++;
            if (totalTasksRejected % 100 == 0) {
                LOGGER.warn("流量控制：已拒绝 {} 个任务，当前待处理: {}", totalTasksRejected, currentPending);
            }
            return;
        }

        pendingTasks.incrementAndGet();
        totalTasksSubmitted++;

        final ServerLevel taskLevel = level;
        final BlockPos taskPos = pos;
        final BlockState taskState = blockState;

        try {
            getExecutor().execute(() -> {
                try {
                    taskState.tick(taskLevel, taskPos, taskLevel.random);
                } catch (Throwable t) {
                    handleAsyncError(taskLevel, taskPos, block, t);
                } finally {
                    pendingTasks.decrementAndGet();
                }
            });

            ci.cancel();
        } catch (Exception e) {
            pendingTasks.decrementAndGet();
            LOGGER.warn("任务提交失败，回退到同步执行: {}", e.getMessage());
        }
    }

    @Unique
    private boolean isUnsafeBlock(String blockName) {
        return
                blockName.contains("water") ||
                        blockName.contains("lava") ||
                        blockName.contains("bubble") ||
                        blockName.contains("flowing") ||
                        blockName.contains("redstone") ||
                        blockName.contains("comparator") ||
                        blockName.contains("repeater") ||
                        blockName.contains("observer") ||
                        blockName.contains("piston") ||
                        blockName.contains("dispenser") ||
                        blockName.contains("dropper") ||
                        blockName.contains("hopper") ||
                        blockName.contains("lever") ||
                        blockName.contains("button") ||
                        blockName.contains("pressure_plate") ||
                        blockName.contains("tripwire") ||
                        blockName.contains("target") ||
                        blockName.contains("daylight_detector") ||
                        blockName.contains("tnt") ||
                        blockName.contains("note_block") ||
                        blockName.contains("fire") ||
                        blockName.contains("torch") ||
                        blockName.contains("lantern") ||
                        blockName.contains("campfire") ||
                        blockName.contains("leaves") ||
                        blockName.contains("sapling") ||
                        blockName.contains("grass") ||
                        blockName.contains("big_dripleaf") ||
                        blockName.contains("dripleaf") ||
                        blockName.contains("fern") ||
                        blockName.contains("flower") ||
                        blockName.contains("mushroom") ||
                        blockName.contains("vine") ||
                        blockName.contains("lily") ||
                        blockName.contains("cactus") ||
                        blockName.contains("sugar_cane") ||
                        blockName.contains("bamboo") ||
                        blockName.contains("kelp") ||
                        blockName.contains("seagrass") ||
                        blockName.contains("sea_pickle") ||
                        blockName.contains("coral") ||
                        blockName.contains("azalea") ||
                        blockName.contains("mangrove") ||
                        blockName.contains("cherry") ||
                        blockName.contains("spore_blossom") ||
                        blockName.contains("moss") ||
                        blockName.contains("chorus") ||
                        blockName.contains("eyeblossom") ||
                        blockName.contains("command") ||
                        blockName.contains("structure") ||
                        blockName.contains("spawner") ||
                        blockName.contains("bed") ||
                        blockName.contains("door") ||
                        blockName.contains("trapdoor") ||
                        blockName.contains("fence_gate") ||
                        blockName.contains("chest") ||
                        blockName.contains("barrel") ||
                        blockName.contains("furnace") ||
                        blockName.contains("enchanting_table") ||
                        blockName.contains("anvil") ||
                        blockName.contains("beacon") ||
                        blockName.contains("conduit") ||
                        blockName.contains("bell") ||
                        blockName.contains("portal") ||
                        blockName.contains("end_gateway") ||
                        blockName.contains("dragon_egg") ||
                        blockName.contains("sponge") ||
                        blockName.contains("cake") ||
                        blockName.contains("scaffolding") ||
                        blockName.contains("sculk") ||
                        blockName.contains("magma") ||
                        blockName.contains("soul") ||
                        blockName.contains("crying_obsidian") ||
                        blockName.contains("copper") ||
                        blockName.contains("farmland") ||
                        blockName.contains("composter") ||
                        blockName.contains("bee_nest") ||
                        blockName.contains("candle") ||
                        blockName.contains("rail") ||
                        blockName.contains("pointed_dripstone") ||
                        blockName.contains("lightning_rod") ||
                        blockName.contains("powder_snow") ||
                        blockName.contains("amethyst_cluster") ||
                        blockName.contains("budding_amethyst") ||
                        blockName.contains("calibrated_sculk_sensor") ||
                        blockName.contains("reinforced_deepslate") ||
                        blockName.contains("decorated_pot") ||
                        blockName.contains("suspicious_sand") ||
                        blockName.contains("suspicious_gravel") ||
                        blockName.contains("trial_spawner") ||
                        blockName.contains("vault");
    }

    @Unique
    private void handleAsyncError(ServerLevel level, BlockPos pos, Block block, Throwable t) {
        if (isAsyncError(t)) {
            level.getServer().execute(() -> {
                try {
                    BlockState current = level.getBlockState(pos);
                    if (current.is(block)) {
                        current.tick(level, pos, level.random);
                        if (Math.random() < 0.01) {
                            LOGGER.warn("异步失败，已回退到同步执行: {} at {}", block, pos);
                        }
                    }
                } catch (Throwable ignored) {}
            });
        }
    }

    @Unique
    private boolean isAsyncError(Throwable t) {
        if (t == null) return false;
        String msg = t.getMessage();
        String className = t.getClass().getName();
        return (msg != null && (
                msg.contains("async") ||
                        msg.contains("main thread") ||
                        msg.contains("thread")
        )) || className.contains("AsyncCatcher");
    }
}
