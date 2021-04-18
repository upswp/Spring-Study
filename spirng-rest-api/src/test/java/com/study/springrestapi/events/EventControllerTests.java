package com.study.springrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.regex.Matcher;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * TDD 방식은 최소 3가지 삼각기법? 을 이용해서 처리를 한다고 한다.
 * 어떤 기준으로 3가지를 하는건지 알아보자.
 */
//Junit4 기준
@RunWith(SpringRunner.class)
/**
 * 통합 테스트로 전환, @WebMvcTest를 빼고 아래의 어노테이션을 작성한다.
 * SpringBootTest 어노테이션을 이용해서 테스트를 진행하면 이제 실제 Repository를 이용해서 Test가 진행이된다.
 * 테스트에 넣어 준 값들은 무시가 된다.
 * 통합테스트로 작업을 하게 될 경우 단위테스트로 작업해야하는 Mocking해줘야 하는 값들이 많아져서 테스트 코드를 작성하기 힘들다.
 * 코드가 바뀔때 마다 테스트가 계속 깨지기 때문이다.
 * SpringBootTest Annotation을 사용하면 @SpringBootApplication 어노테이션을 찾아서 설정에 따라 모든 Bean들이 등록 되도록 한다.
 * 이때 , 애플리케이션과 가장 가까운 테스트를 만들어 작성할 수 있다. 슬라이싱 테스트 보다 테스트 작업하는게 더 수월하다.
  */
@SpringBootTest
@AutoConfigureMockMvc
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
     * Test 할것
     * 입력값들을 전달하면 JSON응답으로 201이 나오는지 확인.
     *      Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인.
     *      id는 DB에 들어갈때 자동 생성된 값으로 나오는지 확인.
     * @throws Exception
     */
    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
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
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()));
    }

    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
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
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                     .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 조건에 맞지 않는 경우에 에러가 발생하는 테스트")
    @DisplayName("입력 값이 조건에 맞지 않는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .closeEnrollmentDateTime(LocalDateTime.of(2021,04,19,12,00))
                .beginEnrollmentDateTime(LocalDateTime.of(2021,04,18,12,00))
                .beginEventDateTime(LocalDateTime.of(2021,04,20,12,30))
                .endEventDateTime(LocalDateTime.of(2021,04,19,12,00))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("서울 광화문")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
}
