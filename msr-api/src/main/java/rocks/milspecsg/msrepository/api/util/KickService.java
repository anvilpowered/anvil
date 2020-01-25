package rocks.milspecsg.msrepository.api.util;

import java.util.UUID;

public interface KickService {

    void kick(UUID userUUID, String reason);

    void kick(String userName, String reason);

    void kick(UUID userUUID);

    void kick(String userName);
}
