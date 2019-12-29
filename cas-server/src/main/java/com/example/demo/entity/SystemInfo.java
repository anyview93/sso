package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shizhiguo
 * @version 1.0
 * @description
 * @date 2019-12-28 20:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemInfo {
    private String ticket;
    private String SystemUrl;
}
