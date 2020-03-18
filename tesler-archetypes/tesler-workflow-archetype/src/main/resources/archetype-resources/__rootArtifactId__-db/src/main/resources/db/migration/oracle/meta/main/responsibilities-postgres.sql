insert into responsibilities (id, dept_id, internal_role_cd) values (0, 0, 'ADMIN');

insert into responsibilities (id, resp_type, screens, dept_id, internal_role_cd) VALUES (nextval('responsibility_id'), 'SCREEN', to_clob(
    '[{
      "id": "id11",
      "name": "doc",
      "text": "Документация",
      "url": "/screen/doc",
      "defaultScreen": true
    },{
      "id": "id12",
      "name": "admin",
      "text": "Администрирование",
      "url": "/screen/admin",
      "defaultScreen": false
    }]'), 0 , 'ADMIN');
