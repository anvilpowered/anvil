package rocks.milspecsg.msrepository.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import rocks.milspecsg.msrepository.api.util.KickService;

import javax.inject.Inject;
import java.util.UUID;

public class VelocityKickService implements KickService {

    @Inject
    private ProxyServer proxyServer;

    @Override
    public void kick(UUID userUUID, String reason) {
        proxyServer.getPlayer(userUUID).ifPresent(p -> p.disconnect(TextComponent.of(reason)));
    }

    @Override
    public void kick(String userName, String reason) {
        proxyServer.getPlayer(userName).ifPresent(p -> p.disconnect(TextComponent.of(reason)));
    }

    @Override
    public void kick(UUID userUUID) {
        kick(userUUID, "You have been kicked");
    }

    @Override
    public void kick(String userName) {
        kick(userName, "You have been kicked");
    }
}
