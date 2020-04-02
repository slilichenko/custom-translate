package com.google.demo.customtranslate.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Support {

  @RequestMapping("/support")
  public String support() {
    return "support";
  }
}
