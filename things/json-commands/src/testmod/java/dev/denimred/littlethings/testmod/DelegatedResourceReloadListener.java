package dev.denimred.littlethings.testmod;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

final class DelegatedResourceReloadListener implements IdentifiableResourceReloadListener {
    private final ResourceLocation id;
    private final PreparableReloadListener delegate;

    public DelegatedResourceReloadListener(ResourceLocation id, PreparableReloadListener delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public ResourceLocation getFabricId() {
        return id;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return delegate.reload(preparationBarrier, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
    }
}
