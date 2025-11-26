create type user_role as enum ('user', 'admin');

create type reading_status as enum ('reading', 'planning', 'finished', 'dropped', 'on-hold');


create table users
(
    user_id       uuid      default gen_random_uuid() not null
        primary key,
    name          varchar(100)                        not null,
    email         varchar(100)                        not null
        unique,
    password_hash varchar(150)                        not null,
    role          user_role default 'user'::user_role not null,
    created_at    timestamp default now()             not null
);


create table books
(
    book_id        uuid default gen_random_uuid() not null
        primary key,
    title          varchar(100)                   not null,
    rating         real     default 0             not null
        constraint books_rating_check
            check ((rating >= (0)::double precision) AND (rating <= (5)::double precision)),
    review_count   integer  default 0             not null
        constraint books_review_count_check
            check (review_count >= 0),
    published_year smallint
        constraint books_published_year_check
            check ((published_year >= 0) AND (published_year <= 9999))
);


create table reviews
(
    review_id  uuid      default gen_random_uuid() not null
        primary key,
    rating     integer                             not null
        constraint reviews_rating_check
            check ((rating >= 1) AND (rating <= 5)),
    comment    text,
    user_id    uuid                                not null
        constraint fk_reviews_user
            references public.users
            on delete cascade,
    book_id    uuid                                not null
        constraint fk_reviews_book
            references public.books
            on delete cascade,
    created_at timestamp default now()             not null
);


create table user_book_statuses
(
    user_id    uuid                    not null
        constraint fk_ubs_user
            references public.users
            on delete cascade,
    book_id    uuid                    not null
        constraint fk_ubs_book
            references public.books
            on delete cascade,
    status     reading_status          not null,
    created_at timestamp default now() not null,
    primary key (user_id, book_id)
);


create table authors
(
    author_id uuid default gen_random_uuid() not null
        primary key,
    name      varchar(100)                   not null
);


create table book_authors
(
    book_id   uuid not null
        constraint fk_ba_book
            references public.books
            on delete cascade,
    author_id uuid not null
        constraint fk_ba_author
            references public.authors
            on delete cascade,
    primary key (book_id, author_id)
);


create table genres
(
    genre_id uuid default gen_random_uuid() not null
        primary key,
    name     varchar(50)                    not null
        unique
);


create table book_genres
(
    book_id  uuid not null
        constraint fk_bg_book
            references public.books
            on delete cascade,
    genre_id uuid not null
        constraint fk_bg_genre
            references public.genres
            on delete cascade,
    primary key (book_id, genre_id)
);

create table friendships
(
    user_id    uuid                    not null
        constraint fk_friend_user
            references public.users
            on delete cascade,
    friend_id  uuid                    not null
        constraint fk_friend_friend
            references public.users
            on delete cascade,
    created_at timestamp default now() not null,
    primary key (user_id, friend_id),
    constraint chk_no_self_follow
        check (user_id <> friend_id)
);

alter table books drop column rating;
alter table books drop column review_count;