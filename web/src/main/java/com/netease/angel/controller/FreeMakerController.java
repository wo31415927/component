package com.netease.angel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/** @author chenxiang 2018/3/28 */
@Controller
public class FreeMakerController {
  @RequestMapping("/free")
  public ModelAndView test() {
    ModelAndView mav = new ModelAndView();
    mav.addObject("hello", "Hello FreeMaker's World!");
    mav.setViewName("index");
    return mav;
  }
}
