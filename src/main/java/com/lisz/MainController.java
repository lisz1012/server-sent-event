package com.lisz;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Date;

@RestController
public class MainController {
	// 伪长连接，前端每隔3秒就会向这里的/sse发送一次请求，这其实是个pull，有点像一个Ajex不断去轮询Server，从浏览器发请求，一旦关闭浏览器，则此轮询停止。
	// 而且这种 text/event-stream 的请求在chrome浏览器里面并没有Response的tab， 而是有一个EventStream的选项卡
	// 这是个伪异步，最好的情况是：有数据就给推送过来，同时不用发这么多请求
	@GetMapping(value = "/sse", produces = "text/event-stream;charset=utf-8")
	public String sse(){
		String dateStr = new Date().toString();
		System.out.println(dateStr + " 当前线程： " + Thread.currentThread().getName()); //当前线程每次都不一样，伪异步，真的异步就不能用http协议
		//这里最前面要有"data:", 以便前端以".data"的方式获取推送过去的数据，最后要加上消息的结尾符，表明一次推送数据的终止
		// http://192.168.1.102:8080/sse.html
		return "data:" + dateStr + "\n\n";
	}

	@GetMapping("/blocking")
	public String get1(){
		System.out.println("--------1");
		// Service
		String result = getResult();
		System.out.println("--------2");
		return result;
	}

	@GetMapping("/reactive")
	public Mono<String> get2(){
		System.out.println("--------1");
		// Service
		Mono<String> result = Mono.create(sink -> {
			sink.success(getResult());
		});
		System.out.println(Thread.currentThread().getName());
		System.out.println("--------2");  // 1和2先在后台打印出来，数据待5s之后才显示在浏览器端，先把水管子返回，等着有水了，再滴答到客户端。此时客户端还没结束, 底层是SSE
		return result;  // 这个return并不是直接return给了客户端，而是把一个空的Mono给了netty容器，由后者在得到可用的数据之后，做服务端推，推给前端。return的这一瞬间，那个String结果其实并没有准备好
	}

	private String getResult() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(Thread.currentThread().getName());
		System.out.println("called");
		return "abc";
	}
}
