package com.study.springrestapi.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface TestDescription {
    String value();
    /**
     * Target(ElementType.METHOD) : 붙일 수 있는 타겟, 메서드에 붙일 것이다.
     * Retention(RetentionPolicy.SOURCE) : 이 Annotation이 얼마나 유지가 되게 할것인가.
     *                                      기본값은 CLASS가 있고 RUNTIME, SOURCE가 있다.
     *                                      RUNTIME : 컴파일 한 이후로도 가져갈 것인가?
     * public @interface TestDescription : 직접 annotation 생성
     */
}
