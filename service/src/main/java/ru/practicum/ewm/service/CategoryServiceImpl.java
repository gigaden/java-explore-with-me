package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryRequestDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.exception.CategoryNotFoundException;
import ru.practicum.ewm.exception.CategoryValidationException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service("categoryServiceImpl")
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category createCategory(CategoryRequestDto dto) {
        log.info("Пытаюсь добавить новую категорию {}", dto);
        checkCategoryName(dto.getName());
        Category newCategory = categoryRepository.save(CategoryMapper.mapDtoToCategory(dto));
        log.info("Новая категория добавлена {}", newCategory);

        return newCategory;
    }

    @Override
    public Category updateCategory(long catId, CategoryRequestDto categoryDto) {
        log.info("Пытаюсь обновить категорию с id = {}", catId);
        checkCategoryName(categoryDto.getName());
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.info("Категория с id = {} не найдена", catId);
                    return new CategoryNotFoundException(String
                            .format("Категории с id = %d не существует", catId));
                });
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.info("Категория с id = {} обновлена", catId);
        return category;
    }

    @Override
    public void deleteCategory(long catId) {
        log.info("Пытаюсь удалить категорию с id = {}", catId);
        checkCategoryIsExist(catId);
        categoryRepository.deleteById(catId);
        log.info("Категория с id = {} удалена", catId);
    }

    @Override
    public Collection<Category> getAll(int from, int size) {
        log.info("Пытаюсь получить категории в диапазаоне from = {} size = {}", from, size);
        Collection<Category> categories = categoryRepository.findCategoriesBetweenFromAndSize(from, size);
        log.info("Категории в диапазаоне from = {} size = {} получены", from, size);

        return categories;
    }

    @Override
    public Category getById(long catId) {
        log.info("Пытаюсь получить категорию с id = {}", catId);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Категория с id = {} не найдена", catId);
                    return new CategoryNotFoundException(String.format("Категория с id = %d не найдена", catId));
                });
        log.info("Категория с id = {} получена", catId);

        return category;
    }

    // Проверяем уникальность имени категории
    private void checkCategoryName(String name) {
        log.info("Проверяем имя категории name = {} на уникальность", name);
        List<String> names = categoryRepository.findAll().stream().map(Category::getName).toList();

        if (names.contains(name)) {
            log.warn("Имя категории = {} не уникально", name);
            throw new CategoryValidationException("Имя категории должно быть уникальным");
        }
    }

    // Проверяем, что категория существует
    private void checkCategoryIsExist(long catId) {
        log.info("Проверяю существует ли категория с id = {}", catId);
        if (categoryRepository.findById(catId).isEmpty()) {
            log.warn("Категория с id = {} не существует", catId);
            throw new CategoryNotFoundException(String.format("Категории с id = %s не существует", catId));
        }
    }
}
