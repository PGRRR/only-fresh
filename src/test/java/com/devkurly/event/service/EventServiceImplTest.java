package com.devkurly.event.service;

import com.devkurly.event.domain.EventDto;
import com.devkurly.event.domain.EventIdDto;
import com.devkurly.event.service.EventService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class EventServiceImplTest {
    @Autowired
    private EventService eventService;

    //    C
    @Test
    public void insertTest() throws Exception {
        // 1. 정상적인 경우
        EventDto normalTestDto = new EventDto("test nm", "test desc", "product-image.kurly.com/cdn-cgi/image/format=auto/banner/event/8622ba29-6cbf-438e-8865-880838ec3d7a.jpg", "test alt", 1, "A001", "19970226", "20220925", 0, 10);
        assertTrue(eventService.insert(normalTestDto) == 1);

        // 2. DB에 넘어가기 전에는 event_id 가 null 이어야 한다.
        EventDto idTestDto = new EventDto(1, "test nm", "test desc", "product-image.kurly.com/cdn-cgi/image/format=auto/banner/event/8622ba29-6cbf-438e-8865-880838ec3d7a.jpg", "test alt", 1, "A001", "19970226", "20220925", 0, 10);
        assertTrue(eventService.isValid(idTestDto).contains("event_id"));

        // 3. nm 의 값이 50자 이상 왔을 때 감지하나
    }

    //    R
    @Test
    public void getCountTest() throws Exception {
        eventService.removeAll();
        assertTrue(eventService.getCount() == 0);

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);
        assertTrue(eventService.insert(eventDto) == 1);

        assertTrue(eventService.getCount() == 1);
    }

    @Test
    public void getEventTest() throws Exception {
        eventService.removeAll();

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);
        assertTrue(eventService.insert(eventDto) == 1);

        Integer event_id = eventService.getEventList().get(0).getEvent_id();
        eventDto.setEvent_id(event_id);
        EventDto eventDto2 = eventService.getEvent(event_id);

        assertTrue(eventDto.equals(eventDto2));
    }

    @Test
    public void getEventListTest() throws Exception {
        eventService.removeAll();
        assertTrue(eventService.getCount() == 0);

        eventService.insert(new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30));
        assertTrue(eventService.getCount() == 1);

        eventService.insert(new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30));
        assertTrue(eventService.getCount() == 2);
    }

    @Test
    public void getEventIdsTest() throws Exception {
        eventService.removeAll();
        assertTrue(eventService.getEventIds().size() == 0);

        eventService.insert(new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30));
        assertTrue(eventService.getCount() == 1);

        EventDto eventDto = eventService.getEventList().get(0);
        int eventDto_id = eventDto.getEvent_id();

        EventIdDto eventIdDto = eventService.getEventIds().get(0);
        int eventIdDto_id = eventIdDto.getEvent_id();

        assertTrue(eventDto_id == eventIdDto_id);
    }

    //    U
    @Test
    public void modifyTest() throws Exception {
        eventService.removeAll();
        assertTrue(eventService.getCount() == 0);


        eventService.insert(new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30));
        assertTrue(eventService.getCount() == 1);
        int event_id = eventService.getEventList().get(0).getEvent_id();

        EventDto eventDto2 = new EventDto("2", "2", "2", "2", 2, "2", "20100226", "20200812", 1, 90);
        eventDto2.setEvent_id(event_id);
        eventService.modify(eventDto2);

        assertTrue(eventService.getEventList().get(0).getNm().equals("2"));
    }

    //new EventDto("1", "1", "1", "1", "1", "1", "19970226", "19960227", 0, 30)
//    D
    @Test
    public void deleteAllTest() throws Exception {
        eventService.removeAll();
        assertTrue(eventService.getCount() == 0);

        eventService.insert(new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30));
        assertTrue(eventService.getCount() == 1);

        eventService.removeAll();
        assertTrue(eventService.getCount() == 0);
    }

    @Test
    public void deleteTest() throws Exception {
        eventService.removeAll();
        assertTrue(eventService.getCount() == 0);

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);
        eventService.insert(eventDto);
        assertTrue(eventService.getCount() == 1);

        Integer event_id = eventService.getEventList().get(0).getEvent_id();
        eventService.remove(event_id);
        assertTrue(eventService.getCount() == 0);
    }
}