package rocks.milspecsg.msrepository.api.repository;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteId;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.objects.ObjectFilter;
import rocks.milspecsg.msrepository.api.cache.RepositoryCacheService;
import rocks.milspecsg.msrepository.datastore.nitrite.NitriteConfig;
import rocks.milspecsg.msrepository.model.data.dbo.ObjectWithId;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface NitriteRepository<T extends ObjectWithId<NitriteId>, C extends RepositoryCacheService<NitriteId, T>> extends Repository<NitriteId, T, C, Nitrite, NitriteConfig> {

    CompletableFuture<WriteResult> delete(ObjectFilter filter);

    default Optional<ObjectFilter> asFilter(NitriteId id) {
        return Optional.empty();
    }
}
