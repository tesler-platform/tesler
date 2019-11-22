SELECT
  task.ID as id,
  task.NAME as name,
  task.JOB as job,
  users_executor.LAST_NAME as executor,
  users_initiator.LAST_NAME as initiator,
  task.REPORT_DATE as report_date
FROM TASK_VANILLA task
  JOIN USERS users_executor ON users_executor.ID = task.EXECUTOR_ID
  JOIN USERS users_initiator ON users_initiator.ID = task.INITIATOR_ID
WHERE (task.ID = :numOrder_equals OR :numOrder_equals IS NULL)
      AND (task.JOB = :job_equals OR :job_equals IS NULL)
      AND (task.EXECUTOR_ID = :executorId OR :executorId IS NULL)
      AND ( ',' || :initiatorId_equalsOneOf || ',' like '%,' || to_char (task.INITIATOR_ID) || ',%'
           OR
           ',' || :initiatorId_equalsOneOf || ',' = ',,')
      AND (1 =
        CASE
          WHEN :isParty_specified LIKE 'Y' AND task.PARTY_ID IS NOT NULL
            THEN 1
          WHEN :isParty_specified LIKE 'N' AND task.PARTY_ID IS NULL
            THEN 1
        END OR :isParty_specified IS NULL)
        AND (trunc(to_date(:reportDate, 'YYYY-MM-DD"T"HH24:MI:SS'), 'YEAR') > to_date('2015-01-01', 'YYYY-MM-DD')
   or :reportDate is NULL )