package ru.practicum.ewm.service.comment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("commentServiceImpl")
@Slf4j
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
}
