-- Semillas de Mariana: 3 apartamentos y 2 cuartos (equivalentes a los de SampleData.kt)
-- Ejecutar en: Supabase → SQL Editor → New query → Run (después de esquema.sql)

insert into public.propiedades (
  id, titulo, descripcion, tipo, precio, barrio, habitaciones, banos, area,
  amenidades, arrendador_nombre, arrendador_calificacion, arrendador_telefono,
  imagen, latitud, longitud
) values
  ('mariana-31', 'Apto en Laureles Norte', 'Apartamento luminoso a dos cuadras de la avenida Nutibara. Cerca a cafés, supermercados y ciclorruta.', 'APARTAMENTO', 1450000, 'Laureles Norte', 2, 2, 62, '["Gimnasio"]'::jsonb, 'Camila Restrepo', 4.4, '320 111 2233', 'sample', 6.2442, -75.5812),
  ('mariana-32', 'Apto con balcón en Estadio', 'Apartamento remodelado a pocos minutos de la estación Estadio del metro. Balcón con vista a la unidad deportiva.', 'APARTAMENTO', 1750000, 'Estadio', 3, 2, 78, '["Parqueadero","Amoblado"]'::jsonb, 'Julián Cardona', 4.6, '321 222 3344', 'sample', 6.2442, -75.5812),
  ('mariana-33', 'Apto en Guayabal', 'Apartamento práctico en unidad cerrada, cerca al aeropuerto Olaya Herrera y a la avenida Guayabal.', 'APARTAMENTO', 1150000, 'Guayabal', 2, 1, 54, '["Parqueadero"]'::jsonb, 'Natalia Zapata', 4.3, '322 333 4455', 'sample', 6.2442, -75.5812),
  ('mariana-34', 'Cuarto económico en Buenos Aires', 'Habitación amoblada en casa de familia, a pocas cuadras del tranvía de Ayacucho. Servicios e internet incluidos.', 'CUARTO', 580000, 'Buenos Aires', 1, 1, 16, '["Amoblado"]'::jsonb, 'Gloria Henao', 4.5, '323 444 5566', 'sample', 6.2442, -75.5812),
  ('mariana-35', 'Cuarto económico en Castilla', 'Habitación independiente con baño compartido, cerca a la estación Tricentenario del metro. Ideal para estudiantes.', 'CUARTO', 560000, 'Castilla', 1, 1, 14, '[]'::jsonb, 'Óscar Muñoz', 4.2, '324 555 6677', 'sample', 6.2442, -75.5812)
on conflict (id) do nothing;
