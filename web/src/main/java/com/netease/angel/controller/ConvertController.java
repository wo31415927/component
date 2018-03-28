package com.netease.angel.controller;

import com.netease.angel.pojo.Student;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** @author chenxiang 2018/3/28 */

//开启了该注解，class下的所有方法返回时才会走MessageConverter,获取使用组合体@RestController，组合了@ResponseBody和@Controller
//假如注释该注解，那么将写回一个Student对象，由于不走RequestResponseBodyMethodProcessor及MessageConverter，那么最后将走到ModelAndViews，找不到对应的view从而返回null，页面显示404
//@Controller
//@RequestBody
@RestController
public class ConvertController {
  @RequestMapping(
    value = "json",
    method = RequestMethod.POST
    //Http请求的Accecpt值为application/xml，与该值匹配，才会匹配到这个方法
    //当Accept属性值与此不匹配时，将无法找到对应的MessageHandler,报406给页面
    //注释此值，则按accept值获取MessageHandler进行处理
    //produces = MediaType.APPLICATION_XML_VALUE
  )
  //假如不加@RequestBody，那么此处获得的Student是一个空的Student
  public Object jsonTest(@RequestBody Student student) {
    System.out.println(student);
    System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));
    return student;
  }

  @RequestMapping(
    value = "xml",
    method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
  public Object xmlTest(@RequestBody Student student) {
    System.out.println(student);
    return student;
  }
}
