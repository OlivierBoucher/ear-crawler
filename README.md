# ear-crawler
A crawler that retrieves grocery products on sale

# The idea behind the project
The idea behind ear-crawler was to create a crawler that would retrieve grocery items being on sale from various stores.
This data could then be centralized on a SasS platform helping users to make their shopping list and save on their groceries bill.

##Features
The platform in its current state can only parse data from 2 websites
- Supermarches.ca - Providing for Quebec grocery stores (Metro, IGA, Loblaws, Maxi, etc.) 
 * STABLE
- Walmart.ca 
 * UNSTABLE
 * Walmart displays its prices using AJAX calls, thus it was not as easy to parse the data
 * Uses phantomJs bindings to run the scripts
 * Uses concurrency because phantomJS is very slow (Still takes about 15 mins to get all the products)

##Normalization
Products from various stores are normalized upon storage. Database creation file is included.

##Installation
Installation is possible using maven.

#Current status
I have paused the development for the moment, please contact me if this project interests you. The product in its state is not usable.  
Most of the code have to be refactored.

#License
####GPLv3
[Included here](LICENSE)
