package com.yugabyte.samples.tradex.api.domain.db;

import java.time.Instant;

public class BaseEntity {
    private Instant createdDate;
    private Instant updatedDate;

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }


    public void prePersist() {
        this.createdDate = Instant.now();
        this.updatedDate = Instant.now();
    }


    public void preUpdate() {
        this.updatedDate = Instant.now();
    }
}
