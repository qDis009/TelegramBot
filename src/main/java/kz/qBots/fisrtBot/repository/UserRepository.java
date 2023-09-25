package kz.qBots.fisrtBot.repository;

import kz.qBots.fisrtBot.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Long>{
    List<User> findAll();
}
