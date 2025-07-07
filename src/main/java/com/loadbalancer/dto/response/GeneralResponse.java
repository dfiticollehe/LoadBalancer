package com.loadbalancer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
/**
 * Standard response for both successful and error status
 * @T specifies the object of successful data
 * @E specifies the object of error response enum
 */
public class GeneralResponse<T, E> {
    private Integer statusCode;

    private T data;

    private E errorReason;

    private String message;
}
