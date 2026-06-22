# Database Design Standard

Primary Key

BIGSERIAL

Every Table

id

created_at

updated_at

deleted

No Foreign Key Constraint

Only keep:

user_id

image_id

question_id

Status Field

0 WAIT

1 PROCESSING

2 SUCCESS

3 FAILED

Migration Tool

Flyway

Never modify old SQL.

Only create new Migration.