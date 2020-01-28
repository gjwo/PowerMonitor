package me.mawood.powerMonitor.publishers;

import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue;

import static me.mawood.powerMonitor.Main.getPowerDataMQTTPublisher;

public class PMLogger extends Thread
{
    String msg;
    LinkedBlockingQueue<String> loggingQ;

    public PMLogger(LinkedBlockingQueue<String> logQ)
    {
        this.loggingQ = logQ;
    }

    //
    // Runnable implementation
    //
    @Override
    public void run()
    {
        PowerDataMQTTPublisher publisher = getPowerDataMQTTPublisher();
        String json;
        boolean exit = false;
        try {
            while (!(interrupted() || exit)) {
                msg = loggingQ.take();
                if (msg.equalsIgnoreCase("exit")) {
                    exit = true;
                } //poison pill
                json = "{\"Time\":" +
                        "\"" + Instant.now().toString() + "\"," +
                        "\"LogMsg\":" +
                        "\"" + msg + "\"}";
                publisher.logToBroker(json);
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}