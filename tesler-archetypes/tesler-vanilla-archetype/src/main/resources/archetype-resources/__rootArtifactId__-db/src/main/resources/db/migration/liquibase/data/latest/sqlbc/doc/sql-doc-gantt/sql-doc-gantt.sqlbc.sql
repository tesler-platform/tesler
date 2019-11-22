with t as (
select 1 as id, date '2016-01-01' as start_dt, date '2016-05-01' as end_dt,0.85 as completed, 'Красный' as name, 'НПФ 1' as y, 'red' as color from dual
union all
select 2 as id, date '2016-05-01' as start_dt, date '2016-07-02' as end_dt,0.85 as completed, 'Зеленый' as name, 'НПФ 1' as y, 'green' as color from dual
union all
select 3 as id, date '2016-02-04' as start_dt, date '2016-03-02' as end_dt,0.85 as completed, 'Розовый' as name, 'НПФ 2' as y, 'pink' as color from dual
union all
select 4 as id, date '2016-03-02' as start_dt, date '2016-07-03' as end_dt,0.85 as completed, 'Голубой' as name, 'НПФ 2' as y, 'blue' as color from dual
union all
select 5 as id, date '2016-01-01' as start_dt, date '2016-05-04' as end_dt,0.85 as completed, 'Красный' as name, 'НПФ 3' as y, 'red' as color from dual
union all
select 6 as id, date '2016-05-04' as start_dt, date '2016-10-05' as end_dt,0.85 as completed, 'Желтый' as name, 'НПФ 3' as y, 'yellow' as color from dual
union all
select 7 as id, date '2016-01-01' as start_dt, date '2016-01-02' as end_dt,0.85 as completed, 'Красный' as name, 'НПФ 4' as y, 'red' as color from dual
union all
select 8 as id, date '2016-01-04' as start_dt, date '2016-01-06' as end_dt,0.85 as completed, 'Желтый' as name, 'НПФ 4' as y, 'yellow' as color from dual
union all
select 9 as id, date '2016-01-01' as start_dt, date '2017-01-02' as end_dt,0.85 as completed, 'Красный' as name, 'НПФ 5' as y, 'red' as color from dual
union all
select 10 as id, date '2017-01-04' as start_dt, date '2016-01-06' as end_dt,0.85 as completed, 'Желтый' as name, 'НПФ 5' as y, 'yellow' as color from dual
union all
select 11 as id, date '2018-01-01' as start_dt, date '2018-01-02' as end_dt,0.85 as completed, 'Красный' as name, 'НПФ 6' as y, 'red' as color from dual
union all
select 12 as id, date '2018-01-04' as start_dt, date '2018-01-06' as end_dt,0.85 as completed, 'Желтый' as name, 'НПФ 6' as y, 'yellow' as color from dual)
select * from t
   