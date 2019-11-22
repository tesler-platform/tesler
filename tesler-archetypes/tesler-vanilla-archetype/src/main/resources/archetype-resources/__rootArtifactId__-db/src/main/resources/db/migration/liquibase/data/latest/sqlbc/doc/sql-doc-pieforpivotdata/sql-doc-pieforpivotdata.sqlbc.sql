with tForm as (
  select
    'Показатель ' || to_char(round(dbms_random.value() * 4) + 1) as mark
    , (sysdate - ((round(dbms_random.value() * 4) + 1) * 30))    as report_date
    , round(dbms_random.value() * 100)                           as amount
  from dual
  connect by level < 160
),
  tPivotData as (
    select *
    from tForm pivot (sum(amount) as amount for mark
                      in ('Показатель 1' as mark1, 'Показатель 2' as mark2, 'Показатель 3' as mark3, 'Показатель 4' as mark4))
  )
  select TO_CHAR(report_date, 'DD.mm.yyyy') as id
    , report_date
    , TO_CHAR(report_date, 'DD.mm.yyyy') as report_date_char
    , mark1_amount
    , 'Показатель 1' as mark1_name
    , mark2_amount
    , 'Показатель 2' as mark2_name
    , mark3_amount
    , 'Показатель 3' as mark3_name
    , mark4_amount
    , 'Показатель 4' as mark4_name
  from tPivotData