package com.h2rd.refactoring.usermanagement.exception;

import com.h2rd.refactoring.usermanagement.domain.User;
import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@XmlRootElement
public class ResponseBody {
    private Integer status;
    private String message;
    private User   user;
}
