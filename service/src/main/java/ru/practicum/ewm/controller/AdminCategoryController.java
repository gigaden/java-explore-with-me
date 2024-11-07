package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.RequestCategoryDto;
import ru.practicum.ewm.dto.ResponseCategoryDto;
import ru.practicum.ewm.entity.Category;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Qualifier("categoryServiceImpl")
    CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    // Добавляем категорию. Обратите внимание: имя категории должно быть уникальным
    @PostMapping
    public ResponseEntity<ResponseCategoryDto> createCategory(@Valid @RequestBody RequestCategoryDto category) {

        Category newCategory = categoryService.createCategory(category);

        return new ResponseEntity<>(CategoryMapper.mapToCategoryDto(newCategory), HttpStatus.CREATED);

    }


    // Обновляем категорию. Обратите внимание: имя категории должно быть уникальным
    @PatchMapping("/{catId}")
    public ResponseEntity<ResponseCategoryDto> updateCategory(@PathVariable long catId,
                                                              @Valid @RequestBody RequestCategoryDto categoryDto) {

        System.out.println("yes");
        Category category = categoryService.updateCategory(catId, categoryDto);

        return new ResponseEntity<>(CategoryMapper.mapToCategoryDto(category), HttpStatus.OK);

    }


    // Удаляем категорию. Обратите внимание: с категорией не должно быть связано ни одного события.
    @DeleteMapping("/{catId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long catId) {

        categoryService.deleteCategory(catId);

        return new ResponseEntity<>("Категория удалена", HttpStatus.NO_CONTENT);
    }


}
