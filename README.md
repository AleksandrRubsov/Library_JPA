# Library Books With JPA
Приложение дает возможность регистрировать читателей, выдавать им книги и освобождать книги после того, как читатель возвратил их обратно в библиотеку.
Так же добавлен функционал сортировки книг по году написания, пагинация по страницам и поиск книг по названию.
## Цель проекта
Целью данного проекта было улучшить [проект 1](https://github.com/AleksandrRubsov/Project_Library) используя более высокоуровневые hibernate и Spring Data JPA и сделать выводы,
для каких целей лучше использовать JDBC API и JDBС Template и для каких Hibernate и Spring Data JPA.
## Использованные технологии и зависимости
**:** **Spring MVC**, **Spring Web**, **Spring Data JPA**,**Hibernate**, **Thymeleaf**, **Spring Validator**, **PostgreSQL**

## Новый функционал
+ Добавлена пагинация для книг. Так как книг может быть много и они могут не помещаться на одной странице в браузере, теперь метод контроллера 
может выдавать не только все книги разом, но и разбивать выдачу на страницы.
+ Добавлена сортировка книг по году написания.
+ Добавлена возможность поиска книг по названию с выводом основной информации(название книги, имя автора, у кого находится книга).
+ Добавлена автоматическая проверка на просрочку возврата книги.

### Сортировка
Метод index() в BookController принимает в адресной строке параметр(ключ) sort_by_year. 
Если он имеет значение true, то выдача должна быть отсортирована по году. Если в адресной строке не передается этот ключ, то книги возвращаются в обычном порядке.

:mag: обычный запрос(http://localhost:8080/book)

![image](https://user-images.githubusercontent.com/70627203/232230263-f6a18d61-5e65-4d40-aff9-1b1e39c86c81.png)

:mag: запрос с сортировкой(http://localhost:8080/book?sort_by_year=true)

![image](https://user-images.githubusercontent.com/70627203/232230306-f10c4e97-8754-44e7-8136-73720b8dd650.png)

Получаем отсортированные по году книги.
### Пагинация
Метод index() в BookController принимает в адресной строке два параметра(ключа): page и books_per_page. 
Первый ключ сообщает, какую страницу мы запрашиваем, а второй ключ сообщает, сколько книг должно быть на одной странице. 
Нумерация страниц стартует с 0. Если в адресной строке не передаются эти ключи, то возвращаются как обычно все книги.

:mag: обычный запрос(http://localhost:8080/book)

![image](https://user-images.githubusercontent.com/70627203/232230593-95f5f146-d41e-4889-8d03-1bb53ee07c01.png)

:mag: запрос с пагинацией(http://localhost:8080/book?page=0&books_per_page=4)

![image](https://user-images.githubusercontent.com/70627203/232230673-dc6d42b6-20e4-4850-a5f0-0235a6908242.png)

Так же возможен запрос используя и пагинацию и сортировку одновременно.

К примеру, запрос 

:mag: http://localhost:8080/book?sort_by_year=true&page=0&books_per_page=3

Результатом которого станет вывод отсортированных по году написания 4 книг с 1(0) страницы списка:

![image](https://user-images.githubusercontent.com/70627203/232230894-7cca37dd-71cf-4043-9d33-21022730a10d.png)

## Поиск книг
Реализована страница поиска по названию книги. Результатов является найденная книга и информация о текущем владельце или его отсутствии. 
Если такой книги не было найдено, то должно выдаваться сообщение о том, что "Книг не найдено"

:mag: Поисковой запрос(http://localhost:8080/book/search)

![image](https://user-images.githubusercontent.com/70627203/232231244-315674d3-7503-4716-a33c-72e0eb4c8bc0.png)

Варианты резeльтатов запроса:

![image](https://user-images.githubusercontent.com/70627203/232231160-68e6779f-6878-48a6-a2c6-7b2b2a4ca097.png)


![image](https://user-images.githubusercontent.com/70627203/232231188-cede4f28-b337-43e2-ae8c-4b362b6e8589.png)

## Проверка просрочки книги

:mag: Поисковой запрос(http://localhost:8080/people/id)

Если книга выделяется красным цветом - она просрочена.

![image](https://user-images.githubusercontent.com/70627203/232232059-917dd1c2-4557-42a8-88c4-380705255e43.png)

## Add javadoc commit
Была добавлена документация по проекту

## Выводы
Высокоуровневые Hibernate и JPA явно упрощают реализацию стандартных CRUD операций веб-приложений. Уменьшается время написания методов,
методы являются более оптимизированными так как не содержат прямых SQL команд, которые с повышением сложности логики приложения могут иметь большой размер. 
К тому же за счет JPA Repository можно создавать свои разнообразные и уникальные методы.
Однако, не все методы можно реализовать с помощью JPA. В более сложных проектах, необходимо будет спускаться на нижние уровени представления(jdbc-api, jdbc-template),
что требует высокого уровня навыков владения этими технологиями.

## Список использованной литературы
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#reference - документация по созданию кастомных запросов JPA.
