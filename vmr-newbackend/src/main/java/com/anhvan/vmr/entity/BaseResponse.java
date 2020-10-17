package com.anhvan.vmr.entity;

import com.anhvan.vmr.consts.ResponseCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class BaseResponse {
  @JsonIgnore private int statusCode;
  private ResponseCode responseCode;
  private String message;
  private Object data;
}
