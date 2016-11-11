package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {
 
	@Autowired
	MyService myService;
	
	@RequestMapping(value = "/submitdata", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> submitData(@RequestParam String data) {
      
		myService.submitData(data);
		
		return ResponseEntity.ok().body(null);
	}
}
