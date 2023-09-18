package kz.qBots.fisrtBot.repository;

import kz.qBots.fisrtBot.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Long>{
    
}
