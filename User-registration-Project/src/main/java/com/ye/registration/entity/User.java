package com.ye.registration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import java.util.Collection;

@Entity
@Table(name="user")
@Getter
@Setter
@NoArgsConstructor
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    @Column(unique=true)
    @NaturalId(mutable = true )
    private String email;

    @Column(nullable = false)
    private String password;

    private boolean isEnabled;

    //cuz default many to many relationship are lazy
    @ManyToMany(fetch = FetchType.EAGER , cascade = CascadeType.ALL)
    @JoinTable(name="user_roles" ,
            joinColumns = @JoinColumn(name="user_id" , referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name="role_id" , referencedColumnName = "id"))
    private Collection<Role> roles;

    public User(String firstName , String lastName , String email ,
                String password , Collection<Role> roles ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
