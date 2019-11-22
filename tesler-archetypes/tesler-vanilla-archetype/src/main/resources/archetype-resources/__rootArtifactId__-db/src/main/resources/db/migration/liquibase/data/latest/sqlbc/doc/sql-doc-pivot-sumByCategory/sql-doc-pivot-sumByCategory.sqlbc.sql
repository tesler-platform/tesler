
with t1 as (
    SELECT
      level                                                                          AS id,
      'Группа ' || to_char(round(dbms_random.value() * 3) + 1)                       AS gmark,
      'Показатель ' || to_char(round(dbms_random.value() * 8) + 1)                   AS mark,
      to_char((sysdate - ((round(dbms_random.value() * 8) + 1) * 30)), 'DD.MM.YYYY') AS report_date,
      round(dbms_random.value() * 100)                                               AS amount,
      'Значение подсказки для соотв.значений поля gmark ' || to_char(round(dbms_random.value() * 8) + 1) AS gmark_hint,
      'Значение подсказки для соотв.значений поля mark ' || to_char(round(dbms_random.value() * 8) + 1) AS mark_hint,
      'Значение подсказки для соотв.значений поля report_date ' || to_char(round(dbms_random.value() * 8) + 1) AS report_date_hint,
      'Значение подсказки для соотв.значений поля amount ' || to_char(round(dbms_random.value() * 8) + 1) AS amount_hint,
      'Значение подсказки для соотв.значений поля amount_for_gmark ' || to_char(round(dbms_random.value() * 8) + 1) AS amount_for_gmark_hint,
      'Значение подсказки для соотв.значений поля sum_amount ' || to_char(round(dbms_random.value() * 8) + 1) AS sum_amount_hint,
      'Значение подсказки для соотв.значений поля sum_amount_for_gmark ' || to_char(round(dbms_random.value() * 8) + 1) AS sum_amount_for_gmark_hint,
      'Значение подсказки для соотв.значений поля forAllDate ' || to_char(round(dbms_random.value() * 8) + 1) AS foralldate_hint,
	    'Значение подсказки для соотв.значений поля sum ' || to_char(round(dbms_random.value() * 8) + 1) AS sum_hint,
	    'Значение подсказки для соотв.значений поля sum_final ' || to_char(round(dbms_random.value() * 8) + 1) AS sum_final_hint,  
      CASE WHEN mod(round(dbms_random.value() * 100), 2) = 0 THEN '#ff0000' ELSE '#00ff00' END AS iconcolor_sum_amount_for_gmark,
      CASE WHEN mod(round(dbms_random.value() * 100), 2) = 0 THEN 'down' ELSE 'up' END AS icontype_sum_amount_for_gmark

    FROM dual
    CONNECT BY level < 160
), t as (
    SELECT id, 
      gmark, gmark_hint,
      mark, mark_hint, 
      report_date, report_date_hint,
      amount, amount_hint,
      SUM(amount) OVER (PARTITION BY gmark, report_date) amount_for_gmark, amount_for_gmark_hint,
      SUM(amount) OVER (PARTITION BY gmark, mark) sum_amount, sum_amount_hint,
      SUM(amount) OVER (PARTITION BY gmark) sum_amount_for_gmark, sum_amount_for_gmark_hint,
	    SUM(amount) OVER (PARTITION BY report_date) sum_final, sum_final_hint,
      foralldate_hint, sum_hint, icontype_sum_amount_for_gmark, iconcolor_sum_amount_for_gmark
    FROM t1
)
select *
from t
   