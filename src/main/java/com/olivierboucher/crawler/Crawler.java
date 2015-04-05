package com.olivierboucher.crawler;

public abstract class Crawler<T> {
	protected Common.CrawlerResult result;
	public abstract CrawlerJobResult<T> StartJobMultiThreaded() throws Exception;
	public abstract CrawlerJobResult<T> StartJob() throws Exception;

}
