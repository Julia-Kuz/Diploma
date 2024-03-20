# Дипломный проект по профессии «Android Developer»
  
## Мобильное приложение NeWork
Приложение состоит из следующих основных блоков пользовательского интерфейса:

*1. Основной экран приложения.*

Стартовый экран содержит:
- нижнее меню с тремя кнопками: посты, события и пользователи,
- кнопку меню в AppBar с возможностью перехода на вход в аккунт и регистрацию или просмотр профиля.

[Cтартовый экран](screens/1_main%20screen.jpg)

*1.1. Экран со списком постов.*

Этот фрагмент главного экрана содержит:
- список постов,
- кнопку +, нажатие которой ведёт на создание нового поста или диалог с предложением войти/зарегистрироваться.

[Экран со списком постов](screens/2_postsList.jpg)

Карточка поста включает:
- [x] аватар автора поста;
- [x] имя автора;
- [x] дату публикации в формате `dd.mm.yyyy HH:mm`;
- [x] кнопку лайка с количеством лайков;
- [x] вложение, при наличии: аудио, видео или фото;
- [x] ссылку, при наличии, с переходом в браузер; 
- [x] кнопку вызова меню с возможностью удаления или перехода к редактированию, в случае, если текущий пользователь является автором;
- [x] текст поста.

При нажатии на карточку поста должен быть произведён переход к детальному виду с отображением дополнительной информации:
- [x] список упомянутых юзеров;
- [x] последнее место работы автора, либо текст «В поиске работы»;
- [x] карту с маркером, при наличии координат.

[Детальный вид поста](screens/8_detailedPost.jpg)

[Детальный вид поста с количеством лайков больше 5](screens/8.1_detailedPost.jpg)

[Просмотр списка тех, кто лайкнул ](screens/9_likers.jpg)


*1.2. Экран со списком событий.*

Этот фрагмент главного экрана содержит:
- список событий,
- кнопку +, нажатие которой ведёт на создание нового события или диалог с предложением войти/зарегистрироваться.

[Экран со списком событий](screens/12_eventsList.jpg)

Карточка события включает:
- [x] аватар автора события;
- [x] имя автора;
- [x] дату публикации в формате `dd.mm.yyyy HH:mm`;
- [x] дату проведения в формате `dd.mm.yyyy HH:mm`;
- [x] тип события: офлайн или онлайн;
- [x] кнопку лайка с количеством лайков;
- [x] вложение, при наличии: аудио, видео или фото;
- [x] ссылку, при наличии, с переходом в браузер; 
- [x] кнопку вызова меню с возможностью удаления или перехода к редактированию, в случае, если текущий пользователь является автором;
- [x] текст поста.

При нажатии на карточку события должен быть произведён переход к детальному виду с отображением дополнительной информации:
- [x] последнее место работы автора, либо текст «В поиске работы»;
- [x] список участников;
- [x] список спикеров;
- [x] карту с маркером, при наличии координат.

[Детальный вид события](screens/13_detailedEvent.jpg)

*1.3. Экран со списком пользователей.*

Этот фрагмент главного экрана содержит список пользователей.

[Список пользователей](screens/14_usersList.jpg)

Карточка пользователя включает:
- [x] логин пользователя;
- [x] имя пользователя;
- [x] аватар пользователя.

При нажатии на карточку пользователя происходит переход к детальному виду.

[Детальный вид пользователя](screens/15_detailedUser_Wall.jpg)

*2. Экран входа в приложение.*

[Экран входа в приложение](screens/4_signIn_screen.jpg)

*3. Экран регистрации.*

[Экран регистрации](screens/3_signUp_screen.jpg)

*4. Экран создания/редактирования поста.*

На этот экран может попасть только авторизованный пользователь.

Экран содержит:
- поле ввода текста;
- кнопку выбора локации: переход на фрагмент с картой;
- кнопку выбора упомянутых пользователей (экран со списком и множественным выбором);
- кнопки выбора изображения: фото или галерея;
- кнопки выбора вложения: аудио, видео;
- кнопку сохранить в ToolBar.

[Экран создания/редактирования поста](screens/7_newPost.jpg)

[Экран выбора пользователей](screens/5_chooseUsers.jpg)

[Экран с картой - выбор локации ](screens/6_map.jpg)


*5. Экран создания/редактирования события.*

На этот экран может попасть только авторизованный пользователь.

Экран содержит:
- поле ввода текста;
- кнопку выбора локации: переход на фрагмент с картой;
- выбор типа между online и offline (по умолчанию online);
- кнопку выбора даты проведения события;
- кнопки выбора изображения: фото или галерея;
- кнопки выбора вложения: аудио, видео;
- кнопку выбора спикеров: диалог со списком пользователей;
- кнопку сохранить в ToolBar.

[Экран создания/редактирования события](screens/10_newEvent.jpgg)

[Экран выбора спикеров](screens/11_chooseSpeakers.jpg)


*6. Экран просмотра пользователя.*

Экран содержит:
- имя и логин в AppBar;
- фото пользователя;
- табы с выбором между стеной и работами пользователя.

*6.1. Экран просмотра стены.*

Стена представляет собой список постов, написанный одним автором. 

[Экран просмотра пользователя - стены](screens/15_detailedUser_Wall.jpg)

*6.2. Экран просмотра списка работ.*

Карточка работы включает:
- [x] название компании;
- [x] стаж в формате `dd MMM yyyy`;
- [x] должность.

[Экран просмотра пользователя - работ](screens/16_detailedUser_Jobsl.jpg)

*7. Экран просмотра своего профиля - при нажатии на аватарку*

*7.1. Экран создания/редактирования работы.*

Экран содержит:
- поле ввода названия места работы;
- поле ввода должности;
- поле ввода ссылки на сайт компании;
- кнопку выбора временного промежутка;

[Экран просмотра пользователя - редактирование работы](screens/17_detailedUser_Jobs_edit.jpg)