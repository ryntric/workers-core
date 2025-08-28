[![Maven Build](https://github.com/ryntric/workers-core-parent/actions/workflows/build.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/build.yml)
[![JMH Benchmark](https://github.com/ryntric/workers-core-parent/actions/workflows/jmh.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/jmh.yml)
[![JCStress Tests](https://github.com/ryntric/workers-core-parent/actions/workflows/jcstress.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/jcstress.yml)
[![Qodana](https://github.com/ryntric/workers-core-parent/actions/workflows/qodana_code_quality.yml/badge.svg)](https://github.com/ryntric/workers-core-parent/actions/workflows/qodana_code_quality.yml)

### workers-core - it is a low latency Java concurrent library.

---
**Build Environment requirements**
- JDK 11 or greater
- Apache Maven 3.9.11 or greater

---
To build and run jmh tests execute the following commands:
```shell
mvn -pl jmh -am clean install
java -jar -Xms2G -Xmx2G -XX:+AlwaysPreTouch jmh/target/jmh-1.0-SNAPSHOT.jar
```

If you want to run with thread affinity execute the following command:
```shell
taskset -c <corerange> java -jar -Xms2G -Xmx2G -XX:+AlwaysPreTouch jmh/target/jmh-1.0-SNAPSHOT.jar 
```


To build and run jcstress tests execute the following commands:
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

---
## Benchmark

### Hardware information:

#### OS:

```text
NAME="Fedora Linux"
VERSION="42 (KDE Plasma Desktop Edition)"
RELEASE_TYPE=stable
ID=fedora
VERSION_ID=42
VERSION_CODENAME=""
PLATFORM_ID="platform:f42"
PRETTY_NAME="Fedora Linux 42 (KDE Plasma Desktop Edition)"
CPE_NAME="cpe:/o:fedoraproject:fedora:42"
SUPPORT_END=2026-05-13
VARIANT="KDE Plasma Desktop Edition"
VARIANT_ID=kde
```

#### CPU:

```text
Architecture:                x86_64
  CPU op-mode(s):            32-bit, 64-bit
  Address sizes:             48 bits physical, 48 bits virtual
  Byte Order:                Little Endian
CPU(s):                      12
  On-line CPU(s) list:       0-11
Vendor ID:                   AuthenticAMD
  Model name:                AMD Ryzen 9 5900X 12-Core Processor
    CPU family:              25
    Model:                   33
    Thread(s) per core:      1
    Core(s) per socket:      12
    Socket(s):               1
    Stepping:                2
    Frequency boost:         disabled
    CPU(s) scaling MHz:      61%
    CPU max MHz:             6291.9360
    CPU min MHz:             720.1620
    BogoMIPS:                9399.78
    Flags:                   fpu vme de pse tsc msr pae mce cx8 apic sep mtrr pge mca cmov pat pse36 clflush mmx fxsr sse sse2 ht syscall nx mmxext fxsr_opt pdpe1gb rdt
                             scp lm constant_tsc rep_good nopl xtopology nonstop_tsc cpuid extd_apicid aperfmperf rapl pni pclmulqdq monitor ssse3 fma cx16 sse4_1 sse4_
                             2 x2apic movbe popcnt aes xsave avx f16c rdrand lahf_lm cmp_legacy svm extapic cr8_legacy abm sse4a misalignsse 3dnowprefetch osvw ibs skin
                             it wdt tce topoext perfctr_core perfctr_nb bpext perfctr_llc mwaitx cat_l3 cdp_l3 hw_pstate ssbd mba ibrs ibpb stibp vmmcall fsgsbase bmi1 
                             avx2 smep bmi2 erms invpcid cqm rdt_a rdseed adx smap clflushopt clwb sha_ni xsaveopt xsavec xgetbv1 xsaves cqm_llc cqm_occup_llc cqm_mbm_t
                             otal cqm_mbm_local user_shstk clzero irperf xsaveerptr rdpru wbnoinvd arat npt lbrv svm_lock nrip_save tsc_scale vmcb_clean flushbyasid dec
                             odeassists pausefilter pfthreshold avic v_vmsave_vmload vgif v_spec_ctrl umip pku ospke vaes vpclmulqdq rdpid overflow_recov succor smca fs
                             rm debug_swap
Virtualization features:     
  Virtualization:            AMD-V
Caches (sum of all):         
  L1d:                       384 KiB (12 instances)
  L1i:                       384 KiB (12 instances)
  L2:                        6 MiB (12 instances)
  L3:                        64 MiB (2 instances)
NUMA:                        
  NUMA node(s):              1
  NUMA node0 CPU(s):         0-11
Vulnerabilities:             
  Gather data sampling:      Not affected
  Ghostwrite:                Not affected
  Indirect target selection: Not affected
  Itlb multihit:             Not affected
  L1tf:                      Not affected
  Mds:                       Not affected
  Meltdown:                  Not affected
  Mmio stale data:           Not affected
  Reg file data sampling:    Not affected
  Retbleed:                  Not affected
  Spec rstack overflow:      Mitigation; Safe RET
  Spec store bypass:         Mitigation; Speculative Store Bypass disabled via prctl
  Spectre v1:                Mitigation; usercopy/swapgs barriers and __user pointer sanitization
  Spectre v2:                Mitigation; Retpolines; IBPB conditional; IBRS_FW; STIBP disabled; RSB filling; PBRSB-eIBRS Not affected; BHI Not affected
  Srbds:                     Not affected
  Tsa:                       Mitigation; Clear CPU buffers
  Tsx async abort:           Not affected

```

**Results:**

| Benchmark                                                 | Mode  | Cnt | Score          | Error          | Units |
| --------------------------------------------------------- | ------|-----|----------------|----------------| ----- |
| ManyToOneRingBufferBatchPerfTest.manyToOne                | thrpt | 5   | 633888117.801  | ± 22445370.207 | ops/s |
| ManyToOneRingBufferBatchPerfTest.manyToOne:producer1      | thrpt | 5   | 158096131.392  | ±  6493754.594 | ops/s |
| ManyToOneRingBufferBatchPerfTest.manyToOne:producer2      | thrpt | 5   | 158349082.843  | ±  6794095.824 | ops/s |
| ManyToOneRingBufferBatchPerfTest.manyToOne:producer3      | thrpt | 5   | 158816849.255  | ±  5161001.374 | ops/s |
| ManyToOneRingBufferBatchPerfTest.manyToOne:producer4      | thrpt | 5   | 158626054.311  | ±  5381796.289 | ops/s |
| ManyToOneRingBufferSingleItemPerfTest.manyToOne           | thrpt | 5   | 38105340.990   | ±   840096.599 | ops/s |
| ManyToOneRingBufferSingleItemPerfTest.manyToOne:producer1 | thrpt | 5   | 9531410.450    | ±   905999.350 | ops/s |
| ManyToOneRingBufferSingleItemPerfTest.manyToOne:producer2 | thrpt | 5   | 9550906.957    | ±   478558.760 | ops/s |
| ManyToOneRingBufferSingleItemPerfTest.manyToOne:producer3 | thrpt | 5   | 9454564.351    | ±   603505.427 | ops/s |
| ManyToOneRingBufferSingleItemPerfTest.manyToOne:producer4 | thrpt | 5   | 9568459.232    | ±   442088.816 | ops/s |
| OneToOneRingBufferBatchPerfTest.producer                  | thrpt | 5   | 436154321.131  | ±   410209.874 | ops/s |
| OneToOneRingBufferSingleItemPerfTest.producer             | thrpt | 5   | 145633732.829  | ±   604711.778 | ops/s |
