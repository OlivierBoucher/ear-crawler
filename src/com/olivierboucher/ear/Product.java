package com.olivierboucher.ear;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Product {
	private int id;
	private Vendor vendor;
	private Category category;
	private String description;
	private String note;
	private String size;
	private String origin;
	private double price;
	private int quantity;
	private double rebate;
	private int rebate_percent;
	private Date start;
	private Date end;
	private String thumbnail;
	public static final int QTY_SAC = -1;
	public static final int QTY_CAISSE = -2;
	public static final int QTY_PQT = -3;
	public static final int QTY_POIDS = -4;
	
	public Product(int id, String description, String size, String origin, double price,int quantity, double rebate, int rebate_percent, String start, String end, Category category,Vendor vendor, String thumbnail){
		
		this.setId(id);
		this.setDescription(description);
		this.setSize(size);
		this.setOrigin(origin);
		this.setPrice(price);
		this.setQuantity(quantity);
		this.setRebate(rebate);
		this.setRebate_percent(rebate_percent);
		this.setStart(start);
		this.setEnd(end);
		this.setCategory(category);
		this.setThumbnail(thumbnail);
		this.setVendor(vendor);
	}
	public Product(int id, String description, String size, String origin, double price,int quantity, double rebate, int rebate_percent, String start, String end, String thumbnail){
		this.setId(id);
		this.setDescription(description);
		this.setSize(size);
		this.setOrigin(origin);
		this.setPrice(price);
		this.setQuantity(quantity);
		this.setRebate(rebate);
		this.setRebate_percent(rebate_percent);
		this.setStart(start);
		this.setEnd(end);
		this.setThumbnail(thumbnail);
	}
	public Product() {
	}
	private Date GetRealDate(String date){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		String[] splitted = date.split(" ");
		switch(splitted[1].charAt(0)){
			
			case 'J':
	            // Deuxième lettre
	            // Possibilités : Janvier, Juin, Juillet
	            switch (splitted[1].charAt(1))
	            {
	                case 'a':
	                    splitted[1] = "01";
	                    break;
	                case 'u':
	                    // Quatrieme lettre
	                    switch (splitted[1].charAt(3))
	                    {
	                        case 'n':
	                            splitted[1] = "06";
	                            break;
	                        case 'l':
	                            splitted[1] = "07";
	                            break;
	                    }
	                    break;
	            }
	            break;
	        case 'M':
	            // Troisième lettre
	            // Possibilités : Mars, Mai
	            switch (splitted[1].charAt(2))
	            {
	                case 'r':
	                    splitted[1] = "03";
	                    break;
	                case 'i':
	                    splitted[1] = "05";
	                    break;
	
	            }
	            break;
	        case 'A':
	            // Deuxième lettre
	            // Possibilités : Avril, Aout
	            switch (splitted[1].charAt(1))
	            {
	                case 'v':
	                    splitted[1] = "04";
	                    break;
	                case 'o':
	                    splitted[1] = "08";
	                    break;
	            }
	            break;
	        case 'F':
	            splitted[1] = "02";
	            break;
	        case 'S':
	            splitted[1] = "09";
	            break;
	        case 'O':
	            splitted[1] = "10";
	            break;
	        case 'N':
	            splitted[1] = "11";
	            break;
	        case 'D':
	            splitted[1] = "12";
	            break;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(splitted[0]);
		sb.append("/");
		sb.append(splitted[1]);
		sb.append("/");
		sb.append("20");
		sb.append(splitted[2]);
		
		try {
			return df.parse(sb.toString());
		} 
		catch (ParseException e) {
			return null;
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getRebate() {
		return rebate;
	}

	public void setRebate(double rebate) {
		this.rebate = rebate;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = GetRealDate(start);
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = GetRealDate(end);
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRebate_percent() {
		return rebate_percent;
	}
	public void setRebate_percent(int rebate_percent) {
		this.rebate_percent = rebate_percent;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public static Comparator<Product> ProductIdComparator
					= new Comparator<Product>(){
		public int compare(Product product1, Product product2){
			return product1.getId() - product2.getId();
		}
	};

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
