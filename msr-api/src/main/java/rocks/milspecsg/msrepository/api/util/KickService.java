package rocks.milspecsg.msrepository.api.util;

import java.util.UUID;

public interface KickService {

    void kick(UUID userUUID, String reason);

    void kick(UUID userUUID);
}
