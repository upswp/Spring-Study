package com.study.springrestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

/**
 * EventValidator Bean 등록
 */
@Component
public class EventValidator {
    public void validate(EventDto eventDto, Errors errors){
        if(eventDto.getBasePrice() > eventDto.getMaxPrice() && eventDto.getMaxPrice() != 0){
            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong");
            errors.rejectValue("maxPrice","wrongValue","MaxPrice is wrong");
            errors.reject("wrongPrices","Values of prices are wrong");
        }

         LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEventDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())){
            errors.rejectValue("endEventDateTime","wrongValue","Time is wrong");
        }
        //TODO BeginEventDateTime
        //TODO CloseEnrollmentDateTime

        /**
         * rejectValue : FieldError
         * reject : GlobalError (여러개의 값이 조합해서 발생한 에러의 경우 GlobalError로 처리 권장)
         */
    }
}
