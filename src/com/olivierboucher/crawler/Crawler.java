package com.olivierboucher.crawler;

public abstract class Crawler<T> {
	
	public abstract CrawlerJobResult<T> StartJobMultiThreaded();
	public abstract CrawlerJobResult<T> StartJob();

}
