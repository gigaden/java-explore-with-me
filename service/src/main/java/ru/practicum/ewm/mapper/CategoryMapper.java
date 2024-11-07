package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.RequestCategoryDto;
import ru.practicum.ewm.dto.ResponseCategoryDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.repository.CategoryRepository;

public class CategoryMapper {

    public static ResponseCategoryDto mapToCategoryDto(Category category) {
        return ResponseCategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category mapDtoToCategory(RequestCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
