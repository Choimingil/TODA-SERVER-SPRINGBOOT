package com.fineapple.toda.api.services;

import com.fineapple.toda.api.abstracts.delegates.*;
import com.fineapple.toda.api.entities.Announcement;
import com.fineapple.toda.api.entities.UserAnnouncement;
import com.fineapple.toda.api.repositories.AnnouncementRepository;
import com.fineapple.toda.api.repositories.UserAnnouncementRepository;
import com.fineapple.toda.api.abstracts.AbstractService;
import com.fineapple.toda.api.abstracts.interfaces.BaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("announcementService")
public class AnnouncementService extends AbstractService implements BaseService {
    private final AnnouncementRepository announcementRepository;
    private final UserAnnouncementRepository userAnnouncementRepository;

    public AnnouncementService(
            DelegateDateTime delegateDateTime,
            DelegateFile delegateFile,
            DelegateStatus delegateStatus,
            DelegateJwt delegateJwt,
            DelegateFcm delegateFcm,
            DelegateUserAuth delegateUserAuth,
            DelegateJms delegateJms,
            AnnouncementRepository announcementRepository,
            UserAnnouncementRepository userAnnouncementRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateJms);
        this.announcementRepository = announcementRepository;
        this.userAnnouncementRepository = userAnnouncementRepository;
    }

    public List<Map<String,Object>> getAnnouncement(long userID, int page){
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<Announcement> announcementList = announcementRepository.findByStatusNotOrderByCreateAtDesc(0,pageable);

        return announcementList.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("announcementID", element.getAnnouncementID());
            map.put("title", element.getTitle());
            map.put("date", element.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            map.put("isRead", userAnnouncementRepository.existsByUserIDAndAnnouncementID(userID, element.getAnnouncementID()));
            return map;
        }).collect(Collectors.toList());
    }

    public List<Map<String,Object>> getAnnouncementDetail(long userID, long announcementID){
        List<Announcement> announcementList = announcementRepository.findByStatusNotAndAnnouncementID(0,announcementID);
        boolean isRead = userAnnouncementRepository.existsByUserIDAndAnnouncementID(userID,announcementID);
        if(!isRead) readAnnouncement(userID,announcementID);

        return announcementList.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", element.getTitle());
            map.put("date", element.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            map.put("image", element.getImage());
            map.put("text", element.getText());
            return map;
        }).collect(Collectors.toList());
    }

    public boolean isAllAnnouncementRead(long userID){
        long announcementNum = announcementRepository.count();
        long userReadNum = userAnnouncementRepository.countByUserID(userID);
        return announcementNum == userReadNum;
    }

    @Transactional
    private void readAnnouncement(long userID, long announcementID){
        UserAnnouncement userAnnouncement = new UserAnnouncement();
        userAnnouncement.setUserID(userID);
        userAnnouncement.setAnnouncementID(announcementID);
        userAnnouncementRepository.save(userAnnouncement);
    }
}
