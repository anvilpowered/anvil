package rocks.milspecsg.msrepository.model.data.dbo;

import org.dizitart.no2.NitriteId;
import org.dizitart.no2.objects.Id;

import java.util.Date;

public abstract class NitriteDbo implements ObjectWithId<NitriteId> {

    @Id
    private NitriteId id;

    private Date createdUtc;
    private Date updatedUtc;

    public NitriteId getId() {
        return id;
    }

    public void setId(NitriteId id) {
        this.id = id;
        createdUtc = new Date();
        prePersist();
    }

    @Override
    public String getIdAsString() {
        return id.toString();
    }

    @Override
    public Date getCreatedUtc() {
        return createdUtc;
    }

    @Override
    public Date getUpdatedUtc() {
        return updatedUtc;
    }

    protected void prePersist() {
        updatedUtc = new Date();
    }
}
