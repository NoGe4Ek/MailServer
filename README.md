Send forward

    curl -X POST -d "filter=59cbc294-c52d-43b1-831d-66b1a4d04047" -d "recipient=9aff7a2e-6b7a-4e14-b51a-dab7dc87e56b" 'http://localhost:8080/forward'

https://www.tutorialspoint.com/javamail_api/javamail_api_overview.html
https://vitalflux.com/integrate-mailgun-spring-boot-java-app/
https://habr.com/ru/post/526162/

https://en.wikipedia.org/wiki/Comparison_of_mail_servers
https://russianblogs.com/article/3738211940/

https://losst.ru/ustanovka-postfix-ubuntu-s-dovecot

deploy scp -P 4080 ./target/MailServer-1.0.0.jar root@poly-sender.ru:/root/server

users /etc/dovecot/users

https://www.8host.com/blog/nastrojka-pochtovogo-servera-postfixdovecot/

2. шарить атрибуты и фильтры сделанные с помощью выражений