package com.olivierboucher.crawler;

import java.util.HashMap;
import java.util.List;

public class CrawlerJobResult<T> {
	private List<T> items;
	private HashMap<String,Common.CrawlerResult> result;
	
	public CrawlerJobResult(List<T> items, HashMap<String,Common.CrawlerResult> result){
		setResult(result);
		setItems(items);
	}
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	public HashMap<String,Common.CrawlerResult> getResult() {
		return result;
	}
	public void setResult(HashMap<String,Common.CrawlerResult> result) {
		this.result = result;
	}
}
