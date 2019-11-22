
with t1 as (
    SELECT
      level                                                                          AS id,
      to_char((sysdate - ((round(dbms_random.value() * 8) + 1) * 30)), 'DD.MM.YYYY') AS report_date,
      round(dbms_random.value() * 200 )                                       AS plus,
      round(dbms_random.value() * 200 )                                       AS minu
    FROM dual
    CONNECT BY level < 30
), t as (
    SELECT id, report_date, plus, minu, plus - minu as sum
    from t1
)
select *
from t
   