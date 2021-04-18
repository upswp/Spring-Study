package com.study.springrestapi.events;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 단위 테스트
 */
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

    @Test
    public void testFree() {
        //Given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isTrue();

        //Given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isFalse();

        //Given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline(){
        //Given
        Event event = Event.builder()
                .location("강남역 7번출구")
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isTrue();

        //Given
        event = Event.builder()
                .build();
        //When
        event.update();

        //Then
        assertThat(event.isOffline()).isFalse();
    }
}