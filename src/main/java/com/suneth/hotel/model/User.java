package com.suneth.hotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();

    /**
     *The @ManyToMany annotation is used to establish a many-to-many relationship between User and Role entities.
     *This implies that a user can have multiple roles, and a role can be associated with multiple users.
     * fetch = FetchType.EAGER specifies that the roles associated with the user should be eagerly loaded from the database.

     * cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH} specifies the cascade operations to be
     applied to associated roles.

     **CascadeType.PERSIST**: This cascade type means that when a new `User` entity is persisted
     * (i.e., saved to the database using the `EntityManager.persist()` method), any
     * transient (newly created) `Role` entities associated with that user will also be persisted.
     * In other words, if a `Role` entity is added to the `roles` collection of a `User` entity and
     * the `User` entity is persisted, the associated `Role` entity will also be saved to the database.

     **CascadeType.MERGE**: This cascade type means that when a `User` entity is merged into the persistence context
     * (i.e., its state is synchronized with the database using the `EntityManager.merge()` method),
     * any detached (previously managed) `Role` entities associated with that user will also be merged.
     * In other words, if a `Role` entity is added to the `roles` collection of a `User` entity and the `User`
     * entity is merged, any changes to the associated `Role` entity will also be synchronized with the database.

     **CascadeType.DETACH**: This cascade type means that when a `User` entity is detached from the persistence context
     * (i.e., it is no longer managed by the persistence context), any managed `Role` entities associated with that
     * user will also be detached.
     * In other words, if a `Role` entity is added to the `roles` collection of a `User`entity and the `User` entity is detached,
     * the associated `Role` entity will also become detached and will no longer be managed by the persistence context.

     * @JoinTable annotation specifies the details of the join table that will be used to represent the
     many-to-many relationship between users and roles. The name attribute specifies the name of the join table,
     while joinColumns and inverseJoinColumns specify the columns in the join table that reference the user ID and role ID,
     respectively. roles field represents the collection of roles associated with the user.
     It is initialized as a HashSet to ensure uniqueness of roles and faster access.*/
}
