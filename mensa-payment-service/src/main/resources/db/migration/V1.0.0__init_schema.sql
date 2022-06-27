CREATE SCHEMA IF NOT EXISTS payment;

CREATE TABLE payment.payment_audit_event (
	id uuid NOT NULL,
	"action" varchar(255) NULL,
	audit_timestamp int8 NOT NULL,
	correlation_id uuid NULL,
	event_source text NULL,
	payload_class varchar(255) NULL,
	service varchar(255) NULL,
	"timestamp" int8 NOT NULL,
	"version" varchar(255) NULL,
	principal varchar(255) NULL,
	payload varchar(4096) NULL,
	CONSTRAINT payment_audit_event_pkey PRIMARY KEY (id)
);
