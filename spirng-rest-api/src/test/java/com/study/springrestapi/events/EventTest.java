package com.study.springrestapi.events;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventTest {

    @Test
    public void builder(){
        //빌드여부 체크
        Event event = Event.builder()
                .name("Spring Rest API")
                .description("REST API development with Srping")
                .build();
        //null 값 체크
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean(){
        //Given
        String name = "Event";
        String description = "Spring";
        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        //Then
        //리팩토링 단축키 : alt + ctrl + v - 중복제거
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}