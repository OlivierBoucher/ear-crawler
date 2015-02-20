package com.olivierboucher.crawler;

public abstract class Crawler<T> {
	protected Common.CrawlerResult result;
	public abstract CrawlerJobResult<T> StartJobMultiThreaded();
	public abstract CrawlerJobResult<T> StartJob();

}
