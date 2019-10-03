package rocks.milspecsg.msrepository.model.data.dbo;

import java.util.Date;

public interface ObjectWithId<TKey> {

    TKey getId();
    void setId(TKey id);

    Date getUpdatedUtc();
}