package com.lisz;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


// SSE 底层就是靠Stream实现的，其实就是在下载东西，只是下载的时候有的时候没给客户端内容。靠的是浏览器内部的事件驱动
// 交互性比较强的场景，跟Ajax差不多，但是后者会频繁发请求
// SSE 不同于websocket的地方是：他并不是全双工，一个请求到服务端之后不能再发新的到服务端的信息，而是只能等着服务端有消息的时候就返回来
// SSE 应用场景：股票行情更新，只是展示，而不是交互。即时通知
// SSE 是无状态连接，服务器推送，Server Sent Event
// 题外话，异步调用能提升性能吗？对于简单的、计算密集度高的请求，反而会降低性能。一般异步调用能提升整体的吞吐量（防止雪崩）。
@RestController
@RequestMapping(path = "/sse")
public class SseRest {
	private static Map<String, SseEmitter> sseCache = new ConcurrentHashMap<>();

	@GetMapping(path = "/subscribe")
	public SseEmitter subscribe(String id) {
		// 超时时间设置为1小时
		SseEmitter sseEmitter = new SseEmitter(3600000L);
		sseCache.put(id, sseEmitter);
		// 超时回调 触发
		sseEmitter.onTimeout(() -> sseCache.remove(id));
		// 结束之后的回调触发
		sseEmitter.onCompletion(() -> System.out.println("完成！！！"));
		return sseEmitter;
	}

	@GetMapping(path = "/push")
	public String push(String id, String content) throws IOException {
		SseEmitter sseEmitter = sseCache.get(id);
		if (sseEmitter != null) {
			// Server向Client发送消息
			sseEmitter.send(content);
		}
		return "over";
	}

	@GetMapping(path = "over")
	public String over(String id) {
		SseEmitter sseEmitter = sseCache.get(id);
		if (sseEmitter != null) {
			// 执行完毕，断开连接
			sseEmitter.complete();
			sseCache.remove(id);
		}
		return "over";
	}

	@GetMapping(path = "/push-all")
	public String pushAll(String content) throws IOException {
		for (String s : sseCache.keySet()) {
			SseEmitter sseEmitter = sseCache.get(s);
			if (sseEmitter != null) {
				// 发送消息
				sseEmitter.send(content);
			}
		}

		return "over";
	}

}
