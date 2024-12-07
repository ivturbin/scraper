create sequence court_case_id_seq;

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
    is_scraped boolean
);

comment on column public.court_case.case_id is '���������� ������������� ��������� ����';

comment on column public.court_case.case_1c_id is '������������� ��������� ���� � ������� 1�:�����������';

comment on column public.court_case.case_number is '����� ��������� ����';

comment on column public.court_case.case_link is '������ �� �������� � ����������� � �������� ����';

comment on column public.court_case.is_scraped is '������ �� ��������� ���� ���������� �������� � �����';

alter table public.court_case
    owner to "user";

create unique index court_case_case_number_uindex
    on public.court_case (case_number);

