drop database if exists CS336Project;
create database CS336Project;
use CS336Project;

create table administrators(
	username varchar(20) primary key,
    email varchar(30),
    password varchar(20),
    name varchar(20),
    dob date
);

create table customer_support_reps(
	username varchar(20) primary key,
    email varchar(30),
    password varchar(20),
    name varchar(20),
    dob date
);

create table end_users(
	username varchar(20) primary key,
    email varchar(30),
    password varchar(20),
    name varchar(20),
    dob date
);

create table generates_report(
	PRIMARY KEY (username, report_id),
	report_id int,
    username varchar(20) NOT NULL,
	report_type int,
    report_data varchar(1024),
	foreign key (username) references administrators(username)
);

create table tickets(
	PRIMARY KEY (support_username, end_username, ticket_num),
    support_username varchar(20) NOT NULL,
    end_username varchar(20) NOT NULL,
	ticket_num int,
    descr varchar(1024),
    sub int,
    foreign key (support_username) references customer_support_reps(username),#NOT NULL
    foreign key (end_username) references end_users(username)#NOT NULL
);

create table auction(
	PRIMARY KEY (auction_id, seller_username),
	auction_id int auto_increment,
    seller_username varchar(20) NOT NULL,
    min_price int,
    starting_price int,
    end_date date,
    end_time time,
    bid_inc int,
    final_price int default -1,
    buyer_username varchar(20),
    foreign key (seller_username) references end_users(username),
    foreign key (buyer_username) references end_users(username)
);

create table messages(
	PRIMARY KEY (message_num),
	message_num int NOT NULL auto_increment,
    auction_id int,
    end_username varchar(20) NOT NULL,
    subject varchar(50),
    description varchar(100),
    foreign key (auction_id) references auction(auction_id),
    foreign key (end_username) references end_users(username)
);

create table bids(
	PRIMARY KEY (buyer_username, auction_id, bid_time, bid_amount),
    buyer_username varchar(20) NOT NULL,
	auction_id int NOT NULL,
	bid_amount int,
    bid_time datetime default now(),
    isReccuring boolean default false,
    max_bid int default 0,
    foreign key (buyer_username) references end_users(username),
    foreign key (auction_id) references auction(auction_id)
);

create table item (
	primary key (item_id, seller_username),
    item_id int auto_increment,
    seller_username varchar(20) NOT NULL,
    auction_id int,
    quantity int,
    title varchar(100),
    description varchar(1024),
    manufacterer varchar(20),
    model_year varchar(5),
    foreign key (seller_username) references end_users(username),
    foreign key (auction_id) references auction(auction_id)
);

create table item_vehicle(
    PRIMARY KEY (item_id, seller_username),
	item_id int,
    seller_username varchar(20) NOT NULL,
    type enum('Car', 'Motorcycle', 'Watercraft'),
    cylinders int,
    displacment_in_liters float,
    miles_on_odometer int,
    foreign key (item_id, seller_username) references item(item_id, seller_username)
);

create table item_computer(
    PRIMARY KEY (item_id, seller_username),
	item_id int,
    seller_username varchar(20) NOT NULL,
    type enum ('Desktop', 'Laptop', 'Tablet'),
    processor varchar(20),
    ram_in_gigabytes int,
    storage_in_gigabytes int,
    graphics varchar(20),
    foreign key (item_id, seller_username) references item(item_id, seller_username)
);

create table item_furniture(
    PRIMARY KEY (item_id, seller_username),
	item_id int,
    seller_username varchar(20) NOT NULL,
    type enum ('Table', 'Chair', 'Cabinet'),
    length_in_inches int,
    width_in_inches int,
    height_in_inches int,
    material varchar(20),
    foreign key (item_id, seller_username) references item(item_id, seller_username)
);

create table watchlist(
	primary key (auction_id, buyer_username),
    auction_id int,
    buyer_username varchar(20),
    foreign key (auction_id) references auction(auction_id),
    foreign key (buyer_username) references end_users(username)
);


# -- Start Insert --
insert into administrators value
('buymeAdmin', 'admin@buyme.com', 'root', 'Jake / Aaron', 20220101);

insert into customer_support_reps values
('randy12', 'randym@buyme.com', 'rand123', 'Randy', 19960201),
('liz46', 'liz@buyme.com', 'liz123', 'Liz', 19960503);

insert into end_users value
('jeff111', 'jeff111@gmail.com', 'jeff111', 'Jeffery', 19970201),
('randy111', 'randy111@gmail.com', 'randy111', 'Randy', 19970101),
('demo', 'demo@gmail.com', 'demo', 'Demo', 19970101);

-- Dummy auction inserts
INSERT INTO `auction`(auction_id, seller_username, min_price, starting_price, end_date, end_time, bid_inc) VALUES (1,'jeff111',500,199,'2022-05-26','19:00:00',5),(2,'jeff111',600,299,'2022-05-26','19:00:00',5),(3,'jeff111',300,100,'2022-05-26','19:00:00',5),(4,'randy111',100,20,'2022-05-26','21:00:00',1),(5,'randy111',60,10,'2022-05-26','21:00:00',1),(6,'randy111',200,50,'2022-05-26','21:00:00',5),(7,'demo',25000,10000,'2022-05-26','17:00:00',100),(8,'demo',40000,10000,'2022-05-26','17:00:00',150),(9,'demo',25000,5000,'2022-05-26','17:00:00',100);
INSERT INTO `item` VALUES (1,'jeff111',1,1,'Macbook Air','Mint Condition','Apple','2011'),(2,'jeff111',2,1,'Surface Pro 6','Good condition minor scratches','Microsoft','2020'),(3,'jeff111',3,1,'Dell Optiplex','Fair condition','Dell','2010'),(4,'randy111',4,2,'Dining Room Table','Great condition','Ikea','2011'),(5,'randy111',5,1,'Desk Chair','Great condition','Ikea','2019'),(6,'randy111',6,1,'Armoire','Great condition','Ikea','2010'),(7,'demo',7,1,'Lexus RC350 F Sport','Mint Condition','Lexus','2019'),(8,'demo',8,1,'Honda CBR','Mint Condition','Honda','2022'),(9,'demo',9,2,'2x Sea-doo GTX','Mint Condition comes with trailer!','Sea-Doo','2010');
INSERT INTO `item_computer` VALUES (1,'jeff111','Laptop','i5',8,500,'Intel Integrated'),(2,'jeff111','Tablet','i7',16,500,'Intel Integrated'),(3,'jeff111','Desktop','i5',4,250,'Intel Integrated');
INSERT INTO `item_furniture` VALUES (4,'randy111','Table',72,36,36,'Wood'),(5,'randy111','Chair',36,36,36,'Leather'),(6,'randy111','Cabinet',60,24,84,'Wood');
INSERT INTO `item_vehicle` VALUES (7,'demo','Car',6,3.5,30000),(8,'demo','Motorcycle',4,0.9,1),(9,'demo','Watercraft',3,1.5,100);



