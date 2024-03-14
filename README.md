# Delivery api (Dostavim)

## Введение
API доставки продуктов питания
## Настройки проекта
1. Настройка подключения к базе данных

   ```
    spring.datasource.url=jdbc:postgresql://${POSTGRES_SERVER}:${POSTGRES_PORT}/${POSTGRES_DB}
    spring.datasource.username=${POSTGRES_USER}
    spring.datasource.password=${POSTGRES_PASSWORD}
   ```
2. Секретный ключ JWT токена

   ```
    jwt_secret=ключ
   ```
3. Swagger генератор
   ```
    springdoc.swagger-ui.path=
    springdoc.api-docs.path=
    openapi.dev-url=
    openapi.prod-url=
   ```

Упрвлением зависимостей и сборки занимается maven. Подробнее в файле [pom.xml](https://github.com/Merantory/delivery-api-pet/blob/master/pom.xml)

## Запуск приложения
Запуск осуществляется через <code>docker-compose up -d</code>

## Документация проекта
Документация проекта реализована при помощи Swagger генератора. URL, по которому доступна документация задается в файле application.properties

Дополнительная документация по проекту в виде ТЗ курсового проекта, USE-CASE диаграммы, а также диаграмм базы данных доступна [здесь](https://github.com/Merantory/delivery-api-pet/tree/master/doc)
