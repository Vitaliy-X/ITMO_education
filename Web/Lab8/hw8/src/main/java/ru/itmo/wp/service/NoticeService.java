package ru.itmo.wp.service;

import org.springframework.stereotype.Service;
import ru.itmo.wp.domain.Notice;
import ru.itmo.wp.form.NoticeForm;
import ru.itmo.wp.repository.NoticeRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<NoticeForm> findAll() {
        List<Notice> notices = noticeRepository.findAllByOrderByCreationTimeDesc();
        return notices.stream()
                .map(notice -> {
                    NoticeForm form = new NoticeForm();
                    form.setContent(notice.getContent());
                    return form;
                })
                .collect(Collectors.toList());
    }

    public void save(NoticeForm noticeForm) {
        Notice notice = new Notice();
        notice.setContent(noticeForm.getContent());
        noticeRepository.save(notice);
    }
}