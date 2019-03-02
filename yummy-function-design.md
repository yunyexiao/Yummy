# Yummy Function Points Design

## 0 Customer

### 0.0 Signing Up

#### 0.0.0 When sign-up request comes

##### 0.0.0.0 Preconditions
* The email does not exist.

##### 0.0.0.1 Procedure
0. Insert a row of data with zero in column 'valid'. 
1. Generate a unique key to that email.
2. Store generated email-key pair in database.
3. Send an email to that address with a url whose request params have 'email' 
and 'key' values.

#### 0.0.1 When sign-up confirmed

##### 0.0.1.0 Preconditions
* This customer exists and is invalid. 
* The email-key pair matches that in database.

##### 0.0.1.1 Procedure
0. Update the row of this email: set 'valid' to '1'.
1. Remove the email-key pair in database.

### 0.1 Info Modification

#### 0.1.0 Preconditions
* The customer should be valid.

#### 0.1.1 Aspects

##### 0.1.1.0 Name & Phone
0. Update related row in database.

##### 0.1.1.1 Addresses
* Insert, update, delete.

##### 0.1.1.2 Payment Account
* Insert, delete.

### 0.2 Level

#### 0.2.0 Preconditions
* The customer should be valid.

#### 0.2.1 Rules
* Only those completed orders should count.
* lvl 0: default, no promotions.
* lvl 1: cost over 300, 95% discount.
* lvl 2: cost over 500, 90% discount.
* lvl 3: cost over 1000, 85% discount.
* lvl 4: cost over 3000, 80% discount.
* lvl 5: cost over 5000, 70% discount.
* lvl 6: cost over 8000, 65% discount.
* lvl 7: cost over 10000, 60% discount.
* lvl 8: cost over 30000, 50% discount.

#### 0.2.2 Implementation
* Update after order completed.
* Rules: divided by total cost. Table driven.

### 0.3 Account Cancellation

#### 0.3.0 Preconditions
* The customer should be valid.

#### 0.3.1 Procedure
1. Set 'valid' to zero in database.

### 0.4 Ordering

#### 0.4.0 Preconditions
* The customer should be valid.

#### 0.4.1 Delivery Time Estimation
* Use fake data first.

#### 0.4.2 Placing Orders

##### 0.4.2.0 Preconditions
* The restaurant exists and is valid.
* All meals exist and are not over-sold.

##### 0.4.2.1 Cost Calculation
0. Total cost of meals multiple by discount rate according to customer level
(see 0.2 Level).
1. Subtract by restaurant discount.

##### 0.4.2.2 Procedure
0. Insert an order and set its state to 'placed' and set its placeTime.
1. Create a timing task to directly cancel this order in 2 min (15 min in theory).

#### 0.4.3 Payment

##### 0.4.3.0 Preconditions
* Payment account and password should match.
* Balance should be enough.
* Order state should be 'placed'.

##### 0.4.3.1 Procedure
0. Reduce balance of the payment account.
1. Insert a row in transaction table.
2. Set this order's state to 'payed'.
3. Cancel its timing task.

### 0.5 Order Cancellation

#### 0.5.0 Preconditions
* The customer should be valid.
* The order exists and its state is not 'canceling' or 'canceled' or 'delivering'
or 'completed' or 'settled'.

#### 0.5.1 Rules
* If order state is 'placed', 100% back.
* If order state is over 'placed', and canceling within 1 min (off payTime), 100% back.
* If order state is over 'placed', and canceling beyond 1 min but within 2 min, 80% back.
* If order state is over 'placed', and canceling beyond 2 min but within 3 min, 60% back.
* If order state is over 'placed', and canceling beyond 3 min but within 4 min, 40% back.
* If order state is over 'placed', and canceling beyond 4 min but within 5 min, 20% back.
* If order state is over 'placed', and canceling beyond 5 min, sorry, cancellation fails.

#### 0.5.2 Procedure
0. If order state is 'placed', set it to 'canceled', or set the order state to 
'canceling'.
1. Set the order's cancel time.

### 0.6 Order States

#### 0.6.0 Preconditions
* The customer is valid.
* This order exists.

#### 0.6.1 Details
* About 'placed' and 'payed' states, see '0.4 Ordering'.
* About 'canceling' and 'canceled' state, see '0.4 Ordering' and '0.5 Order Cancellation'.
* 'accepted' state: after the restaurant accepts this order.
* 'delivering' state: after the restaurant starts a delivery.
* 'completed' state: after the customer confirms its delivery. Update this customer's level.
* 'settled' state: see '2.1 Transaction Settlement'.

### 0.7 Statistics

#### 0.7.0 Preconditions
* The customer should be valid.


## 1 Restaurant

### 1.0 Signing Up

#### 1.0.0 Procedure
0. Read nextRidDec property from properties table.
1. Convert it to 7-bit code as new restaurant id.
2. Add this property by 1 and save back to database.
3. Insert the new restaurant with its generated id into database, 'valid' set 
to 0.
4. Create a new http session and store the id in it.

### 1.1 Info Modification

#### 1.1.0 Preconditions
* This restaurant exists in database.

#### 1.1.1 Procedure
0. If this restaurant is invalid, directly update info in 'restaurant' table.
1. Or if in `restaurant_draft` table there's a row about this restaurant, 
directly update that row.
2. Otherwise, insert a row in `restaurant_draft` table.

### 1.2 Publishing Items

#### 1.2.0 Preconditions
* This restaurant is valid.

#### 1.2.1 Meals & Combos
0. Insert a row in meal table.

#### 1.2.2 Promotions
0. Insert a row in promotion table.

### 1.3 Delivery Records

#### 1.3.0 Preconditions
* This restaurant is valid.

#### 1.3.1 Contents
* All orders whose state is accepted or delivering.

### 1.4 Statistics

#### 1.4.0 Preconditions
* This restaurant is valid.


## 2 Administrator

### 2.0 Restaurant Info Approval

#### 2.0.0 Preconditions
* This restaurant exists.

#### 2.0.1 Procedure
* If this restaurant is invalid, validate it or delete it.
* If this restaurant is valid, update its info with that in `restaurant_draft`
table or do nothing, and finally delete the row in `restaurant_draft` table.

### 2.1 Transaction Settlement

#### 2.1.0 Preconditions
* This order exists and its state is 'completed' or 'canceling'.

#### 2.1.1 Procedure
* For a completed order, find its transaction record and give 70% of income to 
restaurant. Then set order state to 'settled'.
* For a canceling order, find its transaction record, return some money to 
customer according to the rules defined in '0.5 Order Cancellation', calculate 
the income, and give 70% to restaurant. Then set order state to 'cancelled'.

### 2.2 Statistics

#### 2.2.0 Restaurants
* Number, types.

#### 2.2.1 Customers
* Number, levels.

#### 2.2.2 Finance
* Summary, time.

