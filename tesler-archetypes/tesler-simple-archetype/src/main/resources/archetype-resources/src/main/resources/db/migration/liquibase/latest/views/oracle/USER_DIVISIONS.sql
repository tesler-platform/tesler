CREATE
OR REPLACE FORCE VIEW USER_DIVISIONS (USER_ID, DIVISIONS) AS
SELECT u.id AS                        user_id,
       LISTAGG(ud.full_name, CHR(10)) WITHIN GROUP (ORDER BY ud.id) AS divisions
FROM users u,
    (SELECT DISTINCT ur.user_id,
    d.id,
    d.full_name
    FROM user_role ur,
    division d
    WHERE ur.division_id = d.id(+)
    ) ud
WHERE u.id = ud.user_id(+)
GROUP BY u.id;

