package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.category.CategoryCreateDto;
import ru.practicum.ewm.dto.category.CategoryResponseDto;
import ru.practicum.ewm.entity.Category;

public class CategoryMapper {

    public static CategoryResponseDto mapToCategoryDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static Category mapDtoToCategory(CategoryCreateDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
