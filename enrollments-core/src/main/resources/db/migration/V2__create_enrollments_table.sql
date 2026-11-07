CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL,
    visitor_name VARCHAR(255) NOT NULL,
    visitor_email VARCHAR(255) NOT NULL
);