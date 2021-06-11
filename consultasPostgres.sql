set search_path = "plataforma_ed_a dist";

-- consultar la cantidad de alumnos cursando cada materia
select count(dni_alumno_cursa) as cant_alumnos, cod_materia_cursada from realiza group by cod_materia_cursada;

-- consultar la nota maxima de cada alumno en todas las actividades que entrego  
select max (nota) as max_nota, dni_alumno_entrego from resolucion group by dni_alumno_entrego;

-- consultar nombre, dni y promedio de calificacion de cada alumno en cada actividad entregada
select nombre, apellido, dni, promedio_nota from (select avg(nota) as promedio_nota, dni_alumno_entrego from resolucion group by dni_alumno_entrego)
as prom_por_alumno inner join persona on(dni_alumno_entrego = dni);

-- alumnos que no hayan entregrado alguna actividad de una materia dada
select cod_actividad, cod_materia_pert, dni_alumno, nombre, apellido from actividad,alumno inner join persona on (dni_alumno = dni) 
	where (cod_actividad, dni_alumno) not in 
	(select cod_act as cod_actividad, dni_alumno_entrego as dni_alumno from resolucion);
	
--Listar las materias que solo tienen responsable y sin ningún docente en el equipo docente.
select nombre, cod_materia from materia where cod_materia not in (select cod_mat as cod_materia from docente_asignado);
	
--Listar alumnos que también son docentes con todos sus datos persoanles.
select dni_alumno, nombre, apellido, direccion from (alumno inner join docente on (dni_alumno = dni_docente)) inner join persona on (dni_alumno = dni);


