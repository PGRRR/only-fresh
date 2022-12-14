package com.devkurly.event.dao;

import com.devkurly.event.domain.EventDto;
import com.devkurly.event.domain.EventIdDto;

import java.util.List;

public interface EventDao {
//    C
    int create(EventDto eventDto) throws Exception;

//    R
    int count() throws Exception;
    EventDto select(Integer event_id) throws Exception;
    List<EventDto> selectAll() throws Exception;
    List<EventIdDto> selectIds() throws Exception;

//    U
    int update(EventDto eventDto) throws Exception;

//    D
    int deleteAll() throws Exception;
    int delete(Integer event_id) throws Exception;

}
