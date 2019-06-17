package rocks.milspecsg.msrepository.model.results;

import org.mongodb.morphia.query.UpdateResults;

import java.util.function.Function;

public abstract class UpdateResult<TText> extends Result<TText> {

    private UpdateResults updateResults;

    public UpdateResults getUpdateResults() {
        return updateResults;
    }

    public UpdateResult(UpdateResults updateResults, boolean success, TText successMessage, TText errorMessage) {
        this.updateResults = updateResults;
        this.success = success;
        this.successMessage = successMessage;
        this.errorMessage = errorMessage;
    }

    public UpdateResult(UpdateResults updateResults, Function<UpdateResults, Boolean> successCondition, TText successMessage, TText errorMessage) {
        this(updateResults, successCondition.apply(updateResults), successMessage, errorMessage);
    }

    public UpdateResult(UpdateResults updateResults, Function<UpdateResults, Boolean> successCondition) {
        this.updateResults = updateResults;
        this.success = successCondition.apply(updateResults);
    }

    public UpdateResult(UpdateResults updateResults, TText successMessage, TText errorMessage) {
        this(updateResults, updateResults.getUpdatedCount() > 0, successMessage, errorMessage);
    }

    public UpdateResult(UpdateResults updateResults, boolean success) {
        this.updateResults = updateResults;
        this.success = success;
    }

    protected UpdateResult() {
    }

    public UpdateResult(UpdateResults updateResults) {
        this(updateResults, u -> u.getUpdatedCount() > 0);
    }

    public static <T> UpdateResult success(Class<? extends UpdateResult<T>> clazz) {
        try {
            UpdateResult<T> result = clazz.newInstance();
            result.updateResults = null;
            result.success = true;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> UpdateResult fail(Class<? extends UpdateResult<T>> clazz) {
        try {
            UpdateResult<T> result = clazz.newInstance();
            result.updateResults = null;
            result.success = false;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
