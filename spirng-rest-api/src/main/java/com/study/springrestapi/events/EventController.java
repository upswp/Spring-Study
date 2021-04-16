package com.study.springrestapi.events;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
public class EventController {

    @PostMapping("/api/events/")
    /**
     * ResponseEntity를 사용하는 이유 : 응답 코드, 헤더, 본문 모두 다루기 편한 API
     */
    public ResponseEntity createEvent(){
        /**
         * create를 만들때는 항상 URI가 필요하다.
         * Location URI 만들기 : HATEOS가 제공하는 linkTo(), methodOn() 사용
         * 객체를 JSON으로 변환 : Object Mapper 사용
         */
        URI createUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();
        return ResponseEntity.created(createUri).build();
    }
}
