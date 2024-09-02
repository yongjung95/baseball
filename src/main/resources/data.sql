insert into team (description, logo, team_name, team_id, symbol)
values ('', '', 'LG 트윈스', '36c58d0e-6eea-41d7-a05b-3d75e214f290', 'LG');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', 'Kt wiz', 'b0ca7252-70a2-4767-9bb9-0ce0329fb4e8', 'KT');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', 'SSG 랜더스', '189381b6-33a4-49e2-9d33-0aa553298e85', 'SSG');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', 'NC 다이노스', 'fd9a1bef-6e63-40b3-a1e3-b17826efe370', 'NC');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', '두산 베어스', 'dc0c1501-1ae5-4e15-8558-2d86b13e6bf2', 'DOOSAN');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', 'KIA 타이거즈', 'fd28b7cd-ada5-4788-8465-4206810a6711', 'KIA');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', '롯데 자이언츠', 'd56918e7-5dbf-4363-a80f-d7b968c85697', 'LOTTE');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', '삼성 라이온즈', '1f1e6373-94a4-43e6-b198-d88d71d011fa', 'SAMSUNG');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', '한화 이글스', 'eb1dd93e-a7fd-4fff-a101-f5c1b9c9d2b7', 'HANHWA');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '', '키움 히어로즈', '46290e62-7083-428e-aa64-b2eafcd8390b', 'KIWOOM');

insert into member (email, team_id, nickname, password, member_id, created_date, last_login_date, is_use)
values ('yongjung95@gmail.com', '189381b6-33a4-49e2-9d33-0aa553298e85', '정이', '1234',
        '3b619aa2-d4ee-412c-86be-e4face9e3491', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE);
