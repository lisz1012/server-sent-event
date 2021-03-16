package com.lisz;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxJavaTest2 {
	public static void main(String[] args) throws InterruptedException {
		Observable.create(new ObservableOnSubscribe<String>() {

			@Override
			public void subscribe(ObservableEmitter<String> emitter) throws Exception {
				emitter.onNext("1");
				emitter.onNext("2");
				emitter.onNext("3");
				emitter.onNext("4");
				emitter.onNext("5");
				emitter.onComplete();
			}
		})
				.observeOn( //哪个线程是观察者
						Schedulers.computation() // 计算密集度比较高的
				)
				.subscribeOn( Schedulers.computation())
				.subscribe(new Observer<String>() {

					@Override
					public void onSubscribe(Disposable d) {
						// TODO Auto-generated method stub
						System.out.println("onSubscribe");
					}

					@Override
					public void onNext(String t) {
						// TODO Auto-generated method stub
						System.out.println("onNext");
					}

					@Override
					public void onError(Throwable e) {
						// TODO Auto-generated method stub
						System.out.println("onError");
					}

					@Override
					public void onComplete() {
						// TODO Auto-generated method stub
						System.out.println("onComplete");
					}

				});
		// 让主线程等一下回来的数据
		Thread.sleep(10000);
	}
}
