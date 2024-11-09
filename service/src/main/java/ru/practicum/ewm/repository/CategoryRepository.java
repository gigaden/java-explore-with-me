package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = """
                SELECT *
                FROM categories c
                LIMIT ?2 OFFSET ?1
            """, nativeQuery = true)
    List<Category> findCategoriesBetweenFromAndSize(int from, int size);

}