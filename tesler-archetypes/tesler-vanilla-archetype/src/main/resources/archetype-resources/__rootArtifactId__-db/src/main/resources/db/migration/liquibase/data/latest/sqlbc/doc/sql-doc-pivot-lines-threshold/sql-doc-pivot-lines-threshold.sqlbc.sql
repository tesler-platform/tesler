with
  tab AS (
    SELECT
      level as id
      ,add_months(to_date('20170101', 'yyyymmdd'), level - 1) AS as_of_date
      ,round(dbms_random.value() * 100) AS qty
    FROM dual
    CONNECT BY level < 18
  ),
  t as(
    select
      id
      ,1 as sort
      ,as_of_date
      ,to_char(as_of_date, 'dd.mm.yyyy') as report_date
      ,'Значение норматива' as name
      ,qty
    from tab
    union all
    select
      id + 1000 as id
      ,2 as sort
      ,as_of_date
      ,to_char(as_of_date, 'dd.mm.yyyy') as report_date
      ,'Максимальное значение норматива' as name
      ,CASE WHEN as_of_date between TO_DATE('2017/01/01', 'yyyy/mm/dd') and TO_DATE('2017/12/31', 'yyyy/mm/dd') then 50 ELSE 60 END as qty
    from tab
  )
select * from t
   