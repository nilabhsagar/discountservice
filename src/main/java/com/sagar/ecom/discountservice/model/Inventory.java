package com.sagar.ecom.discountservice.model;

import java.util.HashMap;
import java.util.Map;

public class Inventory {
	private Map<Integer, Product> idToProduct = new HashMap<Integer, Product>();
	
	public Inventory() {
	}
	
	public void addProduct(int id, Product product) {
		idToProduct.put(id, product);
	}

	public Product getItem(Integer itemId) {
		return idToProduct.get(itemId);
	}
}
