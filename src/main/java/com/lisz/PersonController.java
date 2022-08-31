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
		System.out.println(Thread.currentThread().getName());
		Mono<Object> mono = Mono.create(sink -> {  // lambda并不会被立即调用，只是相当于set了一下下
			System.out.println("before sink.success()");  // 打印顺序：3
			System.out.println(Thread.currentThread().getName());
			sink.success(personService.getPerson());  // 组装数据序列，等一个数据过来，然后结束Mono
		}).doOnSubscribe(sub -> {
			System.out.println("sub");  // 打印顺序：2
		}).doOnNext(o -> {
			System.out.println("data: " + o);  // 打印顺序：4 有数据的时候打印
		}).doOnSuccess(o -> {
			System.out.println("onSuccess");   // 打印顺序：5
		});
		System.out.println("Right before return");  // 打印顺序： 1
		return mono;  // 容器会接住这个Mono，然后容器阻塞着等事件发生（把阻塞放在了容器里），最后再将数据返回给客户端。mono是一个包装数据序列，包含了数据的特征和注册的处理方式，容器拿到序列，再去执行onXXX方法。容器代码的Operators抽象类的1890行会有一个for(;;)死循环，在里面做的阻塞.
		// 在FluxPeekFuseable.java中807行触发的onSubscribe中的打印；在FluxPeekFuseable.java中840行触发的onSubscribe中的打印（nexthook，好像是责任链）
	}

	@GetMapping("/many")
	public Flux<Person> get2(){
//		Flux<Person> flux = Flux.fromStream(personService.getPersons().toStream());
		return personService.getPersons();
	}
}
