create table hotels (
    hotel_id serial primary key,
    name text not null,
    location text not null,
    stars_rating int check ( stars_rating between 1 and 5 )
);

create table room_types (
    room_type_id serial primary key,
    name text not null unique,
    amenities text,
    base_price int not null
);

create table rooms (
    room_id serial primary key,
    hotel_id int not null references hotels(hotel_id) on delete cascade,
    room_type_id int not null references room_types(room_type_id),
    room_number int not null,
    is_available boolean default true
);

create table guests (
    guest_id serial primary key,
    full_name text not null,
    email text not null unique,
    phone_number text
);

create table reservations (
    reservation_id serial primary key,
    guest_id int not null references guests(guest_id) on delete cascade,
    room_id int not null references rooms(room_id) on delete cascade,
    check_in_date date not null,
    check_out_date date not null,
    status text check (status in ('booked','checked_in','checked_out','canceled')),
    total_cost int not null
);