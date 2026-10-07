package com.rentaya.app

import com.rentaya.app.data.CoordenadasBarrios
import com.rentaya.app.data.ImagenesInmuebles
import com.rentaya.app.data.model.PropertyType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ImagenesInmueblesTest {

    @Test
    fun hashEsEstableYCoincideConRentaGo() {
        assertEquals(ImagenesInmuebles.hashString("1"), ImagenesInmuebles.hashString("1"))
        assertEquals(49, ImagenesInmuebles.hashString("1"))
        assertEquals(50, ImagenesInmuebles.hashString("2"))
    }

    @Test
    fun poolAptoYCasaPorTipo() {
        assertTrue(ImagenesInmuebles.obtenerImagen("1", PropertyType.APARTAMENTO).endsWith("apto5.jpg"))
        assertTrue(ImagenesInmuebles.obtenerImagen("2", PropertyType.CASA).endsWith("casa1.jpg"))
        assertTrue(ImagenesInmuebles.obtenerImagen("3", PropertyType.CUARTO).contains("apto"))
    }

    @Test
    fun galeriaDevuelveTresFotosDelMismoPool() {
        val galeria = ImagenesInmuebles.obtenerGaleria("1", PropertyType.APARTAMENTO, 3)
        assertEquals(3, galeria.size)
        assertEquals(3, galeria.toSet().size)
    }

    @Test
    fun barriosDistintosNoCaenEnElCentroGenerico() {
        val poblado = CoordenadasBarrios.de("El Poblado", "1")
        val laureles = CoordenadasBarrios.de("Laureles", "2")
        assertNotEquals(poblado, laureles)
        assertTrue(poblado.first < 6.22)
        assertTrue(laureles.first > 6.24)
    }
}
