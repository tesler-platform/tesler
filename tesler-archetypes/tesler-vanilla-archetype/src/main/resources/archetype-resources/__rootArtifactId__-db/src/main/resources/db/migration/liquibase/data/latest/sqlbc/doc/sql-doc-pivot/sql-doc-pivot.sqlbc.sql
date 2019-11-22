
with t as (
    select
      level as id,
      'Показатель ' || to_char(round(dbms_random.value() * 8) + 1)                   as mark,
      to_char((sysdate - ((round(dbms_random.value() * 8) + 1) * 30)), 'DD.MM.YYYY') as report_date,
      round(dbms_random.value() * 100)                                               as amount,
      'Значение подсказки для соотв.значений поля mark ' || to_char(round(dbms_random.value() * 8) + 1) as amount_hint,
	  (CASE trunc(dbms_random.value(0,2)) WHEN 1 THEN 'FIRST_VARIANT' ELSE 'ALTERNATE_VARIANT' END) as description,
      (CASE trunc(dbms_random.value(0,2)) WHEN 1 THEN 'remark1' ELSE 'remark2' END)  as remark
    from dual
    connect by level < 160
)
select *
from t
   