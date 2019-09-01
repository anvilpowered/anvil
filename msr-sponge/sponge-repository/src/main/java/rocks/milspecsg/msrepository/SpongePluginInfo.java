package rocks.milspecsg.msrepository;

import org.spongepowered.api.text.Text;

public interface SpongePluginInfo {

    String getId();

    String getName();

    String getVersion();

    String getDescription();

    String getURL();

    String getAuthors();

    Text getPrefix();

}
