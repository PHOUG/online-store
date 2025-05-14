package phoug.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import phoug.store.model.LogTask;

@Repository
public interface LogRepository extends JpaRepository<LogTask, Long> {
}
