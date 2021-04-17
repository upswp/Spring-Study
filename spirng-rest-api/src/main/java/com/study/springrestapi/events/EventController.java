package com.study.springrestapi.events;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.swing.border.EmptyBorder;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/events",produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    public EventController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @PostMapping
    /**
     * ResponseEntity를 사용하는 이유 : 응답 코드, 헤더, 본문 모두 다루기 편한 API
     */
    public ResponseEntity createEvent(@RequestBody Event event){
        Event newEvent = this.eventRepository.save(event);
        /**
         * create를 만들때는 항상 URI가 필요하다.
         * Location URI 만들기 : HATEOS가 제공하는 linkTo(), methodOn() 사용
         * 객체를 JSON으로 변환 : Object Mapper 사용
         */
        URI createUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createUri).body(event);
    }
}
