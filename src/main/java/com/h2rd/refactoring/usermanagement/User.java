package com.h2rd.refactoring.usermanagement;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
@NoArgsConstructor

@Getter
@Setter


public class User {

    private String name;
    private String email;
    private Set<String> roles;



    public Set<String> getRoles() {
        if(roles == null) return roles = Collections.synchronizedSet(new HashSet<>());
        return roles;
    }

    public void setRoles(Set<String> roles) {
        for(String role: roles){
            getRoles().add(role);
        }

    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
