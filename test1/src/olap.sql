select
    rt.name as room_type,
    avg(res.check_out_date - res.check_in_date) as avg_booking_duration_days
from reservations res
         join rooms r on res.room_id = r.room_id
         join room_types rt on r.room_type_id = rt.room_type_id
where res.status != 'canceled'
group by rt.name;


select
    h.name as hotel_name,
    date_trunc('month', res.check_in_date) as month,
    sum(res.total_cost) as monthly_revenue
from reservations res
         join rooms r on res.room_id = r.room_id
         join hotels h on r.hotel_id = h.hotel_id
where res.status in ('booked', 'checked_in', 'checked_out')
  and res.check_in_date >= (current_date - interval '1 year')
group by h.name, date_trunc('month', res.check_in_date)
order by h.name, month;


select
    h.name as hotel_name,
    count(res.reservation_id) as total_bookings,
    rank() over (order by count(res.reservation_id) desc) as booking_rank
from reservations res
         join rooms r on res.room_id = r.room_id
         join hotels h on r.hotel_id = h.hotel_id
where res.status != 'canceled'
group by h.name
order by booking_rank;


with guest_reservations as (
    select
        g.guest_id,
        g.full_name,
        count(res.reservation_id)                                as total_reservations,
        sum(case when res.status = 'canceled' then 1 else 0 end) as canceled_reservations
    from guests g
             join reservations res on g.guest_id = res.guest_id
    group by g.guest_id, g.full_name
)
select
    guest_id,
    full_name,
    total_reservations
from guest_reservations
where total_reservations > 1
  and total_reservations = canceled_reservations;
