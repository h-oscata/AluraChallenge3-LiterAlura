package com.alura.LiterAlura;

import com.alura.LiterAlura.models.Author;
import com.alura.LiterAlura.models.Book;
import com.alura.LiterAlura.models.BookResponse;
import com.alura.LiterAlura.repositories.AuthorRepository;
import com.alura.LiterAlura.repositories.BookRepository;
import com.alura.LiterAlura.services.ConsumoAPI;
import com.alura.LiterAlura.services.ConvertData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Scanner;

@Component 
public class Principal {
    private Scanner scanner = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/";
    private ConvertData conversor = new ConvertData();

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public void showMenu() {
        int option = -1;
        while (option != 6) {
            var menu = """
                    -----------------------------------------------
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Salir
                    -----------------------------------------------
                    """;
            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    searchBookByTitle();
                    break;
                case 2:
                    findRegisteredBooks();
                    break;
                case 3:
                    findRegisteredAuthors();
                    break;
                case 4:
                    findLivingAuthors();
                    break;
                case 5:
                    findBooksByLanguage();
                    break;
                case 6:
                    System.out.println("Gracias por usar literAlura!");
                    break;
                default:
                    System.out.println("Opción inválida. Por favor, elija una opción entre 1 y 6");
            }
        }
    }

    public void searchBookByTitle() {
        System.out.print("Ingresa el título del libro que deseas buscar: ");
        String title = scanner.nextLine();

        // construccion de la URL
        String url = URL_BASE + "?search=" + title.replace(" ", "%20");

        // solicitud a la API
        String jsonResponse = consumoApi.getData(url);

        try {
            // conversion a objeto BookResponse
            BookResponse response = conversor.getData(jsonResponse, BookResponse.class);

            System.out.println("Libros encontrados: " + response.getCount());

            if (response.getResults() != null && !response.getResults().isEmpty()) {
                for (int i = 0; i < response.getResults().size(); i++) {
                    Book book = response.getResults().get(i);

                    //mostrando informacion al usuario
                    System.out.println("\n--------- LIBRO " + (i + 1) + " ---------");
                    System.out.println("Título: " + book.getTitle());

                    if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                        System.out.print("Autor: ");
                        for (Author author : book.getAuthors()) {
                            System.out.print(author.getName() + " ");
                        }
                        System.out.println();
                    } else {
                        System.out.println("Autor: No disponible");
                    }

                    if (book.getLanguages() != null && !book.getLanguages().isEmpty()) {
                        System.out.println("Idioma: " + String.join(", ", book.getLanguages()));
                    } else {
                        System.out.println("Idioma: No disponible");
                    }

                    System.out.println("Número de descargas: " + book.getDownloadCount());
                    System.out.println("--------------------------");

                    //almacenamiendo de resultados
                    Book existingBook = bookRepository.findByTitle(book.getTitle());
                    if (existingBook == null) {
                        bookRepository.save(book);
                    } else {
                        System.out.println("El libro ya existe en la base de datos: " + existingBook.getTitle());
                    }

                    if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
                        for (Author author : book.getAuthors()) {
                            Author existingAuthor = authorRepository.findByName(author.getName());
                            if (existingAuthor == null) {
                                authorRepository.save(author);
                            } else {
                                System.out.println("El autor ya existe en la base de datos: " + existingAuthor.getName());
                            }
                        }
                    }
                }
            } else {
                System.out.println("No se encontraron libros con ese título.");
            }

        } catch (Exception e) {
            System.out.println("Error al procesar los datos: " + e.getMessage());
        }
    }

    public void findRegisteredBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            for (Book book : books) {
                System.out.println("Título: " + book.getTitle() + " | Idiomas: " + String.join(", ", book.getLanguages()));
            }
        }
    }

    public void findRegisteredAuthors() {
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            for (Author author : authors) {
                System.out.println("Autor: " + author.getName() + " | Año de nacimiento: " + author.getBirthYear());
            }
        }
    }

    public void findLivingAuthors() {
        System.out.print("Ingresa el año para listar autores vivos: ");
        int year = scanner.nextInt();
        scanner.nextLine();

        List<Author> authors = authorRepository.findAll();
        for (Author author : authors) {
            if (author.getBirthYear() != null && author.getDeathYear() == null || (author.getBirthYear() <= year && (author.getDeathYear() == null || author.getDeathYear() >= year))) {
                System.out.println("Autor: " + author.getName() + " | Año de nacimiento: " + author.getBirthYear());
            }
        }
    }

    public void findBooksByLanguage() {
        System.out.print("Ingresa el idioma para listar los libros: ");
        System.out.print("OPCIONES: es - en - fr - pt: ");
        String language = scanner.nextLine();

        List<Book> books = bookRepository.findByLanguage(language);
        if (books.isEmpty()) {
            System.out.println("No se encontraron libros en ese idioma.");
        } else {
            for (Book book : books) {
                System.out.println("Título: " + book.getTitle() + " | Idiomas: " + String.join(", ", book.getLanguages()));
            }
        }
    }

}
