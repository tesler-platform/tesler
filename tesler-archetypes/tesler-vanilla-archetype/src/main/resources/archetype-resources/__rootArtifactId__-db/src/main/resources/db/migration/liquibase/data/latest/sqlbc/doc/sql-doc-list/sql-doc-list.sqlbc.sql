
with t as (
  select
    rownum as id,
    'Показатель ' || to_char(round(dbms_random.value() * 8) + 1) as mark,
    round(dbms_random.value() * 100) as amount,
    to_char((sysdate - ((round(dbms_random.value() * 8) + 1) * 30)), 'DD.MM.YYYY') as report_date,
    to_char((sysdate - ((round(dbms_random.value() * 8) + 1) * 30)), 'YYYY-MM-DD"T"HH:MI:SS') as test_datetime,
    substr('YN', dbms_random.value(1,3), 1) as test_checkbox,
    'Y' as test_checkbox_y,
    round(dbms_random.value() * 100)  as percent,
    round(dbms_random.value() * 100, 4)  as fractional,
    123456789012.89 as test_money,
    'Пример развернутого длинного текста для колонки содержащей сокращение из другого поля БК соотносящийся со значением ячейки: Показатель ' || to_char(round(dbms_random.value() * 8) + 1) as long_mark
  from dual
  connect by level < 160
)
select
  t.*,
  t.test_datetime as test_date,
  t.test_datetime as test_datetimesec
from t
   