# Thread
Lorsque l'on conçoit des applications, on s'attend souvent à ce que plusieurs actions 
soient possibles en même temps. Exemple : notre navigateur peut télécharger
des fichiers pendant qu'il charge d'autres pages. En même temps notre OS
déplace la souris. Le tout sans freezing.

On parle de programmation concurrentielle. Celle-ci repose sur :
- des processus
- des threads

Dans le cas d'un processeur avec un seul coeur nous avons tout de même plusieurs
processus qui tournent en même temps, lesquels ont tous au moins un thread.
Le processeur partage alors ses ressources via une méthode appelée "time-slicing".
Avoir plusieurs coeurs sur notre processeur facilite évidemment l'exécution de programmes
concurrents.

## Processus
Environnement d'exécution qui possède des ressources propres, notamment en termes de mémoire
allouée. Souvent synonyme d'application/programme.
Plusieurs processus peuvent communiquer via IPC (Inter Process Communication) qui propose
des canaux et sockets de communication. L'IPC est également possible entre machines distinctes.
La JVM crée par défaut 1 processus, mais on peut changer cela si besoin.

## Threads
Un thread possède également des ressources, mais est beaucoup plus léger (parfois
appellé *lightweight process*). Les threads existent au sein d'un processus.
Ils peuvent accéder aux ressources de leur processus parent.
Une application qui possède plusieurs threads est dite "multithreaded".
Et pour compliquer le tout, un Thread peut en créer d'autres !

Tout programme Java possède au moins un thread principal, au sein d'un processus.

## Mise en application
Un thread est matérialisé par une instance de la classe Thread.
On peut gérer les threads de 2 façons :
- gérer manuellement les threads en instanciant la classe Thread
- déléguer la gestion des threads à un *executor*

### Créer un Thread
Observons la classe Thread. Celle-ci possède un constructeur avec un paramètre
de type Runnable. Runnable est simplement une interface avec une unique méthode
abstraite. Il s'agit donc d'une interface fonctionnelle (= on peut l'utiliser pour créer
un lambda). La méthode `run` devra contenir le code que le Thread exécutera.

Exemple :
```
class MonRunnable implements Runnable {

    @Override
    public void run() {
        System.out.println("Message depuis MonRunnable");
    }
}


public class App {

    public static void main(String[] args){
        Runnable monRunnable = new MonRunnable();
        Thread monThread = new Thread(monRunnable);
        monThread.start(); // démarre le thread
    }

}
```

On peut réduire la méthode main à ceci : 
```
    (new Thread(new MonRunnable())).start();
```

> Pour vous entrainer aux lambdas, réécrivez le code précédent
> en déclarant votre Runnable via un lambda (= sans créer la classe MonRunnable)

Il existe une alternative à la création de Thread : la classe
Thread implémente elle-même l'interface Runnable.
On peut donc : 
- créer une classe qui hérite de Thread
- surcharger sa méthode `run()`
- l'instantier
- et appeler sa méthode `start()`

> Réécrivez l'exemple précédent en adoptant cette approche

Cette 2e approche est plus concise, mais vous oblige à hériter de Thread.
L'héritage multiple en Java n'étant pas possible, cela peut être très contraignant.
On implémentera donc le plus souvent l'interface Runnable sur une classe.

### Sleep
Au sein d'un Thread, on peut appeler des méthodes statiques de la classe Thread.
`Thread.sleep(int millisecondes)` en est une qui met en pause le Thread pendant x millisecondes.
Voici un exemple de son usage (rappel : sans précision, votre code s'exécute dans le Thread principal).

```
public class MaClasse {
    public static void main(String[] args) throws InterruptedException {
        for(int i = 0; i < 5; i++){
            System.out.println(i);
            Thread.sleep(1000);
        }
    }
}
```
Cette méthode peut déclencher une exception `InterruptedException` lorsqu'un
autre Thread interrompt le Thread actuel alors qu'il est en sommeil.
Dans cet exemple il n'y a qu'un seul Thread donc l'exception ne se produira jamais.

Le temps de sommeil renseigné en paramètres est approximatif, car du fait 
de la concurrence, notre Thread peut ne pas avoir la main pour sortir de son sommeil
à l'instant précis demandé. De plus il peut avoir été interrompu par un autre Thread avant
sa sortie de sommeil.

### Interruption de Thread
On peut interrompre un Thread de la façon suivante :
```
monThread.interrupt();
```
Cependant cela ne fait que 2 choses :
- la méthode statique `Thread.interrupted()` renvoie désormais `true` 
au sein du Thread concerné
- si le Thread exécutait une méthode pouvant déclencher un `InterruptedException` (comme
la méthode sleep), cette méthode prend fin avec un déclenchement de l'exception
et l'exécution se poursuit  
Autrement dit : c'est à vous de gérer l'interruption d'un Thread en rédigeant le
code nécessaire.

Exemple : interruption en cas de sleep
```

class MonRunnable implements Runnable {

    @Override
    public void run() {

        for (int i = 0; i < 5; i++) {
            System.out.println(i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Fin du thread secondaire");
                return;
            }
        }
    }
}

public class MaClasse {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        Thread monThread = new Thread(new MonRunnable());
        monThread.start();
        Thread.sleep(2000);
        monThread.interrupt();
    }
}
    
```

Exemple : interruption en testant régulièrement le flag `interrupted`

```
class MonRunnable implements Runnable {

    @Override
    public void run() {

        long i = 0;
        while(true){
            i++;
            if(Thread.interrupted()){
                System.out.println("Le thread secondaire a effectué " + i + " itérations avant interruption.");
                return;
            }
        }
    }
}

public class MaClasse {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        Thread monThread = new Thread(new MonRunnable());
        monThread.start();
        Thread.sleep(2000);
        monThread.interrupt();
    }
}
```

Astuce : si vous ne voulez pas avoir à prendre en charge les 2 sorties
possibles en cas d'interruption (avec exception et avec flag), vous pouvez
manuellement déclencher l'exception :
```
if (Thread.interrupted()) {
    throw new InterruptedException();
}
```

Attention ! L'appel de la méthode `interrupt()` ne mène pas forcément
à l'interruption de votre Thread. Vous pourriez par exemple 
avec codé un mécanisme de vérification autorisant ou non un Thread à s'arrêter
selon certaines conditions. Si vous décidez de poursuivre l'exécution de votre 
Thread, il est utile de pouvoir réceptionner à nouveau des messages d'interruption.
Pour cela, le fait d'appeler la méthode `interrupted()` réinitialise la valeur à `false`.
De même, le déclenchement d'une `InterruptedException` réinitialise aussi le statut.

### Join : attendre un autre Thread
Au sein d'un 



######
Retourner au cours Swing

### Synchronisation
Lorsque 2 threads accèdent à une même variable, il peut y avoir de nombreux imprévus.
Comme vu dans le cours Swing, voici un scénario possible :
- X vaut 0
- Thread A récupère la valeur de X
- Thread B récupère la valeur de X
- Thread A ajoute 10 à X
- Thread B soustrait 20 à X
- Thread A met à jour X : X = 10
- Thread B met à jour X : X = -20

Si l'on avait conservé une séquence logique, le résultat devrait être -10.
Pour éviter ce type d'erreur, on peut ajouter le mot clé `synchronized` devant une méthode
afin de n'autoriser qu'un seul Thread à la fois à y accéder.
Le premier qui y accède ajoute un lock et le retire lorsqu'il a terminé.

Voici un exemple :
```
public class App {

    private static int x = 0;
    private static void increment(){
        x++;
    }
    private static void decrement(){
        x--;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(() -> {
            for (int i = 0; i < 10000000; i++) {
                increment();
            }
        });

        Thread b = new Thread(() -> {
            for (int i = 0; i < 10000000; i++) {
                decrement();
            }
        });

        a.start();
        b.start();

        a.join();
        b.join();

        System.out.print(x);
    }
}
```

> Faites le nécessaire pour corriger le code ci-dessus
>(le résultat affiché devrait être 0) 