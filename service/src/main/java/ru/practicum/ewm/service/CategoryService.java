package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestCategoryDto;
import ru.practicum.ewm.dto.ResponseCategoryDto;
import ru.practicum.ewm.entity.Category;

public interface CategoryService {

    Category createCategory(RequestCategoryDto category);

    Category updateCategory(long catId, RequestCategoryDto categoryDto);

    void deleteCategory(long catId);
}
