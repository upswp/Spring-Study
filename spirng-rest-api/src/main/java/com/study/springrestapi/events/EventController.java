package com.study.springrestapi.events;

import com.study.springrestapi.common.ErrorsResource;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/events",produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository,ModelMapper modelMapper, EventValidator eventValidator) {
        this.modelMapper = modelMapper;
        this.eventRepository = eventRepository;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    /**
     * ResponseEntity를 사용하는 이유 : 응답 코드, 헤더, 본문 모두 다루기 편한 API
     */
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto , Errors errors){
        if (errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return badRequest(errors);
        }

        /**
         * eventDto로 원하는 값들만 정제되어 값을 받고
         * 받아온 eventDto값을 Event 객체에 ModelMapper를 사용해서 Event 객체로 주입시킨다.
         */
        Event event = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = this.eventRepository.save(event);
        /**
         * create를 만들때는 항상 URI가 필요하다.
         * Location URI 만들기 : HATEOS가 제공하는 linkTo(), methodOn() 사용
         * 객체를 JSON으로 변환 : Object Mapper 사용
         */
        WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-events"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(createUri).body(eventResource);
    }

    /**
     * Event 목록 조회 API
     * @param pageable : 페이지 정보를 가져올 수 있는
     * @param assembler : 페이지에 대한 링크가 없다. 페이지에 대한 링크 정보를 Resource로 전달하기 위해 사용한다.
     *                  페이지와 관련된 링크는, 현재 페이지 , 이전 , 다음, 마지막 페이지 등과 같은 정보를 이야기한다.
     * @return
     */
    @GetMapping
    public ResponseEntity queryEvent(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        //repository에서 넘어온 page를 resource로 변경할 수 있다.
        //var pagedModel = assembler.toModel(page);

        //이벤트 각각의 내용들의 Resource를 만든다.
        var pagedResources = assembler.toModel(page, e-> new EventResource(e));
        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(pagedResources);
    }


    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);
        eventResource.add(Link.of("/docs/index.html#resources-events-get").withRel("profile"));
        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(ErrorsResource.modelOf(errors));
    }
}
