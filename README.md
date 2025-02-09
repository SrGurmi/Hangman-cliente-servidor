# Juego del Ahorcado Multijugador

Este proyecto implementa el clásico juego del ahorcado en un entorno cliente-servidor, permitiendo que dos o más jugadores disfruten de la experiencia de forma remota a través de una red. Este repositorio contiene tanto la aplicación del servidor como la del cliente, ambos implementados en Java.

---

## Tabla de Contenidos

- [Descripción del Escenario Práctico](#descripción-del-escenario-práctico)
- [Roles y Funciones](#roles-y-funciones)
  - [Servidor](#servidor)
  - [Cliente](#cliente)
- [Clases y Librerías de Java Utilizadas](#clases-y-librerías-de-java-utilizadas)
- [Documentación del Código](#documentación-del-código)
  - [Estructura del Proyecto](#estructura-del-proyecto)
  - [Comentarios y Anotaciones](#comentarios-y-anotaciones)
- [Casos de Uso y Ejemplos](#casos-de-uso-y-ejemplos)


---

## Descripción del Escenario Práctico

En este proyecto se aborda el escenario en el cual dos jugadores desean disfrutar del clásico juego del ahorcado de manera remota. Para ello, se requiere:

- **Aplicación Servidor:** Se encarga de gestionar la partida, selecciona la palabra secreta a adivinar, controla el número de intentos, mantiene el estado del juego y comunica en tiempo real el progreso a los jugadores.
- **Aplicación Cliente:** Permite a cada jugador interactuar con el juego a través de una interfaz gráfica (GUI), donde se muestran detalles del juego y se permite la entrada de letras y comandos de control (reinicio o cierre de conexión).

La comunicación se establece desde el inicio de la conexión del cliente, se mantiene durante el desarrollo del juego y se concluye cuando se alcanza el resultado final (victoria o derrota) o el jugador opta por reiniciar la partida.

---

## Roles y Funciones

### Servidor

El servidor es el componente central del juego y cumple con las siguientes responsabilidades:

- **Selección de Palabras:** Elige aleatoriamente una palabra secreta de una lista predefinida. Cada palabra viene asociada a una categoría para proporcionar una pista al jugador.
- **Control del Estado del Juego:** Administra y mantiene el estado de la partida, incluyendo:
  - La palabra a adivinar (mostrando solo las letras adivinadas).
  - El número de intentos restantes.
  - Las letras ingresadas correctamente y las erróneas.
- **Procesamiento de Intentos:** Recibe las entradas de los clientes, verifica la letra propuesta y actualiza el estado del juego; revelando letras correctas o reduciendo el número de intentos en caso de errores.
- **Gestión de Múltiples Clientes:** Utiliza un `ExecutorService` (grupo de hilos) para atender múltiples conexiones por medio de hilos independientes, asegurando que varias partidas puedan realizarse al mismo tiempo.
- **Comunicación con el Cliente:** Envía actualizaciones periódicas a los clientes (como la categoría, el estado actualizado de la palabra, mensajes de error o éxito y el resultado final).

### Cliente

La aplicación cliente se enfoca en la interacción del usuario y ofrece:

- **Conexión al Servidor:** Establece una comunicación de red con el servidor utilizando sockets.
- **Interfaz de Usuario (GUI):** Presenta de forma clara:
  - La categoría de la palabra.
  - El estado actual de la palabra (combinación de letras reveladas y guiones).
  - El número de intentos restantes y mensajes del servidor.
  - Una representación gráfica del ahorcado, que se completa progresivamente con cada intento fallido.
- **Entrada del Jugador:** Permite la entrada de letras para intentar adivinar la palabra secreta.
- **Comunicación Bidireccional:** Envía las letras ingresadas al servidor y recibe actualizaciones del estado del juego para actualizar la GUI.
- **Opciones de Control:** Ofrece botones y controles para reiniciar el juego o finalizar la sesión.

---

## Clases y Librerías de Java Utilizadas

El proyecto emplea diversas clases y paquetes de Java para gestionar la comunicación en red, procesamiento de datos y concurrencia:

- **`java.net.Socket`**  
  - Permite establecer una conexión bidireccional entre cliente y servidor.
  
- **`java.net.ServerSocket`**  
  - Utilizado por el servidor para aceptar conexiones entrantes en un puerto determinado.
  
- **`java.io.BufferedReader`**  
  - Facilita la lectura eficiente de datos en forma de texto, utilizado para procesar mensajes y entradas.
  
- **`java.io.PrintWriter`**  
  - Permite la escritura y envío de mensajes formateados a través de la red.
  
- **`java.io.InputStreamReader`**  
  - Convierte flujos de bytes provenientes del socket en caracteres, sirviendo como puente hacia `BufferedReader`.
  
- **`java.util.concurrent.ExecutorService` y `java.util.concurrent.Executors`**  
  - Gestionan la ejecución de tareas en hilos paralelos, facilitando la atención simultánea de múltiples clientes mediante un pool de hilos (por ejemplo, `Executors.newFixedThreadPool(10)`).

---

## Documentación del Código

Esta sección muestra una visión detallada de la estructura y funcionalidad del código.

### Estructura del Proyecto

- **Servidor**  
  - Archivo principal: `HangmanServer.java`  
  - Responsabilidades:
    - Iniciar el `ServerSocket` en el puerto configurado.
    - Aceptar conexiones y asignar un hilo para cada cliente mediante `ExecutorService`.
    - Ejecutar la lógica del juego:
      - Selección aleatoria de palabra.
      - Gestión del estado del juego.
      - Procesamiento y validación de las letras ingresadas.
    - Enviar actualizaciones a los clientes en tiempo real.
    
- **Cliente**  
  - Archivo principal: `HangmanClient.java`  
  - Responsabilidades:
    - Conectar al servidor usando un `Socket`.
    - Proporcionar una interfaz gráfica (GUI) que muestre:
      - La palabra oculta con letras reveladas y guiones.
      - La categoría y número de intentos restantes.
      - Mensajes y notificaciones del estado del juego.
    - Recuperar la entrada del usuario y enviar las letras al servidor.
    - Permitir la reinicialización o el cierre de la conexión.
   
## Casos de Uso y Ejemplos
-----------------------

A continuación, se describen algunos escenarios de uso comunes:

### Inicio de una Nueva Partida

*   **Escenario:**
    
    *   El servidor se inicia, selecciona aleatoriamente una palabra secreta y queda a la espera de conexiones.
        
    *   Cada cliente que se conecte recibirá la primera actualización del juego, que incluye la categoría de la palabra y el estado inicial (con guiones) de la palabra oculta.
        

### Proceso de Adivinanza

*   **Escenario:**
    
    *   El jugador introduce una letra mediante la interfaz gráfica (GUI).
        
    *   El cliente envía el intento al servidor.
        
    *   El servidor valida la letra:
        
        *   Si la letra es correcta, actualiza la palabra revelando la letra en las posiciones correspondientes.
            
        *   Si la letra es incorrecta, decrementa el recuento de intentos y actualiza la visualización gráfica del ahorcado.
            
    *   El servidor envía la actualización del estado del juego a todos los clientes conectados.
        

### Finalización de la Partida

*   **Escenario:**
    
    *   La partida concluye cuando se adivina la palabra o se agotan los intentos.
        
    *   Se muestra un mensaje final (por ejemplo, "¡Has ganado!" o "¡Has perdido!") y se ofrece a los jugadores la opción de reiniciar la partida o cerrar la conexión.
