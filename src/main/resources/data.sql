insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/lg-twins-logo.svg', 'LG 트윈스', '36c58d0e-6eea-41d7-a05b-3d75e214f290', 'LG');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/kt-wiz-logo.svg', 'Kt wiz', 'b0ca7252-70a2-4767-9bb9-0ce0329fb4e8', 'KT');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/ssg-landers-logo.svg', 'SSG 랜더스', '189381b6-33a4-49e2-9d33-0aa553298e85', 'SSG');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/nc-dinos-ogo.svg', 'NC 다이노스', 'fd9a1bef-6e63-40b3-a1e3-b17826efe370', 'NC');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/doosan-bears-logo.svg', '두산 베어스', 'dc0c1501-1ae5-4e15-8558-2d86b13e6bf2', 'DOOSAN');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/kia-tigers-logo.svg', 'KIA 타이거즈', 'fd28b7cd-ada5-4788-8465-4206810a6711', 'KIA');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/lotte-giants-logo.svg', '롯데 자이언츠', 'd56918e7-5dbf-4363-a80f-d7b968c85697', 'LOTTE');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/samsung-lions-logo.svg', '삼성 라이온즈', '1f1e6373-94a4-43e6-b198-d88d71d011fa', 'SAMSUNG');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/hanwha-eagles-logo.svg', '한화 이글스', 'eb1dd93e-a7fd-4fff-a101-f5c1b9c9d2b7', 'HANHWA');
insert into team (description, logo, team_name, team_id, symbol)
values ('', '/logos/kbo/kiwoom-heroes-logo.svg', '키움 히어로즈', '46290e62-7083-428e-aa64-b2eafcd8390b', 'KIWOOM');

insert into member (id, email, team_id, nickname, password, member_id, created_date, last_login_date, is_use)
values ('yongjung95','yongjung95@gmail.com', '189381b6-33a4-49e2-9d33-0aa553298e85', '정이', '$2a$10$7qeSaspIB2ekr7kvhwXR3Oq5PijjmE3F71Q9UlmdcFa.rM3pDewhq',
        '3b619aa2-d4ee-412c-86be-e4face9e3491', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE);
insert into member (id, email, team_id, nickname, password, member_id, created_date, last_login_date, is_use)
values ('123','yongjung95@naver.com', 'dc0c1501-1ae5-4e15-8558-2d86b13e6bf2', '정이2', '$2a$10$7qeSaspIB2ekr7kvhwXR3Oq5PijjmE3F71Q9UlmdcFa.rM3pDewhq',
        '3b619aa2-d4ee-412c-86be-e4face9e3492', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE);
insert into member (id, email, team_id, nickname, password, member_id, created_date, last_login_date, is_use)
values ('1234','yongjung95@daum.net', '189381b6-33a4-49e2-9d33-0aa553298e85', '정이3', '$2a$10$7qeSaspIB2ekr7kvhwXR3Oq5PijjmE3F71Q9UlmdcFa.rM3pDewhq',
        '3b619aa2-d4ee-412c-86be-e4face9e3493', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, TRUE);