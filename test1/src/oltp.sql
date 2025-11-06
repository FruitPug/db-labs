insert into hotels (name, location, stars_rating)
values ('London Hotel', 'London', 5);

insert into guests (full_name, email, phone_number)
values ('Robert Schmitt', 'schmitt@gmail.com', '123456789');


update Rooms
set is_available = false
where room_id = 10;

update Reservations
set status = 'checked_in'
where reservation_id = 3;


delete from Reservations
where reservation_id = 5;

delete from Guests
where guest_id = 4;


select
    r.room_id,
    r.room_number,
    rt.name
from rooms r
         join room_types rt on r.room_type_id = rt.room_type_id
where r.hotel_id = 1
  and r.is_available = true
  and r.room_id not in (
    select room_id from reservations
    where status in ('booked', 'checked_in')
      and (check_in_date, check_out_date) overlaps (date '2025-11-06', date '2025-11-10')
);

select
    reservation_id,
    (rt.base_price * (res.check_out_date - res.check_in_date)) as total_price
from reservations res
         join rooms r on res.room_id = r.room_id
         join room_types rt on r.room_type_id = rt.room_type_id
where res.reservation_id = 1;