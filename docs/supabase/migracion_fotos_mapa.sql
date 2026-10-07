-- Migración demo comercial: fotos múltiples, coordenadas por barrio y Storage.
-- Proyecto: RentaYa (vpivstoawetgxlvfjnjb)
-- Ejecutar en: Supabase → SQL Editor → New query → Run
-- Seguro de repetir (IF NOT EXISTS / ON CONFLICT).

-- imagen sigue existiendo: URL https de Storage, clave de asset (apto1.jpg) o 'sample'.
-- imagenes (jsonb) guarda la galería cuando el arrendador sube varias fotos.
alter table public.propiedades
  add column if not exists imagenes jsonb not null default '[]'::jsonb;

comment on column public.propiedades.imagen is
  'URL pública, content/asset key (ej. apto1.jpg) o sample (la app resuelve por hash del id).';
comment on column public.propiedades.imagenes is
  'Galería JSON de URLs o claves de asset. Vacío = la app usa el pool local RentaGo.';

-- Recolocar semillas que aún están en el centro genérico de Medellín
-- (mismos offsets que CoordenadasBarrios.de + ImagenesInmuebles.hashString).
update public.propiedades set latitud = 6.211250, longitud = -75.566900, imagen = 'apto5.jpg' where id = '1';
update public.propiedades set latitud = 6.248600, longitud = -75.595700, imagen = 'casa1.jpg' where id = '2';
update public.propiedades set latitud = 6.169100, longitud = -75.584750, imagen = 'apto2.jpg' where id = '3';
update public.propiedades set latitud = 6.149150, longitud = -75.618450, imagen = 'apto3.jpg' where id = '4';
update public.propiedades set latitud = 6.230100, longitud = -75.600550, imagen = 'apto4.jpg' where id = '5';
update public.propiedades set latitud = 6.245050, longitud = -75.555950, imagen = 'casa5.jpg' where id = '6';
update public.propiedades set latitud = 6.255500, longitud = -75.591850, imagen = 'apto1.jpg' where id = '7';
update public.propiedades set latitud = 6.267150, longitud = -75.570150, imagen = 'apto2.jpg' where id = '8';
update public.propiedades set latitud = 6.171700, longitud = -75.612850, imagen = 'apto3.jpg' where id = '9';
update public.propiedades set latitud = 6.249850, longitud = -75.608150, imagen = 'casa3.jpg' where id = '10';
update public.propiedades set latitud = 6.279400, longitud = -75.591750, imagen = 'apto4.jpg' where id = '11';
update public.propiedades set latitud = 6.244750, longitud = -75.593950, imagen = 'apto5.jpg' where id = '12';
update public.propiedades set latitud = 6.290300, longitud = -75.573950, imagen = 'apto1.jpg' where id = '13';
update public.propiedades set latitud = 6.171550, longitud = -75.583350, imagen = 'casa2.jpg' where id = '14';
update public.propiedades set latitud = 6.338000, longitud = -75.558150, imagen = 'apto3.jpg' where id = '15';
update public.propiedades set latitud = 6.256550, longitud = -75.612950, imagen = 'apto4.jpg' where id = '16';
update public.propiedades set latitud = 6.185500, longitud = -75.656550, imagen = 'casa5.jpg' where id = '17';
update public.propiedades set latitud = 6.209850, longitud = -75.565150, imagen = 'apto1.jpg' where id = '18';
update public.propiedades set latitud = 6.241000, longitud = -75.585150, imagen = 'apto2.jpg' where id = '19';
update public.propiedades set latitud = 6.270400, longitud = -75.547250, imagen = 'apto4.jpg' where id = '20';
update public.propiedades set latitud = 6.252000, longitud = -75.588750, imagen = 'apto1.jpg' where id = 'mariana-31';
update public.propiedades set latitud = 6.255150, longitud = -75.589050, imagen = 'apto5.jpg' where id = 'mariana-32';
update public.propiedades set latitud = 6.208500, longitud = -75.585350, imagen = 'apto4.jpg' where id = 'mariana-33';
update public.propiedades set latitud = 6.244350, longitud = -75.553150, imagen = 'apto3.jpg' where id = 'mariana-34';
update public.propiedades set latitud = 6.288200, longitud = -75.572550, imagen = 'apto2.jpg' where id = 'mariana-35';
update public.propiedades set latitud = 6.275050, longitud = -75.558450, imagen = 'apto5.jpg' where id = 'steve-21';
update public.propiedades set latitud = 6.271800, longitud = -75.545850, imagen = 'apto1.jpg' where id = 'steve-22';
update public.propiedades set latitud = 6.279750, longitud = -75.589650, imagen = 'apto2.jpg' where id = 'steve-23';
update public.propiedades set latitud = 6.337300, longitud = -75.556050, imagen = 'casa3.jpg' where id = 'steve-24';
update public.propiedades set latitud = 6.172050, longitud = -75.609350, imagen = 'casa4.jpg' where id = 'steve-25';

-- Bucket público de fotos (también se puede crear en Dashboard → Storage).
insert into storage.buckets (id, name, public)
values ('inmuebles', 'inmuebles', true)
on conflict (id) do update set public = true;

-- Lectura pública de las fotos de listados
drop policy if exists "inmuebles_lectura_publica" on storage.objects;
create policy "inmuebles_lectura_publica"
  on storage.objects for select
  using (bucket_id = 'inmuebles');

-- Escritura autenticada solo en su carpeta {auth.uid()}/...
drop policy if exists "inmuebles_subida_autenticada" on storage.objects;
create policy "inmuebles_subida_autenticada"
  on storage.objects for insert to authenticated
  with check (
    bucket_id = 'inmuebles'
    and auth.uid()::text = (storage.foldername(name))[1]
  );

drop policy if exists "inmuebles_actualizacion_propia" on storage.objects;
create policy "inmuebles_actualizacion_propia"
  on storage.objects for update to authenticated
  using (
    bucket_id = 'inmuebles'
    and auth.uid()::text = (storage.foldername(name))[1]
  );

drop policy if exists "inmuebles_borrado_propio" on storage.objects;
create policy "inmuebles_borrado_propio"
  on storage.objects for delete to authenticated
  using (
    bucket_id = 'inmuebles'
    and auth.uid()::text = (storage.foldername(name))[1]
  );
