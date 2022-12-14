create table students (
    id serial primary key,
    name varchar(50)
);

insert into students (name) values ('Иван Иванов');
insert into students (name) values ('Петр Петров');
insert into students (name) values ('Сергей Сергеев');

create table authors (
    id serial primary key,
    name varchar(50)
);

insert into authors (name) values ('Александр Пушкин');
insert into authors (name) values ('Николай Гоголь');
insert into authors (name) values ('Александр Грибоедов');

create table books (
    id serial primary key,
    name varchar(200),
    author_id integer references authors(id)
);

insert into books (name, author_id) values ('Евгений Онегин', 1);
insert into books (name, author_id) values ('Капитанская дочка', 1);
insert into books (name, author_id) values ('Дубровский', 1);
insert into books (name, author_id) values ('Мертвые души', 2);
insert into books (name, author_id) values ('Вий', 2);
insert into books (name, author_id) values ('Тарас Бульба', 2);
insert into books (name, author_id) values ('Горе от ума', 3);

create table orders (
    id serial primary key,
    active boolean default true,
    book_id integer references books(id),
    student_id integer references students(id)
);

insert into orders (book_id, student_id) values (1, 1);
insert into orders (book_id, student_id) values (3, 1);
insert into orders (book_id, student_id) values (4, 1);
insert into orders (book_id, student_id) values (5, 2);
insert into orders (book_id, student_id) values (2, 2);


select s.name as "Студент", '**' as "Скучные книги" from students as s
	where s.id not in (select student_id from orders)
union
select '***', b.name from books as b
	where b.id not in (select book_id from orders);
	
create view poor_imagination
	as select s.name as "Студент", '**' as "Скучные книги" from students as s
	where s.id not in (select student_id from orders)
union
select '***', b.name from books as b
	where b.id not in (select book_id from orders);
	
drop view poor_imagination;		
select * from poor_imagination;
drop table orders cascade;