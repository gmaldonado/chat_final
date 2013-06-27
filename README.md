Chat Peer to Peer
Jaime Campano
Roberto García
Gonzalo Maldonado

https://github.com/gmaldonado/chat_final


Modo de compilación:

javac Chat.java

Modo de uso:

java Chat


Una vez que se ejecute el programa, pide el nombre de usuario y la IP actual
del PC en el que se encuentra porque no se encontró mejor forma para obtener la IP 
en linux, dado que si uno utiliza InetAddress.getHost().getHostAddress() retorna
la dirección de loopback, no así en Windows que retorna la IP deseada. Lo que se podría 
hacer en Linux es revisar todas las interfaces de red.

#Topologia de anillo

Para nuestro caso, se está utilizando una topología de anillo simple, donde cada
nodo tiene un sucesor y se reestructura cada vez que un nodo se conecta o desconecta (pero no cuando se cae).
El primer nodo que se conecta a la red apunta a si mismo como sucesor. En caso contrario, un nodo 
que se conecta, manda un mensaje a broadcast para que le respondan los nodos que se encuentran en la red y 
actualice su sucesor y id según corresponda.
Un ejemplo de un anillo podría ser

1 --> 2 --> 1 

Otro ejemplo

1 --> 2 --> 3 --> 6 --> 1

No existe ningún nodo como servidor central y la escalabilidad del sistema es alta, dado que se pueden
conectar nuevos nodos al sistema solamente iniciando el programa y el sistema se encarga del resto. Si 
se quiere desconectar un nodo tampoco genera problemas, por lo que se logró una descentralización total 
como se solicitaba, donde cada nodos es cliente y servidor a la vez.

#Comandos

Los comandos para utilizar el sistema son los siguientes:

1. Enviar mensaje: para enviar un mensaje se debe hacer de la siguiente forma

msg-nick_usuario-mensaje_a_enviar

Ejemplo:

msg-Jaime-Hola como estas

2. suc

Esto es solo para debug, nos da la IP de nuestro nodo sucesor.

3. connected

Nos indica el nick de todos los usuarios conectados (exceptuando el mismo desde el que se hace la llamada)

4. disconnect

Se desconecta del sistema y le avisa a todos los nodos, para que actualicen registro de sucesor. 

Otro punto importante para el DEBUG (esto solamente 
es para que la revisión sea más fácil), es que cuando se manda un mensaje entre dos nodos y no están directamente
conectados aparecerá un mensaje de debug en el(los) nodos intermedios que dirá que un mensaje 
se está enviando a través de ese nodo:

Ejemplo:

Sending message through this node to 192.168.2.5



#Restricciones

-Los nodos(computadores) deben estar en la misma red
-En caso de trabajar en Windows (o si se tiene problemas al conectar y no se detectan otros usuarios conectados)
 se debe cambiar manualmente la variable "broadcast" en la clase "Chat" por la que corresponda a la red en la que
 se encuentra. En nuestro caso solo hubo problemas con Windows, porque no maneja bien el broadcast 255.255.255.255.
-No se controlan los "nick" repetidos
-No se controla cuando un nodo se cae (pero si cuando se desconecta)<

