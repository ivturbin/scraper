create table case_scraping_log
(
    case_log_id      bigserial
        constraint case_scraping_log_pk
            primary key,
    case_id          bigint,
    scraping_task_id bigint,
    code             text,
    result           text,
    create_dttm      timestamp with time zone default now()
);

alter table case_scraping_log
    owner to "user";