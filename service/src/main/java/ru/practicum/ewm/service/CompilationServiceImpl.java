package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationResponseDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventCompilation;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventCompilationRepository;

import java.util.List;

@Service("compilationServiceImpl")
@Slf4j
@Transactional
public class CompilationServiceImpl implements CompilationService {

    EventService eventService;
    EventCompilationRepository eventCompilationRepository;
    CompilationRepository compilationRepository;

    @Autowired
    public CompilationServiceImpl(EventService eventService,
                                  EventCompilationRepository eventCompilationRepository,
                                  CompilationRepository compilationRepository) {
        this.eventService = eventService;
        this.eventCompilationRepository = eventCompilationRepository;
        this.compilationRepository = compilationRepository;
    }

    @Override
    public CompilationResponseDto createCompilation(NewCompilationDto compilationDto) {

        log.info("Пытаюсь создать подборку = {} для событий = {}", compilationDto.getTitle(), compilationDto.getEvents());
        // Получаем список событий для подборки
        List<Event> events = compilationDto.getEvents().stream()
                .map(eventService::getEventById).toList();
        log.info("Получил список событий");

        // Добавляем новую подборку в таблицу compilations
        Compilation compilation = compilationRepository.save(Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .build());
        log.info("Добавил новую категорию подборок в таблицу");

        // Добавляем события в таблицу events_compilations
        for (Event event: events) {
            eventCompilationRepository.save(EventCompilation.builder()
                    .compilation(compilation)
                    .event(event)
                    .build());
        }
        log.info("Добавил события в таблицу подборок");

        // Собираем ответ
        CompilationResponseDto responseDto = CompilationMapper.mapToCompilationResponseDto(compilation, events);
        log.info("Создана подборка = {} для событий = {}", compilationDto.getTitle(), compilationDto.getEvents());

        return responseDto;
    }
}
