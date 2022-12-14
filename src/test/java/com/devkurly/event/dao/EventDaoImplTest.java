package com.devkurly.event.dao;

import com.devkurly.event.domain.EventDto;
import com.devkurly.event.domain.EventIdDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
public class EventDaoImplTest {
    @Autowired
    private EventDao eventDao;

    //   C
    @Test
    public void createTest() throws Exception {
        eventDao.deleteAll();
        EventDto testDto = new EventDto("test nm", "test desc", "product-image.kurly.com/cdn-cgi/image/format=auto/banner/event/8622ba29-6cbf-438e-8865-880838ec3d7a.jpg", "test alt", 1, "A001", "19970226", "20200812", 0, 10);
        assertTrue(eventDao.create(testDto) == 1);
        eventDao.deleteAll();
    }

    //    R
    @Test
    public void countTest() throws Exception {
        assertTrue(eventDao.count() == 1);
    }

    @Test
    public void selectTest() throws Exception {
        eventDao.deleteAll();
        assertTrue(eventDao.count() == 0);

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);
        assertTrue(eventDao.create(eventDto) == 1);

        Integer event_id = eventDao.selectAll().get(0).getEvent_id();
        eventDto.setEvent_id(event_id);
        EventDto eventDto2 = eventDao.select(event_id);
        assertTrue(eventDto.equals(eventDto2));
        // select 했을 때, 객체가 null 이냐
        // select 한 결과물의 id 가 parameter 로 넣은 id 값과 동일하냐
    }

    @Test
    public void selectAllTest() throws Exception {
        eventDao.deleteAll();
        assertTrue(eventDao.count() == 0);

        List<EventDto> list = eventDao.selectAll();
        assertTrue(list.size() == 0);

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);
        assertTrue(eventDao.create(eventDto) == 1);

        list = eventDao.selectAll();
        assertTrue(list.size() == 1);

        assertTrue(eventDao.create(eventDto) == 1);
        list = eventDao.selectAll();
        assertTrue(list.size() == 2);
    }

    @Test
    public void selectIdsTest() throws Exception {
        eventDao.deleteAll();
        assertTrue(eventDao.count() == 0);

        List<EventIdDto> list = eventDao.selectIds();
        assertTrue(list.size() == 0);

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);
        assertTrue(eventDao.create(eventDto) == 1);

        list = eventDao.selectIds();
        assertTrue(list.size() == 1);

        assertTrue(eventDao.create(eventDto) == 1);
        list = eventDao.selectIds();
        assertTrue(list.size() == 2);
    }

    //    U
    @Test
    public void updateTest() throws Exception {
        eventDao.deleteAll();
        assertTrue(eventDao.count() == 0);

        EventDto eventDto = new EventDto("1", "1", "1", "1", 1, "1", "19970226", "19960227", 0, 30);

        eventDao.create(eventDto);
        assertTrue(eventDao.count() == 1);

        EventDto eventDto2 = new EventDto("2", "2", "2", "2", 2, "2", "20100226", "20200812", 1, 90);
        eventDao.update(eventDto2);
        String updated_nm= eventDao.selectAll().get(0).getNm();
        assertTrue(eventDto.getNm().equals(updated_nm));
    }

    //    D
    @Test
    public void deleteAllTest() throws Exception {
        eventDao.deleteAll();
        assertTrue(eventDao.count() == 0);
    }

    @Test
    public void deleteTest() throws Exception {
        eventDao.deleteAll();
        assertTrue(eventDao.count() == 0);

        eventDao.create(new EventDto("test nm", "test desc", "product-image.kurly.com/cdn-cgi/image/format=auto/banner/event/8622ba29-6cbf-438e-8865-880838ec3d7a.jpg", "test alt", 1, "A001", "19970226", "20200812", 0, 10));
        assertTrue(eventDao.count()==1);
        Integer event_id = eventDao.selectAll().get(0).getEvent_id();

        eventDao.delete(event_id);
        assertTrue(eventDao.count() == 0);
    }
}