-- Opcional: permitir que el dueño borre sus propias propiedades.
-- Ejecutar en SQL Editor si quieres poder limpiar publicaciones de prueba desde la API.

drop policy if exists "propiedades_borrado_propia" on public.propiedades;
create policy "propiedades_borrado_propia" on public.propiedades
  for delete to authenticated
  using (auth.uid() = id_propietario);
