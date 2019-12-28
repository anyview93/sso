package com.example.demo.entity;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

/**
 * @author shizhiguo
 * @ClassName Subject
 * @date 2019-03-26
 */
@Data
@Builder
public class Subject {
    private String sessionId;
    private Set<String> logoutUrls;
    private User user;
    private Set<String> tickets;
}
