create schema if not exists "plataforma_ed_a dist";

set search_path = "plataforma_ed_a dist";

drop domain if exists tipo_dedicacion cascade;
create domain tipo_dedicacion as varchar(15)
	check (value in('EXCLUSIVO','SEMI-EXCLUSIVO','SIMPLE'));

drop table if exists persona cascade;
create table persona (
	dni integer not null,
	nombre varchar(20) not null,
	apellido varchar(20) not null,
	direccion varchar(30) not null,
	constraint clave_prim primary key (dni),
	constraint check_dni check (dni>1000000)
);

drop table if exists telefono;
create table telefono (
	dni_persona integer not null,
	telefono numeric not null,
	constraint pk_telefono_dni primary key (dni_persona, telefono),
	constraint fk_dni foreign key (dni_persona) references persona(dni)
		on delete cascade 
		on update no action
);

drop table if exists docente cascade;
create table docente(
	dni_docente integer not null,
	dedicacion tipo_dedicacion not null,
	constraint pk_dniD primary key (dni_docente),
	constraint fk_dniD foreign key (dni_docente) references persona(dni) 
		on delete cascade 
		on update no action
);

drop table if exists materia cascade;
create table materia (
	cod_materia integer not null,
	nombre varchar(100) not null,
	dni_docente_responsable integer not null,
	constraint pk_cod primary key (cod_materia),
	constraint fk_dni_docente_resp foreign key (dni_docente_responsable) references docente(dni_docente)
		on delete cascade 
		on update no action
);

drop table if exists actividad cascade;
create table actividad (
	cod_actividad integer not null,
	descripcion varchar(100),
	cod_materia_pert integer not null,
	constraint pk_cod_act primary key(cod_actividad),
	constraint fk_cod_mat foreign key (cod_materia_pert) references materia(cod_materia)
		on delete cascade
		on update cascade
);

drop table if exists resolucion cascade;
create table resolucion (
	cod_resol integer not null,
	cod_act integer not null,
	fecha_y_hs_entrega TimeStamp not null,
	dni_alumno_entrego integer not null,
	dni_docente_califica integer,
	nota float4 not null,
	constraint pk_cod_resol primary key (cod_resol),
	constraint fk_cod_act foreign key (cod_act) references actividad(cod_actividad)
		on delete cascade
		on update cascade,
	constraint fk_dni_alumno_entrego foreign key (dni_alumno_entrego) references alumno(dni_alumno)
		on delete cascade
		on update no action,
	constraint fk_dni_docente_calif foreign key (dni_docente_califica) references docente(dni_docente)
		on delete set null
		on update no action,
	constraint check_nota check (nota >= 0.00 and nota<=10.00)
		 
);

drop table if exists alumno cascade;
create table alumno (
	nro_alumno integer not null unique,
	dni_alumno integer not null,
	constraint pk_dni_alum primary key (dni_alumno),
	constraint fk_dniA foreign key (dni_alumno) references persona(dni)
		on delete cascade
		on update no action
);

drop table if exists cargo cascade;
create table cargo (
	cod_cargo integer not null,
	descripcion varchar(100) not null,
	constraint pk_cod_cargo primary key(cod_cargo)
);

drop table if exists facultad cascade;
create table facultad (
	cod_fac integer not null,
	descripcion varchar(100) not null,
	constraint pk_cod_fac primary key (cod_fac)
);

create or replace function alumnoResponsableDeMateria(dniAlumno integer, codMateria integer) returns boolean as $$
	select exists (
		select true from materia where 
			materia.dni_docente_responsable = dniAlumno 
			and
			materia.cod_materia = codMateria);
$$ language sql;

create or replace function alumnoEquipoDeMateria(dniAlumno integer, codMateria integer) returns boolean as $$
	select exists (
		select true from docente_asignado where 
			docente_asignado.dni_docente_asignado = dniAlumno 
			and
			docente_asignado.cod_mat = codMateria);
$$ language sql;

drop table if exists realiza;
create table realiza (
	dni_alumno_cursa integer not null,
	cod_materia_cursada integer not null,
	constraint pk_dni_alumno_cursa_y_materia primary key (dni_alumno_cursa, cod_materia_cursada),
	constraint fk_dni_alumno_cursa foreign key (dni_alumno_cursa) references alumno(dni_alumno)
		on delete cascade
		on update no action,
	constraint fk_cod_materia_cursada foreign key (cod_materia_cursada) references materia(cod_materia)
		on delete cascade
		on update cascade,
	constraint alumno_no_docente_de_materia check(
		alumnoResponsableDeMateria(dni_alumno_cursa, cod_materia_cursada) = false
		and 
		alumnoEquipoDeMateria(dni_alumno_cursa, cod_materia_cursada) = false
	)
 );
 
create or replace function responsableEnEquipo(doc_equipo integer, materia_de_equipo integer) returns bool as $$
	select exists (
		select true from materia where 
		materia.dni_docente_responsable = doc_equipo 
		and
		materia.cod_materia = materia_de_equipo);
$$ language sql;

drop table if exists docente_asignado;
create table docente_asignado (
	cod_mat integer not null,
	dni_docente_asignado integer not null,
	constraint fk_cod_mat foreign key (cod_mat) references materia(cod_materia)
		on delete cascade
		on update cascade,
	constraint fk_dni_docente_asignado foreign key (dni_docente_asignado) references docente(dni_docente)
		on delete cascade
		on update no action,
	constraint check_doc_resp_no_equipo check (responsableEnEquipo(dni_docente_asignado, cod_mat) = false)
);


drop table if exists pertenece;
create table pertenece (
	dni_docente_pert integer not null,
	codigo_fac integer not null,
	codigo_cargo integer not null,
	constraint pk_pert_docente_fac primary key (dni_docente_pert, codigo_fac),
	constraint fk_cod_cargo foreign key (codigo_cargo) references cargo(cod_cargo)
        on delete cascade
		on update cascade,
	constraint fk_cod_fac foreign key (codigo_fac) references facultad(cod_fac)
		on delete cascade
		on update cascade,
	constraint fk_dni_doc_pert foreign key (dni_docente_pert) references docente(dni_docente)
		on delete cascade
		on update no action
);

drop table if exists cambio_calificacion_actividad;
create table cambio_calificacion_actividad (
	cod_resol integer not null,
	fecha_cambio date not null,
	calif_anterior float4 not null,
	calif_nueva float4 not null,
	usuario varchar(100) not null
);

drop table if exists valores_borrados_actualizados_materia;
create table valores_borrados_actualizados_materia (
	operacion_realizada char(6) not null,
	cod_materia_cambiado integer not null,
	nombre_cambiado varchar(100) not null,
	dni_docente_responsable_cambiado integer not null,
	cod_materia_nuevo integer,
	nombre_nuevo varchar(100),
	dni_docente_responsable_nuevo integer,
	fecha_cambio date not null,
	usuario varchar(100) not null
);

create or replace function cambio_calif() returns trigger as $cambio_calif$
	begin 
		insert into cambio_calificacion_actividad values (old.cod_resol, current_date, old.nota, new.nota, current_user);
		return null;
	end;
	$cambio_calif$ language plpgsql; 
	
create trigger cambio_calif after update of nota on resolucion
	for each row execute procedure cambio_calif();
	
create or replace function borradoLogicoMateria() returns trigger as $borradoLogicoMateria$
	begin 
		if (TG_OP = 'DELETE') then
			insert into valores_borrados_actualizados_materia values (TG_OP, old.cod_materia, old.nombre, old.dni_docente_responsable, null, null, null, current_date, current_user);
			return null;
		elseif (TG_OP = 'UPDATE') then
			insert into valores_borrados_actualizados_materia values (TG_OP, old.cod_materia, old.nombre, old.dni_docente_responsable, new.cod_materia, new.nombre, new.dni_docente_responsable, current_date, current_user);
			return null;
		end if;
		return null;
	end;
$borradoLogicoMateria$ language plpgsql; 

create trigger borradoLogicoMateria after delete or update on materia
	for each row execute procedure borradoLogicoMateria();

