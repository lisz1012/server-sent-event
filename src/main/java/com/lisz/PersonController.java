package com.lisz;

import com.lisz.model.Person;
import com.lisz.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

// 注解式、函数式 SpringCloud Gateway
@RestController
@RequestMapping("/person")
public class PersonController {
	@Autowired
	private PersonService personService;

	@GetMapping("")
	public Mono<Person> get(){
		Mono<Person> mono = Mono.create(sink -> {
			sink.success(personService.getPerson());
		});
		return mono;
	}
}
