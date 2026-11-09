CREATE TABLE notification_logs (
    id UUID PRIMARY KEY,
    enrollment_id UUID NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    message VARCHAR(500) NOT NULL
);