package kz.qBots.fisrtBot.repository;

import kz.qBots.fisrtBot.model.Ads;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdsRepository extends CrudRepository<Ads,Long> {
    List<Ads> findAll();
}
