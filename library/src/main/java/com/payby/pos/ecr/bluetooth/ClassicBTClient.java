package com.payby.pos.ecr.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.Executors;

public class ClassicBTClient implements Runnable {

    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket btSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private boolean running = false;
    private boolean connecting = false;
    private final BluetoothDevice bluetoothDevice;

    private ClassicBTClientListener listener;

    public void setListener(ClassicBTClientListener listener) {
        this.listener = listener;
    }

    public ClassicBTClient(BluetoothDevice devices) {
        bluetoothDevice = devices;
    }

    public void connect() {
        boolean connected = isConnected();
        if (connected) {
            if (listener != null) {
                listener.onConnected();
            }
            return;
        }
        if (connecting) {
            if (listener != null) {
                listener.onConnecting();
            }
            return;
        }
        new Thread(this).start();
    }

    public boolean isConnected() {
        return btSocket != null && btSocket.isConnected();
    }

    public void send(byte[] bytes) {
        try {
            if (btSocket != null && btSocket.isConnected() && outputStream != null) {
                logging(bytes, "<--- client send");
                outputStream.write(bytes);
                outputStream.flush();
            } else {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        connecting = false;
        closeSocket();
    }

    @Override
    public void run() {
        connecting = true;
        while (connecting) {
            try {
                btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                // 当客户端调用此方法后，系统会执行 SDP 查找，以找到带有所匹配 UUID 的远程设备。如果查找成功并且远程设备接受连接，则其会共享 RFCOMM 通道以便在连接期间使用，并且 connect() 方法将会返回。
                // 如果连接失败，或者 connect() 方法超时（约 12 秒后），则此方法将引发 IOException。
                btSocket.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean connected = isConnected();
            if (connected) {
                connecting = false;
                if (listener != null) {
                    listener.onConnected();
                }
                Executors.newCachedThreadPool().execute(this::loopRead);
            }
        }
    }

    private void loopRead() {
        try {
            running = true;
            inputStream = btSocket.getInputStream();
            outputStream = btSocket.getOutputStream();
            int len;
            byte[] bytes;
            byte[] buffer;
            while (running && btSocket != null && inputStream != null) {
                buffer = new byte[4 * 1024];
                len = inputStream.read(buffer);
                while (len != -1) {
                    bytes = new byte[len];
                    System.arraycopy(buffer, 0, bytes, 0, len);
                    logging(bytes, "---> client received");
                    if (listener != null) {
                        listener.onMessage(bytes);
                    }
                    len = inputStream.read(buffer);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            closeSocket();
        }
    }

    private void closeSocket() {
        try {
            if (btSocket != null) {
                btSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        running = false;
        btSocket = null;
        inputStream = null;
        outputStream = null;
        if (listener != null) {
            listener.onDisconnected();
        }
    }

    private void logging(byte[] bytes, String message) {
        try {
            String string = new String(bytes);
            Log.e("ECR", message + ": " + string);
        } catch (Exception e) {
            //
        }
    }

}
