-- apply changes
create table snippet (
  id                            integer not null,
  name                          varchar(255) not null,
  value                         clob not null,
  tab_id                        integer not null,
  created_at                    timestamp not null,
  constraint pk_snippet primary key (id),
  foreign key (tab_id) references tab (id) on delete restrict on update restrict
);

create table tab (
  id                            integer not null,
  name                          varchar(255) not null,
  password                      clob,
  constraint uq_tab_name unique (name),
  constraint pk_tab primary key (id)
);

