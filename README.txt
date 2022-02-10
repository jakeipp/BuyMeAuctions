Group #111 - CS336 Project
---------------------------
Members:
	Jake Ippolito
	Aaron Galang
---------------------------
Functionality:

	Auctions with dynamic types
	{
		Creation
		Auction Page
		Browsing Page
		Filters
		Closing (Winner)
		Adding to a watchlist
		View auctions user has won
		View user's created auctions
	}

	Bidding
	{
		Place Bids with recurring functionality
		Alerts when user's bid is exceeded by another user
		Alerts when user's recurring bid is exceeded
		Bid History
	}

	Message Center
	{
		Check Messages
		Dismiss Messages
		Goto Auction that message is about
	}
	
	Additional Functionality
	{
		Servlet URL patterns
		Javadoc Comments
	}

---------------------------
	
Information about the dynamic type system:

	Dynamic item_types to include any number of item_types with any number of specific fields
	When displaying auction attributes, the site queries information about the tables it is pulling from.
	All tables with the "item_" prefix are considered item types of item.
	An item_x table must have a PK(item_id, seller_username) which is a FK which references item.
	An item_x table must also have a field named type which is an enum with any number of values.
	An item_x table can have any number of fields after that which must be of type varchar, int, or float.
	This style of dynamic tables is prevalent throughout the system including auction creation, auction browsing, 
	auction filtering and auction sorting so that to change or add an item type, you need only change the schema.
		(all item types can be changed or added using the following template where x, typeN, and field_n can be any value):
		create table item_x(
    			PRIMARY KEY (item_id, seller_username),
			item_id int,
    			seller_username varchar(20) NOT NULL,
    			type enum ('type1', ..., 'typeN'),
    			field_1 varchar(20),
    			field_2 int,
				...
    			field_n float,
    			foreign key (item_id, seller_username) references item(item_id, seller_username));
	




