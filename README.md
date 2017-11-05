# Discount Service

###1. Technologies used

Maven 3.0
Java 1.8

###2. To Run this project locally

$ git clone https://github.com/nilabhsagar/discountservice
$ mvn install
$ goto target folder
$ java -jar discountapp.jar <Path to input text file with filename>

###3. To import this project into Eclipse IDE

$ mvn eclipse:eclipse
Import into Eclipse via existing projects into workspace option.
Done.


# About the program

A small system that calculates the discounts on all the applicable items a customer has bought.
 
There are several categories of products. In fact, categories have subcategories which themselves can have subcategories. Below is a diagram.
Casuals is a subcategory of Trousers, which by itself is a subcategory of Men's wear. Some categories have discounts.
 
            Men's wear                 Women's wear (50% off)
            |- Shirts                      |- Dresses
            |- Trousers                    |- Footwear
               |- Casuals (30% off)
            |- Jeans   (20% off)

Each product belongs to a brand which by themselves are running discounts. Below is a table that lists them:
Brands Discounts:

	Wrangler        10%
	Arrow           20%
	Vero Moda       60%
	UCB             None
	Adidas          5%
	Provogue        20%
 
This way, a product can have three types of discounts applicable:
1. Discount on the brand
2. Discount on the category
3. Discount on the ancestor category (e.g. Footwear doesn't have a discount, but it's parent category Women's wear has 50% off). It is worth noting, that it is an ancestor: not just a direct parent, anyone in the lineage.
 
The discount that is applied is the greatest of the above three. For example, if the customer buys a Jeans of Wrangler Brand, the discounts are:
1. Discount on brand: 10%
2. Discount on category (Jeans): 20%
3. Discount on parents (Trousers, Men's wear): None
So, the discount that is applied 20%.
  
The shop inventory is given in CSV form as standard input and also customer options as comma separated Id's after a newline. In the example below, 1,2,3,4 are the customer choices.
 
Sample Input file:

	5
	1, Arrow,Shirts,800
	2, Vero Moda,Dresses,1400
	3, Provogue,Footwear,1800
	4, Wrangler,Jeans,2200
	5, UCB,Shirts,1500
	 
	2
	1,2,3,4
	1,5

Expected output:

	3860 
	2140