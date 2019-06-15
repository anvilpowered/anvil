package rocks.milspecsg.msrepository.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import rocks.milspecsg.msrepository.api.CacheInvalidationService;
import rocks.milspecsg.msrepository.api.Repository;
import rocks.milspecsg.msrepository.api.config.ConfigKeys;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Singleton
public abstract class ApiCacheInvalidationService<T> implements CacheInvalidationService<T> {

    protected ConfigurationService configurationService;

    protected Map<T, Long> cache;

    @Inject
    public ApiCacheInvalidationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        cache = new HashMap<>();
        Integer interval = configurationService.getConfigInteger(ConfigKeys.CACHE_INVALIDATION_INTERVAL_SECONDS_INT);
        //Task.builder().interval(interval, TimeUnit.SECONDS).execute(getCacheInvalidationTask()).submit(MSParties.plugin);
        startCacheInvalidationTask();
    }

    abstract void startCacheInvalidationTask();

    /**
     * Cache invalidation task
     */
    @Override
    public Runnable getCacheInvalidationTask() {
        return () -> {
            Integer timeoutSeconds = configurationService.getConfigInteger(ConfigKeys.CACHE_INVALIDATION_TIMOUT_SECONDS_INT);

            List<T> toRemove = new ArrayList<>();
            for (T t : getAll()) {
                if (System.currentTimeMillis() - cache.get(t) > timeoutSeconds * 1000L) {
                    // if time has gone over limit
                    toRemove.add(t);
                    //Sponge.getServer().getConsole().sendMessage(Text.of(PluginInfo.PluginPrefix, "Removing ", t));
                }
            }
            // remove from map
            toRemove.forEach(this::remove);
        };
    }

    @Override
    public Set<T> getAll() {
        return cache.keySet();
    }

    @Override
    public Optional<T> put(T t) {
        if (t == null) return Optional.empty();
        cache.put(t, System.currentTimeMillis());
        return Optional.of(t);
    }

    @Override
    public List<T> put(List<T> list) {
        return list.stream().map(t -> put(t).orElse(null)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public void remove(T t) {
        cache.remove(t);
    }

    @Override
    public void remove(Predicate<? super T> predicate) {
        getOne(predicate).ifPresent(this::remove);
    }

    @Override
    public List<T> getAll(Predicate<? super T> predicate) {
        return getAll().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Optional<T> getOne(Predicate<? super T> predicate) {
        return Repository.single(getAll(predicate));
    }
}
