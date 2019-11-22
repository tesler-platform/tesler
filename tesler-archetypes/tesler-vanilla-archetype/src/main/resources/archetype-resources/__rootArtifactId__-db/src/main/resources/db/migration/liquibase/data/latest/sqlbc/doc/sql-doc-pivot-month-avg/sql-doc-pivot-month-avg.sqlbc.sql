
with t1 as (
    SELECT
      level                                                                          AS id,
      'Показатель ' || to_char(round(dbms_random.value() * 8) + 1)                   AS mark,
      to_char((sysdate - ((round(dbms_random.value() * 8) + 1) * 30)), 'DD.MM.YYYY') AS report_date,
      round(dbms_random.value() * 200 )                                       AS avg,
      round(dbms_random.value() * 200 * 25)                                            AS month
    FROM dual
    CONNECT BY level < 160
), t as (
    SELECT id, mark, report_date, avg, month,
      SUM(avg) OVER (PARTITION BY report_date) sum_avg,
      SUM(month) OVER (PARTITION BY report_date) sum_month
    FROM t1
)
select *
from t
   