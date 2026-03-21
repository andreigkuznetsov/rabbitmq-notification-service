create table notifications (
    id bigserial primary key,
    notification_id varchar(64) not null unique,
    user_id varchar(64) not null,
    channel varchar(20) not null,
    recipient varchar(255) not null,
    template_code varchar(100) not null,
    payload_json text not null,
    status varchar(20) not null,
    retry_count integer not null default 0,
    error_code varchar(100),
    error_message text,
    created_at timestamp not null,
    updated_at timestamp not null
);