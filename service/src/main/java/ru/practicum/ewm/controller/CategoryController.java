package ru.practicum.ewm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.CategoryResponseDto;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.service.CategoryService;

import java.util.Collection;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /*Получаем все категории.
     В случае, если по заданным фильтрам не найдено ни одной категории, возвращает пустой список*/
    @GetMapping
    public ResponseEntity<Collection<CategoryResponseDto>> getAll(@RequestParam(defaultValue = "0") int from,
                                                                  @RequestParam(defaultValue = "10") int size) {

        Collection<CategoryResponseDto> categories = categoryService.getAll(from, size).stream()
                .map(CategoryMapper::mapToCategoryDto).toList();

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    // Получение категории по id. В случае, если категории с заданным id не найдено, возвращает статус код 404
    @GetMapping("/{catId}")
    public ResponseEntity<CategoryResponseDto> getById(@PathVariable long catId) {

        CategoryResponseDto category = CategoryMapper.mapToCategoryDto(categoryService.getById(catId));

        return new ResponseEntity<>(category, HttpStatus.OK);
    }
}
