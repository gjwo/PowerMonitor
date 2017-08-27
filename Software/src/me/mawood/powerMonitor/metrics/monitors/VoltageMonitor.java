package me.mawood.powerMonitor.metrics.monitors;

import me.mawood.powerMonitor.metrics.Metric;
import me.mawood.powerMonitor.metrics.monitors.configs.VoltageSenseConfig;
import me.mawood.powerMonitor.metrics.units.Voltage;
import me.mawood.powerMonitor.packets.Packet;
import me.mawood.powerMonitor.packets.PacketCollector;

public class VoltageMonitor extends Monitor<Metric<Voltage>>
{
    private final VoltageSenseConfig config;

    public VoltageMonitor(int bufferSize, VoltageSenseConfig config, PacketCollector packetCollector)
    {
        super(bufferSize);
        this.config = config;
        packetCollector.addPacketEventListener(this);
    }

    @Override
    protected Metric<Voltage> processPacket(Packet packet)
    {
        return new Metric<>(
                config.offsetValue(config.scaleValue(packet.getVRms())), packet.getTimestamp(), Voltage.VOLTS);
    }

    public VoltageSenseConfig getConfig()
    {
        return config;
    }

    @Override
    public String toString()
    {
        return String.format("Voltage Monitor: {VRms: {%s}}",
                ringBuffer.getNewest());
    }
}