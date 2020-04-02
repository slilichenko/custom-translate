package com.google.demo.customtranslate.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HealthChecks {

  @RequestMapping("/readiness-check")
  public String readinessCheck() {
    return "OK";
  }

  @RequestMapping("/liveness-check")
  public String livenessCheck() {
    return "OK";
  }
}
