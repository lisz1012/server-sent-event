package com.lisz;

import reactor.core.publisher.Flux; // 发布数据的是被观察者

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ReactorTest {
	public static void main(String[] args) {
		System.out.println("Main thread is: " + Thread.currentThread().getName());
		String s[] = new String[]{"aa", "bb"};
		Flux<String> flux1 = Flux.just(s); // 已知数据用just
		flux1.subscribe(System.out::println); // println是观察者，基本上是一对一的，被观察者订阅观察者，或者从右往左反过来读

		Flux<String> flux2 = Flux.just("cc", "dd");
		flux2.subscribe(System.out::println);

		List<String> list = Arrays.asList("hello", "world");
		Flux<String> flux3 = Flux.fromIterable(list);
		flux3.subscribe(System.out::println);

		Stream<String> stream = Stream.of("hi", "hello");
		Flux<String> flux4 = Flux.fromStream(stream);
		flux4.subscribe(i -> {
			System.out.println(i);
			System.out.println("Sub thread: " + Thread.currentThread().getName()); // 这里的执行线程也是主线程
		});

		Flux<Integer> range = Flux.range(0, 5);
		range.subscribe(System.out::println);

		Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(5);
		longFlux.subscribe(System.out::println);

		Flux.range(1, 5).subscribe(System.out::println);

		Flux<String> mergeWith = flux3.mergeWith(flux2); //会把flux2追加到flux3后面
		mergeWith.subscribe(System.out::println);


		// 同步动态创建，next 只能被调用一次,否则报错：More than one call to onNext
		Flux.generate(sink -> {
			System.out.println("Sub thread of generate: " + Thread.currentThread().getName());
			sink.next("aa");
			sink.complete();
		}).subscribe(System.out::print);

		// 异步用create next 可以被多次调用，Flux是一组多个被观察者，所以可以next好多
		Flux.create(sink -> {
			System.out.println("Sub thread of create: " + Thread.currentThread().getName());
			for (int i = 0; i < 10; i++) {
				sink.next("bb:" + i); //next的执行线程不是同一个
			}
			sink.complete();
		}).subscribe(System.out::println); // 被观察者订阅观察者
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
