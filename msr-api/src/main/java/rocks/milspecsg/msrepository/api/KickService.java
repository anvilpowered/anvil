package rocks.milspecsg.msrepository.api;

import java.util.UUID;

public interface KickService {

    void kick(UUID userUUID, String reason);

    void kick(UUID userUUID);
}
