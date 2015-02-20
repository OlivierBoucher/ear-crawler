package com.olivierboucher.crawler;

import java.util.List;

public class CrawlerJobResult<T> {
	private List<T> items;
	private Common.CrawlerResult result;
	
	public CrawlerJobResult(List<T> items, Common.CrawlerResult result){
		setResult(result);
		setItems(items);
	}
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	public Common.CrawlerResult getResult() {
		return result;
	}
	public void setResult(Common.CrawlerResult result) {
		this.result = result;
	}
}
