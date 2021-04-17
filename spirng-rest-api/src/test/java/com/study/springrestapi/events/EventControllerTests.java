package com.study.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TDD 방식은 최소 3가지 삼각기법? 을 이용해서 처리를 한다고 한다.
 * 어떤 기준으로 3가지를 하는건지 알아보자.
 */
//Junit4 기준
@RunWith(SpringRunner.class)
//web과 관련된 Bean들이 자동으로 설정해준다. 따라서 그냥 가져와서 쓰게 되면 웹과 관련 빈만 등록해준다.(슬라이스 테스트, 조금더 빠르고 구역을 나눠서 작업한다. 단위테스트로 보기는 어렵다. 단위테스트로 하기에는 많은 내용이 주입되어있다.)
@WebMvcTest
public class EventControllerTests {

    /**
     * 스프링 MVC 테스트 핵심 클래스//웹 서버를 띄우지 않고도
     * 스프링MVC(DispatcherServlet)가 요청을 처리하는 과정에서
     * 확인할 수 있기 떄문에 컨트롤러 테스트용으로 자주 쓰인다.
     * 간단하게 요청을 만들 수 있고 결과를 확인할 수 있다.
     * 웹서버를 띄우지 않기 떄문에 조금 더 빠르다. 하지만 단위 테스트에 비해서는 만드는 객체들이 많기 떄문에 초반에 구동되는 부분들이 많다.
     * 단위테스트 보다는 좀 더 걸리는 중간 속도이다.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * 객체를 JSON으로 변환
     */
    @Autowired
    ObjectMapper objectMapper;

    /**
     * WebMvcTest Annotation의 경우, 슬라이스 Test이기 떄문에 Web용 Bean들만 등록을 해주지 Repository에 해당하는 Bean들은 등록을 해주지 않는다.
     * 그렇기 때문에 MockBean을 이용해서 Mocking하여 Repository에 해당하는 Bean들을 Mock으로 만들어 달라고 요청한다.
     */
    @MockBean
    EventRepository eventRepository;

    /**
     * Test 할것
     * 입력값들을 전달하면 JSON응답으로 201이 나오는지 확인.
     *      Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인.
     *      id는 DB에 들어갈때 자동 생성된 값으로 나오는지 확인.
     * @throws Exception
     */
    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .closeEnrollmentDateTime(LocalDateTime.of(2021,04,17,12,00))
                .beginEnrollmentDateTime(LocalDateTime.of(2021,04,18,12,00))
                .beginEventDateTime(LocalDateTime.of(2021,04,20,12,30))
                .endEventDateTime(LocalDateTime.of(2021,04,21,12,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("서울 광화문")
                .build();


        event.setId(10);
        /**
         * MockBean을 이용해서 객체를 만들게 되면 return되는 값이 전부 null로 들어온다.
         * eventRepository의 save가 호출되면 event를 return해라.
         */
        Mockito.when(eventRepository.save(event)).thenReturn(event);


        /**
         * perform(post("api/events/")) perform 안에 작성하는 내용은 요청에 해당한다.
         */
        mockMvc.perform(post("/api/events/")
                        /**
                         *이 요청에 JSON을 담아서 보내고 있다 의미.
                         */
                        .contentType(MediaType.APPLICATION_JSON)
                        /**
                         *어떠한 응담을 원하는지에 대한 accept
                         * Hypertext Application Language에 해당하는 값을 원한다.
                         */
                        .accept(MediaTypes.HAL_JSON)
                        //objectMapper를 이용해서 JSON으로 변경
                        .content(objectMapper.writeValueAsString(event)))
                /**
                 * 실제로 어떤 값들이 찍히는지 확인해 볼 수 있다.
                 */
                .andDo(print())
                /**
                 * andExpect는 어떤 결과값을 기대한다는 의미이고
                 * status().isCreated()를 사용하면 좀 더 TypeSafe하다.
                 */
                //.andExpect(status().is(201)) 을 이용하면 특정 원하는 결과를 얻고 싶을때 쓰면 된다.
                .andExpect(status().isCreated())
                //id가 있는지 확인한다.
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE));
    }
}
