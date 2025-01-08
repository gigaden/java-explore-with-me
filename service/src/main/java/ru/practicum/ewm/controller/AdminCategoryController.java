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
import ru.practicum.ewm.dto.category.CategoryCreateDto;
import ru.practicum.ewm.dto.category.CategoryRequestDto;
import ru.practicum.ewm.dto.category.CategoryResponseDto;
import ru.practicum.ewm.service.category.CategoryService;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Qualifier("categoryServiceImpl")
    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    // Добавляем категорию. Обратите внимание: имя категории должно быть уникальным
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryCreateDto category) {

        return new ResponseEntity<>(categoryService.createCategory(category), HttpStatus.CREATED);

    }


    // Обновляем категорию. Обратите внимание: имя категории должно быть уникальным
    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable long catId,
                                                              @Valid @RequestBody CategoryRequestDto categoryDto) {

        return new ResponseEntity<>(categoryService.updateCategory(catId, categoryDto), HttpStatus.OK);

    }


    // Удаляем категорию. Обратите внимание: с категорией не должно быть связано ни одного события.
    @DeleteMapping("/{catId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long catId) {

        categoryService.deleteCategory(catId);

        return new ResponseEntity<>("Категория удалена", HttpStatus.NO_CONTENT);
    }


}
