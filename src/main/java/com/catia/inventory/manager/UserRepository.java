package com.catia.inventory.manager;

import com.catia.inventory.manager.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
}
