package com.h2rd.refactoring.usermanagement;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement
public class User {

    private String name;
    private String email;
    private Set<String> roles;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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
