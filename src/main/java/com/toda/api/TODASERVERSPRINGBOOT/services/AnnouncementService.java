package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.models.entities.UserAnnouncement;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.AnnouncementDetail;
import com.toda.api.TODASERVERSPRINGBOOT.models.entities.mappings.AnnouncementList;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AnnouncementRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserAnnouncementRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final UserAnnouncementRepository userAnnouncementRepository;

    @Transactional
    public List<Map<String,Object>> getAnnouncement(long userID, int page){
        int start = (page-1)*20;
        Pageable pageable = PageRequest.of(start,20);
        List<AnnouncementList> announcementList = announcementRepository.findByStatusNotOrderByCreateAtDesc(0,pageable);

        return announcementList.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("announcementID", element.getAnnouncementID());
            map.put("title", element.getTitle());
            map.put("date", element.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            map.put("isRead", userAnnouncementRepository.existsByUserIDAndAnnouncementID(userID, element.getAnnouncementID()));
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public List<Map<String,Object>> getAnnouncementDetail(long userID, long announcementID){
        List<AnnouncementDetail> announcementDetails = announcementRepository.findByStatusNotAndAnnouncementID(0,announcementID);
        boolean isRead = userAnnouncementRepository.existsByUserIDAndAnnouncementID(userID,announcementID);
        if(!isRead) readAnnouncement(userID,announcementID);

        return announcementDetails.stream().map(element -> {
            Map<String, Object> map = new HashMap<>();
            map.put("title", element.getTitle());
            map.put("date", element.getCreateAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
            map.put("image", element.getImage());
            map.put("text", element.getText());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
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
