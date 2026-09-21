1\. Como levantar el proyecto

 

- git clone https://github.com/julian-larrosa/sagant-prueba.git


- cd sagant-prueba

- docker compose up \-d

- ./mvnw spring-boot:run o mvnw spring-boot:run

- Si se quieren probar los tests: ./mvnw test o mvnw test

2\. Precondiciones

\- JDK 17 o superior instalado

\- Docker y Docker Desktop instalados y corriendo

\- Git y Maven

3\. Credenciales de prueba 

- Api key: “key”

- Postgre : localhost:5432, usuario: postgres, contraseña: HOLA123

- MailHog: http://localhost:8025

4\. Decisiones de diseño 

- Elegí ‘Email vía SMTP” por sobre “Entrega HTTP a otro servicio” simplemente porque nunca había probado usar Mailhog y me pareció más eficiente para el tiempo acotado que tenía.  
- Elegí usar ApiKeyAuthFilter para que las peticiones lleguen antes a ese filtro y no se “choquen” con el controlador. Pero no soy un experto en seguridad y deje que la IA me ayude con la elección. Ya había usado OncePerRequestFilter en otros proyectos pero sigo buscando aprender Spring Security para entender del todo que estoy implementando.  
- Use el patrón de diseño STRATEGY para que agregar canales en un futuro sea sencillo, ya que si quisiera agregar otro canal solamente se implementa la interfaz NotificationSender
- No se exponen entidades JPA en el controlador, se hace mediante dtos.

5\. Trade-offs y limitaciones 

- No use H2 porque no lo conocía, sumado a que ya había hecho proyectos con Docker y Postgres.  
- Mejorar los logs.  
- Investigar más acerca de Mockito.

6\. Reflexión de escalabilidad 

Podría devolver un mensaje duplicado cuando llega una notificación.  
