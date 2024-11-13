package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationResponseDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.entity.Compilation;
import ru.practicum.ewm.entity.Event;
import ru.practicum.ewm.entity.EventCompilation;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventCompilationRepository;

import java.util.Collection;
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
        List<EventCompilation> eventCompilations = events.stream()
                .map(e -> EventCompilation.builder()
                        .compilation(compilation)
                        .event(e)
                        .build()).toList();
        eventCompilationRepository.saveAll(eventCompilations);
        log.info("Добавил события в таблицу подборок");

        // Собираем ответ
        CompilationResponseDto responseDto = CompilationMapper.mapToCompilationResponseDto(compilation, events);
        log.info("Создана подборка = {} для событий = {}", compilationDto.getTitle(), compilationDto.getEvents());

        return responseDto;
    }

    @Override
    public void deleteCompilationById(long compId) {

        log.info("Пытаюсь удалить подборку с id = {}", compId);
        Compilation compilation = getCompilationById(compId);

        // Удаляем все события из таблицы events_compilations
        eventCompilationRepository.deleteAllByCompilationId(compId);
        log.info("Удалил события из подборки");

        // Удаляем подборку из таблицы compilations
        compilationRepository.delete(compilation);
        log.info("Подборка с id = {} удалена", compId);

    }

    /* Обновляем подборку событий.
     Если поле в запросе не указано (равно null) - значит изменение этих данных не требуется.*/
    @Override
    public CompilationResponseDto updateCompilation(long compId, CompilationUpdateDto compilationDto) {
        log.info("Пытаюсь обновить подборку с id = {}", compId);
        Compilation compilation = getCompilationById(compId);
        Collection<Event> events = eventService.getAllEventsByCompilationId(compId);
        compilation.setPinned(compilationDto.getPinned() != null ? compilationDto.getPinned() : compilation.isPinned());
        compilation.setTitle(compilationDto.getTitle() != null ? compilationDto.getTitle() : compilation.getTitle());

        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            // Если есть запрос на изменение событий в подборке, то получаем список новых событий
            events = compilationDto.getEvents().stream()
                    .map(eventService::getEventById).toList();
            // Удаляем старые события из events_compilation
            eventCompilationRepository.deleteAllByCompilationId(compId);
            // Добавляем новые события в таблицу
            List<EventCompilation> eventCompilations = events.stream()
                    .map(e -> eventCompilationRepository.save(EventCompilation.builder()
                            .compilation(compilation)
                            .event(e)
                            .build())).toList();
            eventCompilationRepository.saveAll(eventCompilations);
        }

        compilationRepository.save(compilation);

        return CompilationMapper.mapToCompilationResponseDto(compilation, events);


    }


    public void checkCompilationIsExist(long compId) {
        if (compilationRepository.findById(compId).isEmpty()) {
            log.warn("Подборка с id = {} не найдена", compId);
            throw new CompilationNotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
    }

    public Compilation getCompilationById(long compId) {
        log.info("Пытаюсь получить подборку с id = {}", compId);
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.warn("Подборка с id={} не найдена", compId);
                    return new CompilationNotFoundException(String.format("Compilation with id=%d was not found", compId));
                });
        log.info("Подборка с id = {} получена", compId);

        return compilation;
    }


}
