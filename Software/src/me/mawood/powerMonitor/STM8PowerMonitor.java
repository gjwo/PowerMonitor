package me.mawood.powerMonitor;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.serial.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

/**
 * PowerMonitor
 * Created by Matthew Wood on 19/08/2017.
 */
public class STM8PowerMonitor
{
    private static final byte DEFAULT_I2C_ADDRESS = 0x30;
    private static final int DEFAULT_OUTPUT_DATA_FREQUENCY= 1;
    private final I2CDevice device;
    private final int MIN_ADC_CHANNEL = 0;
    private final int MAX_ADC_CHANNEL = 9;
    enum ChannelType{Voltage, Current}
    private final ChannelType channelTypes[] = {ChannelType.Voltage,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,
                                                ChannelType.Current,};


    private int outputDataFrequency;
    private Serial serial;
    private SerialConfig config;

    public STM8PowerMonitor() throws IOException, I2CFactory.UnsupportedBusNumberException
    {
        this(DEFAULT_I2C_ADDRESS);
    }

    public STM8PowerMonitor(byte address) throws IOException, I2CFactory.UnsupportedBusNumberException
    {
        device = I2CFactory.getInstance(I2CBus.BUS_1).getDevice(address);
        configureRTC();
        outputDataFrequency = DEFAULT_OUTPUT_DATA_FREQUENCY;
        InitialiseSerialPort();
    }

    public int getMinADCChannel() {return this.MIN_ADC_CHANNEL;}
    public int getMAXADCChannnel() {return this.MAX_ADC_CHANNEL;}
    public ChannelType getADCChannelType(int channelNumber) {return channelTypes[channelNumber];}

    public int getOutputDataFrequency() {return outputDataFrequency;}
    public void setOutputDataFrequency(int hertz) {this.outputDataFrequency = hertz;}

    public void configureRTC()
    {
        byte[] timeRegisters = constructTimeRegisters();
        for(byte b:timeRegisters)
            System.out.println(String.format("%8s",Integer.toBinaryString(b)).replace(' ', '0') + " = " + String.format("%02x",b));
    }

    private static byte[] constructTimeRegisters()
    {
        ByteBuffer buffer = ByteBuffer.allocate(6);
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        byte seconds = (byte)ldt.getSecond();
        byte secondsByte = DecToBCDArray(seconds)[0];
        buffer.put(secondsByte);

        byte min = (byte)ldt.getMinute();
        byte minutesByte = DecToBCDArray(min)[0];
        buffer.put(minutesByte);

        byte hour = (byte)ldt.getHour();
        byte hourByte = DecToBCDArray(hour)[0];
        buffer.put(hourByte);

        byte day = (byte)ldt.getDayOfMonth();
        byte dayByte = DecToBCDArray(day)[0];
        buffer.put(dayByte);

        byte month = (byte)ldt.getMonth().getValue();
        byte monthByte = DecToBCDArray(month)[0];
        buffer.put(monthByte);

        byte year = (byte)(ldt.getYear() - 2000);
        byte yearByte = DecToBCDArray(year)[0];
        buffer.put(yearByte);


        return buffer.array();
    }

    private static byte[] DecToBCDArray(long num) {
        int digits = 0;

        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }

        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
        boolean isOdd = digits % 2 != 0;

        byte bcd[] = new byte[byteLen];

        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);

            if (i == digits - 1 && isOdd)
                bcd[i / 2] = tmp;
            else if (i % 2 == 0)
                bcd[i / 2] = tmp;
            else {
                byte foo = (byte) (tmp << 4);
                bcd[i / 2] |= foo;
            }

            num /= 10;
        }

        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }

        return bcd;
    }
    private void InitialiseSerialPort()
    {
        config = new SerialConfig();
        try
        {
            config.device(SerialPort.getDefaultPort())
                    .baud(Baud._9600)
                    .dataBits(DataBits._8)
                    .stopBits(StopBits._1)
                    .parity(Parity.NONE)
                    .flowControl(FlowControl.NONE);
        } catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }

        serial = SerialFactory.createInstance();
    }
    public void AddSerialListener( SerialDataEventListener listener)
    {
        serial.addListener(listener);
    }
    public void OpenSerialPort()
    {
        try
        {
            serial.open(config);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void closeSerialPort()
    {
        try
        {
            serial.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
