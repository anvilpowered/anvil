package rocks.milspecsg.anvil.velocity.util;

import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.text.TextComponent;
import rocks.milspecsg.anvil.api.util.KickService;

import javax.inject.Inject;
import java.util.UUID;

public class VelocityKickService implements KickService {

    @Inject
    private ProxyServer proxyServer;

    private TextComponent getReason(Object reason) {
        return reason instanceof TextComponent ? (TextComponent) reason : TextComponent.of(reason.toString());
    }

    @Override
    public void kick(UUID userUUID, Object reason) {
        proxyServer.getPlayer(userUUID).ifPresent(p -> p.disconnect(getReason(reason)));
    }

    @Override
    public void kick(String userName, Object reason) {
        proxyServer.getPlayer(userName).ifPresent(p -> p.disconnect(getReason(reason)));
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
