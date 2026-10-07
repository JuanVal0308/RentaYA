-- Semillas de Steve: 3 apartamentos y 2 casas (equivalentes a SampleData.kt)
-- Ejecutar en: Supabase → SQL Editor → New query → Run (después de esquema.sql)
-- Coordenadas por barrio (no uses el centro genérico 6.2442,-75.5812).

insert into public.propiedades (
  id, titulo, descripcion, tipo, precio, barrio, habitaciones, banos, area,
  amenidades, arrendador_nombre, arrendador_calificacion, arrendador_telefono,
  imagen, latitud, longitud
) values
  ('steve-21', 'Apto familiar en Aranjuez', 'Apartamento iluminado cerca al Parque de Aranjuez, con fácil acceso a rutas de transporte y comercio local.', 'APARTAMENTO', 1250000, 'Aranjuez', 2, 1, 55, '["Parqueadero"]'::jsonb, 'Daniela Vélez', 4.3, '325 101 2020', 'apto5.jpg', 6.275050, -75.558450),
  ('steve-22', 'Apto renovado en Manrique', 'Apartamento recién renovado cerca a la estación Gardel del Metroplús, ideal para una pareja o familia pequeña.', 'APARTAMENTO', 980000, 'Manrique', 2, 1, 50, '["Amoblado"]'::jsonb, 'Mateo Londoño', 4.1, '325 202 3030', 'apto1.jpg', 6.271800, -75.545850),
  ('steve-23', 'Apto en unidad cerrada de Robledo', 'Apartamento en unidad residencial con zonas verdes, portería permanente y acceso cercano a universidades.', 'APARTAMENTO', 1380000, 'Robledo', 3, 2, 67, '["Parqueadero","Gimnasio"]'::jsonb, 'Valentina Ríos', 4.6, '325 303 4040', 'apto2.jpg', 6.279750, -75.589650),
  ('steve-24', 'Casa amplia en Bello', 'Casa de dos niveles en un sector residencial de Bello, con patio y espacios cómodos para toda la familia.', 'CASA', 1650000, 'Bello', 4, 2, 118, '["Parqueadero"]'::jsonb, 'Santiago Mejía', 4.5, '325 404 5050', 'casa3.jpg', 6.337300, -75.556050),
  ('steve-25', 'Casa moderna en Itagüí', 'Casa remodelada cerca al parque principal de Itagüí, con terraza, cocina integral y buenas rutas de acceso.', 'CASA', 2050000, 'Itagüí', 3, 3, 125, '["Parqueadero","Amoblado"]'::jsonb, 'Paula Castaño', 4.7, '325 505 6060', 'casa4.jpg', 6.172050, -75.609350)
on conflict (id) do nothing;
