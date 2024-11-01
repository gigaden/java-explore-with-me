package ru.practictum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practictum.server.entity.Statistic;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

}
