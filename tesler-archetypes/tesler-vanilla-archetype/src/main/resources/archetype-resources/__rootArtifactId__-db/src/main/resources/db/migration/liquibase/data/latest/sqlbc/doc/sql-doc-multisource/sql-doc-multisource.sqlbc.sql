
with t as (
    select
      level as id,
      'Компания ' || to_char(round(dbms_random.value() * 160) + 1)                as name,
      '11111' || to_char(round(dbms_random.value() * 5123) + 1)                   as inn,
      '22222' || to_char(round(dbms_random.value() * 6142) + 1)                   as inn_rufr,
      '33333' || to_char(round(dbms_random.value() * 7423) + 1)                   as inn_egrul
    from dual
    connect by level < 160
)
select *
from t
   