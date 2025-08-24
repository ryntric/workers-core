/**
 * The worker-core is a concurrent library for exchanging and coordinating work as a continuous series of events.
 * It can be used as a queue to transmit some data and handle it between the producer and the event handler.
 * There can be either only 1 producer or multiple and only 1 event poller.
 * <h2>Single producer</h2>
 *
 * <pre>{@code
 *                                           track to prevent wrap
 *                                           +------------------+
 *                                           |                  |
 *                                           |                  v
 * +----+    +-----+            +----+    +====+    +====+   +-----+
 * | P1 |--->| EP1 |            | P1 |--->| RB |<---| SB |   | EP1 |
 * +----+    +-----+            +----+    +====+    +====+   +-----+
 *                                   claim      get    ^        |
 *                                                     |        |
 *                                                     +--------+
 *                                                       await
 * }</pre>
 *
 * <h2>Multiple producers</h2>
 *
 * <pre>{@code
 *                                           track to prevent wrap
 * +----+                                    +------------------+
 * | P2 |                                    |                  |
 * +----+                                    |                  v
 * +----+    +-----+            +----+    +====+    +====+   +-----+
 * | P1 |--->| EP1 |            | P1 |--->| RB |<---| SB |   | EP1 |
 * +----+    +-----+            +----+    +====+    +====+   +-----+
 *                                   claim      get    ^        |
 *                                                     |        |
 *                                                     +--------+
 *                                                       await
 * }</pre>

 * Many of these things have been borrowed from the<b> LMAX Disruptor</b> and have been adapted for the library purposes.
 */

package io.github.ryntric;
