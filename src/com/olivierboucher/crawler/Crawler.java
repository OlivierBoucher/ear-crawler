package com.olivierboucher.crawler;

import com.olivierboucher.ear.Product;

public abstract class Crawler<T> {
	
	public abstract CrawlerJobResult<T> StartJobMultiThreaded();
	public abstract CrawlerJobResult<T> StartJob();

}
