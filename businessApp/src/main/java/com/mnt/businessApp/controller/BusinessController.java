package com.mnt.businessApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/api/business")
public class BusinessController {

	@RequestMapping(method=RequestMethod.GET)
	@ResponseBody
	public void businessApp() {
		System.out.println("I m here");
	}
}
