package com.sagar.ecom.discountservice;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.gson.Gson;
import com.sagar.ecom.discountservice.model.Brand;
import com.sagar.ecom.discountservice.model.Brands;
import com.sagar.ecom.discountservice.model.Category;
import com.sagar.ecom.discountservice.model.Inventory;
import com.sagar.ecom.discountservice.model.Product;

public class DiscountApp {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Please provide input file path");
			return;
		}
		
		String filePath = args[0];
		
		InputStream stream = DiscountApp.class.getClassLoader().getResourceAsStream("categories.json");
		InputStreamReader reader = new InputStreamReader(stream);
		Gson gson = new Gson();

		Category rootCategory = gson.fromJson(reader, Category.class);
		Map<String, Integer> catNameToIdMap = new HashMap<String, Integer>();
		buildCategoryNametoIdMap(rootCategory.getCategories(), catNameToIdMap);

		stream = DiscountApp.class.getClassLoader().getResourceAsStream("brands.json");
		reader = new InputStreamReader(stream);
		Brands brands = gson.fromJson(reader, Brands.class);

		Map<String, Integer> brandNameToDiscountMap = new HashMap<String, Integer>();
		buildBrandNameToDiscountMap(brands.getBrands(), brandNameToDiscountMap);

		Map<Integer, Integer> categoryDiscount = new HashMap<Integer, Integer>();
		Stack<Category> catStack = new Stack<Category>();
		buildCategoryMaxDiscount(rootCategory.getCategories(), catStack, categoryDiscount);

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

			String input = br.readLine();
			Integer sampleSize = Integer.parseInt(input.trim());
			
			Inventory inventory = new Inventory();
			while (sampleSize > 0) {
				input = br.readLine();

				String[] values = input.split(",");
				if (values.length != 4) {
					System.out.println("Not a proper sample input, Ignoring: " + input);
					continue;
				}

				try {
					Integer id = Integer.parseInt(values[0].trim());
					Product product = new Product(values[1].trim(), values[2].trim(),
							Double.parseDouble(values[3].trim()));
					inventory.addProduct(id, product);

					calculateProductDiscount(product, brandNameToDiscountMap, catNameToIdMap, categoryDiscount);
					sampleSize--;
				} catch (Exception e) {
					System.out.println("Not a proper sample input, Ignoring: " + input);
				}
			}

			input = br.readLine();
			if (!input.equals("")) {
				System.out.println("Terminating, Was expecting an empty line but found: " + input);
				return;
			}
			
			input = br.readLine();
			Integer choiceSize = Integer.parseInt(input.trim());

			List<Integer[]> customerChoices = new ArrayList<Integer[]>(choiceSize);
			while (choiceSize > 0) {
				input = br.readLine();

				String[] values = input.split(",");
				if (values.length == 0) {
					System.out.println("Must have atleast one item, Ignoring: " + input);
					continue;
				}

				Integer[] ids = new Integer[values.length];
				for (int i = 0; i < values.length; i++) {
					ids[i] = Integer.parseInt(values[i]);
				}
				customerChoices.add(ids);
				choiceSize--;
			}

			System.out.println("Output");
			Double[] totalPrices = prepareBill(customerChoices, inventory);
			for (Double totalPrice : totalPrices) {
				System.out.println(totalPrice);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Double[] prepareBill(List<Integer[]> customerChoices, Inventory inventory) {
		Double[] prices = new Double[customerChoices.size()];
		int index = 0;
		for (Integer[] items : customerChoices) {
			Double totalPrice = 0.0;
			for (Integer itemId : items) {
				Product product = inventory.getItem(itemId);
				if (product == null) {
					System.out.println("Product not present with id: " + itemId);
					totalPrice = -1.0;
					break;
				}
				totalPrice += product.getDiscountedPrice();
			}

			prices[index++] = totalPrice;
		}

		return prices;
	}

	private static void calculateProductDiscount(Product product, Map<String, Integer> brandNameToDiscountMap,
			Map<String, Integer> catNameToIdMap, Map<Integer, Integer> categoryDiscount) {
		Integer discount = Math.max(brandNameToDiscountMap.get(product.getBrandName()),
				categoryDiscount.get(catNameToIdMap.get(product.getCategoryName())));

		product.setDiscountedPrice(
				(discount > 0 ? (product.getPrice() * ((100 - discount) / 100.0)) : product.getPrice()));
	}

	private static void buildBrandNameToDiscountMap(List<Brand> brands, Map<String, Integer> brandNameToDiscountMap) {
		for (Brand brand : brands) {
			brandNameToDiscountMap.put(brand.getName(), brand.getDiscount());
		}
	}

	private static void buildCategoryNametoIdMap(List<Category> categories, Map<String, Integer> catNameToIdMap) {
		for (Category category : categories) {
			catNameToIdMap.put(category.getName(), category.getId());

			if (category.getCategories() != null && category.getCategories().size() > 0) {
				buildCategoryNametoIdMap(category.getCategories(), catNameToIdMap);
			}
		}
	}

	private static void buildCategoryMaxDiscount(List<Category> categories, Stack<Category> catStack,
			Map<Integer, Integer> categorySearchTree) {
		for (Category category : categories) {
			catStack.push(category);

			if (category.getCategories() != null && category.getCategories().size() > 0) {
				buildCategoryMaxDiscount(category.getCategories(), catStack, categorySearchTree);
			}

			Category topElement = catStack.pop();
			Iterator<Category> iterator = catStack.iterator();
			while (iterator.hasNext()) {
				Integer discount = categorySearchTree.get(topElement.getId());
				if (discount == null) {
					Integer currDiscount = iterator.next().getDiscount();
					categorySearchTree.put(topElement.getId(),
							(currDiscount > topElement.getDiscount()) ? currDiscount : topElement.getDiscount());
				} else {
					Integer currDiscount = iterator.next().getDiscount();
					if (currDiscount > discount) {
						categorySearchTree.put(topElement.getId(), currDiscount);
					}
				}
			}
		}
	}
}
