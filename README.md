# Онлайн магазин (Online Store)


## 📌 Описание:

Этот проект представляет собой RESTful сервис для работы с товарами,
предостовляемыми онлайн-магазином.
Реализация с помощью **Spring Boot 3.4.2** и **Maven**.

**Онлайн магазин** — сайт, торгующий товарами, позволяющий пользователям онлайн,
в своём браузере или через мобильное приложение, сформировать заказ на покупку,
выбрать способ оплаты и доставки заказа, оплатить заказ.

---


## 🤖 Функционал:

### Основное:

1. **Работа с товарами**:
    - Добавление, изменение продаваемых товаров
    - Поиск товаров по определённым параметрам
    - Корзина выбранных товаров
    - Вкладка _Избранное_


2. **Аккаунт пользователей**:
    - Аккаунт продовца
    - Аккаунт покупателя

### Дополнительно:

- Работа с отзывами на товары
- Вкладка акционной продукции
- История заказов

---


## 🛠 Используемые технологии:

### Основное:

- **ЯП**: Java 17
- **Фреймворк**: Spring Boot 3.4.2
- **Сборка**: Maven


### Дополнительное:

- Lombok (для сокращения шаблонного кода)
- SonarCloud (для анализа качества кода)

---


## 🚀 Установка и запуск

```bash
git clone https://github.com/PHOUG/online-store.git
cd online-store
mvn clean install
mvn spring-boot:run
```
---
## 📝 Postman тесты:
1. **Вывод всего списка**:

```localhost:8080/products```

2. **Поиск товара по названию**:

```localhost:8080/products/search?name=Футболка Hugo```

```localhost:8080/products/search?name=Очки Palaroid```

```localhost:8080/products/search?name=Кроссовки Adidas```

3. **Поиск товара по артиклю**:

```localhost:8080/products/RTLADQ051101```

```localhost:8080/products/RTLADV969101```

```localhost:8080/products/RTLADO561701```


4. **Поиск товарОВ по диапазону цены**:

```localhost:8080/products/price-range/100-200```

---

## ☁️ SonarCloud:
[**SonarCloud**](https://sonarcloud.io/project/overview?id=PHOUG_online-store) ссылка на overview


