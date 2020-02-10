package com.h2rd.refactoring.usermanagement.exception;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
public class ResponseMessage {
    private Integer status;
    private String message;
}
