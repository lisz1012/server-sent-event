package com.lisz;

import com.lisz.model.Person;
import com.lisz.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// 注解式、函数式 SpringCloud Gateway
@RestController
@RequestMapping("/person")
public class PersonController {
	@Autowired
	private PersonService personService;

	@GetMapping("")
	public Mono<Object> get(){
		Mono<Object> mono = Mono.create(sink -> {
			System.out.println("before sink.success()");  // 打印顺序：3
			sink.success(personService.getPerson());  // 等一个数据过来，然后结束Mono
		}).doOnSubscribe(sub -> {
			System.out.println("sub");  // 打印顺序：2
		}).doOnNext(o -> {
			System.out.println("data: " + o);  // 打印顺序：4 有数据的时候打印
		}).doOnSuccess(o -> {
			System.out.println("onSuccess");   // 打印顺序：5
		});
		System.out.println("Right before return");  // 打印顺序： 1
		return mono;  // 容器会接住这个Mono，然后容器阻塞着等事件发生，最后再将数据返回给客户端
	}

	@GetMapping("/many")
	public Flux<Person> get2(){
//		Flux<Person> flux = Flux.fromStream(personService.getPersons().toStream());
		return personService.getPersons();
	}
}
