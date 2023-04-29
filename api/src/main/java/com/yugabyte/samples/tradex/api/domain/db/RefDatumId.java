package com.yugabyte.samples.tradex.api.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefDatumId implements Serializable {
    private static final long serialVersionUID = -3709233261715645987L;

    private Integer id;
    private String keyName;
    private String classifier;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefDatumId that = (RefDatumId) o;
        return Objects.equals(id, that.id) && Objects.equals(keyName, that.keyName) && Objects.equals(classifier, that.classifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, keyName, classifier);
    }
}
