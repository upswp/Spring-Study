package com.study.springrestapi.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
     * Test 할것
     * 입력값들을 전달하면 JSON응답으로 201이 나오는지 확인.
     *      Location 헤더에 생성된 이벤트를 조회할 수 있는 URI 담겨 있는지 확인.
     *      id는 DB에 들어갈때 자동 생성된 값으로 나오는지 확인.
     * @throws Exception
     */
    @Test
    public void createEvent() throws Exception {
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
                .accept(MediaTypes.HAL_JSON))
                /**
                 * 실제로 어떤 값들이 찍히는지 확인해 볼 수 있다.
                 */
                .andDo(print())
                /**
                 * andExpect는 어떤 결과값을 기대한다는 의미이고
                 * status().isCreated()를 사용하면 좀 더 TypeSafe하다.
                 */
                .andExpect(status().isCreated());
                //.andExpect(status().is(201)) 을 이용하면 특정 원하는 결과를 얻고 싶을때 쓰면 된다.
    }
}
