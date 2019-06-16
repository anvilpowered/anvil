package rocks.milspecsg.msrepository.model.results;
import java.util.Optional;

public abstract class CreateResult<T, TText> extends Result<TText> {

    private T value;

    public CreateResult(T value, boolean success, TText errorMessage) {
        this.value = value;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public CreateResult(String errorMessage) {
        this.errorMessage = toDefaultErrorMessage(errorMessage);
        this.value = null;
    }

    // IntelliJ says I shouldn't use an Optional as a type parameter,
    // but this whole class is basically an optional with an error message
    public CreateResult(Optional<? extends T> value, TText errorMessage) {
        this.success = value.isPresent();
        this.value = value.orElse(null);
        this.errorMessage = errorMessage;
    }

    public CreateResult(Optional<? extends T> value, String errorMessage) {
        this.success = value.isPresent();
        this.value = value.orElse(null);
        this.errorMessage = toDefaultErrorMessage(errorMessage);
    }


    public static <A, B> CreateResult<A, B> success(A value, Class<? extends CreateResult<A, B>> clazz) {
        try {
            CreateResult<A, B> result = clazz.newInstance();
            result.success = true;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <A, B> CreateResult<A, B> fail(B message, Class<? extends CreateResult<A, B>> clazz) {
        try {
            CreateResult<A, B> result = clazz.newInstance();
            result.success = false;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Optional<T> getValue() {
        return success ? Optional.ofNullable(value) : Optional.empty();
    }


}
