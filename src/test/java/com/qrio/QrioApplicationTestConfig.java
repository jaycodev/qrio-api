package com.qrio;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@ComponentScan(basePackages = "com.qrio")
public class QrioApplicationTestConfig {
}
