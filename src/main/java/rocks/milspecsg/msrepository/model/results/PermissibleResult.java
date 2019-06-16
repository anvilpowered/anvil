package rocks.milspecsg.msrepository.model.results;

public abstract class PermissibleResult<TText> extends Result<TText> {

    private PermissibleResult() {
    }

    public static <T> PermissibleResult<T> success(T successMessage, Class<? extends PermissibleResult<T>> clazz) {
        try {
            PermissibleResult<T> result = clazz.newInstance();
            result.success = true;
            result.successMessage = successMessage;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> PermissibleResult<T> fail(T errorMessage, Class<? extends PermissibleResult<T>> clazz) {
        try {
            PermissibleResult<T> result = clazz.newInstance();
            result.success = false;
            result.errorMessage = errorMessage;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> PermissibleResult<T> fail(String errorMessage, Class<? extends PermissibleResult<T>> clazz) {
        try {
            PermissibleResult<T> result = clazz.newInstance();
            result.success = false;
            result.errorMessage = result.toDefaultErrorMessage(errorMessage);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> PermissibleResult <T> fail(Class<? extends PermissibleResult<T>> clazz) {
        return fail("You do not have permission to do that", clazz);
    }
}
