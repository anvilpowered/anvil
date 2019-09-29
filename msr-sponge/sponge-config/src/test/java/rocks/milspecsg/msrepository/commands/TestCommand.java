package rocks.milspecsg.msrepository.commands;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import rocks.milspecsg.msrepository.api.config.ConfigurationService;
import rocks.milspecsg.msrepository.service.config.ConfigKeys;

import java.util.List;

public class TestCommand implements CommandExecutor {

    @Inject
    ConfigurationService configurationService;


    @Override
    public CommandResult execute(CommandSource source, CommandContext context) throws CommandException {
        source.sendMessage(Text.of("test start"));

        configurationService.addToConfigList(ConfigKeys.SOME_LIST, 15, new TypeToken<List<Integer>>() {});

        configurationService.save();

        return CommandResult.success();
    }
}
