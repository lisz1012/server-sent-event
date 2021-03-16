package com.lisz;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxJavaTest {
	public static void main(String[] args) {
		// Observable 被观察者
		Observable<String> bird = Observable.create(new ObservableOnSubscribe<String>() {
			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				// 被观察者都做了哪些事情，都会被观察到，onNext可以无限次调用
				emitter.onNext("1");
				Thread.sleep(1000);
				emitter.onNext("2");
				Thread.sleep(1000);
				emitter.onNext("3");
				Thread.sleep(1000);
				emitter.onNext("4");
				Thread.sleep(1000);
				emitter.onNext("5");
				Thread.sleep(1000);
				emitter.onComplete();
				// 订阅者和被订阅者是同一个线程，不是异步的，而是同步的
				System.out.println(Thread.currentThread().getName());
			}
		});

		Observer<String> person = new Observer<String>() {
			@Override
			public void onSubscribe(Disposable d) {
				System.out.println("onSubscribe " + d);
			}

			@Override
			public void onNext(String s) {
				// 订阅者和被订阅者是同一个线程，不是异步的，而是同步的
				System.out.println(Thread.currentThread().getName());
				System.out.println("onNext " + s);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("onError " + t.getMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("onComplete");
			}
		};

		// 被观察者关联观察者
		bird.subscribe(person);
	}
}
