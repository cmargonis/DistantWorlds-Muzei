/*
 *  Copyright 2026 Chris Margonis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ultimus.distantworlds.data.imgur

import com.ultimus.distantworlds.data.imgur.model.Album
import com.ultimus.distantworlds.data.imgur.model.AlbumResponse
import com.ultimus.distantworlds.data.imgur.model.Image
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class ImgurImageProviderTest {

    private val service: ImgurService = mockk()
    private lateinit var testedClass: ImgurImageProvider

    private val albumId = "testAlbumId"
    private val clientId = "testClientId"

    @BeforeEach
    fun setup() {
        testedClass = ImgurImageProvider(service, albumId, clientId)
    }

    @Test
    fun `given successful response with images, when fetching artwork, then return mapped artwork data`() {
        val images = arrayListOf(
            Image(id = "img1", title = "Title 1", description = "Desc 1", link = "https://imgur.com/img1.jpg"),
            Image(id = "img2", title = "Title 2", description = "Desc 2", link = "https://imgur.com/img2.jpg"),
        )
        val album = Album(id = "album1", images = images)
        val albumResponse = AlbumResponse(data = album, success = true)
        val call: Call<AlbumResponse> = mockk()
        every { service.getAlbumDetails(any(), any()) } returns call
        every { call.execute() } returns Response.success(albumResponse)

        val result = testedClass.fetchArtwork()

        assertEquals(2, result.size)
        assertEquals("img1", result[0].token)
        assertEquals("Title 1", result[0].title)
        assertEquals("Desc 1", result[0].byline)
        assertEquals("img2", result[1].token)
        assertEquals("Title 2", result[1].title)
    }

    @Test
    fun `given IOException during API call, when fetching artwork, then propagate exception`() {
        val call: Call<AlbumResponse> = mockk()
        every { service.getAlbumDetails(any(), any()) } returns call
        every { call.execute() } throws IOException("Network error")

        assertThrows(IOException::class.java) {
            testedClass.fetchArtwork()
        }
    }

    @Test
    fun `given unsuccessful response, when fetching artwork, then throw IOException for retry`() {
        val albumResponse = AlbumResponse(data = null, success = false)
        val call: Call<AlbumResponse> = mockk()
        every { service.getAlbumDetails(any(), any()) } returns call
        every { call.execute() } returns Response.success(albumResponse)

        assertThrows(IOException::class.java) {
            testedClass.fetchArtwork()
        }
    }

    @Test
    fun `given response with no images, when fetching artwork, then return empty list`() {
        val album = Album(id = "album1", images = arrayListOf())
        val albumResponse = AlbumResponse(data = album, success = true)
        val call: Call<AlbumResponse> = mockk()
        every { service.getAlbumDetails(any(), any()) } returns call
        every { call.execute() } returns Response.success(albumResponse)

        val result = testedClass.fetchArtwork()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `given response with null images, when fetching artwork, then return empty list`() {
        val album = Album(id = "album1")
        val albumResponse = AlbumResponse(data = album, success = true)
        val call: Call<AlbumResponse> = mockk()
        every { service.getAlbumDetails(any(), any()) } returns call
        every { call.execute() } returns Response.success(albumResponse)

        val result = testedClass.fetchArtwork()

        assertTrue(result.isEmpty())
    }
}
