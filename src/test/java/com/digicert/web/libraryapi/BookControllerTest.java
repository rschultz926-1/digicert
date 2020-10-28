package com.digicert.web.libraryapi;

import com.digicert.libraryapi.entities.BookEntity;
import com.digicert.libraryapi.repositories.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes={com.digicert.libraryapi.LibraryApiApplication.class})
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BookRepository bookRepository;

    private MockMvc mockMvc;
    private BookEntity defaultBookEntity1;
    private BookEntity defaultBookEntity2;
    private BookEntity testBookEntity;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        defaultBookEntity1 = new BookEntity();
        defaultBookEntity1.setAuthor("Stephen King");
        defaultBookEntity1.setTitle("The Shining");
        defaultBookEntity1.setNumCopies(52);
        bookRepository.save(defaultBookEntity1);

        defaultBookEntity2 = new BookEntity();
        defaultBookEntity2.setAuthor("Michael Crichton");
        defaultBookEntity2.setTitle("Jurassic Park");
        defaultBookEntity2.setNumCopies(17);
        bookRepository.save(defaultBookEntity2);

        testBookEntity = new BookEntity();
        testBookEntity.setAuthor("create");
        testBookEntity.setTitle("test");
        testBookEntity.setNumCopies(25);
    }

    @AfterEach
    void cleanUp() {
        bookRepository.delete(defaultBookEntity1);
        bookRepository.delete(defaultBookEntity2);
        bookRepository.delete(testBookEntity);
    }

    @Test
    void testGetAllBooks() throws Exception {
        MvcResult res = mockMvc.perform(
            get("/library/books")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        assertNotNull(res.getResponse());
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<BookEntity> resultList = Arrays.asList(
                    mapper.readValue(res.getResponse().getContentAsString(), BookEntity[].class)
            );
            assertEquals(2, resultList.size());
            assertTrue(resultList.contains(defaultBookEntity1));
            assertTrue(resultList.contains(defaultBookEntity2));
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testGetBook() throws Exception {
        MvcResult res = mockMvc.perform(
            get("/library/books/" + defaultBookEntity1.getAuthor() + "/" + defaultBookEntity1.getTitle())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        assertNotNull(res.getResponse());
        ObjectMapper mapper = new ObjectMapper();
        try {
            BookEntity book = mapper.readValue(res.getResponse().getContentAsString(), BookEntity.class);
            assertEquals(defaultBookEntity1.getAuthor(), book.getAuthor());
            assertEquals(defaultBookEntity1.getTitle(), book.getTitle());
            assertEquals(defaultBookEntity1.getNumCopies(), book.getNumCopies());
        } catch (IOException e) {
            fail();
        }
    }

    @Test
    void testGetBookNotFound() throws Exception {
        MvcResult res = mockMvc.perform(
            get("/library/books/not/found")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void testCreateBook() throws Exception {
        MvcResult res = mockMvc.perform(
            post("/library/books/" + testBookEntity.getAuthor() + "/" + testBookEntity.getTitle()
                    + "?numCopies=" + testBookEntity.getNumCopies()))
            .andExpect(status().isOk())
            .andReturn();
        Optional<BookEntity> book = bookRepository.findOneByAuthorAndTitle(
            testBookEntity.getAuthor(),
            testBookEntity.getTitle()
        );
        assertTrue(book.isPresent());
        assertEquals(testBookEntity, book.get());
    }

    @Test
    void testCreateBookNoDefault() throws Exception {
        MvcResult res = mockMvc.perform(
            post("/library/books/" + testBookEntity.getAuthor() + "/" + testBookEntity.getTitle()))
            .andExpect(status().isOk())
            .andReturn();
        Optional<BookEntity> book = bookRepository.findOneByAuthorAndTitle(
            testBookEntity.getAuthor(),
            testBookEntity.getTitle()
        );
        assertTrue(book.isPresent());
        assertEquals(testBookEntity.getAuthor(), book.get().getAuthor());
        assertEquals(testBookEntity.getTitle(), book.get().getTitle());
        assertEquals(0, book.get().getNumCopies());
    }

    @Test
    void testCreateBookAlreadyExists() throws Exception {
        MvcResult res = mockMvc.perform(
            post("/library/books/" + defaultBookEntity1.getAuthor() + "/" + defaultBookEntity1.getTitle()))
            .andExpect(status().isConflict())
            .andReturn();
    }

    @Test
    void testUpdateBook() throws Exception {
        MvcResult res = mockMvc.perform(
            put("/library/books/" + defaultBookEntity1.getAuthor() + "/" + defaultBookEntity1.getTitle()
                + "/" + testBookEntity.getNumCopies()))
            .andExpect(status().isOk())
            .andReturn();
        Optional<BookEntity> book = bookRepository.findOneByAuthorAndTitle(
            defaultBookEntity1.getAuthor(),
            defaultBookEntity1.getTitle()
        );
        assertTrue(book.isPresent());
        assertEquals(defaultBookEntity1.getAuthor(), book.get().getAuthor());
        assertEquals(defaultBookEntity1.getTitle(), book.get().getTitle());
        assertEquals(testBookEntity.getNumCopies(), book.get().getNumCopies());
    }

    @Test
    void testUpdateBookNotFound() throws Exception {
        MvcResult res = mockMvc.perform(
            put("/library/books/" + testBookEntity.getAuthor() + "/" + testBookEntity.getTitle()
                + "/" + testBookEntity.getNumCopies()))
            .andExpect(status().isNotFound())
            .andReturn();
    }

    @Test
    void testDeleteBook() throws Exception {
        MvcResult res = mockMvc.perform(
            delete("/library/books/" + defaultBookEntity1.getAuthor() + "/" + defaultBookEntity1.getTitle()))
            .andExpect(status().isOk())
            .andReturn();
        Optional<BookEntity> book = bookRepository.findOneByAuthorAndTitle(
            defaultBookEntity1.getAuthor(),
            defaultBookEntity1.getTitle()
        );
        assertFalse(book.isPresent());
    }

    @Test
    void testDeleteBookNotFound() throws Exception {
        MvcResult res = mockMvc.perform(
            delete("/library/books/" + testBookEntity.getAuthor() + "/" + testBookEntity.getTitle()))
            .andExpect(status().isNotFound())
            .andReturn();
    }
}
