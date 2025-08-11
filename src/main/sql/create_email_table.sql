create table Emails (
    id int primary key auto_increment ,
    sender nvarchar(255) not null ,
    recipient nvarchar(255) not null ,
    subject nvarchar(255) not null ,
    body nvarchar(1000) not null ,
    isRead boolean ,
    code nvarchar(6) unique not null ,
    timestamp date
);