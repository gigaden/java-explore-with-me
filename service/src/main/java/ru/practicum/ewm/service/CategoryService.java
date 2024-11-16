package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryCreateDto;
import ru.practicum.ewm.dto.CategoryRequestDto;
import ru.practicum.ewm.entity.Category;

import java.util.Collection;

public interface CategoryService {

    Category createCategory(CategoryCreateDto category);

    Category updateCategory(long catId, CategoryRequestDto categoryDto);

    void deleteCategory(long catId);

    Collection<Category> getAll(int from, int size);

    Category getById(long catId);
}
