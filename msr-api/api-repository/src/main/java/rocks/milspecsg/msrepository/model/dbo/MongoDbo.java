package rocks.milspecsg.msrepository.model.dbo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PrePersist;

import java.util.Date;

public abstract class MongoDbo implements ObjectWithId<ObjectId> {

    @Id
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

    @PrePersist
    void prePersist() {updatedUtc = new Date();}
}

