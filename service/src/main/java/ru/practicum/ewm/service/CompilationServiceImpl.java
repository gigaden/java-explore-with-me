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
import java.util.HashSet;
import java.util.List;

@Service("compilationServiceImpl")
@Slf4j
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final EventService eventService;
    private final EventCompilationRepository eventCompilationRepository;
    private final CompilationRepository compilationRepository;

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
        List<Event> events = compilationDto.getEvents() != null ? compilationDto.getEvents().stream()
                .map(eventService::getEventById).toList() : List.of();
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

    @Override
    @Transactional(readOnly = true)
    public Collection<CompilationResponseDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("Пытаюсь получить подборки в дипазоне from = {} size = {} pinned = {}", from, size, pinned);
        Collection<CompilationResponseDto> response = new HashSet<>();

        // Получаем подборки, исходя из фильтров
        List<Compilation> compilations = compilationRepository.findCompilationsBetweenFromAndSize(pinned, from, size);

        // Создаём подборки с событиями
        for (Compilation compilation: compilations) {
            Collection<Event> events = eventService.getAllEventsByCompilationId(compilation.getId());
            response.add(CompilationMapper.mapToCompilationResponseDto(compilation, events));
        }
        log.info("Подборки в диапазоне from = {} size = {} pinned = {} получены", from, size, pinned);

        return response;
    }

    // Получаем подборку событий по id
    @Override
    @Transactional(readOnly = true)
    public CompilationResponseDto getCompilationEventsById(long compId) {
        log.info("Пытаюсь получить подборку событий с id = {}", compId);
        Compilation compilation = getCompilationById(compId);
        Collection<Event> events = eventService.getAllEventsByCompilationId(compId);
        log.info("Подборка событий с id = {} получена", compId);

        return CompilationMapper.mapToCompilationResponseDto(compilation, events);
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
