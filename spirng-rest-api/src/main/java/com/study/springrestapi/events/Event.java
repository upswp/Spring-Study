package com.study.springrestapi.events;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * Entity간에 상호참조하는 관계가 되면, EqualsAndHashCode를 이용해 만든 코드 내분에서 스택오버플로우가 발생할 수 있다.
 * id의 값을 가지고 EqualsAndHashCode를 이용해서 id 값만 비교해서 사용할 수 있다.
 * 이때 상관관계를 가지는 다른 Entity와의 연관관계를 가지지 않는게 좋다.
 * 넣게 된다면 상호 참조때문에 구현 내에서 서로간의 메서드를 계속 호출하면서 스택 오버 플로우가 발생할 수 있다.
 */
/**
 * Entity에는 @Data Annotation을 사용하면 안된다. @Data에서는 EqualsAndHashCode를 제공하지만
 * 모든 프로퍼티를 다 쓰면서 구현하기 떄문에 상호참조 때문에 동일하게 스택 오버 플로우가 발생할 수 있다.
 */
@EqualsAndHashCode(of = "id")

/**
 * 스프링에서 제공하는 여러 Annotation은 메타 Annotation을 사용한다.
 * 커스터마이징해서 사용자가 원하는 Annotation을 만들어서 사용할 수 있다.
 * 하지만 Lombok에서는 적용이 안되므로 차후 업데이트가 이뤄지게 된다면 가능하다.
 */
@Entity
public class Event {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    // (optional) location이 없으면 온라인 모임
    private String location;
    // (optional)
    private int basePrice;
    // (optional)
    private int maxPrice;
    private int limitOfEnrollment;
    // 모임 온오프확인
    private boolean offline;
    // 모임 유무료 여부
    private boolean free;
    @Enumerated(EnumType.STRING)
    private EventStatus  eventStatus = EventStatus.DRAFT;
    /**
     * Enumerated(EnumType.ORDINAL)
     * Enum에 설정되어있는 순서를 통해서 작업한다. 하지만 순서가 변경되면 꼬일 수 있는 문제가 있으므로 STRING을 추천한다.
     * Enumerated(EnumType.STRING)
     * Enum의 설정되어있는 String 기준으로 가져온다.
     */

}
