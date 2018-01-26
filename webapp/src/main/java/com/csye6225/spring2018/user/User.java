package com.csye6225.spring2018.user;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    public Long getId() {
        return id;
    }

    @Column(name = "emailID")
    private String emailID;

    @Column(name = "password")
    private String password;

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //    @Column(name = "created")
//    @CreationTimestamp
//    private Date created;
//
//    @Column(name = "updated")
//    @UpdateTimestamp
//    private Date updated;

    //getters and setters

}
