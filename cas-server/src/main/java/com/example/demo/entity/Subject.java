package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

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
    private String logoutUrl;
    private User user;
    private List<String> tickets;
}
