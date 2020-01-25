package rocks.milspecsg.msrepository.api.util;

import java.util.UUID;

public interface KickService {

    void kick(UUID userUUID, Object reason);

    void kick(String userName, Object reason);

    void kick(UUID userUUID);

    void kick(String userName);
}
