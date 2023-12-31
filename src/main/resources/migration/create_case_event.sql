create table public.case_event
(
    case_event_id     bigint                   default nextval('case_event_id_seq'::regclass) not null
        constraint case_event_pk
            primary key,
    instantion        text                                                                    not null,
    event_hash        bigint                                                                  not null,
    event_id          text,
    event_date        date                                                                    not null,
    event_type        text                                                                    not null,
    event_actor       text,
    event_description text                                                                    not null,
    file_link         text,
    file_data         bytea,
    file_info         text,
    additional_info   text,
    is_signed         boolean,
    signature_info    text,
    original_data     text,
    create_dttm       timestamp with time zone default now(),
    modify_dttm       timestamp with time zone default now(),
    received_1c       boolean,
    event_case_id     bigint
        constraint case_event_court_case_case_id_fk
            references public.court_case,
    court_name        text,
    data_court        text
);

comment on table public.case_event is 'C������ ������� �������� ���';

comment on column public.case_event.case_event_id is '���������� ������������� �������';

comment on column public.case_event.event_hash is '��� ��� ������� ������������� �������, ����������� �� �����: ����� ����, EventID, EventDate, EventActor, EventDescription';

comment on column public.case_event.event_id is '������������� ������� �� ������ �����';

comment on column public.case_event.event_date is '���� �������';

comment on column public.case_event.event_type is '��� �������';

comment on column public.case_event.event_actor is '����������� ���� �������';

comment on column public.case_event.event_description is '�������� �������';

comment on column public.case_event.file_link is '������ �� ����';

comment on column public.case_event.file_data is '����������� ����';

comment on column public.case_event.file_info is '������ � ���� / ������� ���������� �����';

comment on column public.case_event.signature_info is '������ � �������';

comment on column public.case_event.original_data is '������������ ������ html � �������';

comment on column public.case_event.create_dttm is '������ �������� �������';

comment on column public.case_event.modify_dttm is '������ ���������� ��������� �������';

comment on column public.case_event.received_1c is '1� �������� ������ �� �������';

alter table public.case_event
    owner to "user";

create unique index case_event_event_hash_uindex
    on public.case_event (event_hash);

