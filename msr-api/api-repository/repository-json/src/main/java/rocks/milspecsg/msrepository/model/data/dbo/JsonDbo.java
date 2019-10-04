package rocks.milspecsg.msrepository.model.data.dbo;

import org.bson.types.ObjectId;

import java.util.Date;

public abstract class JsonDbo implements ObjectWithId<ObjectId> {

    private ObjectId id;

    private Date updatedUtc;


    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Date getUpdatedUtc() {
        return updatedUtc;
    }

    void prePersist() {updatedUtc = new Date();}
}
