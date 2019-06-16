package rocks.milspecsg.msrepository.model.results;

public abstract class Result<TText> {

    protected boolean success;
    protected TText successMessage, errorMessage;

    public Result() {
        success = false;
        successMessage = getDefaultSuccessMessage();
        errorMessage = getDefaultErrorMessage();
    }

    public abstract TText getDefaultSuccessMessage();

    public abstract TText getDefaultErrorMessage();

    public abstract TText toDefaultErrorMessage(String errorMessage);

    public boolean isSuccess() {
        return success;
    }

    public TText getMessage() {
        return success ? successMessage : errorMessage;
    }

    public TText getErrorMessage() {
        return errorMessage;
    }


}
