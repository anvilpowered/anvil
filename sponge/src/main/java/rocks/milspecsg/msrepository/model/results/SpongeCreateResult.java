package rocks.milspecsg.msrepository.model.results;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SpongeCreateResult<T> extends CreateResult<T, Text> {

    @Override
    public Text getDefaultSuccessMessage() {
        return Text.of(TextColors.GREEN, "success");
    }

    @Override
    public Text getDefaultErrorMessage() {
        return Text.of(TextColors.RED, "error");
    }

    @Override
    public Text toDefaultErrorMessage(String errorMessage) {
        return Text.of(TextColors.RED, errorMessage);
    }

}
