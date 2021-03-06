package com.study.springrestapi.events;

import com.study.springrestapi.common.AppProperties;
import com.study.springrestapi.common.BaseControllerTest;
import com.study.springrestapi.common.TestDescription;
import com.study.springrestapi.member.AccountRole;
import com.study.springrestapi.member.Member;
import com.study.springrestapi.member.MemberRepository;
import com.study.springrestapi.member.MemberService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp(){
        this.memberRepository.deleteAll();
        this.eventRepository.deleteAll();
    }

    /**
     * Test ??????
     * ??????????????? ???????????? JSON???????????? 201??? ???????????? ??????.
     * Location ????????? ????????? ???????????? ????????? ??? ?????? URI ?????? ????????? ??????.
     * id??? DB??? ???????????? ?????? ????????? ????????? ???????????? ??????.
     */
    @Test
    @TestDescription("??????????????? ???????????? ???????????? ?????????")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 04, 17, 12, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 04, 18, 12, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 04, 20, 12, 30))
                .endEventDateTime(LocalDateTime.of(2021, 04, 21, 12, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????? ?????????")
                .build();
        /**
         * perform(post("api/events/")) perform ?????? ???????????? ????????? ????????? ????????????.
         */
        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                /**
                 *??? ????????? JSON??? ????????? ????????? ?????? ??????.
                 */
                .contentType(MediaType.APPLICATION_JSON)
                /**
                 *????????? ????????? ??????????????? ?????? accept
                 * Hypertext Application Language??? ???????????? ?????? ?????????.
                 */
                .accept(MediaTypes.HAL_JSON)
                //objectMapper??? ???????????? JSON?????? ??????
                .content(objectMapper.writeValueAsString(event)))
                /**
                 * ????????? ?????? ????????? ???????????? ????????? ??? ??? ??????.
                 */
                .andDo(print())
                /**
                 * andExpect??? ?????? ???????????? ??????????????? ????????????
                 * status().isCreated()??? ???????????? ??? ??? TypeSafe??????.
                 */
                //.andExpect(status().is(201)) ??? ???????????? ?????? ????????? ????????? ?????? ????????? ?????? ??????.
                .andExpect(status().isCreated())
                //id??? ????????? ????????????.
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                /**
                 * ?????? ????????? ???????????? ???????????? ?????? Test (???????????? ???????????? ????????? Test??????????????? ???????????? ???????????? Test??? ????????????.)
                 */
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-events").exists())
                .andExpect(jsonPath("_links.profile").exists())
                /**
                 * REST Docs??? ?????? ??????
                 */
                .andDo(document("create-event",
                        //?????? ?????? REST Docs??? ?????? HATEOAS
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query events"),
                                linkWithRel("update-events").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        //Headers REST Docs ?????? ??????
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("hal+json type")
                        ),
                        //???????????? ???????????? ?????????. relaxed
                        //?????? : ????????? ????????? ????????? ?????????.
                        //?????? : ????????? ???????????? ???????????? ??? ??????.
                        relaxedResponseFields(
                                fieldWithPath("id").description("identifier of new event"),
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of begin of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of close of new event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new event"),
                                fieldWithPath("endEventDateTime").description("date time of end of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("base price of new event"),
                                fieldWithPath("maxPrice").description("max price of new event"),
                                fieldWithPath("limitOfEnrollment").description("limit of enrolmment"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline event or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-events.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ));
    }

    private String getBearerToken() throws Exception {
        return "Bearer" + getAccessToken();
    }

    private String getAccessToken() throws Exception {
        //Given
        Member sangwoo = Member.builder()
                .email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        this.memberService.saveMember(sangwoo);

        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));
        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    @Test
    @TestDescription("???????????? ??? ?????? ?????? ????????? ????????? ????????? ???????????? ?????????")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 04, 17, 12, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 04, 18, 12, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 04, 20, 12, 30))
                .endEventDateTime(LocalDateTime.of(2021, 04, 21, 12, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????? ?????????")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("?????? ?????? ???????????? ????????? ????????? ???????????? ?????????")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("?????? ?????? ????????? ?????? ?????? ????????? ????????? ???????????? ?????????")
    @DisplayName("?????? ?????? ????????? ?????? ?????? ????????? ????????? ???????????? ?????????")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 04, 19, 12, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 04, 18, 12, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 04, 20, 12, 30))
                .endEventDateTime(LocalDateTime.of(2021, 04, 19, 12, 00))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????? ?????????")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                /**
                 * ?????? ?????? ???????????? ????????? ?????? ????????? ????????? ??????.
                 */
                .andDo(print())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].field").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists());
    }

    @Test
    @TestDescription("30?????? ???????????? 10?????? ????????? ????????? ????????????")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(this::generateEvent);

        //When & Then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"));
    }

    @Test
    @TestDescription("????????? ???????????? ?????? ????????????")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);
        //When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists());
    }

    @Test
    @TestDescription("?????? ???????????? ??????????????? 404 ????????????")
    public void getEvent404() throws Exception {
        //When & Then
        this.mockMvc.perform(get("/api/events/1183"))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("???????????? ??????????????? ????????????")
    public void updateEvent() throws Exception {
        //Givent
        String changeEventName = "ChangeEventName";
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setName(changeEventName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(changeEventName))
                .andExpect(jsonPath("_links.self").exists());
    }

    @Test
    @TestDescription("???????????? ???????????? ????????? ????????? ?????? ??????")
    public void updateEvent400Empty() throws Exception {
        //Givent
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("???????????? ????????? ????????? ????????? ?????? ??????")
    public void updateEvent400Wrong() throws Exception {
        //Givent
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(10000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("???????????? ?????? ????????? ?????? ??????")
    public void updateEvent404() throws Exception {
        //Givent
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/555555")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("test event")
                .description("REST API Development with Spring")
                .closeEnrollmentDateTime(LocalDateTime.of(2021, 04, 17, 12, 00))
                .beginEnrollmentDateTime(LocalDateTime.of(2021, 04, 18, 12, 00))
                .beginEventDateTime(LocalDateTime.of(2021, 04, 20, 12, 30))
                .endEventDateTime(LocalDateTime.of(2021, 04, 21, 12, 00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("?????? ?????????")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }
}
