package com.tyut.holiday.module.trip;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Constants;
import com.tyut.holiday.entity.Trip;
import com.tyut.holiday.mapper.TripMapper;
import com.tyut.holiday.module.trip.dto.TripDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 多段行程服务：离校/返校共用。保存采用「全量替换」。
 */
@Service
@RequiredArgsConstructor
public class TripService {

    private final TripMapper tripMapper;

    /** 全量替换某条登记的行程（删旧插新） */
    @Transactional
    public void replace(Long refId, String refType, List<TripDTO> trips) {
        if (trips != null && trips.size() > Constants.MAX_TRIPS) {
            throw BizException.badRequest("行程最多 " + Constants.MAX_TRIPS + " 段");
        }
        tripMapper.delete(Wrappers.<Trip>lambdaQuery()
                .eq(Trip::getRefId, refId).eq(Trip::getRefType, refType));
        if (trips == null || trips.isEmpty()) {
            return;
        }
        int seq = 1;
        for (TripDTO d : trips) {
            Trip t = new Trip();
            t.setRefId(refId);
            t.setRefType(refType);
            t.setSeq(d.getSeq() != null ? d.getSeq() : seq);
            t.setTransport(d.getTransport());
            t.setTransportInfo(d.getTransportInfo());
            t.setFromStation(d.getFromStation());
            t.setDestProvince(d.getDestProvince());
            t.setDestCity(d.getDestCity());
            t.setDestDistrict(d.getDestDistrict());
            t.setDestStation(d.getDestStation());
            t.setDepartTime(d.getDepartTime());
            t.setArriveTime(d.getArriveTime());
            tripMapper.insert(t);
            seq++;
        }
    }

    /** 查询某条登记的行程（按 seq 升序） */
    public List<Trip> list(Long refId, String refType) {
        if (refId == null) {
            return new ArrayList<>();
        }
        return tripMapper.selectList(Wrappers.<Trip>lambdaQuery()
                .eq(Trip::getRefId, refId).eq(Trip::getRefType, refType)
                .orderByAsc(Trip::getSeq));
    }
}
