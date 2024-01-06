package com.toda.api.TODASERVERSPRINGBOOT.services;

import com.toda.api.TODASERVERSPRINGBOOT.abstracts.AbstractService;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.delegates.*;
import com.toda.api.TODASERVERSPRINGBOOT.abstracts.interfaces.BaseService;
import com.toda.api.TODASERVERSPRINGBOOT.entities.UserAnnouncement;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.AnnouncementDetail;
import com.toda.api.TODASERVERSPRINGBOOT.entities.mappings.AnnouncementList;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.AnnouncementRepository;
import com.toda.api.TODASERVERSPRINGBOOT.repositories.UserAnnouncementRepository;
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
            DelegateFcmTokenAuth delegateFcmTokenAuth,
            DelegateKafka delegateKafka,
            AnnouncementRepository announcementRepository,
            UserAnnouncementRepository userAnnouncementRepository
    ) {
        super(delegateDateTime, delegateFile, delegateStatus, delegateJwt, delegateFcm, delegateUserAuth, delegateFcmTokenAuth, delegateKafka);
        this.announcementRepository = announcementRepository;
        this.userAnnouncementRepository = userAnnouncementRepository;
    }

    public List<Map<String,Object>> getAnnouncement(String token, int page){
        long userID = getUserID(token);
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

    public List<Map<String,Object>> getAnnouncementDetail(String token, long announcementID){
        long userID = getUserID(token);
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

    public boolean isAllAnnouncementRead(String token){
        long userID = getUserID(token);
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
