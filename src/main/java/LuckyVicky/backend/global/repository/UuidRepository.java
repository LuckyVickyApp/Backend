package LuckyVicky.backend.global.repository;

import LuckyVicky.backend.global.entity.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {

}
