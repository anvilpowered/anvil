package rocks.milspecsg.mscore.api.plugin;

import java.time.Instant;

public interface PluginMessages<TString> {

    TString getBanMessage(String reason, Instant endUtc);

    TString getMuteMessage(String reason, Instant endUtc);
}
