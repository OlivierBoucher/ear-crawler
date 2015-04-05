package com.olivierboucher.crawler;

import com.olivierboucher.model.EpicerieProduct;

import java.util.List;

public class EpicerieCrawlerJobResult {
	private List<EpicerieProduct> items;
	private Common.CrawlerResult result;

	public EpicerieCrawlerJobResult(List<EpicerieProduct> items, Common.CrawlerResult result) {
		setResult(result);
		setItems(items);
	}

	public List<EpicerieProduct> getItems() {
		return items;
	}

	public void setItems(List<EpicerieProduct> items) {
		this.items = items;
	}
	public Common.CrawlerResult getResult() {
		return result;
	}
	public void setResult(Common.CrawlerResult result) {
		this.result = result;
	}
}
