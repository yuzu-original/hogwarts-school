ALTER TABLE student
ADD CONSTRAINT age_constraint CHECK (age >= 16),
ADD CONSTRAINT name_unique UNIQUE (name),
ALTER COLUMN name SET NOT NULL,
ALTER COLUMN age SET DEFAULT 20;


ALTER TABLE faculty
ADD CONSTRAINT name_and_color_unique UNIQUE (name, color);