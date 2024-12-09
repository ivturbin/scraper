create sequence court_case_id_seq;

create table public.court_case
(
    case_id      bigint                   default nextval('court_case_id_seq'::regclass) not null
        constraint court_case_pk
            primary key,
    case_number  text,
    create_dttm  timestamp with time zone default now()                                  not null,
    modify_dttm  timestamp with time zone default now()                                  not null,
    case_link    text,
    is_scraped   boolean,
    case_1c_id   text,
    updated      timestamp with time zone,
    update_error text
);

comment on column court_case.case_id is 'Внутренний идентификатор судебного дела';

comment on column court_case.case_number is 'Номер судебного дела';

comment on column court_case.case_link is 'Ссылка на страницу с информацией о судебном деле';

comment on column court_case.is_scraped is 'Данные по судебному делу необходимо получать с сайта';

comment on column court_case.case_1c_id is 'ID 1C';

comment on column court_case.updated is 'Время обновления дела для приоритизации';

alter table public.court_case
    owner to "user";

create unique index court_case_case_number_uindex
    on public.court_case (case_number);

