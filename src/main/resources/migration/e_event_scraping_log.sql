create table event_scraping_log
(
    event_id         bigint,
    scraping_task_id bigint,
    code             text,
    result           text,
    create_dttm      timestamp with time zone default now()
);

alter table event_scraping_log
    owner to "user";