package com.pahappa.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;
    /**
     * @ManyToMany defines a "Many-to-Many" relationship.
     * This means "Many" Roles can have "Many" Permissions.
     *  Example: The "ADMIN" role has "DELETE_PATIENT" and "CREATE_DOCTOR" permissions.
     *  The "DELETE_PATIENT" permission can also be given to a "RECEPTIONIST" role.
     * <p>
     *  Since there's no direct column that can link two tables with a many-to-many
     *  relationship, Hibernate needs to create a third, separate "linking table".
     *  @JoinTable defines this linking table.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),//store the ID of the Role.
            inverseJoinColumns = @JoinColumn(name = "permission_id")//store the ID of the Permission.
    )
    private Set<Permissions> permissions = new HashSet<>();
    /**
     * @Set (like a HashSet):
     * A Set is like a collection of unique business cards.
     * •Order is NOT Guaranteed: When you throw cards into a box, you don't care about the order.
     * A Set makes no promises about the order of its items.
     * <p>
     * •Duplicates are FORBIDDEN: You would never keep two identical business cards for the same person.
     * If you try to add a card that's already in the box, a Set simply ignores the new one.
     * It ensures every item inside is unique.
     * <p>
     * I am telling Java and Hibernate:
     * "A Role can have a collection of permissions,
     * and I guarantee that every single permission in this collection will be unique."
     *
     */


    // Constructors
    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getters and Setters for permissions
    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    // equals and hashCode for proper object comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name; // Helpful for debugging
    }
}