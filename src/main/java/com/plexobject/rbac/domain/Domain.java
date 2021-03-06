package com.plexobject.rbac.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.validator.GenericValidator;

import com.plexobject.rbac.Configuration;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * The application defines a subject application that will define a set of
 * permissions and then validates them at runtime
 * 
 */
@Entity
@XmlRootElement
public class Domain extends PersistentObject implements Validatable,
        Identifiable<String> {
    public static final String DEFAULT_DOMAIN_NAME = Configuration
            .getInstance().getProperty("default.domain", "default");

    @PrimaryKey
    private String id;
    private String description;
    // @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity =
    // Subject.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
    Set<String> ownerSubjectNames = new HashSet<String>();

    // for JPA
    Domain() {
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public Domain(final String id, final String description) {
        setId(id);
        setDescription(description);
    }

    void setId(final String id) {
        if (GenericValidator.isBlankOrNull(id)) {
            throw new IllegalArgumentException("id is not specified");
        }
        this.id = id;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public Set<String> getOwnerSubjectNames() {
        return new HashSet<String>(ownerSubjectNames);
    }

    public void setOwnerSubjectNames(final Set<String> ownerSubjectNames) {
        firePropertyChange("ownerSubjectNames", this.ownerSubjectNames,
                ownerSubjectNames);

        this.ownerSubjectNames.clear();
        this.ownerSubjectNames.addAll(ownerSubjectNames);
    }

    public void addOwner(final String subjectName) {
        if (GenericValidator.isBlankOrNull(subjectName)) {
            throw new IllegalArgumentException("subjectName is not specified");
        }
        Set<String> old = getOwnerSubjectNames();
        this.ownerSubjectNames.add(subjectName);
        firePropertyChange("ownerSubjectNames", old, this.ownerSubjectNames);

    }

    public void addOwner(final Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject is not specified");
        }
        addOwner(subject.getId());
    }

    public void removeOwner(final Subject subject) {
        if (subject == null) {
            throw new IllegalArgumentException("subject is not specified");
        }
        removeOwner(subject.getId());
    }

    public void removeOwner(final String subjectName) {
        if (GenericValidator.isBlankOrNull(subjectName)) {
            throw new IllegalArgumentException("subjectName is not specified");
        }
        Set<String> old = getOwnerSubjectNames();
        this.ownerSubjectNames.remove(subjectName);
        firePropertyChange("ownerSubjectNames", old, this.ownerSubjectNames);
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Domain)) {
            return false;
        }
        Domain rhs = (Domain) object;
        return new EqualsBuilder().append(this.id, rhs.id).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(786529047, 1924536713).append(this.id)
                .toHashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", this.id).append(
                "owners", this.ownerSubjectNames).toString();
    }

    @Override
    public void validate() throws ValidationException {
        final Map<String, String> errorsByField = new HashMap<String, String>();
        if (GenericValidator.isBlankOrNull(id)) {
            errorsByField.put("name", "domain id is not specified");
        }
        if (errorsByField.size() > 0) {
            throw new ValidationException(errorsByField);
        }
    }

}
