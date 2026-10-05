-- Esquema RentaYa para Supabase (PostgreSQL)
-- Cuenta sugerida del proyecto: juanpa.martinezromero@gmail.com
-- Ejecutar en: Supabase → SQL Editor → New query → Run

-- Extensiones útiles
create extension if not exists "pgcrypto";

-- Perfiles ligados a auth.users
create table if not exists public.perfiles (
  id uuid primary key references auth.users (id) on delete cascade,
  nombre text not null,
  correo text not null unique,
  telefono text default '',
  creado_en timestamptz not null default now()
);

-- Propiedades (campos alineados al modelo Kotlin Property)
create table if not exists public.propiedades (
  id text primary key,
  titulo text not null,
  descripcion text not null default '',
  tipo text not null check (tipo in ('APARTAMENTO', 'CASA', 'CUARTO')),
  precio integer not null check (precio >= 0),
  barrio text not null,
  habitaciones integer not null default 1,
  banos integer not null default 1,
  area integer not null default 0,
  amenidades jsonb not null default '[]'::jsonb,
  arrendador_nombre text not null default 'Arrendador',
  arrendador_calificacion real not null default 4.0,
  arrendador_telefono text not null default '',
  imagen text not null default 'sample',
  latitud double precision not null default 6.2442,
  longitud double precision not null default -75.5812,
  id_propietario uuid references auth.users (id) on delete set null,
  creado_en timestamptz not null default now()
);

-- Favoritos (opcional para fases posteriores)
create table if not exists public.favoritos (
  id bigserial primary key,
  id_usuario uuid not null references auth.users (id) on delete cascade,
  id_propiedad text not null references public.propiedades (id) on delete cascade,
  creado_en timestamptz not null default now(),
  unique (id_usuario, id_propiedad)
);

-- Índices
create index if not exists idx_propiedades_barrio on public.propiedades (barrio);
create index if not exists idx_propiedades_tipo on public.propiedades (tipo);
create index if not exists idx_propiedades_precio on public.propiedades (precio);

-- RLS
alter table public.perfiles enable row level security;
alter table public.propiedades enable row level security;
alter table public.favoritos enable row level security;

-- Perfiles: el usuario ve y edita solo el suyo
drop policy if exists "perfiles_lectura_propia" on public.perfiles;
create policy "perfiles_lectura_propia" on public.perfiles
  for select using (auth.uid() = id);

drop policy if exists "perfiles_insercion_propia" on public.perfiles;
create policy "perfiles_insercion_propia" on public.perfiles
  for insert with check (auth.uid() = id);

drop policy if exists "perfiles_actualizacion_propia" on public.perfiles;
create policy "perfiles_actualizacion_propia" on public.perfiles
  for update using (auth.uid() = id);

-- Propiedades: lectura pública; inserción solo autenticados (propias)
drop policy if exists "propiedades_lectura_publica" on public.propiedades;
create policy "propiedades_lectura_publica" on public.propiedades
  for select using (true);

drop policy if exists "propiedades_insercion_autenticada" on public.propiedades;
create policy "propiedades_insercion_autenticada" on public.propiedades
  for insert to authenticated
  with check (auth.uid() = id_propietario or id_propietario is null);

drop policy if exists "propiedades_actualizacion_propia" on public.propiedades;
create policy "propiedades_actualizacion_propia" on public.propiedades
  for update to authenticated
  using (auth.uid() = id_propietario);

-- Favoritos: solo el dueño
drop policy if exists "favoritos_lectura_propia" on public.favoritos;
create policy "favoritos_lectura_propia" on public.favoritos
  for select using (auth.uid() = id_usuario);

drop policy if exists "favoritos_insercion_propia" on public.favoritos;
create policy "favoritos_insercion_propia" on public.favoritos
  for insert with check (auth.uid() = id_usuario);

drop policy if exists "favoritos_borrado_propia" on public.favoritos;
create policy "favoritos_borrado_propia" on public.favoritos
  for delete using (auth.uid() = id_usuario);

-- Semilla: 20 propiedades actuales de SampleData
insert into public.propiedades (
  id, titulo, descripcion, tipo, precio, barrio, habitaciones, banos, area,
  amenidades, arrendador_nombre, arrendador_calificacion, arrendador_telefono,
  imagen, latitud, longitud
) values
  ('1', 'Apto en El Poblado', 'Hermoso apartamento en el sector más exclusivo de Medellín. Cerca a parques, centros comerciales y restaurantes. Excelente iluminación natural.', 'APARTAMENTO', 2100000, 'El Poblado', 2, 2, 65, '["Parqueadero","Gimnasio"]'::jsonb, 'Carlos Pérez', 4.5, '300 123 4567', 'sample', 6.2442, -75.5812),
  ('2', 'Casa en Laureles', 'Casa espaciosa en barrio tradicional. Ideal para familias. Tiene patio y zona BBQ.', 'CASA', 1800000, 'Laureles', 3, 2, 120, '["Parqueadero","Amoblado"]'::jsonb, 'Ana Gómez', 4.8, '301 234 5678', 'sample', 6.2442, -75.5812),
  ('3', 'Cuarto en Envigado', 'Habitación cómoda en casa compartida. Baño privado. Servicios incluidos.', 'CUARTO', 650000, 'Envigado', 1, 1, 20, '["Amoblado"]'::jsonb, 'Luis Rodríguez', 4.2, '302 345 6789', 'sample', 6.2442, -75.5812),
  ('4', 'Apto en Sabaneta', 'Apartamento moderno con excelente ubicación. Cerca al metro y zona comercial.', 'APARTAMENTO', 1500000, 'Sabaneta', 3, 2, 80, '["Parqueadero"]'::jsonb, 'María López', 4.6, '303 456 7890', 'sample', 6.2442, -75.5812),
  ('5', 'Apto en Belén', 'Apartamento acogedor en conjunto cerrado con zonas verdes y juegos infantiles.', 'APARTAMENTO', 1200000, 'Belén', 2, 1, 55, '["Gimnasio"]'::jsonb, 'Jorge Martínez', 4.3, '304 567 8901', 'sample', 6.2442, -75.5812),
  ('6', 'Casa en Buenos Aires', 'Casa tradicional remodelada. Excelente ubicación cerca a universidades.', 'CASA', 1900000, 'Buenos Aires', 4, 3, 150, '["Parqueadero","Amoblado"]'::jsonb, 'Sandra Díaz', 4.7, '305 678 9012', 'sample', 6.2442, -75.5812),
  ('7', 'Apto en Estadio', 'Apartamento con vista panorámica de la ciudad. Cerca al estadio Atanasio Girardot.', 'APARTAMENTO', 1600000, 'Estadio', 2, 2, 70, '["Gimnasio"]'::jsonb, 'Pedro Ramírez', 4.4, '306 789 0123', 'sample', 6.2442, -75.5812),
  ('8', 'Cuarto en Universidad', 'Habitación para estudiantes. Muy cerca a las principales universidades de Medellín.', 'CUARTO', 550000, 'Universidad', 1, 1, 18, '["Amoblado"]'::jsonb, 'Laura Gómez', 4.1, '307 890 1234', 'sample', 6.2442, -75.5812),
  ('9', 'Apto en Itagüí', 'Apartamento económico y bien ubicado. Cerca a centros comerciales y transporte.', 'APARTAMENTO', 1100000, 'Itagüí', 2, 1, 50, '[]'::jsonb, 'Ricardo Silva', 4.0, '308 901 2345', 'sample', 6.2442, -75.5812),
  ('10', 'Casa en La América', 'Casa amplia con garaje para dos vehículos. Barrio tranquilo y seguro.', 'CASA', 2000000, 'La América', 3, 2, 110, '["Parqueadero"]'::jsonb, 'Claudia Moreno', 4.5, '309 012 3456', 'sample', 6.2442, -75.5812),
  ('11', 'Apto en Robledo', 'Apartamento bien iluminado en conjunto residencial con piscina y zonas sociales.', 'APARTAMENTO', 1400000, 'Robledo', 2, 2, 60, '["Gimnasio"]'::jsonb, 'Fernando Castro', 4.6, '310 123 4567', 'sample', 6.2442, -75.5812),
  ('12', 'Cuarto en Laureles', 'Habitación en sector premium. Baño privado y servicios incluidos.', 'CUARTO', 800000, 'Laureles', 1, 1, 22, '["Amoblado","Gimnasio"]'::jsonb, 'Patricia Ruiz', 4.7, '311 234 5678', 'sample', 6.2442, -75.5812),
  ('13', 'Apto en Castilla', 'Apartamento funcional cerca al metro. Ideal para personas que trabajan en el centro.', 'APARTAMENTO', 1300000, 'Castilla', 2, 1, 58, '[]'::jsonb, 'Andrés Vargas', 4.2, '312 345 6789', 'sample', 6.2442, -75.5812),
  ('14', 'Casa en Envigado Centro', 'Casa colonial restaurada en el centro de Envigado. Cerca a parques y restaurantes.', 'CASA', 2200000, 'Envigado', 4, 3, 140, '["Parqueadero","Amoblado"]'::jsonb, 'Beatriz Ortiz', 4.9, '313 456 7890', 'sample', 6.2442, -75.5812),
  ('15', 'Apto en Bello', 'Apartamento nuevo en conjunto cerrado. Excelente transporte público.', 'APARTAMENTO', 1050000, 'Bello', 2, 1, 52, '["Parqueadero"]'::jsonb, 'Gustavo Herrera', 4.3, '314 567 8901', 'sample', 6.2442, -75.5812),
  ('16', 'Apto en Calasanz', 'Apartamento con balcón y hermosa vista. Conjunto con portería 24 horas.', 'APARTAMENTO', 1350000, 'Calasanz', 3, 2, 68, '["Gimnasio"]'::jsonb, 'Mónica Jiménez', 4.4, '315 678 9012', 'sample', 6.2442, -75.5812),
  ('17', 'Casa en San Antonio de Prado', 'Casa campestre cerca de la ciudad. Ideal para familias que buscan tranquilidad.', 'CASA', 1700000, 'San Antonio de Prado', 3, 2, 130, '["Parqueadero"]'::jsonb, 'Alberto Mejía', 4.5, '316 789 0123', 'sample', 6.2442, -75.5812),
  ('18', 'Cuarto en El Poblado', 'Habitación en apartamento compartido en El Poblado. Zona exclusiva.', 'CUARTO', 950000, 'El Poblado', 1, 1, 25, '["Amoblado","Gimnasio","Parqueadero"]'::jsonb, 'Diana Torres', 4.8, '317 890 1234', 'sample', 6.2442, -75.5812),
  ('19', 'Apto en Conquistadores', 'Apartamento moderno con acabados de lujo. Zona de alta valorización.', 'APARTAMENTO', 2500000, 'Conquistadores', 2, 2, 75, '["Parqueadero","Gimnasio"]'::jsonb, 'Roberto Sánchez', 4.7, '318 901 2345', 'sample', 6.2442, -75.5812),
  ('20', 'Apto en Manrique', 'Apartamento económico y seguro. Cerca a colegios y supermercados.', 'APARTAMENTO', 950000, 'Manrique', 2, 1, 48, '[]'::jsonb, 'Lucía Ramírez', 4.1, '319 012 3456', 'sample', 6.2442, -75.5812)
on conflict (id) do nothing;
