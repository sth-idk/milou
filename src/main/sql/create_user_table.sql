create table Users (
    id int primary key auto_increment ,
    firstName nvarchar(20) ,
    lastName nvarchar(20) ,
    age int,
    email nvarchar(50) UNIQUE,
    password nvarchar(255)
);