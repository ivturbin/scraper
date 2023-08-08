create table public.court_case
(
    case_id     bigint                   default nextval('court_case_id_seq'::regclass) not null
        constraint court_case_pk
            primary key,
    case_1c_id  bigint,
    case_number text,
    create_dttm timestamp with time zone default now(),
    modify_dttm timestamp with time zone default now(),
    case_link   text,
    is_scrapped boolean
);

comment on column public.court_case.case_id is 'Внутренний идентификатор судебного дела';

comment on column public.court_case.case_1c_id is 'Идентификатор судебного дела в системе 1С:Бухгалтерия';

comment on column public.court_case.case_number is 'Номер судебного дела';

comment on column public.court_case.case_link is 'Ссылка на страницу с информацией о судебном деле';

comment on column public.court_case.is_scrapped is 'Данные по судебному делу необходимо получать с сайта';

alter table public.court_case
    owner to "user";

create unique index court_case_case_number_uindex
    on public.court_case (case_number);

