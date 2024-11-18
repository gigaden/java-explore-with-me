package ru.practicum.ewm.service.category;

import ru.practicum.ewm.dto.category.CategoryCreateDto;
import ru.practicum.ewm.dto.category.CategoryRequestDto;
import ru.practicum.ewm.entity.Category;

import java.util.Collection;

public interface CategoryService {

    Category createCategory(CategoryCreateDto category);

    Category updateCategory(long catId, CategoryRequestDto categoryDto);

    void deleteCategory(long catId);

    Collection<Category> getAll(int from, int size);

    Category getById(long catId);
}
