package rocks.milspecsg.mscore.common.plugin;

import com.google.inject.Inject;
import rocks.milspecsg.mscore.api.plugin.PluginMessages;
import rocks.milspecsg.msrepository.api.util.PluginInfo;
import rocks.milspecsg.msrepository.api.util.StringResult;
import rocks.milspecsg.msrepository.api.util.TimeFormatService;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class MSCorePluginMessages<TString, TCommandSource> implements PluginMessages<TString> {

    @Inject
    protected PluginInfo<TString> pluginInfo;

    @Inject
    protected StringResult<TString, TCommandSource> stringResult;

    @Inject
    protected TimeFormatService timeFormatService;

    @Override
    public TString getBanMessage(String reason, Instant endUtc) {
        return stringResult.builder()
            .red().append("You have been banned for: ", stringResult.deserialize(reason))
            .yellow().append("\n\nFor another ", timeFormatService.format(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(), endUtc)))
            .append("\n\nUntil ", timeFormatService.format(endUtc))
            .build();
    }

    @Override
    public TString getMuteMessage(String reason, Instant endUtc) {
        return stringResult.builder()
            .append(pluginInfo.getPrefix())
            .red().append("You have been muted for: ", stringResult.deserialize(reason))
            .yellow().append("\nFor another ", timeFormatService.format(Duration.between(OffsetDateTime.now(ZoneOffset.UTC).toInstant(), endUtc)))
            .build();
    }
}
