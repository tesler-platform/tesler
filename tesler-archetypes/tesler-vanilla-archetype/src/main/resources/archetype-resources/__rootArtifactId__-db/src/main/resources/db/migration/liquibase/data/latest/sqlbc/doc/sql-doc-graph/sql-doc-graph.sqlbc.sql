
with tTab as (
  select
    1 as from__id, 'Сбербанк' from__name, '1111111111' from__inn, '#/screen/doc/view/errors' from__exampledd, 'relativeNew' from__exampleddtype,
    2 as to__id, 'Сбербанк технологии' to__name, '2222222222' to__inn, 'screen/doc/view/errors' to__exampledd, 'inner' to__exampleddtype,
    10 as weight,
    1 as sort
  from dual
  union ALL
  select
    2 as from__id, 'Сбербанк технологии' as from__name, '2222222222' as from__inn, 'screen/doc/view/errors' from__exampledd, 'inner' from__exampleddtype,
    3 as to__id, 'Тройка диалог' as to__name, '3333333333' as to__inn, 'screen/doc/view/errors' to__exampledd, 'inner' to__exampleddtype,
    20 as weight,
    2 as sort
  from dual
  union ALL
  select
    2 as from__id, 'Сбербанк технологии' as from__name, '2222222222' as from__inn, 'screen/doc/view/errors' from__exampledd, 'inner' from__exampleddtype,
    4 as to__id, 'Корус консалтинг' as to__name, '4444444444' as to__inn, 'screen/doc/view/errors' to__exampledd, 'inner' to__exampleddtype,
    30 as weight,
    3 as sort
  from dual
  union ALL
  select
    4 as from__id, 'Корус консалтинг' as from__name, '4444444444' as from__inn, 'screen/doc/view/errors' from__exampledd, 'inner' from__exampleddtype,
    5 as to__id, 'БНП Париба' as to__name, '5555555555' as to__inn, 'screen/doc/view/errors' to__exampleDD, '' from__exampleddtype,
    40 as weight,
    4 as sort
  from dual
)
select from__id||from__name||to__id||to__name||sort as id, tTab.* from tTab