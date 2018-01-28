package com.csye6225.spring2018.user;

import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long> {

    User findByEmailID(String emailID);
    User findByEmailIDAndPassword(String emailID, String password);
}
