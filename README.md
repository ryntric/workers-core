[![Maven Build](https://github.com/ryntric/workers-core-parent/actions/workflows/build.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/build.yml)
[![JMH Benchmark](https://github.com/ryntric/workers-core-parent/actions/workflows/jmh.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/jmh.yml)
[![JCStress Tests](https://github.com/ryntric/workers-core-parent/actions/workflows/jcstress.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/jcstress.yml)
[![Qodana](https://github.com/ryntric/workers-core-parent/actions/workflows/qodana_code_quality.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/qodana_code_quality.yml)

workers-core - it is a low latency Java concurrent library.

---
To build and run jmh tests execute the following commands:
```shell
mvn -pl jmh -am clean install
java -jar -Xms2G -Xmx2G -XX:+AlwaysPreTouch jmh/target/jmh-1.0-SNAPSHOT.jar
```
To build and jcstress tests execute the following commands:
```shell
mvn -pl jcstress -am clean install
java -jar -Xms2G -Xmx2G -XX:+AlwaysPreTouch jcstress/target/jcstress-1.0-SNAPSHOT.jar 
```
---

Example of usage
--- 

```java
public class Main {
    public static void main(String[] args) {
        EventFactory<Event> factory = Event::new;
        RingBuffer<Event> buffer = new RingBuffer<>(factory, SequencerType.SINGLE_PRODUCER, WaitPolicy.SPINNING, 1024);
        EventHandler<Event> handler = new  EventHandler<>() {
            @Override
            public void onEvent(Event event, long sequence) {
                System.out.println("Event received: " + event + ", sequence: " + sequence);
            }

            @Override
            public void onError(Throwable ex) {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onShutdown() {

            }
        };
        EventTranslator.EventTranslatorOneArg<Event, Object> translator = Event::setEvent;
        WorkerThread<Event> workerThread = new WorkerThread<>("test-worker", new ThreadGroup("thread-group"), buffer, WaitPolicy.PARKING, handler, BatchSizeLimit._1_2);
        workerThread.start();

        for (int i = 0; i < 1024; i++) {
            buffer.publishEvent(translator, new Object());
        }
        
    }

    public static class Event {
        private Object event;

        public Object getEvent() {
            return event;
        }

        public void setEvent(Object event) {
            this.event = event;
        }

        @Override
        public String toString() {
            return "Event{" + "event=" + event + '}';
        }
    }
}
```
