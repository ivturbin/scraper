create table scraping_task
(
    scraping_task_id bigserial
        constraint scraping_task_pk
            primary key,
    task_type        text,
    task_status      text                                   not null,
    task_details     text,
    create_dttm      timestamp with time zone default now() not null,
    end_dttm         timestamp with time zone,
    passed           integer,
    failed           integer
);

alter table scraping_task
    owner to "user";