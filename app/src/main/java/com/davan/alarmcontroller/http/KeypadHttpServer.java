package com.davan.alarmcontroller.http;

/*
 * #%L
 * NanoHttpd-Samples
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

import com.davan.alarmcontroller.http.util.ServerRunner;
import com.davan.alarmcontroller.http.util.KeypadHttpRequestListener;
public class KeypadHttpServer extends NanoHTTPD {

    /**
     * logger to log to.
     */
    private static final String CLASSNAME = KeypadHttpServer.class.getName();
    /**
     * Listener of received requests.
     */
    private KeypadHttpRequestListener receiver;

    public static void main(String[] args) {
        ServerRunner.run(KeypadHttpServer.class);
    }

    public KeypadHttpServer(KeypadHttpRequestListener callbackReceiver)
    {
        super(8080);
        receiver = callbackReceiver;
        try
        {
            printLocalAddress();
        }
        catch (IOException e)
        {
            Log.d(CLASSNAME,"Failed to retrieve ipaddress: "+ e.getMessage());
        }
    }

    public void printLocalAddress()throws IOException
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        Log.d(CLASSNAME,inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(CLASSNAME, "Exception:" + ex.toString());
        }
    }

    @Override
    public Response serve(IHTTPSession session)
    {
        Method method = session.getMethod();
        String uri = session.getUri();
        Log.d(CLASSNAME, method + " '" + uri + "' ");
        String responseMessage ="";
        boolean serviceEnabled = false;


        if(uri.contains("/tts=")) // Request to perform tts
        {
            uri = uri.replace("/tts=","");
            serviceEnabled = receiver.tts(uri);
            responseMessage = "TTS initiated";
        }
        if(uri.compareTo("/ttsFetch") == 0) // Request to fetch a completed tts.
        {
            return receiver.getSpeechFile();
 /*           String TTS_DIRECTORY_NAME = "GeneratedTTS";
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),TTS_DIRECTORY_NAME);
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator+ "TTS.wav");
            Log.d(CLASSNAME, "Return Ttsfile: "+ mediaFile.getAbsolutePath());

            try {
                FileInputStream fis = new FileInputStream(mediaFile);
                return newFixedLengthResponse(
                        Response.Status.OK,
                        "audio/mpeg",
                        fis,
                        mediaFile.length());
            }
            catch(IOException e)
            {
                Log.d(CLASSNAME, "Failed to return Ttsfile");
            }
            */
//            receiver.tts(uri);
        }
        if(uri.compareTo("/WakeUp") == 0) // Request to wakeup screen
        {
            serviceEnabled = receiver.wakeup();
            responseMessage = "Wake up";
        }
        if(uri.compareTo("/Ping") == 0) // Recevied alive message
        {
            String msg = "Ping\n";
            Map<String, String> parms = session.getParms();
            return newFixedLengthResponse(msg);
        }

        Map<String, String> parms = session.getParms();
        String msg = "<html><body><h1>"+responseMessage+"</h1>\nServiceEnabled["+serviceEnabled+"]</body></html>\n";
        return newFixedLengthResponse(msg);
    }
}
