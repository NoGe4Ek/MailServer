Send forward

    curl -X POST -d "filter=59cbc294-c52d-43b1-831d-66b1a4d04047" -d "recipient=9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b" 'http://localhost:8080/forward'

https://www.tutorialspoint.com/javamail_api/javamail_api_overview.html
https://vitalflux.com/integrate-mailgun-spring-boot-java-app/
https://habr.com/ru/post/526162/

https://en.wikipedia.org/wiki/Comparison_of_mail_servers
https://russianblogs.com/article/3738211940/

https://losst.ru/ustanovka-postfix-ubuntu-s-dovecot

deploy scp -P 4080 ./target/MailServer-0.0.4.jar root@ntarasenko.simsim.ftp.sh:/root/mail_server/backend-mail

users /etc/dovecot/users

https://www.8host.com/blog/nastrojka-pochtovogo-servera-postfixdovecot/

1. написать алгоритм проверки выражения
2. эндпоинт для получения результата проверки
3. обработать результат проверки на фронте
4. сделать эндпоинт создания аттрибута и фильтра с помощью выражения
5. добавить подсветку текущей вкладки
6. поправить лейблы в фильтрах
7. добавить лейблы текущих страниц
8. протестировать алгоритм обратной польской записи